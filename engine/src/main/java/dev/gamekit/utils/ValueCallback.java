package dev.gamekit.utils;

/** Functional interface for a single-argument void returning method */
@FunctionalInterface
public interface ValueCallback<T> {
  /** Called to notify a receiver of a new value */
  void invoke(T value);
}
