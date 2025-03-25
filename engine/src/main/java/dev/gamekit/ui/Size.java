package dev.gamekit.ui;

import dev.gamekit.ui.widgets.Widget;

/** Represents the width and height dimension of a {@link Widget} */
public class Size {
  public int width;
  public int height;

  /** Creates a new size, setting width and height to {@code size} */
  public Size(int size) { this(size, size); }

  /** Creates a new size, copying values from another size object */
  public Size(Size size) { this(size.width, size.height); }

  /** Creates a new size with explicit values for width and height */
  public Size(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void set(int size) { set(size, size); }

  /** Copies the width and height from another size object */
  public void set(Size size) { set(size.width, size.height); }

  public void set(int width, int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public String toString() {
    return String.format(getClass().getName() + "[width=%d,height=%d]", width, height);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Size
      && ((Size) obj).width == width
      && ((Size) obj).height == height;
  }
}
