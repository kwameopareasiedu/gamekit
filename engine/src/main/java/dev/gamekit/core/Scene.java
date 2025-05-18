package dev.gamekit.core;

import dev.gamekit.ui.widgets.Widget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link Scene} represents a logical part of your game. This can be a main menu, or a level
 * within your game.
 * <p>
 * For simple games, the scene's {@link #start()}, {@link #update()} and
 * {@link #render()} methods are enough to set up, update and render the state of the level.
 * <p>
 * For more complex use cases, a {@link Scene} can contain multiple game objects called
 * {@link Prop} which interact with each other. Each {@link Prop} has its own lifecycle methods
 * which can be used to model complex relationships
 * <p>
 * A scene also supports user interface rendering using {@link Widget} components
 */
public abstract class Scene {
  static Scene current;

  protected final Logger logger = LogManager.getLogger(getClass());
  protected final String name;

  private final Prop tree;
  private final UI ui;

  private State updateState;
  private State renderState;
  private State bufferState;

  public Scene(String name) {
    this.name = name;
    this.tree = new Prop("Root", true) { };
    this.ui = new UI(this);
  }

  public static Scene getCurrent() { return current; }

  public String getName() { return name; }

  protected void addChild(Prop prop) {
    tree.addChild(prop);
  }

  protected void removeChild(Prop prop) {
    tree.removeChild(prop);
  }

  /** Called to set up the scene */
  protected void start() { /* No-op */ }

  /** Called to update the scene */
  protected void update() { /* No-op */ }

  /** Called to render the scene */
  protected void render() { /* No-op */ }

  /** Called to dispose the scene */
  protected void dispose() { /* No-op */ }

  /** Called to create the {@link State} of the scene */
  protected <T extends State> T createState() {
    //noinspection unchecked
    return (T) new State() { };
  }

  /** Called to create the UI {@link Widget} tree of the scene */
  protected Widget createUI() {
    return null;
  }

  /** Trigger a widget tree update. You would use this when some UI variables have changed */
  protected final void updateUI(UI.WidgetTreeUpdater updater) {
    updater.onUpdate();
    ui.triggerUpdate();
  }

  /** Trigger a widget tree update. You would use this when some UI variables have changed */
  protected final void updateUI() {
    ui.triggerUpdate();
  }

  /** Called <b>once</b> by {@link Application} to initialize the scene */
  final void _start() {
    logger.debug("Starting scene");
    ui.setWidgetTree(createUI());
    start();
    tree._start();

    updateState = createState();
    renderState = createState();
    bufferState = createState();

    if (updateState == null) {
      throw new RuntimeException(
        "Scene.createState() must return a non-null state object"
      );
    }
  }

  /** Called by {@link Application} to update the scene */
  final void _update() {
    ui.update();
    update();
    swapUpdateState();
    tree._update();
  }

  /** Called by {@link Application} to render the scene */
  final void _render() {
    ui.render();
    render();
    swapRenderState();
    tree._render();
  }

  /** Called <b>once</b> by {@link Application} to dispose the scene */
  final void _dispose() {
    logger.debug("Disposing scene");
    tree._dispose();
    dispose();
  }

  private void swapUpdateState() {
    synchronized (this) {
      State tempState = updateState;
      updateState = bufferState;
      bufferState = tempState;
    }
  }

  private void swapRenderState() {
    synchronized (this) {
      State tempState = renderState;
      renderState = bufferState;
      bufferState = tempState;
    }
  }

  /**
   * {@link State} is a container for {@link Scene} related data that would have otherwise
   * declared as instance variables in the scene. This forms a foundation to decouple update and
   * rendering into separate threads.
   * <p>
   * During update, the engine passes a state instance to {@link Scene#update()}. Reads and
   * writes that would have otherwise involved instance variables should be done on the supplied
   * state instance.
   * <p>
   * During render, the updated state is passed to {@link Scene#render()}. The values of the
   * updated state should be used for rendering.
   */
  public static abstract class State { }
}
