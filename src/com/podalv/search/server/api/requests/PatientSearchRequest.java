package com.podalv.search.server.api.requests;

import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

public class PatientSearchRequest {

  @JsonProperty("query") String                        query;
  @JsonProperty("returnPids") private boolean          returnPids          = true;
  @JsonProperty("returnTimeIntervals") private boolean returnTimeIntervals = false;
  @JsonProperty("returnYears") private boolean         returnYears         = false;
  @JsonProperty("returnSurvivalData") private boolean  returnSurvivalData  = false;
  @JsonProperty("pidCntLimit") private int             pidCntLimit         = Integer.MAX_VALUE;
  @JsonProperty("checkStatus") private boolean         checkStatus         = false;
  @JsonProperty("statisticsLimit") private int         statisticsLimit     = Integer.MAX_VALUE;
  @JsonProperty("encounterBuckets") private int[]      encounterBuckets;
  @JsonProperty("durationBuckets") private int[]       durationBuckets;
  @JsonProperty("searchablePids") private long[]       searchablePids;
  @JsonProperty("compress") private boolean            compress            = false;

  public static PatientSearchRequest create(final String query) {
    final PatientSearchRequest result = new PatientSearchRequest();
    result.setQuery(query);
    return result;
  }

  public PatientSearchRequest setEncounterBuckets(final int[] encounterBuckets) {
    this.encounterBuckets = encounterBuckets;
    return this;
  }

  public void setCheckStatus(final boolean checkStatus) {
    this.checkStatus = checkStatus;
  }

  public boolean isCheckStatus() {
    return checkStatus;
  }

  public PatientSearchRequest setDurationBuckets(final int[] durationBuckets) {
    this.durationBuckets = durationBuckets;
    return this;
  }

  public boolean isReturnYears() {
    return returnYears;
  }

  public PatientSearchRequest setReturnYears(final boolean value) {
    returnYears = value;
    return this;
  }

  public void setCompress(boolean value) {
    this.compress = value;
  }

  public boolean isCompress() {
    return this.compress;
  }

  public int[] getDurationBuckets() {
    return durationBuckets;
  }

  public int[] getEncounterBuckets() {
    return encounterBuckets;
  }

  public void setStatisticsLimit(final int statisticsLimit) {
    this.statisticsLimit = statisticsLimit;
  }

  public boolean isReturnSurvivalData() {
    return returnSurvivalData;
  }

  public int getStatisticsLimit() {
    return statisticsLimit;
  }

  public void setReturnSurvivalData(final boolean returnSurvivalData) {
    this.returnSurvivalData = returnSurvivalData;
  }

  public void setPidCntLimit(final int pidCntLimit) {
    this.pidCntLimit = pidCntLimit;
  }

  public int getPidCntLimit() {
    return pidCntLimit;
  }

  public void setSearchablePids(final long[] searchablePids) {
    this.searchablePids = searchablePids;
  }

  public long[] getSearchablePids() {
    return searchablePids;
  }

  public String getQuery() {
    return query;
  }

  public PatientSearchRequest setReturnTimeIntervals(final boolean returnTimeIntervals) {
    this.returnTimeIntervals = returnTimeIntervals;
    return this;
  }

  public void setReturnPids(final boolean returnPids) {
    this.returnPids = returnPids;
  }

  public boolean isReturnPids() {
    return returnPids;
  }

  public boolean isReturnTimeIntervals() {
    return returnTimeIntervals;
  }

  public boolean isPidRequest() {
    return false;
  }

  public void setQuery(final String query) {
    this.query = query;
  }

  public static PatientSearchRequest copy(final PatientSearchRequest originalRequest, final String newQueryText) {
    final PatientSearchRequest response = new PatientSearchRequest();
    response.query = newQueryText;
    response.returnPids = originalRequest.returnPids;
    response.returnTimeIntervals = originalRequest.returnTimeIntervals;
    response.returnSurvivalData = originalRequest.returnSurvivalData;
    response.statisticsLimit = originalRequest.statisticsLimit;
    response.pidCntLimit = originalRequest.pidCntLimit;
    response.returnYears = originalRequest.returnYears;
    if (originalRequest.durationBuckets != null) {
      response.durationBuckets = Arrays.copyOf(originalRequest.durationBuckets, originalRequest.durationBuckets.length);
    }
    if (originalRequest.encounterBuckets != null) {
      response.encounterBuckets = Arrays.copyOf(originalRequest.encounterBuckets, originalRequest.encounterBuckets.length);
    }
    if (originalRequest.searchablePids != null) {
      response.searchablePids = Arrays.copyOf(originalRequest.searchablePids, originalRequest.searchablePids.length);
    }
    return response;
  }

  @Override
  public int hashCode() {
    return query.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj != null && obj instanceof PatientSearchRequest) {
      final PatientSearchRequest req = (PatientSearchRequest) obj;
      return Objects.equals(query, req.query) && req.returnYears == returnYears && req.returnPids == returnPids && req.returnTimeIntervals == returnTimeIntervals
          && req.pidCntLimit == pidCntLimit;
    }
    return false;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

}