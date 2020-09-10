package com.podalv.search.server.api.timeintervals;

public class TimeIntervalIcd9 extends TimeInterval {

  private final boolean primary;

  public static String  ICD9_PRIMARY_STRING = "PRIMARY";
  public static String  ICD9_OHER_STRING    = "OTHER";

  public TimeIntervalIcd9(final double start, final double end, final String data) {
    super(start, end);
    primary = (data != null && data.equals(ICD9_PRIMARY_STRING)) ? true : false;
  }

  /** Returns an indicator whether the icd9 code was flagged as primary during the visit
   *
   * @return true is the code was flagged as primary in the visit
   */
  public boolean isPrimary() {
    return primary;
  }

}
