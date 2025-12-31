package dev.gamekit.utils;

/** {@link Size} represents a region in 2D space */
public class Size {
  public double width;
  public double height;

  public Size() {
    this(0, 0);
  }

  public Size(double width, double height) {
    this.width = width;
    this.height = height;
  }

  /** Copy constructor for this class */
  public Size(Size copy) {
    this(copy.width, copy.height);
  }

  public void set(double width, double height) {
    this.width = width;
    this.height = height;
  }

  /** Sets the width of this size */
  public void setWidth(double width) {
    this.width = width;
  }

  /** Sets the height of this size */
  public void setHeight(double height) {
    this.height = height;
  }

  /** Copies values from another size object */
  public void set(Size size) {
    set(size.width, size.height);
  }

  @Override
  public String toString() {
    return String.format("%s[width=%.2f,height=%.2f]", getClass().getName(), width, height);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Size bounds
      && width == bounds.width
      && height == bounds.height;
  }
}
