package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * While an {@link Entity} can have all functionality written entirely in its own class, it's
 * usually a good idea to separate independent functionality from each other, and this is where
 * components come in.
 * <p>
 * {@link Component} represents predefined behaviours that can then be attached to one or more
 * {@link Entity entities}. These behaviors include, but are not limited to, physics, lighting
 * and network components.
 */
public abstract class Component {
  protected final Logger logger = LogManager.getLogger(getClass());

  /** The host {@link Entity entity} the component is attached to */
  protected Entity entity;

  /**
   * Called when attached to an {@link Entity} to set up the component
   * <p>
   * NB: <i>The value of {@link #entity} is set before this method is called and can safely be
   * accessed here.</i>
   */
  protected void start() { /* No-op */ }

  /** Called to update the component */
  protected void update() { /* No-op */ }

  /** Called to render the component */
  protected void render() { /* No-op */ }

  /**
   * Called to dispose the component
   * <p>
   * NB: <i>The value of {@link #entity} can still be accessed here</i>
   */
  protected void dispose() { /* No-op */ }

  /** Called <b>once</b> by the host {@link Entity} to initialize the component */
  void _start(Entity entity) {
    this.entity = entity;
    start();
  }

  /** Called by the host {@link Entity} to update the component */
  void _update() {
    update();
  }

  /** Called by the host {@link Entity} to render the component */
  void _render() {
    render();
  }

  /** Called <b>once</b> by the host {@link Entity} to dispose the component */
  void _dispose() {
    dispose();
    entity = null;
  }
}
