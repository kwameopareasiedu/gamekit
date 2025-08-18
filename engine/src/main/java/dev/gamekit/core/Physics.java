package dev.gamekit.core;

import dev.gamekit.components.Collider;
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
        BodyFixture fx1 = collision.getFixture1();
        BodyFixture fx2 = collision.getFixture2();

        if (COLLISION_LISTENER_MAP.containsKey(body1)) {
          CollisionListener listener = COLLISION_LISTENER_MAP.get(body1);
          listener.handleCollision(
            (Collider.BodyAttachedFixture) fx1,
            (Collider.BodyAttachedFixture) fx2
          );
        }

        if (COLLISION_LISTENER_MAP.containsKey(body2)) {
          CollisionListener listener = COLLISION_LISTENER_MAP.get(body2);
          listener.handleCollision(
            (Collider.BodyAttachedFixture) fx2,
            (Collider.BodyAttachedFixture) fx1
          );
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
    double elapsedTime = Constants.FRAME_INTERVAL_MS / 1000.0;
    WORLD.update(elapsedTime);

    COLLISION_LISTENER_MAP.forEach((body, listener) -> listener.update());
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

  /**
   * Registers a collision listener for the specified {@link Body}.
   * <p>
   * If a listener is already registered for the body, it will be replaced.
   */
  public static void addCollisionListener(Body body, CollisionListener listener) {
    COLLISION_LISTENER_MAP.put(body, listener);
  }

  /** Removes a collision listener for the specified {@link Body} */
  public static void removeCollisionListener(Body body, CollisionListener listener) {
    COLLISION_LISTENER_MAP.remove(body, listener);
  }

  /** Abstract interface for handling physics collisions */
  public static abstract class CollisionListener {
    private final HashMap<String, Collider.BodyAttachedFixture> prevFixtureMap
      = new HashMap<>();
    private final HashMap<String, Collider.BodyAttachedFixture> currentFixtureMap
      = new HashMap<>();

    void update() {
      for (String id : prevFixtureMap.keySet()) {
        if (!currentFixtureMap.containsKey(id)) {
          onCollisionExit(prevFixtureMap.get(id));
        }
      }

      prevFixtureMap.clear();
      prevFixtureMap.putAll(currentFixtureMap);
      currentFixtureMap.clear();
    }

    /** Called when a collision occurs and is passed the bodies and fixtures of the collision */
    void handleCollision(
      Collider.BodyAttachedFixture ignore, Collider.BodyAttachedFixture otherFixture
    ) {
      if (prevFixtureMap.containsKey(otherFixture.id)) {
        onCollisionEnter(otherFixture);
      } else onCollisionStay(otherFixture);

      currentFixtureMap.put(otherFixture.id, otherFixture);
    }

    public void onCollisionEnter(Collider.BodyAttachedFixture otherFixture) { }

    public void onCollisionStay(Collider.BodyAttachedFixture otherFixture) { }

    public void onCollisionExit(Collider.BodyAttachedFixture otherFixture) { }
  }
}
