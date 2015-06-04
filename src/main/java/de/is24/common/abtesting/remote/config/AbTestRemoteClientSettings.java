package de.is24.common.abtesting.remote.config;

public class AbTestRemoteClientSettings {
  private final String remoteServiceBaseUri;

  private final String userName;
  private final String password;

  private final Integer timeoutInMilliseconds;
  private final boolean fallbackEnabled;

  public AbTestRemoteClientSettings(String remoteServiceBaseUri, String userName, String password,
                                    Integer timeoutInMilliseconds, boolean fallbackEnabled) {
    this.remoteServiceBaseUri = remoteServiceBaseUri;
    this.userName = userName;
    this.password = password;
    this.timeoutInMilliseconds = timeoutInMilliseconds;
    this.fallbackEnabled = fallbackEnabled;
  }

  public String getRemoteServiceBaseUri() {
    return remoteServiceBaseUri;
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword() {
    return password;
  }

  public Integer getTimeoutInMilliseconds() {
    return timeoutInMilliseconds;
  }

  public boolean isFallbackEnabled() {
    return fallbackEnabled;
  }
}
