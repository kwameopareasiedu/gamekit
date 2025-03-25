package dev.gamekit.core;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.WidgetState;
import dev.gamekit.ui.widgets.Widget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * A scene is a collection of {@link Prop props} interacting with each other
 * to form a logic part of your application. This can be a main menu, or a
 * level within your game.
 * <p>
 * A scene can also display a {@link Widget} tree which forms its user interface
 */
public abstract class Scene {
  private static final Logger LOGGER = LogManager.getLogger();
  static Scene current;

  protected final String name;
  protected final Map<Integer, Prop> props;

  private final WidgetState.Observer<Object> widgetStateObserver;
  private boolean widgetTreeInvalidated = true;
  private Widget widgetTree;

  /**
   * Creates a scene with the given name
   * @param name The name of the scene for logging purposes
   */
  public Scene(String name) {
    this.name = name;
    props = new HashMap<>();
    widgetStateObserver = (state) -> {
      WidgetState<Object> widgetState = (WidgetState<Object>) state;
      LOGGER.debug(widgetState);
    };
  }

  /** Returns the currently loaded scene instance */
  public static Scene getCurrent() { return current; }

  public String getName() { return name; }

  public WidgetState.Observer<Object> getWidgetStateObserver() {
    return widgetStateObserver;
  }

  public void addProp(Prop prop) {
    LOGGER.debug("Adding child: [{} - {}]", prop.internalId, prop.name);

    if (!props.containsKey(prop.internalId)) {
      Application.getInstance().scheduleTask(() -> {
        LOGGER.debug("Added child: {} ({})", prop.name, prop.internalId);
        props.put(prop.internalId, prop);
        if (!prop.ready) prop.onStart();
      });
    }
  }

  public void removeProp(Prop prop) {
    LOGGER.debug("Removing child: [{} - {}]", prop.internalId, prop.name);

    if (props.containsKey(prop.internalId)) {
      Application.getInstance().scheduleTask(() -> {
        LOGGER.debug("Removed child: {} ({})", prop.name, prop.internalId);
        props.remove(prop.internalId, prop);
        if (prop.ready) prop.onDispose();
      });
    }
  }

  /** Called by {@link #start()} to set up the scene */
  public void onStart() { }

  /** Called by {@link #update()} to update the scene */
  public void onUpdate() { }

  /** Called by {@link #render()} to render the scene */
  public void onRender() { }

  /** Called by {@link #dispose()} to dispose the scene */
  public void onDispose() { }

  /** Sets the {@code widget} as the root UI element. */
  protected void createWidgetTree(Widget widget) {
    if (widgetTree != null) {
      throw new IllegalStateException("Widget tree is already present");
    }

    Window win = Window.getInstance();

    Constraints cs = new Constraints(
      win.getRenderWidth(),
      win.getRenderWidth(),
      win.getRenderHeight(),
      win.getRenderHeight()
    );

    widget.computeLayout(cs);
    widget.getComputedPosition().set(0, 0);
    this.widgetTree = widget;
  }

  /**
   * Called by {@link Application} to initialize the scene.
   * This calls {@link #onStart()} before calling
   * {@link Prop#onStart() onStart()} on each child prop
   */
  final void start() {
    LOGGER.debug("Starting scene");

    var widgetStatesToBind = WidgetState.STATES_TO_BIND_TO_SCENE;

    if (!widgetStatesToBind.isEmpty()) {
      LOGGER.debug("Binding pending states: {}", widgetStatesToBind.size());
      widgetStatesToBind.forEach(state -> state.bindObserver(widgetStateObserver));
      widgetStatesToBind.clear();
    }

    onStart();
    props.forEach((k, v) -> v.onStart());
  }

  /**
   * Called by {@link Application} to update the scene.
   * This calls {@link #onUpdate()} before calling
   * {@link Prop#onUpdate() onUpdate()} on each child prop
   */
  final void update() {
    onUpdate();
    props.forEach((k, v) -> v.onUpdate());
  }

  /**
   * Called by {@link Application} to render the scene.
   * This calls {@link #onRender()} first, then calls
   * {@link Prop#onRender() onRender()} on each child prop
   * and finally renders the widget tree, if set
   */
  final void render() {
    onRender();
    props.forEach((k, v) -> v.onRender());

    if (widgetTree != null && widgetTreeInvalidated) {
      LOGGER.debug("Rendering widget tree");
      Renderer.clearUI();
      Renderer.drawUI(widgetTree);
      widgetTreeInvalidated = false;
    }
  }

  /**
   * Called by {@link Application} to render the scene.
   * This calls {@link Prop#onDispose() onDispose()}
   * on each child prop before calling {@link #onDispose()}
   */
  final void dispose() {
    LOGGER.debug("Disposing scene");
    props.forEach((k, v) -> v.onDispose());
    onDispose();
  }
}
