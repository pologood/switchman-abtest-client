package de.is24.common.abtesting.remote.command;

import de.is24.common.abtesting.remote.api.AbTestConfiguration;
import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hateoas.HateoasRequestEntity;
import de.is24.common.hystrix.HystrixConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import java.util.Collections;


public class GetRemoteConfigurationsCommand
  extends AbstractAbTestRemoteCommand<Resources<Resource<AbTestConfiguration>>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetRemoteConfigurationsCommand.class);
  private final String remoteApiUri;

  public GetRemoteConfigurationsCommand(HystrixConfiguration hysterixConfiguration, RestOperations restOperations,
                                        HateoasLinkProvider hateoasLinkProvider,
                                        String remoteConfigurationProviderUri) {
    super(hysterixConfiguration.getConfiguration(COMMAND_GROUP_KEY), restOperations, hateoasLinkProvider);
    this.remoteApiUri = remoteConfigurationProviderUri;
  }

  @Override
  protected Resources<Resource<AbTestConfiguration>> runCommand() throws Exception {
    Link linkToConfigurations = getLinkByName(remoteApiUri, AbTestConfiguration.REL).expand();

    ResponseEntity<Resources<Resource<AbTestConfiguration>>> responseEntity = restOperations.exchange(
      linkToConfigurations.getHref(),
      HttpMethod.GET,
      HateoasRequestEntity.requestEntity(),
      new ParameterizedTypeReference<Resources<Resource<AbTestConfiguration>>>() {
      });

    return responseEntity.getBody();
  }

  @Override
  protected Resources<Resource<AbTestConfiguration>> getFallback() {
    LOGGER.warn(
      "No configuration could be retrieved. Using empty remote configuration.",
      this.getFailedExecutionException());
    return new Resources<Resource<AbTestConfiguration>>((Iterable) Collections.emptyList(),
      (Iterable) Collections.emptyList());
  }
}
