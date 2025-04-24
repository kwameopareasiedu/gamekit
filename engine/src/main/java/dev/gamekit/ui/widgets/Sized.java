package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.util.Objects;

/** A {@link SingleChildParent} which enforces a fixed size on its child */
public class Sized extends SingleChildParent {
  protected final int width;
  protected final int height;

  public Sized(SizedOptions options, Widget child) {
    super(child);
    this.width = options.width;
    this.height = options.height;
  }

  public static Sized create(SizedOptions options, Widget child) {
    return new Sized(options, child);
  }

  public static SizedOptions options() {
    return new SizedOptions();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    computedBounds.setSize(
      constraints.constrainWidth(width),
      constraints.constrainHeight(height)
    );

    child.layout(
      new Constraints(
        computedBounds.width, computedBounds.width,
        computedBounds.height, computedBounds.height
      )
    );
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Sized sizedWidget) {
      return Objects.equals(width, sizedWidget.width) &&
        Objects.equals(height, sizedWidget.height);
    }
    return false;
  }

  public static class SizedOptions {
    int width = 64;
    int height = 64;

    public SizedOptions width(int width) {
      this.width = width;
      return this;
    }

    public SizedOptions height(int height) {
      this.height = height;
      return this;
    }
  }
}
