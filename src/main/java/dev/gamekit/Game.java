package dev.gamekit;

/**
 * GameKit abstract launcher class which runs a game at 60 frames per second.
 * A game or application must extend this class and call the {@code run()} method.
 */
public abstract class Game {
  private static final long FRAME_TIME = 1000 / 60;

  protected boolean isRunning = true;
  private long lastFrameTime = System.currentTimeMillis();
  private long frameLag = 0;

  public Game() { }

  public void run() throws InterruptedException {
    start();

    while (isRunning) {
      long frameTimeNow = System.currentTimeMillis();
      long elapsedTime = frameTimeNow - lastFrameTime;
      lastFrameTime = frameTimeNow;
      frameLag += elapsedTime;

      while (frameLag >= FRAME_TIME) {
        frameLag -= FRAME_TIME;
        update();
      }

      render();

      // Sleep for at least 5ms to give control to other programs
      //noinspection BusyWait
      Thread.sleep(Math.max(frameLag, 5));
      Time.timeSinceLoad += elapsedTime;
      frameLag = 0;
    }

    cleanup();
  }

  protected void start() { }

  protected void update() { }

  protected void render() { }

  protected void cleanup() { }
}
