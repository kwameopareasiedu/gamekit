package dev.gamekit.core;

import dev.gamekit.components.Transform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * {@link Entity} represents objects that exist in the game world. An entity can also contain and manage the
 * lifecycles of children entities.
 * <p>
 * An {@link Entity} has lifecycle methods which are called by the engine to set up, update, render and dispose
 * themselves.
 */
@SuppressWarnings("unchecked")
public abstract class Entity {
  @SuppressWarnings("unused")
  public final String id = UUID.randomUUID().toString();
  public final String name;

  protected final Logger logger = LogManager.getLogger(getClass());
  protected Entity parent;

  private final ArrayList<Entity> children;
  private final ArrayList<Component> components;
  private final ArrayList<Component> componentSearchList;
  private State state;

  public Entity(String name) {
    this.name = name;
    state = State.NEW;
    children = new ArrayList<>();
    components = new ArrayList<>();
    components.add(new Transform());
    componentSearchList = new ArrayList<>();
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
   * Adds a child to this entity, at the end of the current frame, invoking the child's {@link Entity#start} method
   * if it is new or {@link Entity#restart} if it was previously inactivated.
   */
  public void addChild(Entity child) {
    if (children.contains(child)) return;

    if (child.parent != null) throw new IllegalStateException("Cannot add child with parent. Remove from parent first");

    switch (child.state) {
      case DOOMED, DEAD -> throw new IllegalStateException("Cannot add a doomed or dead child");
      case ACTIVE -> throw new IllegalStateException("Cannot add active child of another parent");
    }

    Application.getInstance().scheduleTask(() -> {
      logger.debug("Adding {} to {}", child.name, name);

      switch (child.state) {
        case NEW -> child._start(this);
        case INACTIVE -> child._restart(this);
      }

      children.add(child);
    });
  }

  /** Removes a child from this entity at the end of the current frame, invoking its {@link Entity#stop} method */
  public void removeChild(Entity child) {
    if (children.contains(child)) {
      child.state = State.INACTIVE;

      Application.getInstance().scheduleTask(() -> {
        logger.debug("Removing {} from {}", child.name, name);
        child.stop();
        child.parent = null;
        children.remove(child);
      });
    }
  }

  /** Returns a {@link Component} of the specified class else {@code null} */
  public <T extends Component> T findComponent(Class<T> clazz) {
    return findComponent(clazz, (Component.Filter<T>) Component.TRUTHY_FILTER);
  }

  /** Returns a {@link Component} of the specified class, matching the provided filter else {@code null} */
  public <T extends Component> T findComponent(Class<T> clazz, Component.Filter<T> filter) {
    // Optimization for finding the Transform component
    if (clazz == Transform.class) return (T) components.get(0);

    for (Component component : components) {
      if (clazz.isInstance(component) && filter.filter((T) component)) return (T) component;
    }

    return null;
  }

  /**
   * Returns a list of {@link Component components} of the specified class
   * <p>
   * <i>NB: For added performance, the returned {@link ArrayList<T>} is reused across multiple invocations so you
   * should not keep a reference to it</i>
   */
  public <T extends Component> List<T> findComponents(Class<T> clazz) {
    componentSearchList.clear();

    for (Component component : components) {
      if (clazz.isInstance(component)) componentSearchList.add(component);
    }

    return (List<T>) componentSearchList;
  }

  /**
   * Schedules the entity for removal at the end of the current frame and immediately sets the state to
   * {@link State#DOOMED}
   */
  public void destroy() {
    if (state == State.ACTIVE) {
      state = State.DOOMED;

      Application.getInstance().scheduleTask(() -> {
        logger.debug("Destroying {}", name);
        _dispose();
      });
    }
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

  /** Called by a new parent {@link Entity} after previously being stopped, to re-initialize this entity */
  void _restart(Entity parent) {
    this.parent = parent;
    restart();
    state = State.ACTIVE;
  }

  /** Called by the parent {@link Entity} to update the entity */
  void _update() {
    if (state == State.ACTIVE) {
      for (Component component : components) {
        component._update();
      }

      update();

      for (Entity child : children) {
        child._update();
      }
    }
  }

  /** Called by the parent {@link Entity} to render the entity */
  void _render() {
    if (state == State.ACTIVE) {
      render();

      for (Component component : components) {
        component._render();
      }

      for (Entity child : children) {
        child._render();
      }
    }
  }

  /** Called <b>once</b> by the parent {@link Entity} to dispose the entity */
  void _dispose() {
    for (Entity child : children)
      child._dispose();

    for (Component component : components) {
      component._dispose();
    }

    dispose();

    parent.children.remove(this);
    parent = null;
    state = State.DEAD;
  }

  /** Constants for an {@link Entity} state */
  public enum State {
    /** Represents a newly created instance of an {@link Entity} */
    NEW,
    /**
     * Represents an {@link Entity} which has been added to another entity or the scene.
     * Only entities in this state can run update/render lifecycle methods
     */
    ACTIVE,
    /**
     * Represents an {@link Entity} which was previously a child of another entity or the scene,
     * but has been removed. The entity is not destroyed, but doesn't run any lifecycle methods
     */
    INACTIVE,
    /** Represents an {@link Entity} which has been marked for destruction at the end of the current frame */
    DOOMED,
    /** Represents an {@link Entity} which has been destroyed */
    DEAD
  }
}
