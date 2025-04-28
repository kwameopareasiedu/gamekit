package dev.gamekit.ui;

import static dev.gamekit.utils.Math.clamp;

/** An immutable record for width and height range values */
public record Constraints(double minWidth, double maxWidth, double minHeight, double maxHeight) {
  public Constraints {
    minWidth = Math.max(minWidth, 0);
    maxWidth = Math.max(maxWidth, 0);
    minHeight = Math.max(minHeight, 0);
    maxHeight = Math.max(maxHeight, 0);
  }

  /** Constraints the given width to respect {@link #minWidth} and {@link #maxWidth} */
  public double constrainWidth(double width) {
    return clamp(width, minWidth, maxWidth);
  }

  /** Constraints the given height to respect {@link #minHeight} and {@link #maxHeight} */
  public double constrainHeight(double height) {
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
