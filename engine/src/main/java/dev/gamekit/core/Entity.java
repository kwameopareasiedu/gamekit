package dev.gamekit.core;

import dev.gamekit.components.Transform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * {@link Entity} represents objects that exist in the game world. An entity can also contain and
 * manage the lifecycles of children entities.
 * <p>
 * An {@link Entity} has lifecycle methods which are called by the engine to set up, update,
 * render and dispose themselves.
 */
@SuppressWarnings("unchecked")
public abstract class Entity {
  @SuppressWarnings("unused")
  public final String id = UUID.randomUUID().toString();

  protected final String name;
  protected final Logger logger = LogManager.getLogger(getClass());
  protected final ArrayList<Entity> children;
  protected final ArrayList<Component> components;
  protected Entity parent;

  private State state;

  public Entity(String name) {
    this.name = name;
    state = State.NEW;
    children = new ArrayList<>();
    components = new ArrayList<>();
    components.add(new Transform());
  }

  /** Returns the parent entity */
  public Entity getParent() {
    return parent;
  }

  /** Returns the current state */
  public State getState() {
    return state;
  }

  /**
   * Adds a child to this entity, if it isn't already.
   * <p>
   * This also invokes the child's {@link Entity#_start} method if it is new or
   * {@link Entity#_restart} if it was previously inactivated.
   */
  public void addChild(Entity child) {
    if (child.parent != null)
      throw new IllegalStateException("Cannot add child with parent. Remove from parent first");

    switch (child.state) {
      case DOOMED, DEAD -> throw new IllegalStateException("Cannot add a doomed or dead child");
      case ACTIVE ->
        throw new IllegalStateException("Cannot add active child. Remove from parent first");
    }

    if (!children.contains(child)) {
      logger.debug("Adding {} to {}", child.name, name);

      Application.getInstance().runLater(() -> {
        switch (child.state) {
          case NEW -> child._start(this);
          case INACTIVE -> child._restart(this);
        }

        children.add(child);
      });
    }
  }

  /**
   * Removes a child from this entity, if it is part of its children.
   * <p>
   * This also invokes the child's {@link Scene#_dispose} method
   */
  public void removeChild(Entity child) {
    if (children.contains(child)) {
      logger.debug("Removing {} from {}", child.name, name);

      Application.getInstance().runLater(() -> {
        child._stop();
        children.remove(child);
      });
    }
  }

  /** Returns a {@link Component} of the specified class else {@code null} */
  public <T extends Component> T findComponent(Class<T> clazz) {
    // Optimization for finding the Transform component
    if (clazz == Transform.class)
      return (T) components.get(0);

    for (Component component : components) {
      if (clazz.isInstance(component))
        return (T) component;
    }

    return null;
  }

  /**
   * Returns a list of {@link Component components} of the specified class
   * <p>
   * NB: Care must be taken when using this method since it creates a new {@link List} object
   * every time
   */
  public <T extends Component> List<T> findComponents(Class<T> clazz) {
    List<T> out = new ArrayList<>();

    for (Component component : components) {
      if (clazz.isInstance(component))
        out.add((T) component);
    }

    return out;
  }

  /**
   * Schedules the entity for removal at the end of the current frame and immediately sets the
   * state to {@link State#DOOMED}
   */
  public void destroy() {
    stop();
    state = State.DOOMED;

    Application.getInstance().runLater(() -> {
      parent.children.remove(this);
      _dispose();
    });
  }

  /** Called during {@link #start} to get the components of the entity */
  protected List<Component> getComponents() {
    return null;
  }

  /** Called to set up the entity */
  protected void start() { }

  /** Called to restart the entity after being added to a new parent */
  protected void restart() { }

  /** Called to update the entity */
  protected void update() { }

  /** Called to render the entity */
  protected void render() { }

  /** Called to stop the entity before removing it from the current parent */
  protected void stop() { }

  /** Called to dispose the entity */
  protected void dispose() { }

  /** Called <b>once</b> by the parent {@link Entity} to initialize the entity */
  void _start(Entity parent) {
    this.parent = parent;

    List<Component> newComponents = getComponents();

    if (newComponents != null) {
      for (Component component : newComponents)
        component._validate(this, newComponents);

      this.components.addAll(newComponents);
    }

    for (Component component : this.components)
      component._start(this);

    start();

    state = State.ACTIVE;
  }

  /**
   * Called by the parent {@link Entity} after being added to it, but if {@link #_stop} was
   * previously called.
   * <p>
   * This can be used to re-initialize the entity with the new parent
   */
  void _restart(Entity newParent) {
    this.parent = newParent;
    restart();
    state = State.ACTIVE;
  }

  /** Called by the parent {@link Entity} to update the entity */
  void _update() {
    if (state == State.ACTIVE) {
      components.forEach(Component::_update);
      update();
      children.forEach(Entity::_update);
    }
  }

  /** Called by the parent {@link Entity} to render the entity */
  void _render() {
    if (state == State.ACTIVE) {
      render();
      components.forEach(Component::_render);
      children.forEach(Entity::_render);
    }
  }

  /**
   * Called by the parent {@link Entity} before it is removed from its children.
   * <p>
   * The entity will not be destroyed, but will no longer run lifecycle methods
   */
  void _stop() {
    stop();
    parent = null;
    state = State.INACTIVE;
  }

  /** Called <b>once</b> by the parent {@link Entity} to dispose the entity */
  void _dispose() {
    children.forEach(Entity::_dispose);
    components.forEach(Component::_dispose);
    dispose();

    parent = null;
    state = State.DEAD;
  }

  /** Represents the state an {@link Entity} can be in */
  public enum State {
    /** Represents a newly created instance of an {@link Entity} */
    NEW,
    /**
     * Represents an {@link Entity} which has been added to another entity or the scene. Only
     * entities in this state can run update/render lifecycle methods
     */
    ACTIVE,
    /**
     * Represents an {@link Entity} which was previously a child of another entity or the scene
     * but has been removed. The entity is not destroyed, but doesn't run any lifecycle methods
     */
    INACTIVE,
    /**
     * Represents an {@link Entity} which has been marked for destruction at the end of the current
     * frame
     */
    DOOMED,
    /** Represents an {@link Entity} which has been destroyed */
    DEAD
  }
}
