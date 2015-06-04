package de.is24.common.abtesting.remote.config;

import de.is24.common.abtesting.remote.RemoteConfigurationClient;
import de.is24.common.abtesting.remote.RemoteDecisionClient;
import de.is24.common.abtesting.remote.api.serialization.HalEnabledObjectMapper;
import de.is24.common.abtesting.remote.http.PreEmptiveAuthHttpRequestFactory;
import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hystrix.HystrixConfiguration;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Configuration
public class RemoteClientConfig {
  public static final MediaType APPLICATION_HAL_JSON = new MediaType("application", "hal+json");

  @Autowired
  private AbTestRemoteClientSettings abTestRemoteClientSettings;

  @Bean
  public AuthScope abTestAuthScope() {
    return new AuthScope(null, -1, null, null);
  }

  @Bean
  public UsernamePasswordCredentials abTestCredentials() {
    return new UsernamePasswordCredentials(
      abTestRemoteClientSettings.getUserName(),
      abTestRemoteClientSettings.getPassword());
  }

  // Credentials provider needed by apache default http client
  @Bean
  public BasicCredentialsProvider abTestCredentialProvider() {
    BasicCredentialsProvider provider = new BasicCredentialsProvider();
    provider.setCredentials(abTestAuthScope(), abTestCredentials());
    return provider;
  }

  @Bean
  public HalEnabledObjectMapper halEnabledObjectMapper() {
    return new HalEnabledObjectMapper();
  }

  @Bean
  public HttpClientBuilder abTestHttpClientBuilder() {
    return HttpClientBuilder.create().setDefaultCredentialsProvider(abTestCredentialProvider());
  }

  @Bean
  public CloseableHttpClient abTestHttpClient() {
    return abTestHttpClientBuilder().build();
  }

  @Bean
  public PreEmptiveAuthHttpRequestFactory abTestHttpClientFactory() {
    return new PreEmptiveAuthHttpRequestFactory(abTestHttpClient());
  }

  @Bean
  public RestTemplate abTestRestTemplate() {
    RestTemplate restTemplate = new RestTemplate(abTestHttpClientFactory());

    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(mappingJackson2HttpMessageConverter());
    restTemplate.setMessageConverters(messageConverters);

    return restTemplate;
  }

  @Bean
  public HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
    mapper.setObjectMapper(halEnabledObjectMapper());
    mapper.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, APPLICATION_HAL_JSON));
    return mapper;
  }

  @Bean
  public HystrixConfiguration hysterixConfiguration() {
    return new HystrixConfiguration(
      abTestRemoteClientSettings.isFallbackEnabled(),
      abTestRemoteClientSettings.getTimeoutInMilliseconds());
  }

  @Bean
  public HateoasLinkProvider hateoasLinkProvider() {
    return new HateoasLinkProvider(abTestRestTemplate());
  }

  @Bean
  public RemoteConfigurationClient remoteConfigurationClient() {
    return new RemoteConfigurationClient(abTestRestTemplate(),
      hateoasLinkProvider(),
      abTestRemoteClientSettings.getRemoteServiceBaseUri());
  }

  @Bean
  public RemoteDecisionClient remoteDecisionClient() {
    return new RemoteDecisionClient(abTestRestTemplate(),
      hateoasLinkProvider(),
      abTestRemoteClientSettings.getRemoteServiceBaseUri());
  }

}
