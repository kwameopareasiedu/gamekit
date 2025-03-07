package dev.gamekit;

import dev.gamekit.interfaces.FrameEndTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * GameKit's abstract launcher class which runs a game at 60 frames per second.
 * A game or application must extend this class and call the {@code run()} method.
 */
@SuppressWarnings("BusyWait")
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

  private final List<FrameEndTask> frameEndTasks;

  public Application(Config config) {
    LOGGER.debug("Created application");
    LOGGER.debug(config);

    this.config = config;
    frameEndTasks = new ArrayList<>();
    Application.instance = this;
  }

  public static Application getInstance() {
    return instance;
  }

  public void loadScene(Scene scene) {
    if (scene == null) {
      LOGGER.fatal("Load scene called with a null scene");
      throw new NullPointerException("Unable to load a null scene");
    }

    LOGGER.debug("Queued scene: {}", scene.name);
    this.nextScene = scene;
  }

  public void runOnFrameEnd(FrameEndTask task) {
    frameEndTasks.add(task);
  }

  public void quit() {
    window.dispatchEvent(
      new WindowEvent(window, WindowEvent.WINDOW_CLOSING)
    );
  }

  public void run() throws InterruptedException {
    onSetup();

    while (isRunning) {
      long frameTimeNow = System.currentTimeMillis();
      long elapsedTime = frameTimeNow - lastFrameTime;
      lastFrameTime = frameTimeNow;
      frameLag += elapsedTime;

      while (frameLag >= Time.FRAME_TIME) {
        frameLag -= Time.FRAME_TIME;
        onUpdate();
      }

      Input.reset();
      onRender();
      onFrameEnd();

      Time.timeSinceLoad += elapsedTime;
      Thread.sleep(Math.max(frameLag, 1));
    }

    onDispose();
  }

  private void onSetup() {
    LOGGER.debug("Initializing application");

    window = new Window(config.title, config.screenWidth, config.screenHeight);

    window.addKeyListener(Input.INSTANCE);
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        LOGGER.debug("Received window closing event");
        onDispose();
      }
    });

    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }

  private void onUpdate() {
    if (activeScene != null) {
      activeScene.onUpdate();
    }
  }

  private void onRender() {
    if (activeScene != null) {
      window.screenGraphics.setTransform(activeScene.camera.transform);
      activeScene.onRender(window.screenGraphics);
    }

    window.refresh();
  }

  private void onFrameEnd() {
    if (!frameEndTasks.isEmpty()) {
      for (var action : frameEndTasks) {
        action.run();
      }

      frameEndTasks.clear();
    }

    if (nextScene != null) {
      if (activeScene != null) {
        activeScene.onDispose();
        LOGGER.debug("Switching scene: {} -> {}", activeScene.name, nextScene.name);
      } else {
        LOGGER.debug("Loading scene: {}", nextScene.name);
      }

      activeScene = nextScene;
      activeScene.onStart();
      nextScene = null;

      Scene.active = activeScene;
    }
  }

  protected void onDispose() {
    LOGGER.debug("Disposing application");

    if (activeScene != null) {
      activeScene.onDispose();
    }

    System.exit(0);
  }
}
