package dev.gamekit.core;

import dev.gamekit.ui.widgets.Widget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link Scene} represents a logical part of your game. This can be a main menu, or a level
 * within your game.
 * <p>
 * For simple games, the scene's {@link #start(State)}, {@link #update(State)} and
 * {@link #render(State)} methods are enough to set up, update and render the state of the level.
 * <p>
 * For more complex use cases, a {@link Scene} can contain multiple game objects called
 * {@link Entity} which interact with each other. Each {@link Entity} has its own lifecycle methods
 * which can be used to model complex relationships
 * <p>
 * A scene also supports user interface rendering using {@link Widget} components
 */
public abstract class Scene<T extends Entity.State<T>> extends Entity<T> {
  protected final Logger logger = LogManager.getLogger(getClass());

  private final UI<T> ui;

  public Scene(String name) {
    super(name);
    this.ui = new UI<>(this);
  }

  /** Called to create the UI {@link Widget} tree of the scene */
  protected Widget createUI(T updateState) {
    return null;
  }

  /** Trigger a widget tree update. You would use this when some UI variables have changed */
  protected final void updateUI() {
    ui.triggerUpdate();
  }

  /** Called <b>once</b> by {@link Application} to initialize the scene */
  @Override
  void _start() {
    logger.debug("Starting scene");
    super._start();
    ui.setWidgetTree(createUI(updateState));
  }

  /** Called by {@link Application} to update the scene */
  @Override
  void _update() {
    super._update();
    ui.update(updateState);
  }

  /** Called by {@link Application} to render the scene */
  @Override
  void _render() {
    super._render();
    ui.render();
  }

  /** Called <b>once</b> by {@link Application} to dispose the scene */
  @Override
  void _dispose() {
    logger.debug("Disposing scene");
    super._dispose();
    dispose();
  }
}
