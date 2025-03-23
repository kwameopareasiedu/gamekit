package dev.gamekit.ui;

public class Constraints {
  public final int minWidth, maxWidth;
  public final int minHeight, maxHeight;
  public final boolean isTight;

  public Constraints(int minWidth, int maxWidth, int minHeight, int maxHeight) {
    this.minWidth = minWidth;
    this.maxWidth = maxWidth;
    this.minHeight = minHeight;
    this.maxHeight = maxHeight;
    this.isTight = minWidth == maxWidth && minHeight == maxHeight;
  }

  public Constraints copyWith(int minWidth, int maxWidth, int minHeight, int maxHeight) {
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
      getClass().getName() + "%d<=width<=%d,%d<=height<=%d",
      minWidth, maxWidth, minHeight, maxHeight
    );
  }
}
