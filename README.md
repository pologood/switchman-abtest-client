```
  _________       .__  __         .__
 /   _____/_  _  _|__|/  |_  ____ |  |__   _____ _____    ____
 \_____  \\ \/ \/ /  \   __\/ ___\|  |  \ /     \\__  \  /    \
 /        \\     /|  ||  | \  \___|   Y  \  Y Y  \/ __ \|   |  \
/_______  / \/\_/ |__||__|  \___  >___|  /__|_|  (____  /___|  /
        \/                      \/     \/      \/     \/     \/
                                  by ImmobilienScout24.de
```
# IS24 Switchman AB Test Client
[![Build Status](https://api.travis-ci.org/ImmobilienScout24/switchman-abtest-client.svg?branch=master)](https://travis-ci.org/ImmobilienScout24/switchman-abtest-client)
[![Coverage Status](https://coveralls.io/repos/ImmobilienScout24/switchman-abtest-client/badge.svg)](https://coveralls.io/r/ImmobilienScout24/switchman-abtest-client)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.is24.common/switchman-abtest-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.is24.common/switchman-abtest-client/)

[IS24 Switchman](https://github.com/ImmobilienScout24/switchman) client for reading and storing AB test configurations and decisions.

## What is this for?
This client may be used to store and provide global A/B-Test configurations. You can have multiple clients that use this global configuration. This enables multiple
services that participate on the same tests or changing a test configuration without redeployment of a cluster.
An other use case is to store test decisions for known users to ensure they get the same test decision everytime they come back (on every device).

The Switchman AB Test client is very basic, so you must implement test integration and the decision making in your app or your own extended client using this lib.

## HowTo configure
To use this client in a Spring Boot project, you can do the following:

Add dependency to switchman-abtest-client to you pom.xml
```xml
    <dependency>
      <groupId>de.is24.common</groupId>
      <artifactId>switchman-abtest-client</artifactId>
      <version>1.0</version>
    </dependency>
```

This lib ships no dependencies, all used libraries have to be provided by the user.

Add a configuration for the client lib:
```java
@Configuration
@Import(RemoteClientConfig.class)
class AbTestClient {
  /* Read config values */
  @Value("${abtest.remote.client.userName:user}")
  private String userName;
  
  @Value("${abtest.remote.client.password:password}")
  private String password;
  
  @Value("${abtest.remote.client.timeout:5000}")
  private int timeout;
  
  @Value("${abtest.remote.client.fallbackEnabled:true}")
  private boolean fallbackEnabled;
  
  @Value("${abtest.remote.client.baseUri:http://localhost:8080}")
  private String remoteServiceBaseUri;
  
  /* Register settings bean to make client work */
  @Bean
  public AbTestRemoteClientSettings abTestRemoteClientSettings() {
   return new AbTestRemoteClientSettings(remoteServiceBaseUri, userName, password, timeout, fallbackEnabled);
  }
}
```

