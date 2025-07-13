package dev.gamekit.core;

import dev.gamekit.ui.widgets.Widget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link Scene} represents a logical part of your game. This can be a main menu, or a level
 * within your game.
 * <p>
 * Internally, a scene is a special kind of {@link Entity} which can also render
 * UI elements to the window
 */
public abstract class Scene extends Entity {
  protected final Logger logger;

  private final UI ui;

  public Scene(String name) {
    super(name);
    logger = LogManager.getLogger(getClass());
    ui = new UI(this);
  }

  /** Called to create the UI {@link Widget} tree of the scene */
  protected Widget createUI() {
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
    ui.setWidgetTree(createUI());
  }

  /** Called by {@link Application} to update the scene */
  @Override
  void _update() {
    super._update();
    ui.update();

    if (Renderer.isCompleted())
      Renderer.reset();
  }

  @Override
  void _render() {
    if (!Renderer.isCommitted()) {
      super._render();
      Renderer.commit();
    }
  }

  /** Called by {@link Application} to draw the scene to the {@link Window} */
  void _draw() {
    if (Renderer.isCommitted() && !Renderer.isCompleted())
      Renderer.draw(Window.getInstance().getDisplayGraphics());

    ui.draw();
  }

  /** Called <b>once</b> by {@link Application} to dispose the scene */
  @Override
  void _dispose() {
    logger.debug("Disposing scene");
    super._dispose();
    dispose();
  }
}
