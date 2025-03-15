package dev.gamekit.core;

import dev.gamekit.animation.Animation;
import dev.gamekit.interfaces.FrameEndTask;
import dev.gamekit.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Application is the heart of a GameKit program.
 * <p>
 * A game or application must extend this class to do anything with the engine.
 */
@SuppressWarnings("BusyWait")
public abstract class Application {
  public static final long FRAME_TIME = 1000 / 90;
  private static final Logger LOGGER = LogManager.getLogger();
  private static Application instance;

  private final String title;
  private final List<FrameEndTask> frameEndTasks;
  private final List<Animation> animations;

  private Window window;
  private boolean isRunning;
  private Scene activeScene;
  private Scene nextScene;

  /** Creates an application with a title */
  public Application(String title) {
    LOGGER.debug("Created application \"{}\"", title);

    this.title = title;
    frameEndTasks = new ArrayList<>();
    animations = new ArrayList<>();
    isRunning = true;

    Application.instance = this;
  }

  /**
   * Returns the current instance of the application
   * @return The current application instance
   */
  public static Application getInstance() { return instance; }

  /**
   * Queues a scene to be loaded after the end of the current frame
   * @param scene {@link Scene} Scene to load
   */
  public void loadScene(Scene scene) {
    if (scene == null) {
      LOGGER.fatal("Load scene called with a null scene");
      throw new NullPointerException("Unable to load a null scene");
    }

    LOGGER.debug("Queued scene: {}", scene.getName());
    this.nextScene = scene;
  }

  /**
   * Schedule a one-off task to be executed at the end of the current frame
   * @param task {@link FrameEndTask} Task to execute
   */
  public void scheduleFrameEndTask(FrameEndTask task) {
    if (!frameEndTasks.contains(task)) {
      frameEndTasks.add(task);
    }
  }

  /**
   * Adds an animation to the application. Animation are updated before the scene's
   * {@code onUpdate()} to ensure current values are used by the scene.
   * @param animation {@link Animation} The animation to add
   */
  public void addAnimation(Animation animation) {
    if (!animations.contains(animation)) {
      animations.add(animation);
    }
  }

  /** Quit the current {@link Application} by dispatching a {@code WINDOW_CLOSING} event to its window */
  public void quit() {
    window.frame.dispatchEvent(
      new WindowEvent(window.frame, WindowEvent.WINDOW_CLOSING)
    );
  }

  /** Begin the game loop of this application */
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
        Thread.sleep(Math.max(frameTimeAccumulator, 5));
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

    window = new Window(title);

    window.frame.addKeyListener(Input.INSTANCE);
    window.frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        LOGGER.debug("Received window closing event");
        isRunning = false;
      }
    });

    window.frame.setVisible(true);
  }

  private void onUpdate() {
    if (!animations.isEmpty()) {
      for (var action : animations)
        action.update();
    }

    if (activeScene != null) {
      activeScene.onUpdate();
    }
  }

  private void onRender() {
    if (activeScene != null) {
      Camera.getInstance().update();
      activeScene.onRender();
    }

    window.redraw();
  }

  private void onFrameEnd() {
    if (!frameEndTasks.isEmpty()) {
      for (var action : frameEndTasks)
        action.run();

      frameEndTasks.clear();
    }

    if (!animations.isEmpty()) {
      animations.removeIf(animation ->
        animation.getState() == Animation.State.ENDED
      );
    }

    if (nextScene != null) {
      animations.clear();

      if (activeScene != null) {
        activeScene.onDispose();
        LOGGER.debug("Switching scene: {} -> {}", activeScene.getName(), nextScene.getName());
      } else {
        LOGGER.debug("Loading scene: {}", nextScene.getName());
      }

      activeScene = nextScene;
      activeScene.onStart();
      nextScene = null;

      window.createRenderLayers();
      Scene.setActive(activeScene);
    }
  }

  /** Runs cleanup code before exiting the application */
  protected void onDispose() {
    LOGGER.debug("Disposing application");

    if (activeScene != null) {
      activeScene.onDispose();
    }
  }
}
