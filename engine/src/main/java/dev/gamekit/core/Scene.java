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

  public Scene(String name) {
    this.name = name;
    props = new HashMap<>();
  }

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

  /** Indicates that the widget tree should be re-rendered */
  public final void redrawUI() { ui.triggerRender(); }

  /** Called <b>once</b> by {@link Application} to initialize the scene */
  final void start() {
    logger.debug("Starting scene");
    ui = new UI(this);
    ui.setWidgetTree(onCreateUI());
    onStart();
    props.forEach((k, v) -> v.onStart());
  }

  /** Called by {@link Application} to update the scene */
  final void update() {
    onUpdate();
    props.forEach((k, v) -> v.onUpdate());
    ui.update();
  }

  /** Called by {@link Application} to render the scene */
  final void render() {
    onRender();
    props.forEach((k, v) -> v.onRender());
    ui.render();
  }

  /** Called <b>once</b> by {@link Application} to dispose the scene */
  final void dispose() {
    logger.debug("Disposing scene");
    props.forEach((k, v) -> v.onDispose());
    onDispose();
  }
}
