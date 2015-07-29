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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;


public class GetRemoteConfigurationsCommand
  extends AbstractAbTestRemoteCommand<Resources<Resource<AbTestConfiguration>>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetRemoteConfigurationsCommand.class);
  private final String remoteApiUri;
  private final Map<PageableParameters, String> parameterMap;

  public GetRemoteConfigurationsCommand(final HystrixConfiguration hysterixConfiguration,
                                        final RestOperations restOperations,
                                        final HateoasLinkProvider hateoasLinkProvider,
                                        final String remoteConfigurationProviderUri,
                                        final Map<PageableParameters, String> parameterMap) {
    super(hysterixConfiguration.getConfiguration(COMMAND_GROUP_KEY), restOperations, hateoasLinkProvider);
    this.remoteApiUri = remoteConfigurationProviderUri;
    this.parameterMap = parameterMap;
  }

  @Override
  protected Resources<Resource<AbTestConfiguration>> runCommand() throws RestClientException {
    final Map<String, String> stringMap =
        parameterMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), Entry::getValue));

    Link linkToConfigurations = getLinkByName(remoteApiUri, AbTestConfiguration.REL).expand(stringMap);

    LOGGER.info(parameterMap.toString());
    ResponseEntity<Resources<Resource<AbTestConfiguration>>> responseEntity = restOperations.exchange(
      linkToConfigurations.getHref(),
      HttpMethod.GET,
      HateoasRequestEntity.requestEntity(),
      new ParameterizedTypeReference<Resources<Resource<AbTestConfiguration>>>() {
      });

    return responseEntity.getBody();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Resources<Resource<AbTestConfiguration>> getFallback() {
    LOGGER.warn(
      "No configuration could be retrieved. Using empty remote configuration.",
      this.getFailedExecutionException());

    return new Resources<>((Iterable) Collections.emptyList(),
        (Iterable) Collections.emptyList());
  }

}
