package dev.gamekit.core;

import dev.gamekit.ui.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * A representation of a logical part of your application.
 * This can be a main menu, testing area or a level within your game.
 * <p>
 * A scene can contain {@link Prop props} which interact with each other
 * to implement the goal of the scene.
 */
public abstract class Scene {
  private static final Logger LOGGER = LogManager.getLogger();
  static Scene current;

  protected final String name;
  protected final Map<Integer, Prop> props;
  protected Node rootNode;

  /**
   * Creates a scene with the given name
   * @param name The name of the scene for logging purposes
   */
  public Scene(String name) {
    this.name = name;
    props = new HashMap<>();
  }

  /**
   * Returns the currently loaded scene instance
   * @return {@link Scene} The active scene of the application
   */
  public static Scene getCurrent() { return current; }

  /**
   * Returns the name of the scene
   * @return The name of the scene
   */
  public String getName() { return name; }

  /**
   * Adds a {@link Prop} object to this scene
   * @param prop The prop to be added
   */
  public void addChild(Prop prop) {
    LOGGER.debug("Adding child: [{} - {}]", prop.internalId, prop.name);

    if (!props.containsKey(prop.internalId)) {
      Application.getInstance().scheduleTask(() -> {
        props.put(prop.internalId, prop);

        if (!prop.ready) prop.onStart();

        LOGGER.debug("Added child: [{} - {}]", prop.internalId, prop.name);
      });
    }
  }

  /**
   * Removes a {@link Prop} object from this scene
   * @param prop The prop to be removed
   */
  public void removeChild(Prop prop) {
    LOGGER.debug("Removing child: [{} - {}]", prop.internalId, prop.name);

    if (props.containsKey(prop.internalId)) {
      Application.getInstance().scheduleTask(() -> {
        props.remove(prop.internalId, prop);

        if (prop.ready) prop.onDispose();

        LOGGER.debug("Removed child: [{} - {}]", prop.internalId, prop.name);
      });
    }
  }

  /** Overridable method to add scene setup logic. This is called once. */
  public void onStart() { }

  /** Overridable method to add scene update logic */
  public void onUpdate() { }

  /** Overridable method to add scene render logic */
  public void onRender() { }

  /** Overridable method to add scene update logic This is called once. */
  public void onDispose() { }

  /**
   * Called by {@link Application} to initialize the scene.
   * <p>
   * This calls {@link #onStart()} before calling {@link Prop#onStart() onStart()} on each child prop
   */
  void onApplicationStart() {
    LOGGER.debug("Starting scene");
    onStart();
    props.forEach((k, v) -> v.onStart());
  }

  /**
   * Called by {@link Application} to update the scene.
   * <p>
   * This calls {@link #onUpdate()} before calling {@link Prop#onUpdate() onUpdate()} on each child prop
   */
  void onApplicationUpdate() {
    onUpdate();
    props.forEach((k, v) -> v.onUpdate());
    if (rootNode != null) rootNode.onUpdate();
  }

  /**
   * Called by {@link Application} to render the scene.
   * <p>
   * This calls {@link #onRender()} before calling {@link Prop#onRender() onRender()} on each child prop
   */
  void onApplicationRender() {
    onRender();
    props.forEach((k, v) -> v.onRender());
    if (rootNode != null) rootNode.onRender();
  }

  /**
   * Called by {@link Application} to render the scene.
   * <p>
   * This calls {@link Prop#onDispose() onDispose()} on each child prop before calling {@link #onDispose()}
   */
  void onApplicationDispose() {
    LOGGER.debug("Disposing scene");
    props.forEach((k, v) -> v.onDispose());
    onDispose();
  }
}
