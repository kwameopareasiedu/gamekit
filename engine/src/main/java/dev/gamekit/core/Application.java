package dev.gamekit.core;

import dev.gamekit.animation.Animation;
import dev.gamekit.settings.Settings;
import dev.gamekit.utils.Task;
import dev.gamekit.utils.Timeout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Application} is the heart of a GameKit program. A game or application must extend this
 * class to do anything with the engine.
 * <p>
 * It runs a fixed-step game update loop which protects against lag spikes. It also loads scenes,
 * schedule animations and timeouts and quit the running instance. Use
 * {@link Application#getInstance()} to get the current instance from anywhere in your project
 * to access its methods.
 * <p>
 */
@SuppressWarnings("BusyWait")
public abstract class Application {
  public static final long FRAME_TIME_MS = 1000 / 120;
  private static Application instance;

  protected final Logger logger = LogManager.getLogger(getClass());

  private final Settings settings;
  private final List<Timeout> timeouts;
  private final List<Timeout> newTimeouts;
  private final List<Animation> animations;
  private final Window window;
  private boolean isRunning;
  private Scene currentScene;
  private Scene nextScene;

  public Application(String title) {
    this(new Settings(title));
  }

  public Application(Settings settings) {
    Application.instance = this;

    logger.debug("Created application \"{}\"", settings);

    this.settings = settings;
    this.window = new Window();
    this.timeouts = new ArrayList<>();
    this.newTimeouts = new ArrayList<>();
    this.animations = new ArrayList<>();
    this.isRunning = true;
  }

  public static Application getInstance() { return instance; }

  public Settings getSettings() { return settings; }

  /** Schedules a scene to be loaded after the end of the current frame */
  public void loadScene(Scene scene) {
    if (scene == null) {
      logger.fatal("Load scene called with a null scene");
      throw new NullPointerException("Unable to load a null scene");
    }

    logger.debug("Queued scene: {}", scene.getName());
    this.nextScene = scene;
  }

  /** Schedule a task to be executed immediately after the end of the current frame. */
  public void scheduleTask(Task task) { scheduleTask(task, 0); }

  /**
   * Schedule a task to be executed after some timeout in <b>milliseconds</b>.
   * <p>
   * If {@code timeout} is zero, {@code task} is executed immediately after the current frame
   */
  public void scheduleTask(Task task, long timeout) {
    if (timeout < 0)
      throw new RuntimeException("timeout cannot be negative");
    newTimeouts.add(new Timeout(timeout, task));
  }

  /**
   * Schedule an {@link Animation} to run. Animations are updated before the scene's
   * {@code onUpdate()} to ensure current values are available to the scene's next update cycle
   */
  public void scheduleAnimation(Animation animation) {
    if (!animations.contains(animation)) {
      animations.add(animation);
    }
  }

  /**
   * Quit the current {@link Application} by dispatching a {@link WindowEvent#WINDOW_CLOSING}
   * event to the {@link Window} {@link javax.swing.JFrame frame}
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

      while (isRunning) {
        long frameTimeNow = System.currentTimeMillis();
        long frameTimeAccumulator = frameTimeNow - lastFrameTime;
        lastFrameTime = frameTimeNow;

        while (frameTimeAccumulator >= FRAME_TIME_MS) {
          Input.freeze();
          frameTimeAccumulator -= FRAME_TIME_MS;
          update();
          Input.reset();
        }

        render();
        endFrame();
        Thread.sleep(Math.max(frameTimeAccumulator, 1));
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
   * Called in each frame to update the current scene all running animations and timeouts have
   * been updated
   */
  private void update() {
    if (!animations.isEmpty()) {
      for (var anim : animations)
        anim.update();
    }

    if (!timeouts.isEmpty()) {
      for (var timeout : timeouts)
        timeout.update();
    }

    if (currentScene != null)
      currentScene._update();

    Audio.update();
  }

  /**
   * Applies the camera's transformation on the {@link Window} scene buffer and renders the
   * current scene
   */
  private void render() {
    if (currentScene != null) {
      Camera.update();
      currentScene._render();
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

    if (!newTimeouts.isEmpty()) {
      timeouts.addAll(newTimeouts);
      newTimeouts.clear();
    }

    if (nextScene != null) {
      animations.clear();
      timeouts.clear();

      if (currentScene != null) {
        currentScene._dispose();
        logger.debug("Switching scene: {} -> {}", currentScene.getName(), nextScene.getName());
      } else {
        logger.debug("Loading scene: {}", nextScene.getName());
      }

      currentScene = nextScene;
      currentScene._start();
      nextScene = null;

      window.createRenderBuffers();
      Scene.current = currentScene;
    }
  }

  /** Runs cleanup code before exiting the application */
  protected void dispose() {
    logger.debug("Disposing application");

    if (currentScene != null)
      currentScene._dispose();

    Audio.dispose();
    IO.dispose();
  }
}
