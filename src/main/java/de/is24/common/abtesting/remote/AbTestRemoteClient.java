package de.is24.common.abtesting.remote;

import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hystrix.HystrixConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;


public abstract class AbTestRemoteClient {
  protected final HateoasLinkProvider hateoasLinkProvider;
  protected final String remoteServiceBaseUri;
  protected final RestOperations restOperations;

  @Autowired
  protected HystrixConfiguration hysterixConfiguration;

  public AbTestRemoteClient(RestOperations restOperations, HateoasLinkProvider hateoasLinkProvider,
                            String remoteServiceBaseUri) {
    this.restOperations = restOperations;
    this.hateoasLinkProvider = hateoasLinkProvider;
    this.remoteServiceBaseUri = remoteServiceBaseUri;
  }
}
