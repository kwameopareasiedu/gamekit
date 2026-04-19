package dev.gamekit.components;

import dev.gamekit.core.Component;
import dev.gamekit.core.Entity;
import dev.gamekit.core.Physics;
import dev.gamekit.core.Renderer;
import dev.gamekit.utils.Vector;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RigidBody} enables physics-based motion for the entity.
 * <p>
 * During its {@link #update} phase and after a physics simulation step is complete, this component will update the
 * attached entity's {@link Transform} component's position and rotation
 * <p>
 * <strong>NB</strong>: Once a {@link RigidBody} is added to an entity, position and rotation changes
 * <strong>must</strong> be done with the {@link RigidBody} component and not directly on the {@link Transform}
 * component
 */
public class RigidBody extends Component {
  public static boolean DEBUG = false;
  private static final Vector VEC_BUFFER = new Vector();
  private static final Vector2 VEC2_BUFFER = new Vector2();

  private final RefBody body;

  /** Creates a {@link RigidBody} with infinite mass (I.e. static object) */
  public RigidBody() {
    body = new RefBody(this);
    body.setMassType(MassType.INFINITE);
  }

  /** Creates a {@link RigidBody} with a defined mass and inertia (I.e. dynamic object) */
  public RigidBody(MassType massType, Vector massCenter, double mass, double inertia) {
    body = new RefBody(this);
    body.setMassType(massType);
    body.setMass(new Mass(new Vector2(massCenter.x, massCenter.y), mass, inertia));
  }

  /** Sets a custom object with user-defined attributes as the metadata */
  public void setMetaData(Object metadata) {
    body.setUserData(metadata);
  }

  /**
   * Sets the world gravity multiplier on this {@link RigidBody}
   *
   * @see org.dyn4j.dynamics.PhysicsBody#setGravityScale(double)
   */
  public void setGravityScale(double scale) {
    body.setGravityScale(scale);
  }

  /** Sets the world position of this {@link RigidBody} */
  public void setPosition(double x, double y) {
    body.getTransform().setTranslation(x / Physics.PIXELS_PER_METER, y / Physics.PIXELS_PER_METER);
  }

  /** Sets the global rotation (radian) of this {@link RigidBody} about its center */
  public void setRotation(double rad) {
    body.getTransform().setRotation(-rad);
  }

  /**
   * Sets the global rotation (radian) of this {@link RigidBody} about a point {@code (px, py)}
   * <p>
   * Rotating about a non-center point changes the position, so the starting position {@code (sx, sy)} is required
   */
  public void setRotation(double rad, double px, double py, double sx, double sy) {
    body.getTransform().setRotation(0);
    body.getTransform().setTranslation(sx / Physics.PIXELS_PER_METER, sy / Physics.PIXELS_PER_METER);
    body.rotate(-rad, px / Physics.PIXELS_PER_METER, py / Physics.PIXELS_PER_METER);
  }

  /**
   * Sets the linear velocity of this {@link RigidBody}
   *
   * @see org.dyn4j.dynamics.PhysicsBody#setLinearVelocity(double, double)
   */
  public void setLinearVelocity(double x, double y) {
    body.setAtRest(false);
    body.setLinearVelocity(x, y);
  }

  /**
   * Returns the linear velocity of this {@link RigidBody}
   * <p>
   * <i>NB: For added performance, the returned {@link Vector} is reused across multiple invocations, so you should
   * not keep a reference to it. Rather, retrieve the x and y values and store them if you need to</i>
   */
  public Vector getLinearVelocity() {
    Vector2 vel = body.getLinearVelocity();
    VEC_BUFFER.set(vel.x, vel.y);
    return VEC_BUFFER;
  }

  /**
   * Applies a linear force vector to this {@link RigidBody}
   *
   * @see org.dyn4j.dynamics.PhysicsBody#applyForce(Vector2)
   */
  public void applyForce(double x, double y) {
    body.applyForce(new Vector2(x, y));
  }

  /**
   * Applies a linear impulse vector to this {@link RigidBody}
   *
   * @see org.dyn4j.dynamics.PhysicsBody#applyImpulse(Vector2)
   */
  public void applyImpulse(double x, double y) {
    body.applyImpulse(new Vector2(x, y));
  }

  /**
   * Applies a torque about the center of this {@link RigidBody}
   *
   * @see org.dyn4j.dynamics.PhysicsBody#applyTorque(double)
   */
  public void applyTorque(double torque) {
    body.applyTorque(torque);
  }

  /**
   * Determines if a {@link Vector point} is contained in at least one of the {@link Collider colliders} associated
   * with this {@link RigidBody}
   *
   * @see org.dyn4j.dynamics.PhysicsBody#contains(Vector2)
   */
  public boolean containsPoint(Vector point) {
    VEC2_BUFFER.set(point.x / Physics.PIXELS_PER_METER, point.y / Physics.PIXELS_PER_METER);
    return body.contains(VEC2_BUFFER);
  }

  /**
   * Determines if a {@link Vector point} is contained in at least one of the {@link Collider colliders} associated
   * with this {@link RigidBody}, which matches the specified category mask
   *
   * @see org.dyn4j.dynamics.PhysicsBody#contains(Vector2)
   */
  public boolean containsPoint(Vector point, int categories) {
    VEC2_BUFFER.set(point.x / Physics.PIXELS_PER_METER, point.y / Physics.PIXELS_PER_METER);
    List<BodyFixture> fixtures = body.getFixtures();

    for (BodyFixture fx : fixtures) {
      if (
        fx.getShape().contains(VEC2_BUFFER, body.getTransform()) &&
          fx.getFilter() instanceof CategoryFilter fixtureFilter &&
          (categories & fixtureFilter.getCategory()) == fixtureFilter.getCategory()) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void validate(Entity entity, List<Component> components) {
    for (Component component : components) {
      if (component instanceof RigidBody) {
        // Ensure only one RigidBody on the entity
        if (component != this)
          throw new IllegalArgumentException("Entity cannot have more than one RigidBody component");

        // Ensure no entity ancestor has a RigidBody component
        Entity parentEntity = entity.getParent();
        String descendantClassName = entity.getClass().getName();

        while (parentEntity != null) {
          if (parentEntity.findComponent(RigidBody.class) != null) {
            String ancestorClassName = parentEntity.getClass().getName();

            throw new IllegalArgumentException(
              String.format(
                "Entities with a RigidBody component [%s] cannot have descendants [%s] which also have a RigidBody " +
                  "component",
                ancestorClassName, descendantClassName
              )
            );
          }

          parentEntity = parentEntity.getParent();
        }
      }
    }
  }

  @Override
  protected void start() {
    // Find all colliders and add their fixtures to the body
    List<Collider> colliders = new ArrayList<>();

    entity.findComponents(Collider.class, colliders);

    for (Collider collider : colliders)
      body.addFixture(collider.fixture);

    double initialX = body.getTransform().getTranslationX();
    double initialY = body.getTransform().getTranslationY();
    double initialRotation = body.getTransform().getRotationAngle();

    body.updateMass();
    body.getTransform().setTranslation(initialX, initialY);
    body.getTransform().setRotation(initialRotation);

    Physics.addBody(body);

    syncPositionAndRotation();
  }

  @Override
  protected void resume() {
    Physics.addBody(body);

    syncPositionAndRotation();
  }

  @Override
  protected void update() {
    syncPositionAndRotation();
  }

  @Override
  protected void render() {
    if (DEBUG) {
      Renderer.onLayer(Renderer.LAYER_COUNT - 1, () -> {
        Transform tx = entity.findComponent(Transform.class);
        Vector globalPosition = tx.getGlobalPosition();
        Renderer.fillCircle((int) globalPosition.x, (int) globalPosition.y, 3).withColor(Color.RED);
      });
    }
  }

  @Override
  protected void stop() {
    Physics.removeBody(body);
  }

  @Override
  protected void dispose() {
    Physics.removeBody(body);
  }

  /**
   * Synchronizes the {@link Transform} component global position and rotation with position and rotation of the
   * physics body
   */
  private void syncPositionAndRotation() {
    Transform tx = entity.findComponent(Transform.class);
    double bodyX = body.getTransform().getTranslationX();
    double bodyY = body.getTransform().getTranslationY();
    tx.setGlobalPosition(bodyX * Physics.PIXELS_PER_METER, bodyY * Physics.PIXELS_PER_METER);
    tx.setGlobalRotation(-body.getTransform().getRotationAngle());
  }

  /** {@link RefBody} extends {@link Body} adding a reference to its parent {@link RigidBody} */
  public static class RefBody extends Body {
    private final RigidBody rigidBody;

    public RefBody(RigidBody rigidBody) {
      this.rigidBody = rigidBody;
    }

    public RigidBody getRigidBody() {
      return rigidBody;
    }
  }
}
