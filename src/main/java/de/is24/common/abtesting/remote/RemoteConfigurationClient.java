package de.is24.common.abtesting.remote;

import de.is24.common.abtesting.remote.api.AbTestConfiguration;
import de.is24.common.abtesting.remote.command.CreateRemoteConfigurationCommand;
import de.is24.common.abtesting.remote.command.DeleteRemoteConfigurationCommand;
import de.is24.common.abtesting.remote.command.GetRemoteConfigurationsCommand;
import de.is24.common.abtesting.remote.command.PageableParameters;
import de.is24.common.abtesting.remote.command.SearchRemoteConfigurationsByNamePrefixCommand;
import de.is24.common.abtesting.remote.command.UpdateRemoteConfigurationCommand;
import de.is24.common.hateoas.HateoasLinkProvider;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestOperations;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class RemoteConfigurationClient extends AbTestRemoteClient {
  public RemoteConfigurationClient(RestOperations restOperations, HateoasLinkProvider hateoasLinkProvider,
                                   String remoteServiceBaseUri) {
    super(restOperations, hateoasLinkProvider, remoteServiceBaseUri);
  }

  public Map<String, AbTestConfiguration> getRemoteConfiguration() {
    return getRemoteConfiguration(new HashMap<>());
  }

  public Map<String, AbTestConfiguration> getRemoteConfiguration(final Map<PageableParameters, String> parameterMap) {
    final Resources<Resource<AbTestConfiguration>> abTestConfigurationResources =
        new GetRemoteConfigurationsCommand(hysterixConfiguration,
            restOperations,
            hateoasLinkProvider,
            remoteServiceBaseUri,
            parameterMap).execute();


    final Map<String, AbTestConfiguration> fromApi = new LinkedHashMap<>();
    for (Resource<AbTestConfiguration> remoteConfiguration : abTestConfigurationResources) {
      final AbTestConfiguration abTestConfiguration = remoteConfiguration.getContent();
      fromApi.put(abTestConfiguration.getName(), abTestConfiguration);
    }
    return fromApi;
  }

  public Map<String, AbTestConfiguration> searchByNamePrefix(final Map<PageableParameters, String> parameterMap,
                                                             final String prefix) {
    final Resources<Resource<AbTestConfiguration>> abTestConfigurationResources =
        new SearchRemoteConfigurationsByNamePrefixCommand(hysterixConfiguration,
            restOperations,
            hateoasLinkProvider,
            remoteServiceBaseUri,
            parameterMap,
            prefix).execute();


    final Map<String, AbTestConfiguration> fromApi = new LinkedHashMap<>();
    for (Resource<AbTestConfiguration> remoteConfiguration : abTestConfigurationResources) {
      final AbTestConfiguration abTestConfiguration = remoteConfiguration.getContent();
      fromApi.put(abTestConfiguration.getName(), abTestConfiguration);
    }
    return fromApi;
  }

  public HttpStatus createRemoteConfiguration(AbTestConfiguration testConfiguration) {
    HttpStatus answerFromApi = new CreateRemoteConfigurationCommand(hysterixConfiguration,
        restOperations,
        hateoasLinkProvider,
        remoteServiceBaseUri,
        testConfiguration).execute();
    return answerFromApi;
  }

  public HttpStatus deleteRemoteConfiguration(AbTestConfiguration testConfiguration) {
    HttpStatus answerFromApi = new DeleteRemoteConfigurationCommand(hysterixConfiguration,
        restOperations,
        hateoasLinkProvider,
        remoteServiceBaseUri,
        testConfiguration).execute();
    return answerFromApi;
  }

  public HttpStatus updateRemoteConfiguration(AbTestConfiguration abTestConfiguration) {
    HttpStatus answerFromApi = new UpdateRemoteConfigurationCommand(hysterixConfiguration,
        restOperations,
        hateoasLinkProvider,
        remoteServiceBaseUri,
        abTestConfiguration).execute();
    return answerFromApi;
  }

}
