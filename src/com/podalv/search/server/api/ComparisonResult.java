package com.podalv.search.server.api;

public class ComparisonResult<S> {

  private S missingLeft;
  private S missingRight;
  private S differentRight;
  private S differentLeft;

  public void setMissingLeft(final S missingLeft) {
    this.missingLeft = missingLeft;
  }

  public void setMissingRight(final S missingRight) {
    this.missingRight = missingRight;
  }

  public void setDifferentLeft(final S differentLeft) {
    this.differentLeft = differentLeft;
  }

  public void setDifferentRight(final S differentRight) {
    this.differentRight = differentRight;
  }

  S missingLeft() {
    return missingLeft;
  }

  S missingRight() {
    return missingRight;
  }

  S differentLeft() {
    return differentLeft;
  }

  S differentRight() {
    return differentRight;
  }

}
