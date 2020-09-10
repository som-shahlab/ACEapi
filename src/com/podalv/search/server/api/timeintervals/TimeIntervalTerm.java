package com.podalv.search.server.api.timeintervals;

public class TimeIntervalTerm extends TimeInterval {

  private final String noteType;
  private final int    nodeId;

  public TimeIntervalTerm(final double start, final double end, final int noteId, final String noteType) {
    super(start, end);
    this.nodeId = noteId;
    this.noteType = noteType;
  }

  public int getNodeId() {
    return nodeId;
  }

  public String getNoteType() {
    return noteType;
  }

}
