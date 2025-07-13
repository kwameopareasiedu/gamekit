package dev.gamekit.core;

import dev.gamekit.traits.Transform;
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
public abstract class Entity {
  protected final String name;
  protected final Logger logger = LogManager.getLogger(getClass());
  protected final ArrayList<Entity> children;
  protected final ArrayList<Trait> traits;
  protected Entity parent;

  public Entity(String name) {
    this.name = name;
    children = new ArrayList<>();
    traits = new ArrayList<>();
    traits.add(new Transform());
  }

  public Entity getParent() {
    return parent;
  }

  public void addChild(Entity child) {
    if (child.parent != null)
      throw new IllegalStateException("Child already has parent");

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

  /** Returns a {@link Trait} of the specified class else {@code null} */
  public <T extends Trait> T findTrait(Class<T> clazz) {
    for (Trait trait : traits) {
      if (clazz.isInstance(trait))
        //noinspection unchecked
        return (T) trait;
    }

    return null;
  }

  /** Called during {@link #start()} to get the traits of the entity */
  protected List<Trait> setTraits() {
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
    List<Trait> traits = setTraits();

    if (traits != null) {
      for (Trait trait : traits) {
        if (this.traits.stream().anyMatch(t -> t.getClass().isInstance(trait)))
          throw new IllegalArgumentException("Entity cannot have more than one type of a Trait");

        this.traits.add(trait);
      }

      for (Trait trait : this.traits)
        trait._start(this);
    }

    start();
  }

  /** Called by the parent {@link Entity} to update the entity */
  void _update() {
    traits.forEach(Trait::_update);
    update();
    children.forEach(Entity::_update);
  }

  /** Called by the parent {@link Entity} to render the entity */
  void _render() {
    traits.forEach(Trait::_render);
    render();
    children.forEach(Entity::_render);
  }

  /** Called <b>once</b> by the parent {@link Entity} to dispose the entity */
  void _dispose() {
    children.forEach(Entity::_dispose);
    traits.forEach(Trait::_dispose);
    dispose();
    parent = null;
  }
}
