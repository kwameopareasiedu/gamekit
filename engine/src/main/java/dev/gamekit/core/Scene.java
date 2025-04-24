package dev.gamekit.core;

import dev.gamekit.ui.widgets.Widget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link Scene} represents a logical part of your game. This can be a main menu, or a level
 * within your game.
 * <p>
 * For simple games, the scene's {@link #start()}, {@link #update()} and {@link #render()}
 * methods are enough to set up, update and render the state of the level.
 * <p>
 * For more complex use cases, a {@link Scene} can contain multiple game objects called
 * {@link Prop} which interact with each other. Each {@link Prop} has its own lifecycle methods
 * which can be used to model complex relationships
 * <p>
 * A scene also supports user interface rendering using {@link Widget} components
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

  protected void add(Prop prop) {
    logger.debug("Adding child: [{} - {}]", prop.internalId, prop.name);

    if (!props.containsKey(prop.internalId)) {
      Application.getInstance().scheduleTask(() -> {
        logger.debug("Added child: [{} - {}]", prop.internalId, prop.name);
        props.put(prop.internalId, prop);
        if (!prop.ready) prop._start(Scene.this);
      });
    }
  }

  protected void remove(Prop prop) {
    logger.debug("Removing child: [{} - {}]", prop.internalId, prop.name);

    if (props.containsKey(prop.internalId)) {
      Application.getInstance().scheduleTask(() -> {
        logger.debug("Removed child: [{} - {}]", prop.internalId, prop.name);
        props.remove(prop.internalId, prop);
        if (prop.ready) prop._dispose();
      });
    }
  }

  /** Called by {@link #_start()} to set up the scene */
  protected void start() { /* No-op */ }

  /** Called by {@link #_update()} to update the scene */
  protected void update() { /* No-op */ }

  /** Called by {@link #_render()} to render the scene */
  protected void render() { /* No-op */ }

  /** Called by {@link #_dispose()} to dispose the scene */
  protected void dispose() { /* No-op */ }

  @Override
  public Widget createUI() { return null; }

  /** Trigger a widget tree update. You would use this when some UI variables have changed */
  protected final void updateUI(UI.WidgetTreeUpdater updater) {
    updater.onUpdate();
    ui.triggerUpdate();
  }

  /** Called <b>once</b> by {@link Application} to initialize the scene */
  final void _start() {
    logger.debug("Starting scene");
    ui = new UI(this);
    ui.setWidgetTree(createUI());
    start();
    props.forEach((k, v) -> v.start());
  }

  /** Called by {@link Application} to update the scene */
  final void _update() {
    update();
    props.forEach((k, v) -> v.update());
    ui.update();
  }

  /** Called by {@link Application} to render the scene */
  final void _render() {
    render();
    props.forEach((k, p) -> p.render());
    ui.render();
  }

  /** Called <b>once</b> by {@link Application} to dispose the scene */
  final void _dispose() {
    logger.debug("Disposing scene");
    props.forEach((k, p) -> p._dispose());
    dispose();
  }
}
