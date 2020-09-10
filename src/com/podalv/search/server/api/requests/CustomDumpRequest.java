package com.podalv.search.server.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomDumpRequest {

  public static String                            DEFAULT_COLUMN_SEPARATOR = "\t";
  public static String                            DEFAULT_ROW_SEPARATOR    = "\n";
  public static String                            DEFAULT_QUOTE            = "";
  @JsonProperty("patientIds") private long[]      patientIds;
  @JsonProperty("header") private String[]        header;
  @JsonProperty("columns") private String[]       columns;
  @JsonProperty("columnSeparator") private String columnSeparator;
  @JsonProperty("rowSeparator") private String    rowSeparator;
  @JsonProperty("quote") private String           quote;
  @JsonProperty("compress") private boolean       compress;

  public String[] getColumns() {
    return columns;
  }

  public long[] getPatientIds() {
    return patientIds;
  }

  public boolean isCompress() {
    return compress;
  }

  public void setCompress(boolean compress) {
    this.compress = compress;
  }

  public String getColumnSeparator() {
    return columnSeparator == null ? DEFAULT_COLUMN_SEPARATOR : columnSeparator;
  }

  public String getRowSeparator() {
    return rowSeparator == null ? DEFAULT_ROW_SEPARATOR : rowSeparator;
  }

  public String getQuote() {
    return quote == null ? DEFAULT_QUOTE : quote;
  }

  public String[] getHeader() {
    return header;
  }

  public void setColumns(final String[] columns) {
    this.columns = columns;
  }

  public void setPatientIds(final long[] patientIds) {
    this.patientIds = patientIds;
  }

  public void setColumnSeparator(final String columnSeparator) {
    this.columnSeparator = columnSeparator;
  }

  public void setRowSeparator(final String rowSeparator) {
    this.rowSeparator = rowSeparator;
  }

  public void setHeader(final String[] header) {
    this.header = header;
  }

  public void setQuote(final String quote) {
    this.quote = quote;
  }
}
