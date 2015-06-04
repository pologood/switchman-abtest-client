package de.is24.common.abtesting.remote.command;

import de.is24.common.abtesting.remote.api.AbTestConfiguration;
import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hateoas.HateoasRequestEntity;
import de.is24.common.hystrix.HystrixConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;


public class UpdateRemoteConfigurationCommand extends AbstractAbTestRemoteCommand<HttpStatus> {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRemoteConfigurationCommand.class);

  private final String resourceUri;
  private final AbTestConfiguration existingConfiguration;

  public UpdateRemoteConfigurationCommand(HystrixConfiguration hysterixConfiguration, RestOperations restOperations,
                                          HateoasLinkProvider hateoasLinkProvider,
                                          String resourceUri,
                                          AbTestConfiguration existingConfiguration) {
    super(hysterixConfiguration.getConfiguration(COMMAND_GROUP_KEY), restOperations, hateoasLinkProvider);
    this.resourceUri = resourceUri;
    this.existingConfiguration = existingConfiguration;
  }

  @Override
  protected HttpStatus runCommand() throws Exception {
    ResponseEntity<?> response = restOperations.exchange(
      resourceUri,
      HttpMethod.PUT,
      HateoasRequestEntity.requestEntity(existingConfiguration),
      (Class) null);
    HttpStatus statusCode = response.getStatusCode();

    if (statusCode != HttpStatus.NO_CONTENT) {
      LOGGER.warn("Received unexpected answer when updating test configuration. Status code {}", statusCode);

    }
    return statusCode;
  }

}
