package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link Trait} represent predefined behaviors that can be attached to {@link Entity entities}.
 * These behaviors include, but are not limited to, physics, lighting and network components.
 */
public abstract class Trait {
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
