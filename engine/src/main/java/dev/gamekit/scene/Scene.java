package dev.gamekit.scene;

import dev.gamekit.core.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * A representation of a logical part of your application.
 * This can be a main menu, testing area or a level within your game.
 * <p>
 * A scene can contain {@link Prop Props} which interact with each other
 * to implement the goal of the scene.
 */
public abstract class Scene {
  private static final Logger LOGGER = LogManager.getLogger();
  private static Scene active;

  protected final String name;

  private final Map<Integer, Prop> props;

  /**
   * Create a scene with the given name
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
  public static Scene getActive() { return active; }

  /**
   * Sets the currently active scene instance. This is called internally by the application
   * @param scene The active scene
   */
  public static void setActive(Scene scene) { active = scene; }

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
      Application.getInstance().runOnFrameEnd(() -> {
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
      Application.getInstance().runOnFrameEnd(() -> {
        props.remove(prop.internalId, prop);

        if (prop.ready) prop.onDispose();

        LOGGER.debug("Removed child: [{} - {}]", prop.internalId, prop.name);
      });
    }
  }

  /**
   * Called by the application to initialize the scene.
   * It can be overridden to implement custom setup.
   * <p>
   * <b>Remember to call {@code super.onStart()}</b>
   */
  public void onStart() {
    LOGGER.debug("Starting scene");
    props.forEach((k, v) -> v.onStart());
  }

  /**
   * Called by the application's game loop to update the scene.
   * It can be overridden to implement custom update logic.
   * <p>
   * <b>Remember to call {@code super.onUpdate()}</b>
   */
  public void onUpdate() {
    props.forEach((k, v) -> v.onUpdate());
  }

  /**
   * Called by the application game loop to render the scene.
   * It can be overridden to implement custom render logic.
   * <p>
   * <blockquote>Use {@link dev.gamekit.core.Renderer Renderer} methods to draw to the window</blockquote>
   * <p>
   * <b>Remember to call {@code super.onRender()}</b>
   */
  public void onRender() {
    props.forEach((k, v) -> v.onRender());
  }

  /**
   * Called by the application to dispose the scene
   * It can be overridden to implement custom dispose logic.
   * <p>
   * <b>Remember to call {@code super.onDispose()}</b>
   */
  public void onDispose() {
    LOGGER.debug("Disposing scene");
    props.forEach((k, v) -> v.onDispose());
  }
}
