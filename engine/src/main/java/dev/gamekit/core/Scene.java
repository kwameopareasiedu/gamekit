package dev.gamekit.core;

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
 * A scene can also display a user interface
 */
public abstract class Scene implements UI.WidgetTreeCreator {
  static Scene current;

  protected final Logger logger = LogManager.getLogger(getClass());
  protected final String name;
  protected final Map<Integer, Prop> props;

  private UI ui;

  /**
   * Creates a scene with the given name
   * @param name The name of the scene for logging purposes
   */
  public Scene(String name) {
    this.name = name;
    props = new HashMap<>();
  }

  /** Returns the currently loaded scene instance */
  public static Scene getCurrent() { return current; }

  public String getName() { return name; }

  protected void addProp(Prop prop) {
    logger.debug("Adding child: [{} - {}]", prop.internalId, prop.name);

    if (!props.containsKey(prop.internalId)) {
      Application.getInstance().scheduleTask(() -> {
        logger.debug("Added child: {} ({})", prop.name, prop.internalId);
        props.put(prop.internalId, prop);
        if (!prop.ready) prop.onStart();
      });
    }
  }

  protected void removeProp(Prop prop) {
    logger.debug("Removing child: [{} - {}]", prop.internalId, prop.name);

    if (props.containsKey(prop.internalId)) {
      Application.getInstance().scheduleTask(() -> {
        logger.debug("Removed child: {} ({})", prop.name, prop.internalId);
        props.remove(prop.internalId, prop);
        if (prop.ready) prop.onDispose();
      });
    }
  }

  /** Called by {@link #start()} to set up the scene */
  protected void onStart() { }

  /** Called by {@link #update()} to update the scene */
  protected void onUpdate() { }

  /** Called by {@link #render()} to render the scene */
  protected void onRender() { }

  /** Called by {@link #dispose()} to dispose the scene */
  protected void onDispose() { }

  @Override
  public Widget onCreateUI() { return null; }

  /** Indicates that the widget tree should be updated based on a state change */
  protected final void updateUI(UI.WidgetTreeUpdater updater) {
    updater.onUpdate();
    ui.triggerUpdate();
  }

  /**
   * Called by {@link Application} to initialize the scene.
   * This calls {@link #onStart()} before calling
   * {@link Prop#onStart() onStart()} on each child prop
   */
  final void start() {
    logger.debug("Starting scene");
    ui = new UI(this);
    ui.setWidgetTree(onCreateUI());
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
    ui.update();
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
    ui.render();
  }

  /**
   * Called by {@link Application} to render the scene.
   * This calls {@link Prop#onDispose() onDispose()}
   * on each child prop before calling {@link #onDispose()}
   */
  final void dispose() {
    logger.debug("Disposing scene");
    props.forEach((k, v) -> v.onDispose());
    onDispose();
  }
}
