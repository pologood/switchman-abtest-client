package de.is24.common.abtesting.remote;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import de.is24.common.abtesting.remote.api.AbTestDecision;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;


public class RemoteDecisionClientTest extends RemoteClientTest {
  private static final Long USER_ID = 1L;
  private static final String TEST_NAME = "test";
  private AbTestDecision decision;
  private List<AbTestDecision> allDecisionsForUser;
  private HttpStatus status;

  @Before
  @Override
  public void setup() {
    super.setup();
    HystrixRequestContext.initializeContext();
  }

  @Test
  public void shouldGetStoredDecisionForUserIdAndTest() throws IOException {
    givenStoredTestDecisionForUser();
    whenLoadingDecision();
    thenStoredDecisionIsReturned();
  }

  @Test
  public void shouldGetAllDecisionsByUserId() throws IOException {
    givenStoredDecisionsForUser();
    whenLoadingAllDecisions();
    thenStoredDecisionsAreLoaded();
  }

  @Test
  public void shouldCreateDecisions() throws IOException {
    givenServerCanStoreDecisions();
    whenCreatingDecisionForUser();
    thenServerIsCalledCorrectly();
  }

  private void givenStoredDecisionsForUser() throws IOException {
    givenNavigationToDecisionSearch();
    expectServerToReturn(REMOTE_BASE_SERVICE_URI + "/abTestDecisions/search/findByUserSsoId?userSsoId=1",
      "/remoteAbTestDecisionsFindByUserSsoId.json");
  }

  private void givenStoredTestDecisionForUser() throws IOException {
    givenNavigationToDecisionSearch();
    expectServerToReturn(
      REMOTE_BASE_SERVICE_URI + "/abTestDecisions/search/findByTestNameAndUserSsoId?testName=test&userSsoId=1",
      "/remoteAbTestDecisionsFindByTestNameAndUserSsoId.json");
  }

  private void givenNavigationToDecisionSearch() throws IOException {
    expectServerToReturn(REMOTE_BASE_SERVICE_URI, "/testAbRemoteServiceLinks.json");
    expectServerToReturn(REMOTE_BASE_SERVICE_URI + "/abTestDecisions", "/remoteAbTestDecisions.json");
    expectServerToReturn(REMOTE_BASE_SERVICE_URI + "/abTestDecisions/search", "/remoteAbTestDecisionsSearch.json");
  }

  private void givenServerCanStoreDecisions() throws IOException {
    expectServerToReturn(REMOTE_BASE_SERVICE_URI, "/testAbRemoteServiceLinks.json");
    expectServerToReturnHttpStatusAndLocation(
      HttpMethod.POST,
      HttpStatus.CREATED,
      REMOTE_BASE_SERVICE_URI + "/abTestDecisions",
      REMOTE_BASE_SERVICE_URI + "/abTestDecisions/1234567890");
  }

  private void whenLoadingDecision() {
    decision = setupRemoteDecisionClient().getDecisionFor(USER_ID, TEST_NAME);
  }

  private void whenLoadingAllDecisions() {
    allDecisionsForUser = setupRemoteDecisionClient().getDecisionsFor(USER_ID);
  }

  private void whenCreatingDecisionForUser() {
    status = setupRemoteDecisionClient().createDecision(USER_ID, new AbTestDecision());
  }

  private void thenStoredDecisionIsReturned() {
    assertThat(decision, is(notNullValue()));
    mockedRestServer.verify();
  }

  private void thenStoredDecisionsAreLoaded() {
    assertThat(allDecisionsForUser, hasSize(2));
    mockedRestServer.verify();
  }

  private void thenServerIsCalledCorrectly() {
    assertThat(status, is(HttpStatus.CREATED));
    mockedRestServer.verify();
  }

}
