package com.podalv.search.server.api.timeintervals;

public class TimeIntervalLabs extends TimeInterval {

  private final String computedValue;

  public TimeIntervalLabs(final double start, final double end, final String computedValue) {
    super(start, end);
    this.computedValue = computedValue;
  }

  public String getComputedValue() {
    return computedValue;
  }

}
