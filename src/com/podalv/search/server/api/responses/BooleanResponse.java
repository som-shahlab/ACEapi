package com.podalv.search.server.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BooleanResponse {

  private Boolean response;
  private String  error;

  public BooleanResponse(final String error) {
    this.error = error;
    response = null;
  }

  public BooleanResponse(final boolean response) {
    this.response = response;
  }

  public void setResponse(final boolean response) {
    this.response = response;
  }

  public boolean getResponse() {
    return response;
  }

  public String getError() {
    return error;
  }

  public void setError(final String error) {
    this.error = error;
  }

  public static class CustomDumpResponse {

    @JsonProperty("error") private String  error;
    @JsonProperty("result") private String result;

    public void setError(final String error) {
      this.error = error;
    }

    public void setResult(final String result) {
      this.result = result;
    }

    public String getError() {
      return error;
    }

    public String getResult() {
      return result;
    }

    public static CustomDumpResponse createError(final String error) {
      final CustomDumpResponse result = new CustomDumpResponse();
      result.setError(error);
      return result;
    }
  }
}
