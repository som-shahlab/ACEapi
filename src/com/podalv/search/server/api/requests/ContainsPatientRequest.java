package com.podalv.search.server.api.requests;

public class ContainsPatientRequest {

  private long patientId;

  public ContainsPatientRequest(final long pid) {
    patientId = pid;
  }

  public long getPatientId() {
    return patientId;
  }

  public void setPatientId(final long patientId) {
    this.patientId = patientId;
  }

}
