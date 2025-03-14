package dev.gamekit.interfaces;

/**
 * Interface for a task which can be scheduled to run
 * at the end of an {@link dev.gamekit.core.Application Application} frame.
 */
public interface FrameEndTask {
  /** Abstract method implementing the task's logic */
  void run();
}
