package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * While an {@link Entity} can have all functionality written entirely in its own class, it's usually a good idea to
 * separate independent functionality from each other, and this is where components come in.
 * <p>
 * {@link Component} represents predefined behaviours that can then be attached to {@link Entity entities}.
 * These behaviors include, but are not limited to, physics, lighting and network components.
 */
public abstract class Component {
  static final Component.Filter<Component> TRUTHY_FILTER = (ignored) -> true;

  protected final Logger logger = LogManager.getLogger(getClass());

  /** The host {@link Entity entity} the component is attached to */
  protected Entity entity;

  /** Returns the {@link Entity} of this component */
  public Entity getEntity() {
    return entity;
  }

  /**
   * Called before {@link #start} to run validation logic on the components.
   * <p>
   * This method should throw an exception with an appropriate description if validation failed and <b>must not</b>
   * modify the provided component list.
   */
  public void validate(Entity entity, List<Component> components) { /* No-op */ }

  /**
   * Called when attached to an {@link Entity} to set up the component
   * <p>
   * NB: <i>The value of {@link #entity} is set before this method is called and can safely be accessed here.</i>
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

  /**
   * Called <b>once</b> by the host {@link Entity} before {@link #_start} with the list of new components from
   * {@link Entity#getComponents} for validation before adding the components to the entity.
   * <p>
   * If validation logic fails, this method should throw an exception with an appropriate description
   */
  void _validate(Entity entity, List<Component> components) {
    validate(entity, components);
  }

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
  }

  /** A SAM interface for a component filter */
  public interface Filter<T extends Component> {
    /** Returns {@code true} if the provided component passes a filter function */
    boolean filter(T component);
  }
}
