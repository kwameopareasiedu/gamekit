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
import java.util.Stack;

/**
 * {@link Application} is the heart of a GameKit program. A game or application must extend this class to do anything
 * with the engine.
 * <p>
 * It runs a fixed-step game update loop which protects against lag spikes. It also loads scenes, schedule animations
 * and timeouts and quit the running instance.
 * <p>
 * NB: <i>Use {@link Application#getInstance} to get the current instance from anywhere in your project to access its
 * methods.</i>
 */
@SuppressWarnings({ "BusyWait", "SynchronizeOnNonFinalField" })
public abstract class Application {
  public static final long FRAME_INTERVAL_MS = 1000 / 240;
  public static final long DRAW_INTERVAL_MS = 1000 / 60;

  private static final int STACK_SCENE_FLAG = 1;
  private static Application instance;

  protected final Logger logger = LogManager.getLogger(getClass());

  private final Window window;
  private final Settings settings;
  private final WorkerThread audioThread;
  private final WorkerThread physicsThread;
  private final WorkerThread drawThread;
  private final List<Timeout> pendingTimeouts;
  private final Stack<Scene> sceneStack;
  private boolean isRunning;
  private Scene currentScene;
  private Scene nextScene;
  private int sceneFlags;
  private Object sceneData;

  public Application(String title) {
    this(new Settings(title));
  }

  public Application(Settings settings) {
    System.setProperty("sun.java2d.opengl", "True");
    System.setProperty("sun.java2d.accthreshold", "0");

    logger.debug("Created application");
    logger.debug(settings);

    Application.instance = this;
    this.settings = settings;
    this.window = new Window();
    this.audioThread = new WorkerThread("audio", FRAME_INTERVAL_MS, Audio::update);
    this.physicsThread = new WorkerThread("physics", FRAME_INTERVAL_MS, Physics::update);
    this.drawThread = new WorkerThread("draw", DRAW_INTERVAL_MS, this::draw);
    this.pendingTimeouts = new ArrayList<>();
    this.sceneStack = new Stack<>();
    this.isRunning = true;
    this.sceneFlags = 0;
  }

  /** Returns the current instance of {@link Application} */
  public static Application getInstance() {
    return instance;
  }

  /** Returns the settings used to initialize the current instance of {@link Application} */
  public Settings getSettings() {
    return settings;
  }

  /** Schedules a scene to be loaded at the end of the current frame, replacing the current scene */
  public void loadScene(Scene scene) {
    loadScene(scene, 0);
  }

  /** Schedules a scene to be loaded at the end of the current frame, stacking the current scene */
  public void stackScene(Scene scene) {
    loadScene(scene, STACK_SCENE_FLAG);
  }

  /**
   * Pops the scene stack and schedules the popped scene to be resumed at the end of the current frame.
   * <p>
   * If the current scene was loaded to generate some data, it can be passed here to be forwarded to the popped scene
   *
   * @see #popSceneStack()
   */
  public void popSceneStack(Object data) {
    if (sceneStack.empty())
      throw new IllegalStateException("Scene stack is empty");

    this.sceneData = data;
    loadScene(sceneStack.pop());
  }

  /**
   * Pops the scene stack and schedules the popped scene to be resumed at the end of the current frame
   *
   * @see #popSceneStack(Object)
   */
  public void popSceneStack() {
    popSceneStack(null);
  }

  /**
   * Schedules a task to be executed immediately after the end of the current frame
   *
   * @see #scheduleTask(VoidCallback, long)
   */
  public Timeout scheduleTask(VoidCallback callback) {
    return scheduleTask(callback, 0);
  }

  /**
   * Schedules a task to be executed after a specified time.
   * <p>
   * If {@code timeoutMs} is zero, {@code task} is executed immediately after the current frame
   *
   * @see #scheduleTask(VoidCallback)
   */
  public Timeout scheduleTask(VoidCallback callback, long timeoutMs) {
    if (timeoutMs < 0) throw new IllegalArgumentException("Timeout cannot be negative");

    Timeout timeout = new Timeout(timeoutMs, callback);

    if (currentScene != null) {
      currentScene.newTimeouts.add(timeout);
    } else {
      pendingTimeouts.add(timeout);
    }

    return timeout;
  }

  /**
   * Schedules an {@link Animation} to play. Animations are updated before {@link Scene#update} to ensure current
   * values are available to the scene's next update cycle.
   * <p>
   * NB: <i>{@link Animation#start} calls this method internally, so there is no need to explicitly invoke this</i>
   */
  public void playAnimation(Animation animation) {
    if (currentScene == null) throw new IllegalStateException("No currently loaded scene");

    if (animation != null && !currentScene.animations.contains(animation))
      scheduleTask(() -> currentScene.animations.add(animation));
  }

  /**
   * Quits the current {@link Application} by dispatching a {@link WindowEvent#WINDOW_CLOSING} event to the
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

        while (frameTimeAccumulator >= FRAME_INTERVAL_MS && isRunning) {
          frameTimeAccumulator -= FRAME_INTERVAL_MS;
          Input.freeze();
          update();
          Input.reset();
          Thread.sleep(1);
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

  /** Schedules a scene to be loaded after the end of the current frame */
  private void loadScene(Scene scene, int flags) {
    if (scene == null)
      throw new IllegalArgumentException("Unable to load a null scene");

    nextScene = scene;
    sceneFlags = flags;
    logger.debug("Loaded next scene: {}, Flags: {}", scene.name, flags);
  }

  /** Sets up GameKit's internals before starting the game loop */
  private void setup() {
    logger.debug("Initializing application");

    window.getCanvas().addKeyListener(Input.INSTANCE);
    window.getCanvas().addMouseListener(Input.INSTANCE);
    window.getCanvas().addMouseMotionListener(Input.INSTANCE);

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
    if (currentScene != null)
      currentScene._update();
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
      Camera.updateWindowTransform();

      synchronized (currentScene) {
        currentScene._draw();
      }

      window.update();
    }
  }

  /** Runs cleanup code at the end of a frame */
  private void disposeFrame() {
    if (currentScene != null)
      currentScene._disposeFrame();

    if (nextScene != null) {
      final boolean stackCurrentScene = (sceneFlags | STACK_SCENE_FLAG) == STACK_SCENE_FLAG;

      if (currentScene != null) {
        synchronized (currentScene) {
          if (stackCurrentScene) {
            currentScene._stop();
            sceneStack.push(currentScene);
          } else {
            currentScene._dispose();
          }
        }
      }

      Camera.reset();

      currentScene = nextScene;

      Entity.State currentSceneState = currentScene.getState();

      if (currentSceneState == Entity.State.NEW) {
        currentScene._start(null);
        currentScene.newTimeouts.addAll(pendingTimeouts);
        pendingTimeouts.clear();
      } else if (currentSceneState == Entity.State.INACTIVE) {
        currentScene._resume(sceneData);
      }

      nextScene = null;
      sceneData = null;
      sceneFlags = 0;
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
