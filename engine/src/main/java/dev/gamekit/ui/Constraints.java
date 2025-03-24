package dev.gamekit.ui;

/** An immutable container for range values for width and height */
public class Constraints {
  public final int minWidth, maxWidth;
  public final int minHeight, maxHeight;
  public final boolean isTight;

  /** Creates a new constraints object with the given values */
  public Constraints(int minWidth, int maxWidth, int minHeight, int maxHeight) {
    this.minWidth = java.lang.Math.max(0, minWidth);
    this.maxWidth = java.lang.Math.max(0, maxWidth);
    this.minHeight = java.lang.Math.max(0, minHeight);
    this.maxHeight = java.lang.Math.max(0, maxHeight);
    this.isTight = minWidth == maxWidth && minHeight == maxHeight;
  }

  /**
   * Creates a new constraints object, copying values from another constraints object
   * @param constraints The constraints object to copy from
   */
  public Constraints(Constraints constraints) {
    this(constraints.minWidth, constraints.maxWidth, constraints.minHeight, constraints.maxHeight);
  }

  /**
   * Returns this {@link Constraints} if all incoming values are the same as instance values
   * else it creates a new {@link Constraints} object with the new values
   * @param minWidth  The new minimum width
   * @param maxWidth  The new maximum width
   * @param minHeight The new minimum height
   * @param maxHeight The new maximum height
   * @return This object if all incoming values are the same as instance
   * values else a new {@link Constraints} object with the new values
   */
  public Constraints update(int minWidth, int maxWidth, int minHeight, int maxHeight) {
    if (this.minWidth != minWidth ||
      this.maxWidth != maxWidth ||
      this.minHeight != minHeight ||
      this.maxHeight != maxHeight) {
      return new Constraints(minWidth, maxWidth, minHeight, maxHeight);
    } else return this;
  }

  @Override
  public String toString() {
    return String.format(
      getClass().getName() + "[%d<=width<=%d,%d<=height<=%d]",
      minWidth, maxWidth, minHeight, maxHeight
    );
  }
}
