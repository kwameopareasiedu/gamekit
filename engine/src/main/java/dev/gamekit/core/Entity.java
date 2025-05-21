package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * {@link Entity} represent game objects in a {@link Scene}.
 * <p>
 * Like {@link Scene}, {@link Entity} has lifecycle methods which are called by the engine to
 * set up, update, render and dispose.
 */
public abstract class Entity<T extends Entity.State<T>> {
  protected final Logger logger = LogManager.getLogger(getClass());
  protected final ArrayList<Entity<?>> children;
  protected Entity<?> parent;

  protected final String name;

  protected T updateState;
  protected T renderState;
  protected T bufferState;

  public Entity(String name) {
    this.name = name;
    children = new ArrayList<>();
  }

  public void addChild(Entity<?> child) {
    if (!children.contains(child)) {
      logger.debug("Adding {} to {}", child.name, name);

      Application.getInstance().scheduleTask(() -> {
        children.add(child);
        child.setParent(this);
        child._start();
      });
    }
  }

  public void removeChild(Entity<?> child) {
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
  protected void start(T state) { }

  /** Called to update the entity */
  protected void update(T state) { }

  /** Called to render the entity */
  protected void render(T state) { }

  /** Called to dispose the entity */
  protected void dispose() { }

  /** Called to create the {@link State} of the entity */
  protected abstract T createState();

  void setParent(Entity<?> parent) {
    this.parent = parent;
  }

  /** Called <b>once</b> by the parent {@link Entity} to initialize the entity */
  void _start() {
    updateState = createState();
    renderState = createState();
    bufferState = createState();

    if (updateState == null) {
      throw new RuntimeException(
        "Scene.createState() must return a non-null state object"
      );
    }

    start(updateState);
  }

  /** Called by the parent {@link Entity} to update the entity */
  void _update() {
    update(updateState);
    children.forEach(Entity::_update);
    swapUpdateState();
  }

  /** Called by the parent {@link Entity} to render the entity */
  void _render() {
    render(renderState);
    children.forEach(Entity::_render);
    swapRenderState();
  }

  /** Called <b>once</b> by the parent {@link Entity} to dispose the entity */
  void _dispose() {
    dispose();
    children.forEach(Entity::_dispose);
    parent = null;
  }

  private void swapUpdateState() {
    synchronized (this) {
      T tempState = updateState;
      updateState = bufferState;
      bufferState = tempState;

      updateState.copy(bufferState);
    }
  }

  private void swapRenderState() {
    synchronized (this) {
      T tempState = renderState;
      renderState = bufferState;
      bufferState = tempState;

      renderState.copy(updateState);
    }
  }

  /**
   * {@link State} is a container for {@link Entity} related data that would have otherwise been
   * declared as instance variables in the entity. This forms a foundation for true update/render
   * multi-threading.
   * <p>
   * GameKit uses a triple buffering approach for multi-threading which doesn't support the use
   * of shared instance variables on an object. Instead, it creates three (3) state objects and
   * shares them between {@link Entity#update(State)} and {@link Entity#render(State)} running on
   * separate threads.
   * <p>
   * During update, the engine passes a state instance to {@link Entity#update(State)}. Reads and
   * writes that would have otherwise involved instance variables should be done on the supplied
   * state instance.
   * <p>
   * During render, the updated state is passed to {@link Entity#render(State)}. The values of the
   * updated state should be used for rendering.
   * @see <a href="https://developer.arm.com/documentation/ka005284/latest/">Triple Buffering</a>
   */
  public static abstract class State<T extends State<T>> {
    /** Called to copy {@code state} to this {@link State} */
    public abstract void copy(T state);
  }

  public static final class EmptyState extends State<EmptyState> {
    @Override
    public void copy(EmptyState state) { }
  }
}
