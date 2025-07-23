package dev.gamekit.utils;

import dev.gamekit.core.Constants;

/** Timeout executes a specified {@link Task} after its duration has elapsed. */
public class Timeout {
  boolean completed;
  long durationMs;
  Task task;

  public Timeout(long durationMs, Task task) {
    if (durationMs < 0)
      throw new RuntimeException("Timeout duration cannot be negative");
    if (task == null)
      throw new RuntimeException("Timeout task cannot be null");
    this.durationMs = durationMs;
    this.completed = false;
    this.task = task;
  }

  public boolean isCompleted() { return completed; }

  /**
   * Called internally by the application to update the timeout by decrementing its duration till
   * it reaches 0. When the duration reaches zero, then the task's {@link Task#run()} method is
   * executed.
   */
  public void update() {
    if (!completed) {
      durationMs = java.lang.Math.max(0, durationMs - Constants.FRAME_TIME_MS);

      if (durationMs == 0) {
        completed = true;
        task.run();
      }
    }
  }
}
