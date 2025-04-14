package dev.gamekit.ui;

import dev.gamekit.ui.widgets.Widget;

/** Represents a padding or margin of a {@link Widget} */
public class Spacing {
  public int top;
  public int right;
  public int bottom;
  public int left;

  public Spacing() { this(0); }

  /** Creates a new spacing, setting all sides to {@code size} */
  public Spacing(int size) { this(size, size); }

  /** Creates a new spacing, copying values from another spacing object */
  public Spacing(Spacing spacing) {
    this(spacing.top, spacing.right, spacing.bottom, spacing.left);
  }

  /**
   * Creates a new spacing with vertical and horizontal values
   * @param vertical   Vertical spacing (Top and bottom)
   * @param horizontal Horizontal spacing (Left and right)
   */
  public Spacing(int horizontal, int vertical) {
    this(vertical, horizontal, vertical, horizontal);
  }

  public Spacing(int top, int right, int bottom, int left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  /** Returns the sum of the top and bottom spacing */
  public int getVertical() { return top + bottom; }

  /** Returns the sum of the left and right spacing */
  public int getHorizontal() { return left + right; }

  public void set(int size) { set(size, size); }

  public void set(int horizontal, int vertical) {
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
    return obj instanceof Spacing
      && ((Spacing) obj).top == top
      && ((Spacing) obj).right == right
      && ((Spacing) obj).bottom == bottom
      && ((Spacing) obj).left == left;
  }
}
