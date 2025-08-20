package dev.gamekit.utils;

/** Represents an (x,y) position */
public class Vector {
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
  public Vector(Vector position) {
    this(position.x, position.y);
  }

  public static double squaredDistance(Vector v1, Vector v2) {
    double x2mx1 = v1.x - v2.x;
    double y2my1 = v1.y - v2.y;
    return x2mx1 * x2mx1 + y2my1 * y2my1;
  }

  public static double dot(Vector v1, Vector v2) {
    Vector v1n = v1.getNormalized();
    Vector v2n = v2.getNormalized();
    return v1n.x * v2n.x + v1n.y * v2n.y;
  }

  @Override
  public String toString() {
    return String.format(getClass().getName() + "[x=%.2f,y=%.2f]", x, y);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Vector posObject
      && x == posObject.x
      && y == posObject.y;
  }

  public void set(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /** Copies values from another position object */
  public void set(Vector position) { set(position.x, position.y); }

  public void setX(double x) { this.x = x; }

  public void setY(double y) { this.y = y; }

  public double getMagnitude() {
    return java.lang.Math.sqrt(x * x + y * y);
  }

  public Vector getNormalized() {
    double mag = getMagnitude();
    return new Vector(x / mag, y / mag);
  }

  /**
   * Rotates another {@link Vector} about this one by {@code rad} radian in the clockwise
   * direction, modifying it with the result
   */
  public void rotatePoint(Vector point, double rad) {
    double sin = java.lang.Math.sin(rad);
    double cos = java.lang.Math.cos(rad);

    double px = point.x - x;
    double py = point.y - y;

    point.x = cos * px + sin * py + x;
    point.y = -sin * px + cos * py + y;
  }
}
