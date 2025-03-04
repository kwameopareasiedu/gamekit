package dev.gamekit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * GameKit's abstract launcher class which runs a game at 60 frames per second.
 * A game or application must extend this class and call the {@code run()} method.
 */
public abstract class Application {
  private static final Logger LOGGER = LogManager.getLogger();
  private static Application instance;

  protected boolean isRunning = true;
  protected Window window;

  private long lastFrameTime = System.currentTimeMillis();
  private long frameLag = 0;
  private final Config config;
  private Scene activeScene;
  private Scene nextScene;

  public Application(Config config) {
    LOGGER.debug("Created GameKit application");
    LOGGER.debug(config);

    this.config = config;
    Application.instance = this;
  }

  public static Application getInstance() {
    return instance;
  }

  public void loadScene(Scene scene) {
    if (scene == null) {
      LOGGER.warn("Load scene called with a null scene");
      return;
    }

    LOGGER.debug("Queued scene load: {}", scene.name);
    this.nextScene = scene;
  }

  public void quit() {
    window.dispatchEvent(
      new WindowEvent(window, WindowEvent.WINDOW_CLOSING)
    );
  }

  public void run() throws InterruptedException {
    start();

    while (isRunning) {
      long frameTimeNow = System.currentTimeMillis();
      long elapsedTime = frameTimeNow - lastFrameTime;
      lastFrameTime = frameTimeNow;
      frameLag += elapsedTime;

      Input.reset();

      while (frameLag >= Time.FRAME_TIME) {
        frameLag -= Time.FRAME_TIME;
        update();
      }

      render();

      if (nextScene != null) {
        loadNextScene();
      }

      // noinspection BusyWait
      Thread.sleep(Math.max(frameLag, 1));
      Time.timeSinceLoad += elapsedTime;
    }

    dispose();
  }

  private void start() {
    LOGGER.debug("Started GameKit application");

    window = new Window(config.title, config.screenWidth, config.screenHeight);

    window.addKeyListener(Input.INSTANCE);
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        LOGGER.debug("Received window closing event");
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
    if (activeScene != null) {
      activeScene.render(window.screenGraphics);
    }

    window.refresh();
  }

  protected void dispose() {
    LOGGER.debug("Disposing GameKit application");
  }

  private void loadNextScene() {
    LOGGER.debug("Switching scenes");

    if (activeScene != null) {
      activeScene.dispose();
    }

    nextScene.start();
    activeScene = nextScene;
    nextScene = null;
  }
}
