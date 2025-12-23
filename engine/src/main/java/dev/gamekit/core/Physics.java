package dev.gamekit.core;

import dev.gamekit.components.Collider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.world.ManifoldCollisionData;
import org.dyn4j.world.World;
import org.dyn4j.world.listener.CollisionListenerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** {@link Physics} handles updates of the Dyn4J {@link World} object */
public final class Physics {
  public static final double PIXELS_PER_METER = 128.0;

  private static final World<Body> WORLD = new World<>();
  private static final HashMap<String, CollisionListener> COLLISION_LISTENER_MAP = new HashMap<>();
  private static final Logger LOGGER = LogManager.getLogger(Physics.class);
  private static final List<Body> NEW_BODIES = new ArrayList<>();
  private static final List<Body> DESTROYED_BODIES = new ArrayList<>();

  static {
    WORLD.addCollisionListener(new CollisionListenerAdapter<>() {
      @Override
      public boolean collision(ManifoldCollisionData<Body, BodyFixture> collision) {
        if (COLLISION_LISTENER_MAP.isEmpty()) return true;

        Collider.ColliderFixture fx1 = (Collider.ColliderFixture) collision.getFixture1();
        Collider.ColliderFixture fx2 = (Collider.ColliderFixture) collision.getFixture2();

        if (COLLISION_LISTENER_MAP.containsKey(fx1.id)) {
          CollisionListener listener = COLLISION_LISTENER_MAP.get(fx1.id);
          listener.handleCollision(fx2);
        }

        if (COLLISION_LISTENER_MAP.containsKey(fx2.id)) {
          CollisionListener listener = COLLISION_LISTENER_MAP.get(fx2.id);
          listener.handleCollision(fx1);
        }

        return true;
      }
    });
  }

  private Physics() { }

  /**
   * Performs a simulation step of the physics world
   *
   * @see org.dyn4j.world.AbstractPhysicsWorld#update(double)
   */
  static void update() {
    double elapsedTime = Application.FRAME_INTERVAL_MS / 1000.0;

    if (WORLD.update(elapsedTime)) {
      COLLISION_LISTENER_MAP.forEach((body, listener) -> listener.update());

      synchronized (NEW_BODIES) {
        for (Body body : NEW_BODIES)
          WORLD.addBody(body);

        NEW_BODIES.clear();
      }

      synchronized (DESTROYED_BODIES) {
        for (Body body : DESTROYED_BODIES)
          try { WORLD.removeBody(body); } //
          catch (Exception ignored) { }

        DESTROYED_BODIES.clear();
      }
    }
  }

  /** Adds a {@link Body} to the physics world for simulation */
  public static void addBody(Body body) {
    synchronized (NEW_BODIES) {
      NEW_BODIES.add(body);
    }
  }

  /** Removes a {@link Body} from the physics world */
  public static void removeBody(Body body) {
    synchronized (DESTROYED_BODIES) {
      DESTROYED_BODIES.add(body);
    }
  }

  /** Registers a collision listener with the specified id, replacing any existing listener */
  public static void addCollisionListener(String id, CollisionListener listener) {
    COLLISION_LISTENER_MAP.put(id, listener);
  }

  /** Removes a collision listener for the specified {@link Body} */
  public static void removeCollisionListener(String id, CollisionListener listener) {
    COLLISION_LISTENER_MAP.remove(id, listener);
  }

  /** Abstract interface for handling physics collisions */
  public static abstract class CollisionListener {
    private final HashMap<String, Collider> prevColliderMap = new HashMap<>();
    private final HashMap<String, Collider> currentColliderMap = new HashMap<>();

    void update() {
      for (String id : prevColliderMap.keySet()) {
        if (!currentColliderMap.containsKey(id)) {
          onCollisionExit(prevColliderMap.get(id));
        }
      }

      prevColliderMap.clear();
      prevColliderMap.putAll(currentColliderMap);
      currentColliderMap.clear();
    }

    /** Called when a collision occurs and is passed the bodies and fixtures of the collision */
    void handleCollision(Collider.ColliderFixture otherFixture) {
      if (!prevColliderMap.containsKey(otherFixture.id)) {
        onCollisionEnter(otherFixture.getCollider());
      } else {
        onCollisionStay(otherFixture.getCollider());
      }

      currentColliderMap.put(otherFixture.id, otherFixture.getCollider());
    }

    public void onCollisionEnter(Collider otherCollider) { /* No-op */ }

    public void onCollisionStay(Collider otherCollider) { /* No-op */ }

    public void onCollisionExit(Collider otherCollider) { /* No-op */ }
  }
}
