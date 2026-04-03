package dev.gamekit.utils;

/** Functional interface for a no-argument value returning method */
@FunctionalInterface
public interface ValueGetter<T> {
  /** Called to return a value to a consumer */
  T get();
}
