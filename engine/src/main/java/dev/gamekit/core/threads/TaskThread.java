package dev.gamekit.core.threads;

import dev.gamekit.animation.Animation;
import dev.gamekit.core.Audio;
import dev.gamekit.utils.Timeout;

import java.util.ArrayList;
import java.util.List;

public final class TaskThread extends WorkerThread {
  private final List<Timeout> timeouts;
  private final List<Timeout> newTimeouts;
  private final List<Animation> animations;

  public TaskThread() {
    timeouts = new ArrayList<>();
    newTimeouts = new ArrayList<>();
    animations = new ArrayList<>();
  }

  public void scheduleAnimation(Animation animation) {
    synchronized (animations) {
      if (!animations.contains(animation))
        animations.add(animation);
    }
  }

  public void scheduleTimeout(Timeout timeout) {
    synchronized (newTimeouts) {
      newTimeouts.add(timeout);
    }
  }

  public void clear() {
    synchronized (animations) {
      animations.clear();
    }

    timeouts.clear();
  }

  @Override
  public void performUpdate() {
    synchronized (animations) {
      if (!animations.isEmpty()) {
        for (var anim : animations)
          anim.update();
      }
    }

    if (!timeouts.isEmpty()) {
      for (var timeout : timeouts)
        timeout.update();
    }

    Audio.update();

    synchronized (animations) {
      if (!animations.isEmpty())
        animations.removeIf(Animation::isEnded);
    }

    if (!timeouts.isEmpty())
      timeouts.removeIf(Timeout::isCompleted);

    synchronized (newTimeouts) {
      if (!newTimeouts.isEmpty()) {
        timeouts.addAll(newTimeouts);
        newTimeouts.clear();
      }
    }
  }
}
