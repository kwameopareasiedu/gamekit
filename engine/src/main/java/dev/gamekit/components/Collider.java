package dev.gamekit.components;

import dev.gamekit.core.Component;
import dev.gamekit.core.Physics;
import dev.gamekit.core.Renderer;
import dev.gamekit.utils.Vector;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;

import java.awt.*;
import java.util.UUID;

/** {@link Collider} defines the physics shape of an entity for the purposes of physics collision detection */
public abstract class Collider extends Component {
  public static boolean DEBUG = false;
  private static final Stroke SENSOR_DEBUG_STROKE =
    new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{ 10, 2 }, 0);

  protected final Fixture fixture;
  protected Physics.CollisionListener collisionListener;

  protected Collider(Fixture fixture) {
    this.fixture = fixture;
    fixture.setCollider(this);
  }

  /** Returns the custom metadata object */
  public Object getMetaData() {
    return fixture.getUserData();
  }

  /**
   * Sets a custom object with user-defined attributes as the metadata.
   * <p>
   * This can be used to, for example, add tags which can identify it in a future collision
   *
   * @see org.dyn4j.dynamics.BodyFixture#setUserData(Object)
   */
  public void setMetaData(Object metadata) {
    fixture.setUserData(metadata);
  }

  /** Sets the local offset relative to the entity's {@link RigidBody} */
  public void setOffset(double x, double y) {
    fixture.getShape().translate(x / Physics.PIXELS_PER_METER, y / Physics.PIXELS_PER_METER);
  }

  /** Sets a {@link Physics.CollisionListener} to be notified when this collider collides with another collider */
  public void setCollisionListener(Physics.CollisionListener collisionListener) {
    this.collisionListener = collisionListener;
  }

  /**
   * Set the collision category this collider resides in, and a layer mask indicating which layers this collider can
   * collide with
   *
   * @see org.dyn4j.collision.CategoryFilter
   */
  public void setCollisionFilter(long category, long mask) {
    fixture.setFilter(new CategoryFilter(category, mask));
  }

  /** @see #setCollisionFilter(long, long) */
  public void setCollisionFilter(CategoryFilter filter) {
    fixture.setFilter(filter);
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
    if (collisionListener != null)
      Physics.addCollisionListener(fixture.id, collisionListener);
  }

  @Override
  protected void resume() {
    if (collisionListener != null)
      Physics.addCollisionListener(fixture.id, collisionListener);
  }

  @Override
  protected void render() {
    if (DEBUG) {
      Renderer.onLayer(Renderer.LAYER_COUNT - 1, () -> {
        Transform tx = entity.findComponent(Transform.class);
        Vector globalPosition = tx.getGlobalPosition();
        int positionX = (int) globalPosition.x;
        int positionY = (int) globalPosition.y;
        double rotation = tx.getGlobalRotation();

        Convex shape = fixture.getShape();
        Vector2 shapeCenter = shape.getCenter();
        int shapePositionX = (int) (globalPosition.x + shapeCenter.x * Physics.PIXELS_PER_METER);
        int shapePositionY = (int) (globalPosition.y + shapeCenter.y * Physics.PIXELS_PER_METER);
        Stroke stroke = fixture.isSensor() ? SENSOR_DEBUG_STROKE : null;

        if (shape instanceof Circle circle) {
          int radius = (int) (circle.getRadius() * Physics.PIXELS_PER_METER);

          Renderer.drawCircle(shapePositionX, shapePositionY, radius)
            .withColor(Color.CYAN).withStroke(stroke).withRotation(positionX, positionY, rotation);

          Renderer.drawVerticalLine(shapePositionX, shapePositionY, shapePositionY + radius)
            .withRotation(positionX, positionY, rotation);
        } else if (shape instanceof Rectangle rect) {
          int width = (int) (rect.getWidth() * Physics.PIXELS_PER_METER);
          int height = (int) (rect.getHeight() * Physics.PIXELS_PER_METER);

          Renderer.drawRect(shapePositionX, shapePositionY, width, height)
            .withColor(Color.CYAN).withStroke(stroke).withRotation(positionX, positionY, rotation);

          Renderer.drawVerticalLine(shapePositionX, shapePositionY, shapePositionY + height / 2)
            .withRotation(positionX, positionY, rotation);
        } else if (shape instanceof Polygon poly) {
          Vector2[] vertices = poly.getVertices();

          for (int i = 0; i < vertices.length; i++) {
            Vector2 vertex = vertices[i];
            int vx = (int) (positionX + vertex.x * Physics.PIXELS_PER_METER);
            int vy = (int) (positionY + vertex.y * Physics.PIXELS_PER_METER);

            Renderer.fillCircle(vx, vy, 1).withRotation(positionX, positionY, rotation);

            Vector2 vertex2 = i < vertices.length - 1 ? vertices[i + 1] : vertices[0];
            int v2x = (int) (positionX + vertex2.x * Physics.PIXELS_PER_METER);
            int v2y = (int) (positionY + vertex2.y * Physics.PIXELS_PER_METER);

            Renderer.drawLine(vx, vy, v2x, v2y).withRotation(positionX, positionY, rotation);
          }
        }

        Renderer.fillCircle(shapePositionX, shapePositionY, 2)
          .withColor(Color.ORANGE).withRotation(positionX, positionY, rotation);
      });
    }
  }

  @Override
  protected void stop() {
    if (collisionListener != null)
      Physics.removeCollisionListener(fixture.id, collisionListener);
  }

  @Override
  protected void dispose() {
    if (collisionListener != null)
      Physics.removeCollisionListener(fixture.id, collisionListener);
  }

  /** {@link Fixture} extends {@link BodyFixture} adding a reference to its parent {@link Collider} */
  public static class Fixture extends BodyFixture {
    public final String id = UUID.randomUUID().toString();

    private Collider collider;

    /** Creates a {@link Fixture} with the associated {@link Convex shape} */
    public Fixture(Convex shape) {
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
