package de.is24.common.abtesting.remote;

import de.is24.common.abtesting.remote.api.AbTestDecision;
import de.is24.common.abtesting.remote.command.CreateOrUpdateRemoteDecisionCommand;
import de.is24.common.abtesting.remote.command.CreateRemoteDecisionCommand;
import de.is24.common.abtesting.remote.command.GetRemoteDecisionCommand;
import de.is24.common.abtesting.remote.command.GetRemoteDecisionsCommand;
import de.is24.common.hateoas.HateoasLinkProvider;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestOperations;
import java.util.ArrayList;
import java.util.List;


public class RemoteDecisionClient extends AbTestRemoteClient {
  public RemoteDecisionClient(RestOperations restOperations, HateoasLinkProvider hateoasLinkProvider,
                              String remoteServiceBaseUri) {
    super(restOperations, hateoasLinkProvider, remoteServiceBaseUri);
  }

  public List<AbTestDecision> getDecisionsFor(Long userId) {
    Resources<Resource<AbTestDecision>> abTestDecisionResources = new GetRemoteDecisionsCommand(hysterixConfiguration,
      restOperations,
      hateoasLinkProvider,
      remoteServiceBaseUri,
      userId).execute();

    List<AbTestDecision> fromApi = new ArrayList<>();
    for (Resource<AbTestDecision> abTestDecisionResource : abTestDecisionResources) {
      AbTestDecision abTestDecision = abTestDecisionResource.getContent();
      fromApi.add(abTestDecision);
    }
    return fromApi;
  }

  public AbTestDecision getDecisionFor(Long userId, String testName) {
    Resource<AbTestDecision> abTestDecisionResource = new GetRemoteDecisionCommand(hysterixConfiguration,
      restOperations,
      hateoasLinkProvider,
      remoteServiceBaseUri,
      userId,
      testName).execute();
    if (abTestDecisionResource != null) {
      return abTestDecisionResource.getContent();
    }
    return null;
  }

  public HttpStatus createDecision(Long userId, AbTestDecision newDecision) {
    return new CreateRemoteDecisionCommand(hysterixConfiguration,
      restOperations,
      hateoasLinkProvider,
      remoteServiceBaseUri,
      newDecision.getTestName(),
      newDecision.getVariantId(),
      userId).execute();
  }


  public HttpStatus createOrUpdateDecision(Long userId, AbTestDecision newDecision) {
    return new CreateOrUpdateRemoteDecisionCommand(hysterixConfiguration,
      restOperations,
      hateoasLinkProvider,
      remoteServiceBaseUri,
      newDecision.getTestName(),
      newDecision.getVariantId(),
      userId).execute();
  }
}
