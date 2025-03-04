package dev.gamekit;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * GameKit's abstract launcher class which runs a game at 60 frames per second.
 * A game or application must extend this class and call the {@code run()} method.
 */
public abstract class Game {
  private static Game instance;
  private static final long FRAME_TIME = 1000 / 60;

  protected boolean isRunning = true;
  protected Window window;

  private long lastFrameTime = System.currentTimeMillis();
  private long frameLag = 0;
  private final Config config;

  public Game(Config config) {
    this.config = config;
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
    window = new Window(config.title, config.screenWidth, config.screenHeight);

    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        cleanup();
      }
    });

    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }

  protected void update() { }

  protected void render() {
    window.clearScreen(Color.BLACK);
    window.refresh();
  }

  protected void cleanup() {
    System.out.println("Exiting");
  }
}
