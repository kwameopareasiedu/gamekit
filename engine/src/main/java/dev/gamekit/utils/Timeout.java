package dev.gamekit.utils;

import dev.gamekit.core.Application;

/** Timeout executes a specified {@link VoidCallback} after its duration has elapsed. */
public class Timeout {
  boolean completed;
  long durationMs;
  VoidCallback callback;

  public Timeout(long durationMs, VoidCallback callback) {
    if (durationMs < 0) throw new RuntimeException("Timeout duration cannot be negative");
    if (callback == null) throw new RuntimeException("Timeout task cannot be null");
    this.durationMs = durationMs;
    this.completed = false;
    this.callback = callback;
  }

  /** Returns the completed status */
  public boolean isCompleted() {
    return completed;
  }

  /** Cancels the timeout and prevents the {@link #callback} from being executed */
  public void cancel() {
    completed = true;
  }

  /**
   * Called internally by the application to update the timeout by decrementing its duration till it reaches 0.
   * When the duration reaches zero, then the task's {@link VoidCallback#run} method is executed.
   */
  public void update() {
    if (completed) return;

    durationMs = java.lang.Math.max(0, durationMs - Application.FRAME_INTERVAL_MS);

    if (durationMs == 0) {
      completed = true;
      callback.run();
    }
  }
}
