package dev.gamekit.components;

import dev.gamekit.core.Physics;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.decompose.AbstractDecomposer;
import org.dyn4j.geometry.decompose.EarClipping;

import java.util.List;

/** {@link ConvexCollider} defines an arbitrary <strong>convex</strong> {@link Collider} for an entity */
public class ConvexCollider extends Collider {
  private static final AbstractDecomposer DECOMPOSER = new EarClipping();

  /**
   * Creates a new {@link ConvexCollider} from the given {@code points}.
   * <p>
   * The points should be of the format {@code [x1, y1, x2, y2, ..., xn, yn]}
   * <p>
   * <strong>NB:</strong> The given list of points MUST form a convex shape else an exception will be thrown.
   * <p>
   * For concave shapes, use {@link ConvexCollider#decompose(double...)} which decompose it into a list of
   * {@link ConvexCollider}
   */
  public ConvexCollider(double... points) {
    super(createFixture(points));
  }

  /** Creates a new {@link ConvexCollider} from the given {@link Convex shape} */
  private ConvexCollider(Convex shape) {
    super(new Fixture(shape));
  }

  /**
   * Creates a list of {@link ConvexCollider} which approximate the shape of the polygon formed by the given points
   * <p>
   * The points should be of the format {@code [x1, y1, x2, y2, ..., xn, yn]}
   */
  public static List<ConvexCollider> decompose(double... points) {
    if (points.length % 2 != 0)
      throw new IllegalArgumentException("ConvexCollider points must be an even array");

    if (points.length < 6)
      throw new IllegalArgumentException("At least two (3) pairs of ConvexCollider points required");

    Vector2[] vertices = new Vector2[points.length / 2];

    for (int i = 0; i < points.length; i += 2) {
      vertices[i / 2] = new Vector2(
        points[i] / Physics.PIXELS_PER_METER,
        points[i + 1] / Physics.PIXELS_PER_METER
      );
    }

    List<Convex> polygons = DECOMPOSER.decompose(vertices);

    return polygons.stream().map(ConvexCollider::new).toList();
  }

  /**
   * Creates a convex polygon {@link org.dyn4j.collision.Fixture} from the given points.
   * <p>
   * The points should be of the format {@code [x1, y1, x2, y2, ..., xn, yn]}
   */
  private static Fixture createFixture(double... points) {
    if (points.length % 2 != 0)
      throw new IllegalArgumentException("ConvexCollider points must be an even array");

    if (points.length < 6)
      throw new IllegalArgumentException("At least two (3) pairs of ConvexCollider points required");

    Vector2[] vertices = new Vector2[points.length / 2];

    for (int i = 0; i < points.length; i += 2) {
      vertices[i / 2] = new Vector2(
        points[i] / Physics.PIXELS_PER_METER,
        points[i + 1] / Physics.PIXELS_PER_METER
      );
    }

    return new Fixture(new Polygon(vertices));
  }
}
