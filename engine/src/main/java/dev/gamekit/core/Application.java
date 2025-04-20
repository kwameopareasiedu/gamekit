package dev.gamekit.core;

import dev.gamekit.animation.Animation;
import dev.gamekit.utils.Config;
import dev.gamekit.utils.Resolution;
import dev.gamekit.utils.Task;
import dev.gamekit.utils.Timeout;
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
  private static Application instance;

  protected final Logger logger = LogManager.getLogger(getClass());

  private final List<Timeout> timeouts;
  private final List<Animation> animations;
  private final Window window;
  private boolean isRunning;
  private Scene currentScene;
  private Scene nextScene;

  public Application(String title) {
    this(new Config(title, Resolution.SVGA, false));
  }

  public Application(Config config) {
    logger.debug("Created application \"{}\"", config.title());
    logger.debug("Configuration: {}", config);

    this.window = new Window(config);
    this.timeouts = new ArrayList<>();
    this.animations = new ArrayList<>();
    this.isRunning = true;

    Application.instance = this;
  }

  public static Application getInstance() { return instance; }

  /** Schedules a scene to be loaded after the end of the current frame */
  public void loadScene(Scene scene) {
    if (scene == null) {
      logger.fatal("Load scene called with a null scene");
      throw new NullPointerException("Unable to load a null scene");
    }

    logger.debug("Queued scene: {}", scene.getName());
    this.nextScene = scene;
  }

  /**
   * Schedule a task to be executed immediately after the end of the current
   * frame.
   */
  public void scheduleTask(Task task) { scheduleTask(task, 0); }

  /**
   * Schedule a task to be executed after some timeout in <b>milliseconds</b>.
   * <p>
   * If {@code timeout} is zero, {@code task} is executed immediately after the
   * current frame
   */
  public void scheduleTask(Task task, long timeout) {
    if (timeout < 0)
      throw new RuntimeException("timeout cannot be negative");
    timeouts.add(new Timeout(timeout, task));
  }

  /**
   * Schedule an {@link Animation} to run. Animations are updated before the
   * scene's {@code onUpdate()} to ensure current values are available to the
   * scene.
   */
  public void scheduleAnimation(Animation animation) {
    if (!animations.contains(animation)) {
      animations.add(animation);
    }
  }

  /**
   * Quit the current {@link Application} by dispatching a
   * {@link WindowEvent#WINDOW_CLOSING} event to the {@link Window}
   * {@link javax.swing.JFrame frame}
   */
  public void quit() {
    window.getFrame().dispatchEvent(
      new WindowEvent(window.getFrame(), WindowEvent.WINDOW_CLOSING)
    );
  }

  /** Begins the game loop of this application */
  public void run() {
    try {
      setup();

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
          update();
          Input.reset();
        }

        render();
        endFrame();
        Thread.sleep(Math.max(frameTimeAccumulator, 5));
      }

      dispose();
      System.exit(0);
    } catch (Exception e) {
      logger.error("Application loop raised an exception", e);
      System.exit(-1);
    }
  }

  /** Sets up GameKit's internals before starting the game loop */
  private void setup() {
    logger.debug("Initializing application");

    window.getFrame().addKeyListener(Input.INSTANCE);
    window.getFrame().addMouseListener(Input.INSTANCE);
    window.getFrame().addMouseMotionListener(Input.INSTANCE);

    window.getFrame().addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        logger.debug("Received window closing event");
        isRunning = false;
      }
    });

    window.getFrame().setVisible(true);
  }

  /**
   * Called in each frame to update the current scene all running animations and
   * timeouts have been updated
   */
  private void update() {
    if (!animations.isEmpty()) {
      for (var anim : animations)
        anim.update();
    }

    if (!timeouts.isEmpty()) {
      for (var timeout : timeouts)
        timeout.onUpdate();
    }

    if (currentScene != null) {
      currentScene.updateInternal();
    }

    Audio.update();
  }

  /**
   * Applies the camera's transformation on the {@link Window} scene buffer and
   * renders the current scene
   */
  private void render() {
    if (currentScene != null) {
      Camera.update();
      currentScene.renderInternal();
    }

    window.redraw();
  }

  /**
   * Runs end-of-frame tasks which include:
   * <ul>
   *   <li>Removing ended animations</li>
   *   <li>Removing completed timeouts</li>
   *   <li>Loading a scheduled scene</li>
   * </ul>
   */
  private void endFrame() {
    if (!animations.isEmpty()) {
      animations.removeIf(animation ->
        animation.getState() == Animation.State.ENDED
      );
    }

    if (!timeouts.isEmpty()) {
      timeouts.removeIf(Timeout::isCompleted);
    }

    if (nextScene != null) {
      animations.clear();
      timeouts.clear();

      if (currentScene != null) {
        currentScene.disposeInternal();
        logger.debug("Switching scene: {} -> {}", currentScene.getName(), nextScene.getName());
      } else {
        logger.debug("Loading scene: {}", nextScene.getName());
      }

      currentScene = nextScene;
      currentScene.startInternal();
      nextScene = null;

      window.createRenderBuffers();
      Scene.current = currentScene;
    }
  }

  /** Runs cleanup code before exiting the application */
  protected void dispose() {
    logger.debug("Disposing application");

    if (currentScene != null) {
      currentScene.disposeInternal();
    }

    IO.dispose();
  }
}
