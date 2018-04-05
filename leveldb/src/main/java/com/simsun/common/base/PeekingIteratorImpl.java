package com.simsun.common.base;

import android.support.annotation.Nullable;
import java.util.Iterator;

import static com.simsun.common.base.Preconditions.checkNotNull;
import static com.simsun.common.base.Preconditions.checkState;

/**
 * Implementation of PeekingIterator that avoids peeking unless necessary.
 */
public class PeekingIteratorImpl<E> implements PeekingIterator<E> {

  private final Iterator<? extends E> iterator;
  private boolean hasPeeked;
  @Nullable private E peekedElement;

  public PeekingIteratorImpl(Iterator<? extends E> iterator) {
    this.iterator = checkNotNull(iterator);
  }

  @Override
  public boolean hasNext() {
    return hasPeeked || iterator.hasNext();
  }

  @Override
  public E next() {
    if (!hasPeeked) {
      return iterator.next();
    }
    E result = peekedElement;
    hasPeeked = false;
    peekedElement = null;
    return result;
  }

  @Override
  public void remove() {
    checkState(!hasPeeked, "Can't remove after you've peeked at next");
    iterator.remove();
  }

  @Override
  public E peek() {
    if (!hasPeeked) {
      peekedElement = iterator.next();
      hasPeeked = true;
    }
    return peekedElement;
  }
}