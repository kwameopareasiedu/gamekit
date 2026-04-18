package dev.gamekit.core;

import dev.gamekit.animation.Animation;
import dev.gamekit.ui.widgets.Widget;
import dev.gamekit.utils.Timeout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Scene} represents a logical part of your game. This can be a main menu, or a level within your game.
 * <p>
 * Internally, a scene is a special kind of {@link Entity} which can also render UI elements to the window
 */
public abstract class Scene extends Entity {
  protected final Logger logger;
  protected final Camera camera;

  final List<Timeout> timeouts;
  final List<Timeout> newTimeouts;
  final List<Animation> animations;

  private final UI ui;

  public Scene(String name) {
    super(name);
    logger = LogManager.getLogger(getClass());
    camera = new Camera();
    timeouts = new ArrayList<>();
    newTimeouts = new ArrayList<>();
    animations = new ArrayList<>();
    ui = new UI(this);
  }

  @Override
  public final void destroy() {
    throw new IllegalStateException("Destroy cannot be called on scene");
  }

  /** Called when the scene resumes after being suspended with some optional data */
  protected void resume(Object data) { /* No-op */ }

  /** Called to create the UI {@link Widget} tree of the scene */
  protected Widget createUI() {
    return null;
  }

  /** Trigger a widget tree update. You would use this when some UI variables have changed */
  protected final void updateUI() {
    ui.triggerUpdate();
  }

  @Override
  protected final List<Component> getComponents() {
    return null;
  }

  /** Called <b>once</b> by {@link Application} to initialize the scene */
  @Override
  void _start(Entity parent) {
    logger.debug("Starting scene");
    super._start(parent);
    Camera.current = camera;
    ui.setWidgetTree(createUI());
    ui.clear();
  }

  @Override
  void _resume(Entity parent) {
    logger.debug("Resuming scene");
    super._resume(parent);
    Camera.current = camera;
  }

  /** Called by {@link Application} to update the scene */
  @Override
  void _update() {
    for (Animation animation : animations)
      animation.update();

    for (Timeout timeout : timeouts)
      timeout.update();

    super._update();
    ui.update();

    if (Renderer.isCompleted()) {
      Renderer.reset();
    }

    animations.removeIf(Animation::isEnded);
    timeouts.removeIf(Timeout::isCompleted);
  }

  @Override
  void _render() {
    if (!Renderer.isCommitted()) {
      super._render();
      Renderer.commit();
    }

    ui.render();
  }

  /** Called by {@link Application} to draw the scene to the {@link Window} */
  void _draw() {
    camera.updateWindowTransform();

    if (Renderer.isCommitted() && !Renderer.isCompleted()) {
      Renderer.draw(Window.getInstance().getDisplayGraphics());
    }

    ui.draw();
  }

  /** Called by {@link Application} to resume the scene with optional data */
  void _resume(Object data) {
    super._resume(parent);

    resume(data);
  }

  /** Called by {@link Application} to run cleanup at the end of a frame */
  void _disposeFrame() {
    if (!newTimeouts.isEmpty()) {
      timeouts.addAll(newTimeouts);
      newTimeouts.clear();
    }
  }

  /** Called <b>once</b> by {@link Application} to dispose the scene */
  @Override
  void _dispose() {
    logger.debug("Disposing scene");

    animations.clear();
    timeouts.clear();
    newTimeouts.clear();

    super._dispose();
    ui.unmount();
    dispose();
  }
}
