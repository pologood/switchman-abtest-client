package de.is24.common.abtesting.remote.command;

import de.is24.common.abtesting.remote.api.AbTestConfiguration;
import de.is24.common.abtesting.remote.api.AbTestDecision;
import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hateoas.HateoasRequestEntity;
import de.is24.common.hystrix.HystrixConfiguration;
import org.slf4j.Logger;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import static org.slf4j.LoggerFactory.getLogger;


public class DeleteRemoteConfigurationCommand extends AbstractAbTestRemoteCommand<HttpStatus> {
  private static final Logger LOGGER = getLogger(DeleteRemoteConfigurationCommand.class);

  private String remoteApiUri;
  private AbTestConfiguration configurationToDelete;

  public DeleteRemoteConfigurationCommand(HystrixConfiguration hysterixConfiguration,
                                          RestOperations restOperations,
                                          HateoasLinkProvider hateoasLinkProvider,
                                          String remoteApiUri,
                                          AbTestConfiguration configurationToDelete) {
    super(hysterixConfiguration.getConfiguration(COMMAND_GROUP_KEY), restOperations, hateoasLinkProvider);
    this.remoteApiUri = remoteApiUri;
    this.configurationToDelete = configurationToDelete;
  }

  @Override
  protected HttpStatus runCommand() throws Exception {
    HttpStatus statusCode = deleteDecisionsOfConfigurationToDelete();
    if (statusCode != HttpStatus.OK) {
      LOGGER.warn("Received unexpected answer when deleting all test decisions for test {}. Status code {}",
        configurationToDelete.getName(),
        statusCode);
      return statusCode;
    }
    statusCode = deleteConfiguration();
    if (statusCode != HttpStatus.NO_CONTENT) {
      LOGGER.warn("Received unexpected answer when deleting test configuration {}. Status code {}",
        configurationToDelete.getName(),
        statusCode);
    }
    return statusCode;
  }

  private HttpStatus deleteDecisionsOfConfigurationToDelete() {
    Link linkToDecisions = getLinkByName(remoteApiUri, AbTestDecision.REL).expand();
    Link linkToSearch = getLinkByName(linkToDecisions.getHref(), "search").expand();
    Link linkToDeleteByTestName = getLinkByName(linkToSearch.getHref(), AbTestDecision.REL_DELETE_BY_TESTNAME).expand(
      configurationToDelete.getName());
    ResponseEntity<?> response = restOperations.exchange(
      linkToDeleteByTestName.getHref(),
      HttpMethod.GET,
      HateoasRequestEntity.requestEntity(),
      (Class) null);

    return response.getStatusCode();
  }

  private HttpStatus deleteConfiguration() {
    Link linkToConfigurations = getLinkByName(remoteApiUri, AbTestConfiguration.REL).expand();

    ResponseEntity<?> response = restOperations.exchange(
      linkToConfigurations.getHref() + "/" + configurationToDelete.getName(),
      HttpMethod.DELETE,
      null,
      (Class) null);
    return response.getStatusCode();
  }

  @Override
  protected HttpStatus getFallback() {
    LOGGER.warn("Failed to delete ab test configuration.", this.getFailedExecutionException());
    return HttpStatus.SERVICE_UNAVAILABLE;
  }
}
