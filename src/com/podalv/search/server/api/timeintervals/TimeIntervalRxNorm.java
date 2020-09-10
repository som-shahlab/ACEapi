package com.podalv.search.server.api.timeintervals;

public class TimeIntervalRxNorm extends TimeInterval {

  private final String drugStatus;
  private final String drugRoute;

  public TimeIntervalRxNorm(final double start, final double end, final String drugRoute, final String drugStatus) {
    super(start, end);
    this.drugRoute = drugRoute;
    this.drugStatus = drugStatus;
  }

  public String getDrugRoute() {
    return drugRoute;
  }

  public String getDrugStatus() {
    return drugStatus;
  }

}
