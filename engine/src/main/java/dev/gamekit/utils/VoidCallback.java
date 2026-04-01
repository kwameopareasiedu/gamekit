package dev.gamekit.utils;

/** Functional interface for a no-argument void returning method */
@FunctionalInterface
public interface VoidCallback {
  /** Called to notify a receiver */
  void invoke();
}
