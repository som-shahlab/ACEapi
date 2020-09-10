package com.podalv.search.server.api.responses;

public class ServerStatusResponse {

  private static final String OK_RESPONSE = "OK";
  private String              status;
  private final String        datasetVersion;
  private final String        version;
  private boolean             workshop;
  private String              ipAddress   = null;

  public static ServerStatusResponse createOkResponse(final String version, final boolean workshop, final String datasetVersion) {
    return new ServerStatusResponse(version, OK_RESPONSE, workshop, datasetVersion);
  }

  public ServerStatusResponse setIpAddress(final String ipAddress) {
    this.ipAddress = ipAddress;
    return this;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public ServerStatusResponse(final String version, final String status, final boolean workshop, final String dataSetVersion) {
    this.version = version;
    this.status = status;
    this.workshop = workshop;
    datasetVersion = dataSetVersion;
  }

  public String getStatus() {
    return status;
  }

  public String getVersion() {
    return version;
  }

  public boolean getWorkshop() {
    return workshop;
  }

  public String getDatasetVersion() {
    return datasetVersion;
  }

  public void setWorkshop(final boolean workshop) {
    this.workshop = workshop;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public boolean isOk() {
    return status.equals(OK_RESPONSE);
  }
}
