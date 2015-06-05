package de.is24.common.abtesting.remote;

import de.is24.common.abtesting.remote.api.serialization.HalEnabledObjectMapper;
import de.is24.common.abtesting.remote.config.RemoteClientConfig;
import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hystrix.HystrixConfiguration;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


abstract class RemoteClientTest {
  protected static final String REMOTE_BASE_SERVICE_URI = "http://microservice.abtest.test/api";
  private static final HystrixConfiguration HYSTRIX_CONFIGURATION = new HystrixConfiguration(false, 2000);
  protected RestTemplate restOperations;
  protected HateoasLinkProvider hateoasLinkProvider;
  protected MockRestServiceServer mockedRestServer;

  protected RestTemplate setupRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(mappingJackson2HttpMessageConverter());
    restTemplate.setMessageConverters(messageConverters);
    return restTemplate;
  }

  private HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
    mapper.setObjectMapper(new HalEnabledObjectMapper());
    mapper.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, RemoteClientConfig.APPLICATION_HAL_JSON));
    return mapper;
  }

  protected RemoteConfigurationClient setupRemoteConfigurationClient() {
    RemoteConfigurationClient remoteConfigurationClient = new RemoteConfigurationClient(restOperations,
      hateoasLinkProvider,
      REMOTE_BASE_SERVICE_URI);
    ReflectionTestUtils.setField(remoteConfigurationClient, "hysterixConfiguration", HYSTRIX_CONFIGURATION);
    return remoteConfigurationClient;
  }

  protected RemoteDecisionClient setupRemoteDecisionClient() {
    RemoteDecisionClient remoteDecisionClient = new RemoteDecisionClient(restOperations,
      hateoasLinkProvider,
      REMOTE_BASE_SERVICE_URI);
    ReflectionTestUtils.setField(remoteDecisionClient, "hysterixConfiguration", HYSTRIX_CONFIGURATION);
    return remoteDecisionClient;
  }

  protected void expectServerToReturn(String uri, String fileToRead) throws IOException {
    String testJsonContent = IOUtils.toString(this.getClass().getResourceAsStream(fileToRead), "UTF-8");
    mockedRestServer.expect(requestTo(uri))
    .andExpect(method(HttpMethod.GET))
    .andRespond(withSuccess(testJsonContent, MediaType.APPLICATION_JSON));
  }

  protected void expectServerToReturnHttpStatusAndLocation(HttpMethod httpMethod, HttpStatus httpStatus,
                                                           String uri, String location) throws IOException {
    DefaultResponseCreator responseCreator = withStatus(httpStatus);

    if (location != null) {
      responseCreator.location(URI.create(location));
    }

    mockedRestServer.expect(requestTo(uri)).andExpect(method(httpMethod)).andRespond(responseCreator);
  }

  @Before
  public void setup() {
    restOperations = setupRestTemplate();
    hateoasLinkProvider = new HateoasLinkProvider(restOperations);
    mockedRestServer = MockRestServiceServer.createServer(restOperations);
  }
}
