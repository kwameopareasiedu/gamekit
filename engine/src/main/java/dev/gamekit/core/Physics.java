package dev.gamekit.core;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.world.ManifoldCollisionData;
import org.dyn4j.world.World;
import org.dyn4j.world.listener.CollisionListenerAdapter;

import java.util.HashMap;

/** {@link Physics} handles updates of the {@link World} object on the main application thread */
public final class Physics {
  private static final World<Body> WORLD;
  private static final HashMap<Body, CollisionListener> COLLISION_LISTENER_MAP;

  static {
    WORLD = new World<>();
    COLLISION_LISTENER_MAP = new HashMap<>();

    WORLD.addCollisionListener(new CollisionListenerAdapter<>() {
      @Override
      public boolean collision(ManifoldCollisionData<Body, BodyFixture> collision) {
        if (COLLISION_LISTENER_MAP.isEmpty())
          return true;

        Body body1 = collision.getBody1();
        Body body2 = collision.getBody2();
        BodyFixture fixture1 = collision.getFixture1();
        BodyFixture fixture2 = collision.getFixture2();

        if (COLLISION_LISTENER_MAP.containsKey(body1)) {
          CollisionListener listener = COLLISION_LISTENER_MAP.get(body1);
          listener.onCollision(body1, fixture1, body2, fixture2);
        }

        if (COLLISION_LISTENER_MAP.containsKey(body2)) {
          CollisionListener listener = COLLISION_LISTENER_MAP.get(body2);
          listener.onCollision(body2, fixture2, body1, fixture1);
        }

        return true;
      }
    });
  }

  private Physics() { }

  /**
   * Performs a simulation step of the physics world
   * @see org.dyn4j.world.AbstractPhysicsWorld#update(double)
   */
  static void update() {
    double elapsedTime = Constants.FRAME_TIME_MS / 1000.0;
    WORLD.update(elapsedTime);
  }

  /** Adds a {@link Body} to the physics world for simulation */
  public static void addBody(Body body) {
    WORLD.addBody(body);
  }

  /** Removes a {@link Body} from the physics world */
  public static void removeBody(Body body) {
    COLLISION_LISTENER_MAP.remove(body);
    WORLD.removeBody(body);
  }

  /** Registers a collision listener for the specified {@link Body} */
  public static void addCollisionListener(Body body, CollisionListener listener) {
    COLLISION_LISTENER_MAP.put(body, listener);
  }

  /** Removes a collision listener for the specified {@link Body} */
  public static void removeCollisionListener(Body body, CollisionListener listener) {
    COLLISION_LISTENER_MAP.remove(body, listener);
  }

  /** Callback interface for physics world collisions */
  public interface CollisionListener {
    /** Called when a collision occurs and is passed the bodies and fixtures of the collision */
    void onCollision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2);
  }
}
