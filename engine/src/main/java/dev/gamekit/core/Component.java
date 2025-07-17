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

  protected Entity entity;

  protected void start() { }

  protected void update() { }

  protected void render() { }

  protected void dispose() { }

  void _start(Entity entity) {
    this.entity = entity;
    start();
  }

  void _update() {
    update();
  }

  void _render() {
    render();
  }

  void _dispose() {
    dispose();
    entity = null;
  }
}
