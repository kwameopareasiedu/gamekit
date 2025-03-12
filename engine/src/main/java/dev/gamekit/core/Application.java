package dev.gamekit.core;

import dev.gamekit.interfaces.FrameEndTask;
import dev.gamekit.scene.Scene;
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
  public static final long FRAME_TIME = 1000 / 60;
  private static final Logger LOGGER = LogManager.getLogger();
  private static Application instance;

  private final String title;
  private final int screenWidth;
  private final int screenHeight;
  private final List<FrameEndTask> frameEndTasks;

  private Window window;
  private Renderer renderer;
  private boolean isRunning;
  private Scene activeScene;
  private Scene nextScene;

  public Application(String title, int screenWidth, int screenHeight) {
    LOGGER.debug("Created application [{} @ {}x{}]", title, screenWidth, screenHeight);

    this.title = title;
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    frameEndTasks = new ArrayList<>();
    isRunning = true;

    Application.instance = this;
  }

  public static Application getInstance() { return instance; }

  public int getScreenWidth() { return screenWidth; }

  public int getScreenHeight() { return screenHeight; }

  public void loadScene(Scene scene) {
    if (scene == null) {
      LOGGER.fatal("Load scene called with a null scene");
      throw new NullPointerException("Unable to load a null scene");
    }

    LOGGER.debug("Queued scene: {}", scene.getName());
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

  public void run() {
    try {
      onSetup();

      long lastFrameTime = System.currentTimeMillis();
      long frameTimeAccumulator = 0;

      while (isRunning) {
        long frameTimeNow = System.currentTimeMillis();
        long elapsedTime = frameTimeNow - lastFrameTime;
        lastFrameTime = frameTimeNow;
        frameTimeAccumulator += elapsedTime;

        while (frameTimeAccumulator >= FRAME_TIME) {
          Input.freeze();
          frameTimeAccumulator -= FRAME_TIME;
          onUpdate();
          Input.reset();
        }

        onRender();
        onFrameEnd();
        Thread.sleep(Math.max(frameTimeAccumulator, 1));
      }

      onDispose();
      System.exit(0);
    } catch (Exception e) {
      LOGGER.error("Application loop raised an exception", e);
      System.exit(-1);
    }
  }

  private void onSetup() {
    LOGGER.debug("Initializing application");

    window = new Window(title, screenWidth, screenHeight);
    renderer = new Renderer(window.screenGraphics);

    window.addKeyListener(Input.INSTANCE);
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        LOGGER.debug("Received window closing event");
        isRunning = false;
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
      window.screenGraphics.setTransform(activeScene.getCamera().getTransform());
      activeScene.onRender();
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
        LOGGER.debug("Switching scene: {} -> {}", activeScene.getName(), nextScene.getName());
      } else {
        LOGGER.debug("Loading scene: {}", nextScene.getName());
      }

      activeScene = nextScene;
      activeScene.onStart();
      nextScene = null;

      renderer.restoreInitialState();
      Scene.setActive(activeScene);
    }
  }

  protected void onDispose() {
    LOGGER.debug("Disposing application");

    if (activeScene != null) {
      activeScene.onDispose();
    }
  }
}
