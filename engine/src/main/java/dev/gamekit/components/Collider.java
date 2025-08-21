package dev.gamekit.components;

import dev.gamekit.core.Component;
import dev.gamekit.core.Constants;
import dev.gamekit.core.Physics;
import dev.gamekit.core.Renderer;
import dev.gamekit.utils.Vector;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.util.UUID;

/**
 * {@link Collider} defines the physics shape of an entity for the purposes of physics collision
 * detection.
 */
public abstract class Collider extends Component {
  private static final Stroke SENSOR_DEBUG_STROKE = new BasicStroke(
    1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{ 10, 2 }, 0
  );

  public static boolean DEBUG_DRAW = false;

  protected final ColliderFixture fixture;
  protected Physics.CollisionListener collisionListener;

  protected Collider(ColliderFixture fixture) {
    this.fixture = fixture;
    fixture.setCollider(this);
  }

  /**
   * Sets a custom object with user-defined attributes as the metadata.
   * <p>
   * This can be used to, for example, add tags which can identify it in a future collision
   * @see org.dyn4j.dynamics.BodyFixture#setUserData(Object)
   */
  public void setMetaData(Object metadata) {
    fixture.setUserData(metadata);
  }

  /** Returns the custom metadata object */
  public Object getMetaData() {
    return fixture.getUserData();
  }

  /** Sets the local offset relative to the entity's {@link RigidBody} */
  public void setOffset(double x, double y) {
    fixture.getShape().translate(x / Constants.PIXELS_PER_METER, y / Constants.PIXELS_PER_METER);
  }

  /**
   * Sets a {@link Physics.CollisionListener} to be notified when this collider collides with
   * another collider
   */
  public void setCollisionListener(Physics.CollisionListener collisionListener) {
    this.collisionListener = collisionListener;
  }

  /** @see org.dyn4j.dynamics.BodyFixture#setDensity(double) */
  public void setDensity(double density) {
    fixture.setDensity(density);
  }

  /** @see org.dyn4j.dynamics.BodyFixture#setRestitution(double) */
  public void setRestitution(double restitution) {
    fixture.setRestitution(restitution);
  }

  /** @see org.dyn4j.dynamics.BodyFixture#setFriction(double) */
  public void setFriction(double friction) {
    fixture.setFriction(friction);
  }

  /** @see org.dyn4j.dynamics.BodyFixture#setSensor(boolean) */
  public void setSensor(boolean sensor) {
    fixture.setSensor(sensor);
  }

  @Override
  protected void start() {
    // Register the collision listener
    if (collisionListener != null)
      Physics.addCollisionListener(fixture.id, collisionListener);
  }

  @Override
  protected void render() {
    if (DEBUG_DRAW) {
      Transform tx = entity.findComponent(Transform.class);
      Vector globalPosition = tx.getGlobalPosition();
      int positionX = (int) globalPosition.x;
      int positionY = (int) globalPosition.y;
      double rotation = tx.getGlobalRotation();

      Convex shape = fixture.getShape();
      Vector2 shapeCenter = shape.getCenter();
      int shapePositionX = (int) (globalPosition.x + shapeCenter.x * Constants.PIXELS_PER_METER);
      int shapePositionY = (int) (globalPosition.y + shapeCenter.y * Constants.PIXELS_PER_METER);
      Stroke stroke = fixture.isSensor() ? SENSOR_DEBUG_STROKE : null;

      if (shape instanceof Circle circle) {
        int radius = (int) (circle.getRadius() * Constants.PIXELS_PER_METER);

        Renderer.drawCircle(shapePositionX, shapePositionY, radius)
          .withColor(Color.CYAN).withStroke(stroke)
          .withRotation(positionX, positionY, rotation);
        Renderer.drawVerticalLine(shapePositionX, shapePositionY, shapePositionY + radius)
          .withRotation(positionX, positionY, rotation);
      } else if (shape instanceof Rectangle rect) {
        int width = (int) (rect.getWidth() * Constants.PIXELS_PER_METER);
        int height = (int) (rect.getHeight() * Constants.PIXELS_PER_METER);

        Renderer.drawRect(shapePositionX, shapePositionY, width, height)
          .withColor(Color.CYAN).withStroke(stroke)
          .withRotation(positionX, positionY, rotation);
        Renderer.drawVerticalLine(shapePositionX, shapePositionY, shapePositionY + height / 2)
          .withRotation(positionX, positionY, rotation);
      }

      Renderer.fillCircle(shapePositionX, shapePositionY, 2).withColor(Color.ORANGE)
        .withRotation(positionX, positionY, rotation);
    }
  }

  @Override
  protected void dispose() {
    // Unregister the collision listener
    if (collisionListener != null)
      Physics.removeCollisionListener(fixture.id, this.collisionListener);
  }

  /**
   * {@link ColliderFixture} extends {@link BodyFixture} adding a reference to its parent
   * {@link Collider}
   */
  public static class ColliderFixture extends BodyFixture {
    public final String id = UUID.randomUUID().toString();

    private Collider collider;

    /** Creates a {@link ColliderFixture} with the associated {@link Convex shape} */
    public ColliderFixture(Convex shape) {
      super(shape);
    }

    /** Returns the parent {@link Collider} */
    public Collider getCollider() {
      return collider;
    }

    /** Called internally to set the parent {@link Collider} */
    private void setCollider(Collider collider) {
      this.collider = collider;
    }
  }
}
