package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;

/**
 * {@link Entity} represents an object in the game world.
 * <p>
 * An{@link Entity} has lifecycle methods which are called by the engine to set up, update,
 * render and dispose themselves.
 * <p>
 * An {@link Entity} can contain other entities as children and manage their lifecycle methods.
 */
public abstract class Entity {
  protected final String name;
  protected final Logger logger = LogManager.getLogger(getClass());
  protected final ArrayList<Entity> children;
  protected Entity parent;

  private final Renderer renderer;

  public Entity(String name) {
    this.name = name;
    children = new ArrayList<>();
    renderer = new Renderer();
  }

  public void addChild(Entity child) {
    if (!children.contains(child)) {
      logger.debug("Adding {} to {}", child.name, name);

      Application.getInstance().scheduleTask(() -> {
        children.add(child);
        child.setParent(this);
        child._start();
      });
    }
  }

  public void removeChild(Entity child) {
    if (children.contains(child)) {
      logger.debug("Removing {} from {}", child.name, name);

      Application.getInstance().scheduleTask(() -> {
        children.remove(child);
        child.setParent(null);
        child._dispose();
      });
    }
  }

  /** Called to set up the entity */
  protected void start() { }

  /** Called to update the entity */
  protected void update() { }

  /** Called to render the entity */
  protected void render(Renderer renderer) { }

  /** Called to dispose the entity */
  protected void dispose() { }

  void setParent(Entity parent) {
    this.parent = parent;
  }

  /** Called <b>once</b> by the parent {@link Entity} to initialize the entity */
  void _start() {
    start();
  }

  /** Called by the parent {@link Entity} to update the entity */
  void _update() {
    update();
    children.forEach(Entity::_update);
  }

  /** Called by the parent {@link Entity} to render the entity */
  void _render() {
    render(renderer);
    children.forEach(Entity::_render);
    renderer.swapFrontBuffer();
  }

  /**
   * Called by the parent {@link Entity} to apply the render calls to a {@link Graphics2D}
   * object
   */
  final void _draw(Graphics2D g) {
    renderer.apply(g);
    renderer.swapBackBuffer();
  }

  /** Called <b>once</b> by the parent {@link Entity} to dispose the entity */
  void _dispose() {
    children.forEach(Entity::_dispose);
    dispose();
    parent = null;
  }
}
