package dev.gamekit;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * GameKit's abstract launcher class which runs a game at 60 frames per second.
 * A game or application must extend this class and call the {@code run()} method.
 */
public abstract class Application {
  private static Application instance;
  private static final long FRAME_TIME = 1000 / 60;

  protected boolean isRunning = true;
  protected Window window;

  private long lastFrameTime = System.currentTimeMillis();
  private long frameLag = 0;
  private final Config config;
  private Scene activeScene;
  private Scene nextScene;

  public Application(Config config) {
    this.config = config;
    Application.instance = this;
  }

  public static Application getInstance() {
    return instance;
  }

  public void loadScene(Scene scene) {
    if (scene == null) return;
    this.nextScene = scene;
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

      if (nextScene != null) {
        loadNextScene();
      }

      // noinspection BusyWait
      Thread.sleep(Math.max(frameLag, 5));
      Time.timeSinceLoad += elapsedTime;
      Input.reset();
      frameLag = 0;
    }

    dispose();
  }

  private void start() {
    window = new Window(config.title, config.screenWidth, config.screenHeight);

    window.addKeyListener(Input.instance);
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        dispose();
      }
    });

    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }

  private void update() {
    if (activeScene != null) {
      activeScene.update();
    }
  }

  private void render() {
    window.clearScreen();

    if (activeScene != null) {
      activeScene.render(window.screenGraphics);
    }

    window.refresh();
  }

  protected void dispose() {
    System.out.println("Exiting");
  }

  private void loadNextScene() {
    if (activeScene != null) {
      activeScene.dispose();
    }

    nextScene.start();
    activeScene = nextScene;
    nextScene = null;
  }
}
