package dev.gamekit.utils;

import dev.gamekit.core.Application;

/** Timeout executes a specified {@link Task} after its duration has elapsed. */
public class Timeout {
  boolean completed;
  long duration;
  Task task;

  /** Creates a Timeout with a specified duration */
  public Timeout(long duration, Task task) {
    if (duration < 0) throw new RuntimeException("Timeout duration cannot be negative");
    if (task == null) throw new RuntimeException("Timeout task cannot be null");
    this.completed = duration == 0;
    this.duration = duration;
    this.task = task;
  }

  /**
   * Returns the completed status
   * @return The completed status of this timeout
   */
  public boolean isCompleted() {
    return completed;
  }

  /**
   * Called internally by the application to update the timeout by decrementing its duration till it reaches 0.
   * When the duration reaches zero, then the task's {@link Task#run() run()} method is executed.
   */
  public void onUpdate() {
    if (!completed) {
      duration = java.lang.Math.max(0, duration - Application.FRAME_TIME);

      if (duration == 0) {
        completed = true;
        task.run();
      }
    }
  }
}
