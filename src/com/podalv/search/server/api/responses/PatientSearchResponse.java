package com.podalv.search.server.api.responses;

import java.util.LinkedList;

/** Encapsulates the search result
 *
 * @author podalv
 *
 */
public class PatientSearchResponse {

  private final LinkedList<double[]> patientIds        = new LinkedList<double[]>();
  private String                     originalUnparsedQuery;
  private long                       timeTook;
  private String                     parsedQuery;
  private String                     exportLocation;
  private int                        cohortPatientCnt  = 0;
  private int                        generalPatientCnt = 0;
  private int                        processedPatients = 0;
  String                             errorMessage      = null;
  private final String[]             warningMessage    = null;

  public void setOriginalUnparsedQuery(final String originalUnparsedQuery) {
    this.originalUnparsedQuery = originalUnparsedQuery;
  }

  public int getProcessedPatients() {
    return processedPatients;
  }

  public String getOriginalUnparsedQuery() {
    return originalUnparsedQuery;
  }

  public static PatientSearchResponse createError(final String errorMessage) {
    final PatientSearchResponse response = new PatientSearchResponse();
    response.setError(errorMessage);
    return response;
  }

  public String[] getWarningMessage() {
    return warningMessage;
  }

  public void setExportLocation(final String exportLocation) {
    this.exportLocation = exportLocation;
  }

  public String getExportLocation() {
    return exportLocation;
  }

  public void setParsedQuery(final String parsedQuery) {
    this.parsedQuery = parsedQuery;
  }

  public String getParsedQuery() {
    return parsedQuery;
  }

  public void setError(final String error) {
    this.errorMessage = error;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public int getCohortPatientCnt() {
    return cohortPatientCnt;
  }

  public void setCohortPatientCnt(final int cohortPatientCnt) {
    this.cohortPatientCnt = cohortPatientCnt;
  }

  public void setGeneralPatientCnt(final int generalPatientCnt) {
    this.generalPatientCnt = generalPatientCnt;
  }

  public int getGeneralPatientCnt() {
    return generalPatientCnt;
  }

  public void setTimeTook(final long time) {
    this.timeTook = time;
  }

  public void setProcessedPatients(final int patientCnt) {
    this.processedPatients = patientCnt;
  }

  public LinkedList<double[]> getPatientIds() {
    return patientIds;
  }

  public long getTimeTook() {
    return timeTook;
  }

  public boolean containsErrors() {
    return errorMessage != null && !errorMessage.isEmpty();
  }

}