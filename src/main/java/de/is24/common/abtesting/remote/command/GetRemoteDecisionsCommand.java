package de.is24.common.abtesting.remote.command;

import de.is24.common.abtesting.remote.api.AbTestDecision;
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


public class GetRemoteDecisionsCommand extends AbstractAbTestRemoteCommand<Resources<Resource<AbTestDecision>>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetRemoteDecisionsCommand.class);

  private final String remoteApiUri;
  private final Long userSsoId;

  public GetRemoteDecisionsCommand(HystrixConfiguration hysterixConfiguration, RestOperations restOperations,
                                   HateoasLinkProvider hateoasLinkProvider,
                                   String remoteApiUri, Long userSsoId) {
    super(hysterixConfiguration.getConfiguration(COMMAND_GROUP_KEY), restOperations, hateoasLinkProvider);
    this.remoteApiUri = remoteApiUri;
    this.userSsoId = userSsoId;
  }

  @Override
  protected Resources<Resource<AbTestDecision>> runCommand() throws Exception {
    Link linkToDecisions = getLinkByName(remoteApiUri, AbTestDecision.REL).expand();
    Link linkToSearch = getLinkByName(linkToDecisions.getHref(), "search").expand();
    Link linkToFindByTestAndUserId = getLinkByName(linkToSearch.getHref(), AbTestDecision.REL_FIND_BY_USERID).expand(
      userSsoId);

    ResponseEntity<Resources<Resource<AbTestDecision>>> remoteDecisionsEntity = restOperations.exchange(
      linkToFindByTestAndUserId.getHref(),
      HttpMethod.GET,
      HateoasRequestEntity.requestEntity(),
      new ParameterizedTypeReference<Resources<Resource<AbTestDecision>>>() {
      });

    return remoteDecisionsEntity.getBody();

  }

  @Override
  protected Resources<Resource<AbTestDecision>> getFallback() {
    LOGGER.warn("No decisions could be retrieved.", this.getFailedExecutionException());
    return new Resources<Resource<AbTestDecision>>((Iterable) Collections.emptyList(),
      (Iterable) Collections.emptyList());
  }

}
