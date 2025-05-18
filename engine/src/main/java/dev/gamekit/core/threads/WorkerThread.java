package dev.gamekit.core.threads;

import dev.gamekit.core.Application;

@SuppressWarnings("BusyWait")
abstract class WorkerThread extends Thread {
  @Override
  public final void run() {
    super.run();

    long lastFrameTime = System.currentTimeMillis();
    long frameTimeAccumulator = 0;

    while (!isInterrupted()) {
      long currentFrameTime = System.currentTimeMillis();
      long timeDiff = currentFrameTime - lastFrameTime;
      lastFrameTime = currentFrameTime;
      frameTimeAccumulator += timeDiff;

      while (frameTimeAccumulator >= Application.FRAME_TIME_MS) {
        frameTimeAccumulator -= Application.FRAME_TIME_MS;
        performUpdate();
      }

      try { Thread.sleep(1); } catch (InterruptedException ignored) { }
    }
  }

  abstract void performUpdate();
}
