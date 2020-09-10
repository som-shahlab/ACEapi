package com.podalv.search.server.api.exceptions;

@SuppressWarnings("serial")
public class QueryException extends Exception {

  private final String message;

  public QueryException(final String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return message;
  }
}
