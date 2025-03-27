package dev.gamekit.ui;

import static dev.gamekit.utils.Math.clamp;

/** An immutable record for width and height range values for width and height */
public record Constraints(int minWidth, int maxWidth, int minHeight, int maxHeight) {
  public Constraints {
    if (minWidth < 0 || maxWidth < 0 || minHeight < 0 || maxHeight < 0)
      throw new IllegalArgumentException("Constraint values cannot be negative");
    if (minWidth > maxWidth) throw new IllegalArgumentException("minWidth cannot be greater than maxWidth");
    if (minHeight > maxHeight) throw new IllegalArgumentException("minHeight cannot be greater than maxHeight");
  }

  /** Constraints the given width to respect {@link #minWidth} and {@link #maxWidth} */
  public int constrainWidth(int width) {
    return clamp(width, minWidth, maxWidth);
  }

  /** Constraints the given height to respect {@link #minHeight} and {@link #maxHeight} */
  public int constrainHeight(int height) {
    return clamp(height, minHeight, maxHeight);
  }

  @Override
  public String toString() {
    return String.format(
      getClass().getName() + "[%d<=width<=%d,%d<=height<=%d]",
      minWidth, maxWidth, minHeight, maxHeight
    );
  }
}
