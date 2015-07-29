package de.is24.common.abtesting.remote.command;

public enum PageableParameters {
  SIZE("size"),
  PAGE("page"),
  SORT("sort");

  private final String pageableParameterName;

  PageableParameters(final String pageableParameterName) {
    this.pageableParameterName = pageableParameterName;
  }

  @Override
  public String toString() {
    return this.pageableParameterName;
  }
}