package dev.gamekit.ui;

import dev.gamekit.ui.widgets.Widget;

/** Represents a padding or margin of a {@link Widget} */
public class Spacing {
  public int top;
  public int right;
  public int bottom;
  public int left;

  /**
   * Creates a new spacing, setting all sides to {@code size}
   * @param size Spacing value for all sides
   */
  public Spacing(int size) {
    this(size, size);
  }

  /**
   * Creates a new spacing, copying values from another spacing object
   * @param spacing The spacing object to copy
   */
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

  /**
   * Creates a new spacing with explicit values for each side
   * @param top    Top spacing
   * @param bottom Bottom spacing
   * @param right  Right spacing
   * @param left   Left spacing
   */
  public Spacing(int top, int right, int bottom, int left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  /**
   * Returns the sum of the top and bottom spacing
   * @return The sum of the top and bottom spacing
   */
  public int getVertical() { return top + bottom; }

  /**
   * Returns the sum of the left and right spacing
   * @return The sum of the left and right spacing
   */
  public int getHorizontal() { return left + right; }

  /**
   * Sets all sides to the value of {@code size}
   * @param size Spacing value for all sides
   */
  public void set(int size) { set(size, size); }

  /**
   * Sets the vertical and horizontal spacing
   * @param vertical   Vertical spacing (Top and bottom)
   * @param horizontal Horizontal spacing (Left and right)
   */
  public void set(int horizontal, int vertical) {
    set(vertical, horizontal, vertical, horizontal);
  }

  /**
   * Copies the spacing value from another spacing object
   * @param spacing The spacing object
   */
  public void set(Spacing spacing) {
    set(spacing.top, spacing.right, spacing.bottom, spacing.left);
  }

  /**
   * Sets the spacing value for each side
   * @param top    Top spacing
   * @param bottom Bottom spacing
   * @param right  Right spacing
   * @param left   Left spacing
   */
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
}
