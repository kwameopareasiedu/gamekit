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

import static dev.gamekit.utils.Math.*;

/**
 * {@link RigidBody} enables physics-based motion for the entity.
 * <p>
 * During its {@link #update} phase and after a physics simulation step is complete, this
 * component will update the attached entity's {@link Transform} component's position and rotation
 * <p>
 * Once a {@link RigidBody} is added to an entity, position and rotation changes should be done
 * with the {@link RigidBody} component and not the {@link Transform} component
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

  /**
   * Sets custom user data to this {@link RigidBody} which can be used to identify this
   * {@link RigidBody} in collision listeners
   */
  public void setUserData(Object data) {
    body.setUserData(data);
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

  /**
   * Attaches a collision listener to this {@link RigidBody}. When this {@link RigidBody}
   * collides with other rigid bodies, this listener is invoked with the details of the collision
   * <p>
   * <i>The listener is automatically removed when the host entity is disposed</i>
   */
  public void addCollisionListener(Physics.CollisionListener listener) {
    Physics.addCollisionListener(body, listener);
  }

  @Override
  protected void start() {
    // Find all colliders and add their fixtures
    List<Collider> colliders = entity.findComponents(Collider.class);
    colliders.forEach(collider -> collider.fixture.addToBody(body));

    Physics.addBody(body);
  }

  @Override
  protected void update() {
    Transform tx = entity.findComponent(Transform.class);
    Vector2 center = body.getWorldCenter();

    tx.setPosition(center.x * Constants.PIXELS_PER_METER, center.y * Constants.PIXELS_PER_METER);
    tx.setRotation(radToDeg(body.getTransform().getRotationAngle()));
  }

  @Override
  protected void render() {
    if (DEBUG_DRAW) {
      Vector2 bodyCenter = body.getWorldCenter();
      int bodyCenterX = toInt(bodyCenter.x * Constants.PIXELS_PER_METER);
      int bodyCenterY = toInt(bodyCenter.y * Constants.PIXELS_PER_METER);
      Renderer.fillCircle(bodyCenterX, bodyCenterY, 3).withColor(Color.RED);
    }
  }

  @Override
  protected void dispose() {
    Physics.removeBody(body);
  }
}
