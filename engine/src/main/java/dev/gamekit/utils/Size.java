package dev.gamekit.utils;

import dev.gamekit.ui.Node;

/** Represents the width and height dimension of a {@link Node} */
public class Size {
  public int width;
  public int height;

  /**
   * Creates a new size, setting width and height to {@code size}
   * @param size Value for width and height
   */
  public Size(int size) {
    this(size, size);
  }

  /**
   * Creates a new size, copying values from another size object
   * @param size The size object to copy
   */
  public Size(Size size) {
    this(size.width, size.height);
  }

  /**
   * Creates a new size with explicit values for width and height
   * @param width  Width of this size
   * @param height Height of this size
   */
  public Size(int width, int height) {
    this.width = width;
    this.height = height;
  }

  /**
   * Sets the width and height to {@code size}
   * @param size New size
   */
  public void set(int size) { set(size, size); }

  /**
   * Copies the width and height from another size object
   * @param size The size object
   */
  public void set(Size size) {
    set(size.width, size.height);
  }

  /**
   * Sets the width and height
   * @param width  New width
   * @param height New height
   */
  public void set(int width, int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public String toString() {
    return String.format(getClass().getName() + "[width=%d,height=%d]", width, height);
  }
}
