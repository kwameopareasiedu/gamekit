package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Prop} represent game objects in a {@link Scene}.
 * <p>
 * Like {@link Scene}, {@link Prop} has lifecycle methods which are called by the engine to
 * set up, update, render and dispose.
 */
public abstract class Prop {
  protected final Logger logger = LogManager.getLogger(getClass());
  protected final List<Prop> children;
  protected Prop parent;

  final String name;
  boolean ready;

  public Prop(String name) {
    this.name = name;
    this.ready = false;
    children = new ArrayList<>();
  }

  Prop(String name, boolean ready) {
    this.name = name;
    this.ready = ready;
    children = new ArrayList<>();
  }

  public void addChild(Prop prop) {
    if (!children.contains(prop)) {
      logger.debug("Adding {} to {}", prop.name, name);

      Application.getInstance().scheduleTask(() -> {
        children.add(prop);
        prop.setParent(this);

        if (!prop.ready)
          prop._start();
      });
    }
  }

  public void removeChild(Prop prop) {
    if (children.contains(prop)) {
      logger.debug("Removing {} to {}", prop.name, name);

      Application.getInstance().scheduleTask(() -> {
        children.remove(prop);
        prop.setParent(null);

        if (prop.ready)
          prop._dispose();
      });
    }
  }

  public void setParent(Prop parent) {
    this.parent = parent;
  }

  /** Called to set up the prop */
  protected void start() { }

  /** Called to update the prop */
  protected void update() { }

  /** Called to render the prop */
  protected void render() { }

  /** Called to dispose the prop */
  protected void dispose() { }

  /** Called <b>once</b> by the parent {@link Prop} to initialize the prop */
  void _start() {
    ready = true;
    start();
  }

  /** Called by the parent {@link Prop} to update the prop */
  void _update() {
    update();
    children.forEach(Prop::_update);
  }

  /** Called by the parent {@link Prop} to render the prop */
  void _render() {
    render();
    children.forEach(Prop::_render);
  }

  /** Called <b>once</b> by the parent {@link Prop} to dispose the prop */
  void _dispose() {
    dispose();
    children.forEach(Prop::_dispose);
    parent = null;
  }
}
