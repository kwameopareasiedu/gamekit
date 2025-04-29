package dev.gamekit.utils;

import java.lang.Math;

/** {@link Bounds} represents a region in 2D space */
public class Bounds {
  public double x;
  public double y;
  public double width;
  public double height;

  public Bounds() {
    this(0, 0, 0, 0);
  }

  /** Copy constructor for this class */
  public Bounds(Bounds bounds) {
    this(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  public Bounds(double x, double y, double width, double height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /** Computes the intersection of two bounds and stores the result in {@code out} */
  public static void intersect(Bounds bounds1, Bounds bounds2, Bounds out) {
    double x1 = java.lang.Math.max(bounds1.x, bounds2.x);
    double y1 = java.lang.Math.max(bounds1.y, bounds2.y);
    double x2 = java.lang.Math.min(bounds1.x + bounds1.width, bounds2.x + bounds2.width);
    double y2 = java.lang.Math.min(bounds1.y + bounds1.height, bounds2.y + bounds2.height);
    out.set(x1, y1, x2 - x1, y2 - y1);
  }

  public void set(double x, double y, double width, double height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /** Sets the position (x, y) of the bounds */
  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /** Sets the size (width, height) of the bounds */
  public void setSize(double width, double height) {
    this.width = width;
    this.height = height;
  }

  public void setX(double x) { this.x = x; }

  public void setY(double y) { this.y = y; }

  public void setWidth(double width) { this.width = width; }

  public void setHeight(double height) { this.height = height; }

  /** Copies values from another bounds object */
  public void set(Bounds bounds) {
    set(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  /** Determines if this bounds contains the point {@code (x,y)} */
  public boolean contains(double x, double y) {
    return this.x <= x && x <= this.x + this.width &&
      this.y <= y && y <= this.y + this.height;
  }

  /** Adjust this bounds to be a union of itself and {@code bounds} */
  public void extend(Bounds bounds) {
    double x1 = java.lang.Math.min(x, bounds.x);
    double y1 = java.lang.Math.min(y, bounds.y);
    double w = java.lang.Math.max(x + width, bounds.x + bounds.width) - x1;
    double h = Math.max(y + height, bounds.y + bounds.height) - y1;
    set(x1, y1, w, h);
  }

  @Override
  public String toString() {
    return String.format(
      getClass().getName() + "[x=%.2f,y=%.2f,width=%.2f,height=%.2f]",
      x, y, width, height
    );
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Bounds bounds
      && x == bounds.x
      && y == bounds.y
      && width == bounds.width
      && height == bounds.height;
  }
}
