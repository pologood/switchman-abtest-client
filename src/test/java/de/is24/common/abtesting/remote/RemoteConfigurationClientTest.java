package de.is24.common.abtesting.remote;

import de.is24.common.abtesting.remote.api.AbTestConfiguration;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.Map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;


public class RemoteConfigurationClientTest extends RemoteClientTest {
  private Map<String, AbTestConfiguration> remoteConfigurations;
  private HttpStatus returnStatus;

  @Test
  public void shouldLoadRemoteConfigurations() throws IOException {
    givenRemoteConfigurations();
    whenLoadingRemoteConfigurations();
    thenAllRemoteConfigurationsWhereLoaded();
  }

  @Test
  public void shouldCreateRemoteConfigurations() throws IOException {
    givenServiceAcceptsCreateCalls();
    whenStoringConfiguration();
    thenConfigurationIsStoredOnService();
  }

  private void whenStoringConfiguration() {
    AbTestConfiguration testConfiguration = givenTestConfiguration();
    returnStatus = setupRemoteConfigurationClient().createRemoteConfiguration(testConfiguration);
  }

  private AbTestConfiguration givenTestConfiguration() {
    AbTestConfiguration testConfiguration = new AbTestConfiguration();
    testConfiguration.setName("name");
    return testConfiguration;
  }

  private void thenConfigurationIsStoredOnService() {
    mockedRestServer.verify();
    assertThat(returnStatus, is(HttpStatus.CREATED));
  }

  private void givenServiceAcceptsCreateCalls() throws IOException {
    expectServerToReturn(REMOTE_BASE_SERVICE_URI, "/testAbRemoteServiceLinks.json");
    expectServerToReturnHttpStatusAndLocation(
      HttpMethod.POST,
      HttpStatus.CREATED,
      REMOTE_BASE_SERVICE_URI + "/abTestConfigurations",
      REMOTE_BASE_SERVICE_URI + "/abTestConfigurations/1234567890");
  }

  private void givenRemoteConfigurations() throws IOException {
    expectServerToReturn(REMOTE_BASE_SERVICE_URI, "/testAbRemoteServiceLinks.json");
    expectServerToReturn(REMOTE_BASE_SERVICE_URI + "/abTestConfigurations", "/testAbRemoteConfigurations.json");
  }

  private void whenLoadingRemoteConfigurations() {
    remoteConfigurations = setupRemoteConfigurationClient().getRemoteConfiguration();
  }

  private void thenAllRemoteConfigurationsWhereLoaded() {
    mockedRestServer.verify();
    assertThat(remoteConfigurations.keySet(), hasSize(2));
  }

}
