package dev.gamekit.utils;

/** Interface for an arbitrary task which accepts no arguments */
@FunctionalInterface
public interface VoidCallback {
  /** Abstract method implementing the callback's logic */
  void run();
}
