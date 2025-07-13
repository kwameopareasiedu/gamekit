package dev.gamekit.core;

import dev.gamekit.utils.Constants;
import dev.gamekit.utils.Task;
import org.dyn4j.dynamics.Body;
import org.dyn4j.world.World;

/** {@link Physics} handles updates of the {@link World} object on the main application thread */
public final class Physics {
  private static final World<Body> WORLD;

  static {
    WORLD = new World<>();
  }

  private Physics() { }

  static void update() {
    double elapsedTime = Constants.FRAME_TIME_MS / 1000.0;
    WORLD.update(elapsedTime);
  }

  /** Adds a {@link Body} to the physics world for simulation */
  public static void addBody(Body body) {
    WORLD.addBody(body);
  }

  /**
   * Removes a {@link Body} from the physics world. This should be called in a scheduled task
   * via {@link Application#scheduleTask(Task)} to ensure removal at the end of an update frame
   */
  public static void removeBody(Body body) {
    WORLD.removeBody(body);
  }
}
