package dev.gamekit.utils;

import java.lang.Math;

/** {@link Bounds} represents a region in 2D space */
public class Bounds {
  public int x;
  public int y;
  public int width;
  public int height;

  public Bounds() {
    this(0, 0, 0, 0);
  }

  /** Copy constructor for this class */
  public Bounds(Bounds bounds) {
    this(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  public Bounds(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /** Returns a new bounds which is an intersection of two bounds */
  public static Bounds intersect(Bounds bounds1, Bounds bounds2) {
    int x1 = java.lang.Math.max(bounds1.x, bounds2.x);
    int y1 = java.lang.Math.max(bounds1.y, bounds2.y);
    int x2 = java.lang.Math.min(bounds1.x + bounds1.width, bounds2.x + bounds2.width);
    int y2 = java.lang.Math.min(bounds1.y + bounds1.height, bounds2.y + bounds2.height);

    return new Bounds(x1, y1, x2 - x1, y2 - y1);
  }

  public void set(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /** Sets the position (x, y) of the bounds */
  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /** Sets the size (width, height) of the bounds */
  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void setX(int x) { this.x = x; }

  public void setY(int y) { this.y = y; }

  public void setWidth(int width) { this.width = width; }

  public void setHeight(int height) { this.height = height; }

  /** Copies values from another bounds object */
  public void set(Bounds bounds) {
    set(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  /** Adjust this bounds to be a union of itself and {@code bounds} */
  public void extend(Bounds bounds) {
    int x1 = java.lang.Math.min(x, bounds.x);
    int y1 = java.lang.Math.min(y, bounds.y);
    int w = java.lang.Math.max(x + width, bounds.x + bounds.width) - x1;
    int h = Math.max(y + height, bounds.y + bounds.height) - y1;
    set(x1, y1, w, h);
  }

  @Override
  public String toString() {
    return String.format(
      getClass().getName() + "[x=%d,y=%d,width=%d,height=%d]",
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
