package de.is24.common.abtesting.remote.command;

import de.is24.common.abtesting.remote.api.AbTestDecision;
import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hystrix.HystrixConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestOperations;


public class CreateOrUpdateRemoteDecisionCommand extends AbstractAbTestRemoteCommand<HttpStatus> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateOrUpdateRemoteDecisionCommand.class);

  private final HystrixConfiguration hysterixConfiguration;
  private final String remoteApiUri;
  private final String testName;
  private final int variantId;
  private final Long userId;

  public CreateOrUpdateRemoteDecisionCommand(HystrixConfiguration hysterixConfiguration, RestOperations restOperations,
                                             HateoasLinkProvider hateoasLinkProvider,
                                             String remoteApiUri,
                                             String testName, int variantId, Long userId) {
    super(hysterixConfiguration.getConfiguration(COMMAND_GROUP_KEY), restOperations, hateoasLinkProvider);
    this.hysterixConfiguration = hysterixConfiguration;
    this.remoteApiUri = remoteApiUri;
    this.testName = testName;
    this.variantId = variantId;
    this.userId = userId;

  }

  @Override
  protected HttpStatus runCommand() throws Exception {
    Resource<AbTestDecision> existingDecisionResource = new GetRemoteDecisionCommand(this.hysterixConfiguration,
      this.restOperations,
      this.hateoasLinkProvider,
      this.remoteApiUri,
      this.userId,
      this.testName).execute();

    if (existingDecisionResource != null) {
      String resourceUri = existingDecisionResource.getId().getHref();
      AbTestDecision abTestDecision = existingDecisionResource.getContent();
      abTestDecision.setVariantId(variantId);
      return new UpdateRemoteDecisionCommand(this.hysterixConfiguration,
        this.restOperations,
        this.hateoasLinkProvider,
        resourceUri,
        abTestDecision).execute();

    } else {
      return new CreateRemoteDecisionCommand(this.hysterixConfiguration,
        this.restOperations,
        this.hateoasLinkProvider,
        this.remoteApiUri,
        this.testName,
        variantId,
        this.userId).execute();
    }
  }

  @Override
  protected HttpStatus getFallback() {
    LOGGER.warn("Failed to create ab test decision.", this.getFailedExecutionException());
    return HttpStatus.SERVICE_UNAVAILABLE;
  }

}
