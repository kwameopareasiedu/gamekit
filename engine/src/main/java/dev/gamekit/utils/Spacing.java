package dev.gamekit.utils;

import dev.gamekit.ui.widgets.Widget;

import java.lang.Math;

/** Represents a padding or margin of a {@link Widget} */
public class Spacing {
  public int top;
  public int right;
  public int bottom;
  public int left;

  public Spacing() {
    this(0);
  }

  /** Creates a new spacing, setting all sides to {@code size} */
  public Spacing(int size) {
    this(size, size);
  }

  /**
   * Creates a new spacing with vertical and horizontal values
   *
   * @param vertical   Vertical spacing (Top and bottom)
   * @param horizontal Horizontal spacing (Left and right)
   */
  public Spacing(int vertical, int horizontal) {
    this(vertical, horizontal, vertical, horizontal);
  }

  public Spacing(int top, int right, int bottom, int left) {
    this.top = java.lang.Math.max(0, top);
    this.right = java.lang.Math.max(0, right);
    this.bottom = java.lang.Math.max(0, bottom);
    this.left = Math.max(0, left);
  }

  /** Creates a new spacing, copying values from another spacing object */
  public Spacing(Spacing copy) {
    this(copy.top, copy.right, copy.bottom, copy.left);
  }

  /** Returns the sum of the top and bottom spacing */
  public int getVertical() { return top + bottom; }

  /** Returns the sum of the left and right spacing */
  public int getHorizontal() { return left + right; }

  public void set(int size) {
    set(size, size, size, size);
  }

  public void set(int vertical, int horizontal) {
    set(vertical, horizontal, vertical, horizontal);
  }

  /** Copies the spacing value from another spacing object */
  public void set(Spacing spacing) {
    set(spacing.top, spacing.right, spacing.bottom, spacing.left);
  }

  public void set(int top, int right, int bottom, int left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  @Override
  public String toString() {
    return String.format(
      getClass().getName() + "[top=%d,right=%d,bottom=%d,left=%d]",
      top, right, bottom, left
    );
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Spacing spacing
      && spacing.top == top
      && spacing.right == right
      && spacing.bottom == bottom
      && spacing.left == left;
  }
}
