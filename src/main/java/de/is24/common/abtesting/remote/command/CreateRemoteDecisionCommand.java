package de.is24.common.abtesting.remote.command;

import de.is24.common.abtesting.remote.api.AbTestDecision;
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


public class CreateRemoteDecisionCommand extends AbstractAbTestRemoteCommand<HttpStatus> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateRemoteDecisionCommand.class);

  private final String remoteApiUri;
  private final String testName;
  private final int variantId;
  private final Long userId;

  public CreateRemoteDecisionCommand(HystrixConfiguration hysterixConfiguration, RestOperations restOperations,
                                     HateoasLinkProvider hateoasLinkProvider,
                                     String remoteApiUri,
                                     String testName, int variantId, Long userId) {
    super(hysterixConfiguration.getConfiguration(COMMAND_GROUP_KEY), restOperations, hateoasLinkProvider);
    this.remoteApiUri = remoteApiUri;
    this.userId = userId;
    this.testName = testName;

    this.variantId = variantId;
  }

  @Override
  protected HttpStatus runCommand() throws Exception {
    Link linkToDecisions = getLinkByName(remoteApiUri, AbTestDecision.REL).expand();

    AbTestDecision abTestDecision = createAbTestDecision(testName, userId, variantId);

    ResponseEntity<?> response = restOperations.exchange(linkToDecisions.getHref(),
      HttpMethod.POST,
      HateoasRequestEntity.requestEntity(abTestDecision),
      (Class) null);
    HttpStatus statusCode = response.getStatusCode();

    if (statusCode != HttpStatus.CREATED) {
      LOGGER.warn("Received unexpected answer when storing test decision. Status code {}", statusCode);

    }
    return statusCode;
  }

  @Override
  protected HttpStatus getFallback() {
    LOGGER.warn("Failed to create ab test decision.", this.getFailedExecutionException());
    return HttpStatus.SERVICE_UNAVAILABLE;
  }


  private AbTestDecision createAbTestDecision(String testName, Long userSsoId, int variantId) {
    AbTestDecision abTestDecision = new AbTestDecision();
    abTestDecision.setTestName(testName);
    abTestDecision.setUserSsoId(userSsoId.toString());
    abTestDecision.setVariantId(variantId);
    return abTestDecision;
  }
}
