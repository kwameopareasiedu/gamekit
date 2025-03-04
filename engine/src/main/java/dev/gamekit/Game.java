package dev.gamekit;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * GameKit abstract launcher class which runs a game at 60 frames per second.
 * A game or application must extend this class and call the {@code run()} method.
 */
public abstract class Game {
  private static Game instance;
  private static final long FRAME_TIME = 1000 / 60;

  protected boolean isRunning = true;
  protected final Window window;

  private long lastFrameTime = System.currentTimeMillis();
  private long frameLag = 0;

  public Game(String title) {
    window = new Window(title, 1280, 720);

    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        cleanup();
      }
    });

    Game.instance = this;
  }

  public static Game getInstance() {
    return instance;
  }

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

      // noinspection BusyWait
      Thread.sleep(Math.max(frameLag, 5));
      Time.timeSinceLoad += elapsedTime;
      frameLag = 0;
    }

    cleanup();
  }

  protected void start() {
    window.setVisible(true);
  }

  protected void update() { }

  protected void render() {
    window.refresh();
  }

  protected void cleanup() { }
}
