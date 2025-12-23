package dev.gamekit.utils;

/** Represents an (x,y) position */
public class Vector {
  public static final double TWO_PI = 2 * java.lang.Math.PI;

  public double x;
  public double y;

  public Vector() {
    this(0, 0);
  }

  public Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /** Copy constructor for this class */
  public Vector(Vector vector) {
    this(vector.x, vector.y);
  }

  /** Computes the squared distance between two vectors */
  public static double squaredDistance(Vector v1, Vector v2) {
    double x2mx1 = v1.x - v2.x;
    double y2my1 = v1.y - v2.y;
    return x2mx1 * x2mx1 + y2my1 * y2my1;
  }

  /** Computes the dot product between two vectors */
  public static double dot(Vector v1, Vector v2) {
    Vector v1n = v1.getNormalized();
    Vector v2n = v2.getNormalized();
    return v1n.x * v2n.x + v1n.y * v2n.y;
  }

  /**
   * Computes the angle (radian, from 0 to 2π) between two vectors starting from {@code v1} to {@code v2} in a
   * clockwise direction starting from the positive y-axis
   */
  public static double angle(Vector v1, Vector v2) {
    double diffX = v2.x - v1.x;
    double diffY = v2.y - v1.y;
    double angle = java.lang.Math.atan2(diffX, diffY);

    if (angle < 0) angle = TWO_PI + angle;

    return angle;
  }

  /**
   * Creates a new vector from the magnitude and angle (radian, which must be clockwise from the positive y-axis and
   * between 0 and 2π)
   */
  public static Vector from(double magnitude, double rad) {
    return new Vector(
      magnitude * java.lang.Math.sin(rad),
      magnitude * java.lang.Math.cos(rad)
    );
  }

  /** Returns a new vector which is the sum of all input vectors */
  public static Vector sum(Vector... vectors) {
    double x = 0, y = 0;

    for (Vector v : vectors) {
      x += v.x;
      y += v.y;
    }

    return new Vector(x, y);
  }

  @Override
  public String toString() {
    return String.format(getClass().getName() + "[x=%.2f,y=%.2f]", x, y);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Vector vectorObject
      && x == vectorObject.x
      && y == vectorObject.y;
  }

  public void set(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /** Copies values from another vector object */
  public void set(Vector vector) {
    set(vector.x, vector.y);
  }

  /** Sets the {@code x} component of this vector */
  public void setX(double x) {
    this.x = x;
  }

  /** Sets the {@code y} component of this vector */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * Returns the magnitude of this vector
   * <p>
   * This method uses {@link java.lang.Math#sqrt sqrt} and can be expensive computer per frame
   * <p>
   * Consider using {@link #getSquaredMagnitude} where possible
   */
  public double getMagnitude() {
    return java.lang.Math.sqrt(x * x + y * y);
  }

  /** Returns the squared magnitude of this vector */
  public double getSquaredMagnitude() {
    return x * x + y * y;
  }

  /**
   * Returns the angle (radian, from 0 to 2π) of this vector in a clockwise direction starting from the positive
   * y-axis
   */
  public double getAngle() {
    double angle = java.lang.Math.atan2(x, y);

    if (angle < 0) angle = TWO_PI + angle;

    return angle;
  }

  /**
   * Returns a normalized version of this vector
   * <p>
   * A normalized vector has the same direction but with a magnitude of 1 unit
   */
  public Vector getNormalized() {
    double mag = getMagnitude();
    return new Vector(x / mag, y / mag);
  }

  /** Interpolates the angle of this vector to match that of the target vector, preserving its magnitude */
  public void lerpAngle(Vector target, double rate) {
    double magnitude = getMagnitude();
    double newRotation = Math.lerpAngle(getAngle(), target.getAngle(), rate);
    set(magnitude * java.lang.Math.sin(newRotation), magnitude * java.lang.Math.cos(newRotation));
  }

  /**
   * Rotates another {@link Vector} about this one by {@code rad} radian in the clockwise direction.
   * <p>
   * NB: <b>This method modifies the provided {@code point} with the result</b>
   */
  public void rotatePoint(Vector point, double rad) {
    double sin = java.lang.Math.sin(rad);
    double cos = java.lang.Math.cos(rad);

    double px = point.x - x;
    double py = point.y - y;

    point.set(cos * px + sin * py + x, -sin * px + cos * py + y);
  }
}
