package dev.gamekit.ui;

/** Padding or margin of a {@link UINode} */
public class Spacing {
  public int top;
  public int right;
  public int bottom;
  public int left;

  public Spacing(int size) {
    this(size, size);
  }

  public Spacing(int vertical, int horizontal) {
    this(vertical, horizontal, vertical, horizontal);
  }

  public Spacing(int top, int right, int bottom, int left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  public int getVertical() { return top + bottom; }

  public int getHorizontal() { return left + right; }

  public void set(int size) { set(size, size); }

  public void set(int vertical, int horizontal) {
    set(vertical, horizontal, vertical, horizontal);
  }

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
    return String.format("Spacing[top=%d,right=%d,bottom=%d,left=%d]", top, right, bottom, left);
  }
}
