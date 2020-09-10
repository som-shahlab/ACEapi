package com.podalv.search.server.api.timeintervals;

/** Time interval for an event in days from the date of birth.
 *  Time points will have the same start and end
 *
 * @author podalv
 *
 */
public class TimeInterval implements Comparable<TimeInterval> {

  private final double start;
  private final double end;

  public TimeInterval(final double start, final double end) {
    this.start = start;
    this.end = end;
  }

  public double getStart() {
    return start;
  }

  public double getEnd() {
    return end;
  }

  @Override
  public int compareTo(final TimeInterval o) {
    return start == o.start ? Double.compare(end, o.end) : Double.compare(start, o.start);
  }

  @Override
  public boolean equals(final Object obj) {
    return obj != null && obj instanceof TimeInterval && Double.compare(((TimeInterval) obj).start, start) == 0 && Double.compare(((TimeInterval) obj).end, end) == 0;
  }

  @Override
  public int hashCode() {
    return Double.hashCode(start);
  }
}
