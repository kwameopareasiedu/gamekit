package dev.gamekit.components;

import dev.gamekit.core.Component;
import dev.gamekit.core.Constants;
import dev.gamekit.core.Physics;
import dev.gamekit.core.Renderer;
import dev.gamekit.utils.Vector;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.util.List;

import static dev.gamekit.utils.Math.degToRad;
import static dev.gamekit.utils.Math.radToDeg;

/**
 * {@link RigidBody} enables physics-based motion for the entity.
 * <p>
 * During its {@link #update} phase and after a physics simulation step is complete, this
 * component will update the attached entity's {@link Transform} component's position and rotation
 * <p>
 * <strong>NB</strong>: Once a {@link RigidBody} is added to an entity, position and rotation
 * changes <strong>must</strong> be done with the {@link RigidBody} component and not directly
 * on the {@link Transform} component
 */
public class RigidBody extends Component {
  public static boolean DEBUG_DRAW = false;

  private final Body body;

  /** Creates a {@link RigidBody} with infinite mass (I.e. static object) */
  public RigidBody() {
    body = new Body();
    body.setMassType(MassType.INFINITE);
  }

  /** Creates a {@link RigidBody} with infinite mass (I.e. static object) */
  public RigidBody(MassType massType, Vector massCenter, double mass, double inertia) {
    body = new Body();
    body.setMassType(massType);
    body.setMass(new Mass(new Vector2(massCenter.x, massCenter.y), mass, inertia));
  }

  /** Sets the {@link RigidBody}'s metadata */
  public void setMetaData(Object metadata) {
    body.setUserData(metadata);
  }

  /**
   * Sets the world gravity multiplier on this {@link RigidBody}
   * @see org.dyn4j.dynamics.PhysicsBody#setGravityScale(double)
   */
  public void setGravityScale(double scale) {
    body.setGravityScale(scale);
  }

  /** Sets the world position of this {@link RigidBody} */
  public void setPosition(double x, double y) {
    body.getTransform().setTranslation(
      x / Constants.PIXELS_PER_METER,
      y / Constants.PIXELS_PER_METER
    );
  }

  /** Sets the global rotation of this {@link RigidBody} about its center */
  public void setRotation(double deg) {
    body.getTransform().setRotation(-degToRad(deg));
  }

  /**
   * Sets the global rotation of this {@link RigidBody} about a point {@code (rx, ry)}
   * <p>
   * Rotating about a non-center point changes the position, so the starting position
   * {@code (sx, sy)} is required
   */
  public void setRotation(double deg, double rx, double ry, double sx, double sy) {
    body.getTransform().setRotation(0);

    body.getTransform().setTranslation(
      sx / Constants.PIXELS_PER_METER,
      sy / Constants.PIXELS_PER_METER
    );

    body.rotate(-degToRad(deg),
      rx / Constants.PIXELS_PER_METER,
      ry / Constants.PIXELS_PER_METER
    );
  }

  /**
   * Applies a linear impulse vector to this {@link RigidBody}
   * @see org.dyn4j.dynamics.PhysicsBody#applyImpulse(Vector2)
   */
  public void applyImpulse(double x, double y) {
    body.applyImpulse(new Vector2(x, y));
  }

  /**
   * Applies a torque about the center of this {@link RigidBody}
   * @see org.dyn4j.dynamics.PhysicsBody#applyTorque(double)
   */
  public void applyTorque(double torque) {
    body.applyTorque(torque);
  }

  @Override
  protected void start() {
    // Find all colliders and add their fixtures to the body
    List<Collider> colliders = entity.findComponents(Collider.class);
    colliders.forEach(collider -> collider.fixture.addToBody(body));

    body.updateMass();

    Physics.addBody(body);

    // Register all non-null collider collision listeners
    colliders.forEach(collider -> {
      if (collider.collisionListener != null) {
        Physics.addCollisionListener(
          collider.fixture.id, collider.collisionListener
        );
      }
    });
  }

  @Override
  protected void update() {
    Transform tx = entity.findComponent(Transform.class);
    tx.setGlobalPosition(
      body.getTransform().getTranslationX() * Constants.PIXELS_PER_METER,
      body.getTransform().getTranslationY() * Constants.PIXELS_PER_METER
    );
    tx.setGlobalRotation(-radToDeg(body.getTransform().getRotationAngle()));
  }

  @Override
  protected void render() {
    if (DEBUG_DRAW) {
      Transform tx = entity.findComponent(Transform.class);
      Vector globalPosition = tx.getGlobalPosition();
      Renderer.fillCircle((int) globalPosition.x, (int) globalPosition.y, 3)
        .withColor(Color.RED);
    }
  }

  @Override
  protected void dispose() {
    // Find all colliders and unregister their collision listeners
    List<Collider> colliders = entity.findComponents(Collider.class);
    colliders.forEach(collider -> collider.fixture.addToBody(body));

    colliders.forEach(collider -> {
      if (collider.collisionListener != null) {
        Physics.removeCollisionListener(
          collider.fixture.id, collider.collisionListener
        );
      }
    });

    Physics.removeBody(body);
  }
}
