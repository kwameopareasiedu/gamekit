package dev.gamekit.core;

import dev.gamekit.animation.Animation;
import dev.gamekit.settings.Settings;
import dev.gamekit.utils.Timeout;
import dev.gamekit.utils.VoidCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Application} is the heart of a GameKit program. A game or application must extend this class to do anything
 * with the engine.
 * <p>
 * It runs a fixed-step game update loop which protects against lag spikes. It also loads scenes, schedule animations
 * and timeouts and quit the running instance.
 * <p>
 * NB: <i>Use {@link Application#getInstance} to get the current instance from anywhere in your project to access its
 * methods.</i>
 * TODO: Experiment with multithreaded rendering when each render thread draws to a fixed section
 */
@SuppressWarnings({ "BusyWait", "SynchronizeOnNonFinalField" })
public abstract class Application {
  public static final long FRAME_INTERVAL_MS = 1000 / 240;
  public static final long DRAW_INTERVAL_MS = 1000 / 60;

  private static Application instance;

  protected final Logger logger = LogManager.getLogger(getClass());

  private final Window window;
  private final Settings settings;
  private final List<Timeout> timeouts;
  private final List<Timeout> newTimeouts;
  private final List<Animation> animations;
  private final WorkerThread audioThread;
  private final WorkerThread physicsThread;
  private final WorkerThread drawThread;
  private boolean isRunning;
  private Scene currentScene;
  private Scene nextScene;

  public Application(String title) {
    this(new Settings(title));
  }

  public Application(Settings settings) {
    logger.debug("Created application");
    logger.debug(settings);

    Application.instance = this;
    this.settings = settings;
    this.window = new Window();
    this.timeouts = new ArrayList<>();
    this.newTimeouts = new ArrayList<>();
    this.animations = new ArrayList<>();
    this.audioThread = new WorkerThread("audio", FRAME_INTERVAL_MS, Audio::update);
    this.physicsThread = new WorkerThread("physics", FRAME_INTERVAL_MS, Physics::update);
    this.drawThread = new WorkerThread("draw", DRAW_INTERVAL_MS, this::draw);
    this.isRunning = true;
  }

  /** Returns the current instance of {@link Application} */
  public static Application getInstance() {
    return instance;
  }

  /** Returns the settings used to initialize the current instance of {@link Application} */
  public Settings getSettings() {
    return settings;
  }

  /** Schedules a scene to be loaded after the end of the current frame */
  public void loadScene(Scene scene) {
    if (scene == null) {
      logger.fatal("Unable to load a null scene");
      throw new IllegalArgumentException("Unable to load a null scene");
    }

    this.nextScene = scene;
    logger.debug("Primed next scene: {}", scene.name);
  }

  /** Schedule and returns a {@link Timeout timeout} to be executed immediately after the end of the current frame */
  public Timeout scheduleTask(VoidCallback callback) {
    return scheduleTask(callback, 0);
  }

  /**
   * Schedule and returns a {@link Timeout task} to be executed after a specified time.
   * <p>
   * If {@code timeoutMs} is zero, {@code task} is executed immediately after the current frame
   *
   * @see #scheduleTask(VoidCallback)
   */
  public Timeout scheduleTask(VoidCallback callback, long timeoutMs) {
    if (timeoutMs < 0) throw new IllegalArgumentException("Timeout cannot be negative");

    Timeout timeout = new Timeout(timeoutMs, callback);
    newTimeouts.add(timeout);
    return timeout;
  }

  /**
   * Schedule an {@link Animation} to play. Animations are updated before {@link Scene#update} to ensure current
   * values are available to the scene's next update cycle.
   * <p>
   * NB: <i>{@link Animation#start} calls this method internally, so there is no need to explicitly invoke this</i>
   */
  public void playAnimation(Animation animation) {
    if (animation != null && !animations.contains(animation)) {
      animations.add(animation);
    }
  }

  /**
   * Quit the current {@link Application} by dispatching a {@link WindowEvent#WINDOW_CLOSING} event to the
   * {@link Window} {@link javax.swing.JFrame frame}
   */
  public void quit() {
    window.getFrame().dispatchEvent(new WindowEvent(window.getFrame(), WindowEvent.WINDOW_CLOSING));
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

        while (frameTimeAccumulator >= FRAME_INTERVAL_MS) {
          frameTimeAccumulator -= FRAME_INTERVAL_MS;
          Input.freeze();
          update();
          Input.reset();
        }

        render();
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

    window.show();
    audioThread.start();
    physicsThread.start();
    drawThread.start();
  }

  /** Called in each frame to update the current scene */
  private void update() {
    for (Animation animation : animations)
      animation.update();

    for (Timeout timeout : timeouts)
      timeout.update();

    if (currentScene != null)
      currentScene._update();

    animations.removeIf(Animation::isEnded);
    timeouts.removeIf(Timeout::isCompleted);
  }

  /** Called in each frame to render the current scene */
  private void render() {
    if (currentScene != null)
      currentScene._render();
  }

  /**
   * Applies the {@link Camera} transformation on the {@link Window} scene buffer and draws the current scene to the
   * {@link Window}
   */
  private void draw() {
    if (currentScene != null) {
      Camera.updateWindow();

      synchronized (currentScene) {
        currentScene._draw();
      }

      window.refresh();
    }
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
        synchronized (currentScene) {
          currentScene._dispose();
          UI.getInstance().clear();
        }
      }

      Camera.reset();
      currentScene = nextScene;
      currentScene._start(null);
      nextScene = null;
    }
  }

  /** Runs cleanup code before exiting the application */
  protected void dispose() throws InterruptedException {
    logger.debug("Disposing application");

    if (currentScene != null) currentScene._dispose();

    audioThread.interrupt();
    audioThread.join(500);

    physicsThread.interrupt();
    physicsThread.join(500);

    drawThread.interrupt();
    drawThread.join(500);

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
          try { runnable.run(); } //
          catch (Exception ignored) { }
        }

        try { Thread.sleep(1); } //
        catch (InterruptedException ignored) { }
      }
    }
  }
}
