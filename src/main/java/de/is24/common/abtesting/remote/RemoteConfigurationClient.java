package de.is24.common.abtesting.remote;

import de.is24.common.abtesting.remote.api.AbTestConfiguration;
import de.is24.common.abtesting.remote.command.CreateRemoteConfigurationCommand;
import de.is24.common.abtesting.remote.command.DeleteRemoteConfigurationCommand;
import de.is24.common.abtesting.remote.command.GetRemoteConfigurationsCommand;
import de.is24.common.abtesting.remote.command.UpdateRemoteConfigurationCommand;
import de.is24.common.hateoas.HateoasLinkProvider;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestOperations;
import java.util.HashMap;
import java.util.Map;


public class RemoteConfigurationClient extends AbTestRemoteClient {
  public RemoteConfigurationClient(RestOperations restOperations, HateoasLinkProvider hateoasLinkProvider,
                                   String remoteServiceBaseUri) {
    super(restOperations, hateoasLinkProvider, remoteServiceBaseUri);
  }

  public Map<String, AbTestConfiguration> getRemoteConfiguration() {
    Resources<Resource<AbTestConfiguration>> abTestConfigurationResources =
      new GetRemoteConfigurationsCommand(hysterixConfiguration,
        restOperations,
        hateoasLinkProvider,
        remoteServiceBaseUri).execute();


    Map<String, AbTestConfiguration> fromApi = new HashMap<>();
    for (Resource<AbTestConfiguration> remoteConfiguration : abTestConfigurationResources) {
      AbTestConfiguration abTestConfiguration = remoteConfiguration.getContent();
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
