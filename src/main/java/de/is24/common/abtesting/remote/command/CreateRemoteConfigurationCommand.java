package de.is24.common.abtesting.remote.command;

import de.is24.common.abtesting.remote.api.AbTestConfiguration;
import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hateoas.HateoasRequestEntity;
import de.is24.common.hystrix.HystrixConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;


public class CreateRemoteConfigurationCommand extends AbstractAbTestRemoteCommand<HttpStatus> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateRemoteConfigurationCommand.class);

  private final String remoteApiUri;
  private final AbTestConfiguration abTestConfiguration;

  public CreateRemoteConfigurationCommand(HystrixConfiguration hysterixConfiguration, RestOperations restOperations,
                                          HateoasLinkProvider hateoasLinkProvider,
                                          String remoteApiUri,
                                          AbTestConfiguration configurationToCreate) {
    super(hysterixConfiguration.getConfiguration(COMMAND_GROUP_KEY), restOperations, hateoasLinkProvider);
    this.remoteApiUri = remoteApiUri;
    this.abTestConfiguration = configurationToCreate;
  }

  @Override
  protected HttpStatus runCommand() throws Exception {
    Link linkToConfigurations = getLinkByName(remoteApiUri, AbTestConfiguration.REL).expand();

    ResponseEntity<?> response = restOperations.exchange(linkToConfigurations.getHref(),
      HttpMethod.POST,
      HateoasRequestEntity.requestEntity(abTestConfiguration),
      (Class) null);
    HttpStatus statusCode = response.getStatusCode();

    if (statusCode != HttpStatus.CREATED) {
      LOGGER.warn("Received unexpected answer when storing test configuration. Status code {}", statusCode);
    }
    return statusCode;
  }

  @Override
  protected HttpStatus getFallback() {
    LOGGER.warn("Failed to create ab test configuration.", this.getFailedExecutionException());
    return HttpStatus.SERVICE_UNAVAILABLE;
  }

}
