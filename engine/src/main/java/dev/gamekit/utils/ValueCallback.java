package dev.gamekit.utils;

/** Interface for an arbitrary task which accepts one argument arguments */
@FunctionalInterface
public interface ValueCallback<T> {
  /** Abstract method implementing the callback's logic */
  void run(T value);
}
