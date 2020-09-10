package com.podalv.search.server.api.datastructures;

import java.util.LinkedList;

import com.podalv.search.server.api.timeintervals.TimeInterval;

/** Contains patient id and optionally a list of time intervals for which the query was true
 *
 * @author podalv
 *
 */
public class PatientId {

  private final long                     patientId;
  private final LinkedList<TimeInterval> startEndIntervals = new LinkedList<>();

  public PatientId(final long patientId) {
    this.patientId = patientId;
  }

  public void addStartEndInterval(final TimeInterval ti) {
    startEndIntervals.add(ti);
  }

  public long getPatientId() {
    return patientId;
  }

  public LinkedList<TimeInterval> getStartEndIntervals() {
    return startEndIntervals;
  }

  @Override
  public boolean equals(final Object arg0) {
    return (arg0 != null && arg0 instanceof PatientId && ((PatientId) arg0).patientId == patientId);
  }

  @Override
  public int hashCode() {
    return Long.hashCode(patientId);
  }

}
