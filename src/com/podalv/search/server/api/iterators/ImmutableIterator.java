package com.podalv.search.server.api.iterators;

import java.util.Iterator;

public class ImmutableIterator<S> implements java.util.Iterator<S> {

  private final Iterator<S> iterator;

  public ImmutableIterator(final Iterator<S> iterator) {
    this.iterator = iterator;
  }

  @Override
  public boolean hasNext() {
    return iterator != null ? iterator.hasNext() : false;
  }

  @Override
  public S next() {
    return iterator != null ? iterator.next() : null;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
