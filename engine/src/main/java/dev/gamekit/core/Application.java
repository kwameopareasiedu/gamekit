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
  public static final long FRAME_TIME_MS = 1000 / 240;
  private static Application instance;

  protected final Logger logger = LogManager.getLogger(getClass());

  private final Settings settings;
  private final UtilityWorker utility;
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
    this.utility = new UtilityWorker();
    this.window = new Window();
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
    utility.scheduleTimeout(new Timeout(timeout, task));
  }

  /**
   * Schedule an {@link Animation} to run. Animations are updated before the scene's
   * {@code onUpdate()} to ensure current values are available to the scene's next update cycle
   */
  public void scheduleAnimation(Animation animation) {
    utility.scheduleAnimation(animation);
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
      long frameTimeAccumulator = 0;

      while (isRunning) {
        long currentFrameTime = System.currentTimeMillis();
        long timeDiff = currentFrameTime - lastFrameTime;
        lastFrameTime = currentFrameTime;
        frameTimeAccumulator += timeDiff;

        while (frameTimeAccumulator >= FRAME_TIME_MS) {
          frameTimeAccumulator -= FRAME_TIME_MS;
          Input.freeze();
          update();
          Input.reset();
        }

        render();
        endFrame();
        Thread.sleep(1);
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
    utility.start();
  }

  /** Called in each frame to update the current scene */
  private void update() {
    if (currentScene != null)
      currentScene._update();
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

    window.render();
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
    if (nextScene != null) {
      utility.clear();

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
  protected void dispose() throws InterruptedException {
    logger.debug("Disposing application");

    if (currentScene != null)
      currentScene._dispose();

    utility.interrupt();
    utility.join(1000);

    Audio.dispose();
    IO.dispose();
  }

  private static final class UtilityWorker extends Thread {
    private final List<Timeout> timeouts;
    private final List<Timeout> newTimeouts;
    private final List<Animation> animations;

    UtilityWorker() {
      timeouts = new ArrayList<>();
      newTimeouts = new ArrayList<>();
      animations = new ArrayList<>();
    }

    private void scheduleAnimation(Animation animation) {
      synchronized (animations) {
        if (!animations.contains(animation))
          animations.add(animation);
      }
    }

    private void scheduleTimeout(Timeout timeout) {
      synchronized (newTimeouts) {
        newTimeouts.add(timeout);
      }
    }

    @Override
    public void run() {
      super.run();

      long lastFrameTime = System.currentTimeMillis();
      long frameTimeAccumulator = 0;

      while (!isInterrupted()) {
        long currentFrameTime = System.currentTimeMillis();
        long timeDiff = currentFrameTime - lastFrameTime;
        lastFrameTime = currentFrameTime;
        frameTimeAccumulator += timeDiff;

        while (frameTimeAccumulator >= FRAME_TIME_MS) {
          frameTimeAccumulator -= FRAME_TIME_MS;
          update();
        }

        endFrame();

        try {
          Thread.sleep(1);
        } catch (InterruptedException ignored) {

        }
      }
    }

    private void clear() {
      synchronized (animations) {
        animations.clear();
      }

      timeouts.clear();
    }

    private void update() {
      synchronized (animations) {
        if (!animations.isEmpty()) {
          for (var anim : animations)
            anim.update();
        }
      }

      if (!timeouts.isEmpty()) {
        for (var timeout : timeouts)
          timeout.update();
      }

      Audio.update();
    }

    private void endFrame() {
      synchronized (animations) {
        if (!animations.isEmpty())
          animations.removeIf(Animation::isEnded);
      }

      if (!timeouts.isEmpty())
        timeouts.removeIf(Timeout::isCompleted);

      synchronized (newTimeouts) {
        if (!newTimeouts.isEmpty()) {
          timeouts.addAll(newTimeouts);
          newTimeouts.clear();
        }
      }
    }
  }
}
