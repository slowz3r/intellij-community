package org.jetbrains.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Obsolescent;

public abstract class ObsolescentConsumer<T> implements Obsolescent.Consumer<T> {
  private final Obsolescent obsolescent;

  protected ObsolescentConsumer(@NotNull Obsolescent obsolescent) {
    this.obsolescent = obsolescent;
  }

  @Override
  public final boolean isObsolete() {
    return obsolescent.isObsolete();
  }
}