package dev.gamekit.core;

import dev.gamekit.components.Collider;
import dev.gamekit.components.RigidBody;
import dev.gamekit.utils.Math;
import dev.gamekit.utils.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.DetectFilter;
import org.dyn4j.world.ManifoldCollisionData;
import org.dyn4j.world.World;
import org.dyn4j.world.listener.CollisionListenerAdapter;
import org.dyn4j.world.result.RaycastResult;

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
  private static final List<RaycastHit> RAYCAST_RESULTS = new ArrayList<>();

  static {
    WORLD.addCollisionListener(new CollisionListenerAdapter<>() {
      @Override
      public boolean collision(ManifoldCollisionData<Body, BodyFixture> collision) {
        if (COLLISION_LISTENER_MAP.isEmpty()) return true;

        Collider.Fixture fx1 = (Collider.Fixture) collision.getFixture1();
        Collider.Fixture fx2 = (Collider.Fixture) collision.getFixture2();

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

  /** Adds a {@link RigidBody.RefBody} to the physics world for simulation */
  public static void addBody(RigidBody.RefBody body) {
    synchronized (NEW_BODIES) {
      NEW_BODIES.add(body);
    }
  }

  /** Removes a {@link RigidBody.RefBody} from the physics world */
  public static void removeBody(RigidBody.RefBody body) {
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

  /**
   * Projects a ray from one point to another point and returns a list of {@link RaycastHit} items which contain
   * information on the hit point, hit normal and the {@link Collider colliders} that were hit
   */
  public static List<RaycastHit> raycast(Vector start, Vector end, long categories) {
    double angle = Vector.angle(start, end);
    double distance = Vector.distance(start, end);

    return raycast(start, angle, distance, categories);
  }

  /**
   * Projects a fixed-length ray from starting position with the specified angle (in radian) and returns a list of
   * {@link RaycastHit} items which contain information on the hit point and the {@link Collider} and {@link RigidBody}
   * that were hit
   */
  public static List<RaycastHit> raycast(
    Vector start, double angle, double distance, long categories
  ) {
    RAYCAST_RESULTS.clear();

    List<RaycastResult<Body, BodyFixture>> results = WORLD.raycast(
      new Ray(
        new Vector2(start.x / PIXELS_PER_METER, -start.y / PIXELS_PER_METER),
        angle - Math.HALF_PI
      ),
      distance / PIXELS_PER_METER,
      new DetectFilter<>(false, true, filter -> {
        if (filter instanceof CategoryFilter categoryFilter) {
          long cat = categoryFilter.getCategory();
          return (categories & cat) == cat;
        }
        return true;
      })
    );

    for (RaycastResult<Body, BodyFixture> result : results) {
      Vector2 p = result.getRaycast().getPoint();
      Vector2 n = result.getRaycast().getNormal();

      RAYCAST_RESULTS.add(
        new RaycastHit(
          new Vector(p.x * PIXELS_PER_METER, -p.y * PIXELS_PER_METER),
          new Vector(n.x * PIXELS_PER_METER, -n.y * PIXELS_PER_METER),
          ((RigidBody.RefBody) result.getBody()).getRigidBody(),
          ((Collider.Fixture) result.getFixture()).getCollider()
        )
      );
    }

    return RAYCAST_RESULTS;
  }

  /**
   * {@link CollisionListener} is a listener class which can be attached to the physics engine via {@link Collider}
   * components, to be notified of collisions.
   * <p>
   * It allows the consumer to be notified of collisions that just occurred, ongoing collisions and collissions that
   * have just stopped.
   */
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
    void handleCollision(Collider.Fixture otherFixture) {
      if (!prevColliderMap.containsKey(otherFixture.id)) {
        onCollisionEnter(otherFixture.getCollider());
      } else {
        onCollisionStay(otherFixture.getCollider());
      }

      currentColliderMap.put(otherFixture.id, otherFixture.getCollider());
    }

    /** Called with the information of the other {@link Collider} component when a collision has just started */
    public void onCollisionEnter(Collider otherCollider) { /* No-op */ }

    /** Called with the information of the other {@link Collider} component when a collision is still ongoing */
    public void onCollisionStay(Collider otherCollider) { /* No-op */ }

    /** Called with the information of the other {@link Collider} component when a collision has just stopped */
    public void onCollisionExit(Collider otherCollider) { /* No-op */ }
  }

  /** {@link RaycastHit} contains information of a {@link Physics#raycast} operation */
  public record RaycastHit(Vector point, Vector normal, RigidBody body, Collider collider) { }
}
