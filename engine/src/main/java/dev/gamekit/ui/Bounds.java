package dev.gamekit.ui;

import dev.gamekit.ui.widgets.Widget;

/** Represents a render bound on the screen in which a {@link Widget} is rendered */
public class Bounds {
  public int x;
  public int y;
  public int width;
  public int height;

  /** Copy constructor for this class */
  public Bounds(Bounds bounds) {
    this(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  /** Creates a new bounds with explicit values for each side */
  public Bounds(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
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
    int x1 = Math.min(x, bounds.x);
    int y1 = Math.min(y, bounds.y);
    int w = Math.max(x + width, bounds.x + bounds.width) - x1;
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
    return obj instanceof Bounds
      && ((Bounds) obj).x == x
      && ((Bounds) obj).y == y
      && ((Bounds) obj).width == width
      && ((Bounds) obj).height == height;
  }
}
