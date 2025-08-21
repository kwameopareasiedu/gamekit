package dev.gamekit.core;

import dev.gamekit.components.Transform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Entity} represents objects that exist in the game world. An entity can also contain and
 * manage the lifecycles of children entities.
 * <p>
 * An {@link Entity} has lifecycle methods which are called by the engine to set up, update,
 * render and dispose themselves.
 */
@SuppressWarnings("unchecked")
public abstract class Entity {
  protected final String name;
  protected final Logger logger = LogManager.getLogger(getClass());
  protected final ArrayList<Entity> children;
  protected final ArrayList<Component> components;
  protected Entity parent;

  public Entity(String name) {
    this.name = name;
    children = new ArrayList<>();
    components = new ArrayList<>();
    components.add(new Transform());
  }

  public Entity getParent() {
    return parent;
  }

  /**
   * Adds a child to this entity, if it isn't already.
   * <p>
   * This also invokes the child's {@link Entity#_start} method
   */
  public void addChild(Entity child) {
    if (child.parent != null)
      throw new IllegalStateException("Child already has parent");

    if (!children.contains(child)) {
      logger.debug("Adding {} to {}", child.name, name);

      Application.getInstance().runLater(() -> {
        children.add(child);
        child.setParent(this);
        child._start();
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
        children.remove(child);
        child.setParent(null);
        child._dispose();
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

  /** Called during {@link #start} to get the components of the entity */
  protected List<Component> getComponents() {
    return null;
  }

  /** Called to set up the entity */
  protected void start() { }

  /** Called to update the entity */
  protected void update() { }

  /** Called to render the entity */
  protected void render() { }

  /** Called to dispose the entity */
  protected void dispose() { }

  void setParent(Entity parent) {
    this.parent = parent;
  }

  /** Called <b>once</b> by the parent {@link Entity} to initialize the entity */
  void _start() {
    List<Component> newComponents = getComponents();

    if (newComponents != null) {
      for (Component component : newComponents)
        component._validate(this, newComponents);

      this.components.addAll(newComponents);
    }

    for (Component component : this.components)
      component._start(this);

    start();
  }

  /** Called by the parent {@link Entity} to update the entity */
  void _update() {
    components.forEach(Component::_update);
    update();
    children.forEach(Entity::_update);
  }

  /** Called by the parent {@link Entity} to render the entity */
  void _render() {
    render();
    components.forEach(Component::_render);
    children.forEach(Entity::_render);
  }

  /** Called <b>once</b> by the parent {@link Entity} to dispose the entity */
  void _dispose() {
    children.forEach(Entity::_dispose);
    components.forEach(Component::_dispose);
    dispose();
    parent = null;
  }
}
