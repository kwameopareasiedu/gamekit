package dev.gamekit.utils;

import dev.gamekit.core.Application;
import dev.gamekit.interfaces.Task;

/** Interface for a task which is scheduled to run after some time has passed. */
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

  /** Updates the timeout by decrementing its duration till it reaches 0, then the task is run */
  public void update() {
    if (!completed) {
      duration = Math.max(0, duration - Application.FRAME_TIME);

      if (duration == 0) {
        completed = true;
        task.run();
      }
    }
  }
}
