package de.is24.common.abtesting.remote;

import de.is24.common.abtesting.remote.api.AbTestConfiguration;
import de.is24.common.abtesting.remote.command.GetRemoteConfigurationsCommand.Parameters;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
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
  public void shouldLoadParametrizedRemoteConfigurations() throws IOException {
    givenRemoteConfigurations("page=1&size=5&sort=from,desc");
    final Map<Parameters, String> params = new HashMap<>();
    params.put(Parameters.SIZE, "5");
    params.put(Parameters.PAGE, "1");
    params.put(Parameters.SORT, "from,desc");
    whenLoadingParametrizedRemoteConfigurations(params);
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
    givenRemoteConfigurations(null);
  }

  private void givenRemoteConfigurations(final String parameters) throws IOException {
    expectServerToReturn(REMOTE_BASE_SERVICE_URI, "/testAbRemoteServiceLinks.json");
    expectServerToReturn(REMOTE_BASE_SERVICE_URI + "/abTestConfigurations" + (parameters != null ? "?" + parameters : ""),
        "/testAbRemoteConfigurations.json");
  }

  private void whenLoadingRemoteConfigurations() {
    remoteConfigurations = setupRemoteConfigurationClient().getRemoteConfiguration();
  }

  private void whenLoadingParametrizedRemoteConfigurations(final Map<Parameters, String> params) {
    remoteConfigurations = setupRemoteConfigurationClient().getRemoteConfiguration(params);
  }

  private void thenAllRemoteConfigurationsWhereLoaded() {
    mockedRestServer.verify();
    assertThat(remoteConfigurations.keySet(), hasSize(2));
  }

}
