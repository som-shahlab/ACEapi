package com.podalv.search.server.api.timeintervals;

public class TimeIntervalNumericValue extends TimeInterval {

  private final double value;

  public TimeIntervalNumericValue(final double start, final double end, final double value) {
    super(start, end);
    this.value = value;
  }

  public double getValue() {
    return value;
  }

}
