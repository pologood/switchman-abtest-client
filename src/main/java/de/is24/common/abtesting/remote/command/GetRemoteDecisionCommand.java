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


public class GetRemoteDecisionCommand extends AbstractAbTestRemoteCommand<Resource<AbTestDecision>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetRemoteDecisionCommand.class);

  private final String remoteApiUri;
  private final String testName;
  private final Long userSsoId;

  public GetRemoteDecisionCommand(HystrixConfiguration hysterixConfiguration, RestOperations restOperations,
                                  HateoasLinkProvider hateoasLinkProvider,
                                  String remoteApiUri, Long userSsoId, String testName) {
    super(hysterixConfiguration.getConfiguration(COMMAND_GROUP_KEY), restOperations, hateoasLinkProvider);
    this.remoteApiUri = remoteApiUri;
    this.userSsoId = userSsoId;
    this.testName = testName;
  }

  @Override
  protected Resource<AbTestDecision> runCommand() throws Exception {
    Link linkToDecisions = getLinkByName(remoteApiUri, AbTestDecision.REL).expand();
    Link linkToSearch = getLinkByName(linkToDecisions.getHref(), "search").expand();
    Link linkToFindByTestAndUserId = getLinkByName(linkToSearch.getHref(), AbTestDecision.REL_FIND_BY_NAME_AND_USERID)
      .expand(testName, userSsoId);

    ResponseEntity<Resources<Resource<AbTestDecision>>> remoteDecisionsEntity = restOperations.exchange(
      linkToFindByTestAndUserId.getHref(),
      HttpMethod.GET,
      HateoasRequestEntity.requestEntity(),
      new ParameterizedTypeReference<Resources<Resource<AbTestDecision>>>() {
      });

    Resources<Resource<AbTestDecision>> remoteDecisions = remoteDecisionsEntity.getBody();

    if (remoteDecisions.iterator().hasNext()) {
      return remoteDecisions.iterator().next();
    }

    return null;
  }

  @Override
  protected String getCacheKey() {
    return testName + ":" + String.valueOf(userSsoId);
  }

  @Override
  protected Resource<AbTestDecision> getFallback() {
    LOGGER.warn(
      "No decision could be retrieved.",
      this.getFailedExecutionException());
    return null;
  }
}
