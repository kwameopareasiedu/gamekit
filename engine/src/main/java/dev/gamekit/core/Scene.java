package dev.gamekit.core;

import dev.gamekit.ui.widgets.Widget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
  protected final Prop tree;

  private UI ui;

  public Scene(String name) {
    this.name = name;
    this.tree = new Prop("Root", true) { };
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

  @Override
  public Widget createUI() { return null; }

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
    ui = new UI(this);
    ui.setWidgetTree(createUI());
    start();
    tree._start();
  }

  /** Called by {@link Application} to update the scene */
  final void _update() {
    ui.update();
    update();
    tree._update();
  }

  /** Called by {@link Application} to render the scene */
  final void _render() {
    ui.render();
    render();
    tree._render();
  }

  /** Called <b>once</b> by {@link Application} to dispose the scene */
  final void _dispose() {
    logger.debug("Disposing scene");
    tree._dispose();
    dispose();
  }
}
