package dev.gamekit.components;

import dev.gamekit.core.*;
import dev.gamekit.core.Component;
import dev.gamekit.utils.Vector;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Rectangle;

import java.awt.*;

import static dev.gamekit.utils.Math.*;

/**
 * {@link RigidBody} enables physics-based motion for the entity.
 * <p>
 * During its {@link #update} phase and after a physics simulation step is complete, this
 * component will update the attached entity's {@link Transform} component's position and rotation
 * <p>
 * Once a {@link RigidBody} is added to an entity, position and rotation changes should be done
 * with the rigidbody component and not the {@link Transform} component
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
    double ppm = Constants.PIXELS_PER_METER;
    body.getTransform().setTranslation(x / ppm, y / ppm);
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
    double ppm = Constants.PIXELS_PER_METER;

    body.getTransform().setRotation(0);
    body.getTransform().setTranslation(sx / ppm, sy / ppm);
    body.rotate(-degToRad(deg), rx / ppm, ry / ppm);
  }

  /**
   * Creates a circle with the specified {@code radius} and attaches it to this
   * {@link RigidBody} via a {@link BodyFixture}.
   * <p>
   * After creation, the new {@link Circle} and {@link BodyFixture} are passed to the
   * {@code tuner} for configuration before they are added to this {@link RigidBody}
   */
  public void addCircleFixture(double radius, FixtureTuner<Circle> tuner) {
    double ppm = Constants.PIXELS_PER_METER;

    Circle circle = new Circle(radius / ppm);
    BodyFixture fx = new BodyFixture(circle);
    tuner.tuneFixture(fx, circle);
    body.addFixture(fx);
    body.updateMass();
  }

  /**
   * Creates a circle with the specified {@code radius} and attaches it to this
   * {@link RigidBody} via a {@link BodyFixture} without additional tuning
   * @see #addCircleFixture(double, FixtureTuner)
   */
  public void addCircleFixture(double radius) {
    addCircleFixture(radius, (fx, shape) -> { });
  }

  /**
   * Creates a rectangle with the specified {@code size} and {@code height} and attaches it to
   * this {@link RigidBody} via a {@link BodyFixture}
   * <p>
   * After creation, the new {@link Rectangle} and {@link BodyFixture} are passed to the {@code
   * tuner} for configuration before they are added to this {@link RigidBody}
   */
  public void addRectFixture(double width, double height, FixtureTuner<Rectangle> tuner) {
    double ppm = Constants.PIXELS_PER_METER;

    Rectangle rect = new Rectangle(width / ppm, height / ppm);
    BodyFixture fx = new BodyFixture(rect);
    tuner.tuneFixture(fx, rect);
    body.addFixture(fx);
    body.updateMass();
  }

  /**
   * Creates a rectangle with the specified {@code size} and {@code height} and attaches it to
   * this {@link RigidBody} via a {@link BodyFixture} without additional tuning
   * @see #addRectFixture(double, double, FixtureTuner)
   */
  public void addRectFixture(double width, double height) {
    addRectFixture(width, height, (fx, shape) -> { });
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
    Physics.addBody(body);
  }

  @Override
  protected void update() {
    double ppm = Constants.PIXELS_PER_METER;
    Transform tx = entity.findComponent(Transform.class);
    Vector2 center = body.getWorldCenter();

    tx.setPosition(center.x * ppm, center.y * ppm);
    tx.setRotation(radToDeg(body.getTransform().getRotationAngle()));
  }

  @Override
  protected void render() {
    if (DEBUG_DRAW) {
      double ppm = Constants.PIXELS_PER_METER;
      Vector2 bodyCenter = body.getWorldCenter();
      int bodyCenterX = toInt(bodyCenter.x * ppm);
      int bodyCenterY = toInt(bodyCenter.y * ppm);
      double bodyRotation = radToDeg(-body.getTransform().getRotationAngle());

      for (BodyFixture fx : body.getFixtures()) {
        Convex shape = fx.getShape();
        Vector2 shapeCenter = shape.getCenter();
        int shapeCenterX = toInt((bodyCenter.x + shapeCenter.x) * ppm);
        int shapeCenterY = toInt((bodyCenter.y + shapeCenter.y) * ppm);

        if (shape instanceof Circle circle) {
          int radius = toInt(circle.getRadius() * ppm);

          Renderer.drawCircle(shapeCenterX, shapeCenterY, radius)
            .withColor(Color.CYAN).withRotation(bodyCenterX, bodyCenterY, bodyRotation);
          Renderer.drawVerticalLine(shapeCenterX, shapeCenterY, shapeCenterY + radius)
            .withRotation(bodyCenterX, bodyCenterY, bodyRotation);
        } else if (shape instanceof Rectangle rect) {
          int width = toInt(rect.getWidth() * ppm);
          int height = toInt(rect.getHeight() * ppm);

          Renderer.drawRect(shapeCenterX, shapeCenterY, width, height)
            .withColor(Color.CYAN).withRotation(bodyCenterX, bodyCenterY, bodyRotation);
          Renderer.drawVerticalLine(shapeCenterX, shapeCenterY, shapeCenterY + height / 2)
            .withRotation(bodyCenterX, bodyCenterY, bodyRotation);
        }
      }

      Renderer.fillCircle(bodyCenterX, bodyCenterY, 3).withColor(Color.RED);
    }
  }

  @Override
  protected void dispose() {
    Application.getInstance().runLater(
      () -> Physics.removeBody(body)
    );
  }

  /** Interface for an object which tunes a {@link BodyFixture} */
  public interface FixtureTuner<S extends Convex> {
    /**
     * Called with the new {@link BodyFixture} and {@link Convex Shape} before adding to a
     * {@link Body}
     */
    void tuneFixture(BodyFixture fixture, S shape);
  }
}
