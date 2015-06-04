package de.is24.common.abtesting.remote.api;

import lombok.Data;
import java.io.Serializable;


@Data
public class AbTestDecision implements Serializable {
  public static final String REL = "abTestDecisions";
  public static final String REL_FIND_BY_NAME_AND_USERID = "findByTestNameAndUserSsoId";
  public static final String REL_DELETE_BY_TESTNAME = "deleteByTestName";
  public static final String REL_FIND_BY_USERID = "findByUserSsoId";

  private String userSsoId;
  private String testName;
  private int variantId;

}
