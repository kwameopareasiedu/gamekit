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
 * The {@link RigidBody} component enables physics-based motion for the entity.
 * <p>
 * During its {@link #update()} phase and after a physics simulation step is complete, this
 * component will update the attached entity's {@link Transform} component's position and rotation
 */
public class RigidBody extends Component {
  public static boolean DEBUG_DRAW = false;

  private final Body body;

  public RigidBody() {
    body = new Body();
    body.setMassType(MassType.INFINITE);
  }

  public RigidBody(MassType massType, Vector massCenter, double mass, double inertia) {
    body = new Body();
    body.setMassType(massType);
    body.setMass(new Mass(new Vector2(massCenter.x, massCenter.y), mass, inertia));
  }

  public void setCustomData(Object data) {
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
    body.getTransform().setTranslation(x, y);
  }

  /** Sets the global rotation of this {@link RigidBody} about its center */
  public void setRotation(double deg) {
    body.getTransform().setRotation(-degToRad(deg));
  }

  /** Sets the global rotation of this {@link RigidBody} about a point */
  public void setRotation(double deg, Vector point) {
    setRotation(0);
    body.rotate(-degToRad(deg), point.x, point.y);
  }

  /**
   * Creates a circle with the specified {@code radius} and attaches it to this
   * {@link RigidBody} via a {@link BodyFixture}. After creation, the new {@link Circle} and
   * {@link BodyFixture} are passed to the {@code tuner} for configuration before they are added
   * to this {@link RigidBody}
   */
  public void addCircleFixture(double radius, FixtureTuner<Circle> tuner) {
    Circle circle = new Circle(radius);
    BodyFixture fx = new BodyFixture(circle);
    tuner.tuneFixture(fx, circle);
    body.addFixture(fx);
    body.updateMass();
  }

  /**
   * Creates a rectangle with the specified {@code width} and {@code height} and attaches it to
   * this {@link RigidBody} via a {@link BodyFixture}.After creation, the new {@link Rectangle} and
   * {@link BodyFixture} are passed to the {@code tuner} for configuration before they are added
   * to this {@link RigidBody}
   */
  public void addRectFixture(double width, double height, FixtureTuner<Rectangle> tuner) {
    Rectangle rect = new Rectangle(width, height);
    BodyFixture fx = new BodyFixture(rect);
    tuner.tuneFixture(fx, rect);
    body.addFixture(fx);
    body.updateMass();
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
    super.start();
    Physics.addBody(body);
  }

  @Override
  protected void update() {
    super.update();

    Transform tx = entity.findComponent(Transform.class);
    Vector2 center = body.getWorldCenter();
    tx.setPosition(center.x, center.y);
    tx.setRotation(radToDeg(body.getTransform().getRotationAngle()));
  }

  @Override
  protected void render() {
    super.render();

    if (DEBUG_DRAW) {
      Vector2 bodyCenter = body.getWorldCenter();
      int bodyCenterX = toInt(bodyCenter.x * Constants.PIXELS_PER_METER);
      int bodyCenterY = toInt(bodyCenter.y * Constants.PIXELS_PER_METER);
      double bodyRotation = radToDeg(-body.getTransform().getRotationAngle());

      for (BodyFixture fx : body.getFixtures()) {
        Convex shape = fx.getShape();
        Vector2 shapeCenter = shape.getCenter();
        int shapeCenterX = toInt((bodyCenter.x + shapeCenter.x) * Constants.PIXELS_PER_METER);
        int shapeCenterY = toInt((bodyCenter.y + shapeCenter.y) * Constants.PIXELS_PER_METER);

        if (shape instanceof Circle circle) {
          int radius = toInt(circle.getRadius() * Constants.PIXELS_PER_METER);

          Renderer.drawCircle(shapeCenterX, shapeCenterY, radius)
            .withColor(Color.CYAN).withRotation(bodyCenterX, bodyCenterY, bodyRotation);
          Renderer.drawVerticalLine(shapeCenterX, shapeCenterY, shapeCenterY + radius)
            .withRotation(bodyCenterX, bodyCenterY, bodyRotation);
        } else if (shape instanceof Rectangle rect) {
          int width = toInt(rect.getWidth() * Constants.PIXELS_PER_METER);
          int height = toInt(rect.getHeight() * Constants.PIXELS_PER_METER);

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
    super.dispose();

    Application.getInstance().scheduleTask(() -> {
      Physics.removeBody(body);
    });
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
