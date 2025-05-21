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
@SuppressWarnings({ "BusyWait", "SynchronizeOnNonFinalField" })
public abstract class Application {
  public static final long FRAME_TIME_MS = 1000 / 240;
  private static Application instance;

  protected final Logger logger = LogManager.getLogger(getClass());

  private final Window window;
  private final Settings settings;
  private final List<Timeout> timeouts;
  private final List<Timeout> newTimeouts;
  private final List<Animation> animations;
  private final WorkerThread renderThread;
  private final WorkerThread audioThread;
  private boolean isRunning;
  private Scene<?> currentScene;
  private Scene<?> nextScene;

  public Application(String title) {
    this(new Settings(title));
  }

  public Application(Settings settings) {
    Application.instance = this;

    logger.debug("Created application");
    logger.debug(settings);

    this.settings = settings;
    this.window = new Window();
    this.timeouts = new ArrayList<>();
    this.newTimeouts = new ArrayList<>();
    this.animations = new ArrayList<>();
    this.renderThread = new WorkerThread("render", FRAME_TIME_MS, this::render);
    this.audioThread = new WorkerThread("audio", 1000 / 90, Audio::update);
    this.isRunning = true;
  }

  public static Application getInstance() { return instance; }

  public Settings getSettings() { return settings; }

  /** Schedules a scene to be loaded after the end of the current frame */
  public void loadScene(Scene<?> scene) {
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
    if (!animations.contains(animation))
      animations.add(animation);
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

        disposeFrame();
        Thread.sleep(1);
      }
    } catch (Exception e) {
      logger.error("Application loop raised an exception", e);
    } finally {
      try { dispose(); } //
      catch (Exception ignored) { }
    }

    System.exit(0);
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
    renderThread.start();
    audioThread.start();
  }

  /** Called in each frame to update the current scene */
  private void update() {
    if (!animations.isEmpty()) {
      animations.forEach(Animation::update);
      animations.removeIf(Animation::isEnded);
    }

    if (!timeouts.isEmpty()) {
      timeouts.forEach(Timeout::update);
      timeouts.removeIf(Timeout::isCompleted);
    }

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

      synchronized (currentScene) {
        currentScene._render();
      }
    }

    window.render();
  }

  /** Runs cleanup code at the end of a frame */
  private void disposeFrame() {
    if (!newTimeouts.isEmpty()) {
      timeouts.addAll(newTimeouts);
      newTimeouts.clear();
    }

    if (nextScene != null) {
      animations.clear();
      timeouts.clear();
      newTimeouts.clear();

      if (currentScene != null) {
        logger.debug("Disposing scene: {}", currentScene.getName());

        synchronized (currentScene) {
          currentScene._dispose();
        }
      }

      logger.debug("Starting scene: {}", nextScene.getName());
      currentScene = nextScene;
      currentScene._start();
      nextScene = null;

      window.createRenderBuffers();
    }
  }

  /** Runs cleanup code before exiting the application */
  protected void dispose() throws InterruptedException {
    logger.debug("Disposing application");

    if (currentScene != null)
      currentScene._dispose();

    renderThread.interrupt();
    renderThread.join(1000);

    audioThread.interrupt();
    audioThread.join(1000);

    Audio.dispose();
    IO.dispose();
  }

  private static class WorkerThread extends Thread {
    private final long frameTimeMs;
    private final Runnable runnable;

    private WorkerThread(String threadName, long frameTimeMs, Runnable runnable) {
      super(threadName);
      this.frameTimeMs = frameTimeMs;
      this.runnable = runnable;
    }

    @Override
    public final void run() {
      super.run();

      long lastFrameTime = System.currentTimeMillis();
      long frameTimeAccumulator = 0;

      while (!isInterrupted()) {
        long currentFrameTime = System.currentTimeMillis();
        long timeDiff = currentFrameTime - lastFrameTime;
        lastFrameTime = currentFrameTime;
        frameTimeAccumulator += timeDiff;

        while (frameTimeAccumulator >= frameTimeMs) {
          frameTimeAccumulator -= frameTimeMs;
          try { runnable.run(); } catch (Exception ignored) { }
        }

        try { Thread.sleep(1); } catch (InterruptedException ignored) { }
      }
    }
  }
}
