package dev.gamekit.utils;

import static dev.gamekit.utils.Math.clamp;

/** An immutable record for size and height range values */
public record Constraints(double minWidth, double maxWidth, double minHeight, double maxHeight) {
  public Constraints {
    minWidth = java.lang.Math.max(minWidth, 0);
    maxWidth = java.lang.Math.max(maxWidth, 0);
    minHeight = java.lang.Math.max(minHeight, 0);
    maxHeight = java.lang.Math.max(maxHeight, 0);
  }

  /** Constrains the given size to respect {@link #minWidth} and {@link #maxWidth} */
  public double constrainWidth(double width) {
    return clamp(width, minWidth, maxWidth);
  }

  /** Constrains the given height to respect {@link #minHeight} and {@link #maxHeight} */
  public double constrainHeight(double height) {
    return clamp(height, minHeight, maxHeight);
  }

  @Override
  public String toString() {
    return String.format(
      "%s[%.2f<=width<=%.2f, %.2f<=height<=%.2f]",
      getClass().getName(), minWidth, maxWidth, minHeight, maxHeight
    );
  }
}
