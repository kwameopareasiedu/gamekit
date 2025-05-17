package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.util.Objects;

/** A {@link SingleChildParent} which scales the computed size of its child */
public class Scaled extends SingleChildParent {
  protected final double scale;

  public Scaled(ScaledOptions options, Widget child) {
    super(child);
    this.scale = Math.max(0, options.scale);
  }

  public static Scaled create(ScaledOptions options, Widget child) {
    return new Scaled(options, child);
  }

  public static ScaledOptions options() {
    return new ScaledOptions();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    int scaledWidth = (int) (child.computedBounds.width * scale);
    int scaledHeight = (int) (child.computedBounds.width * scale);

    intrinsicBounds.setSize(scaledWidth, scaledHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
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
    if (widget instanceof Scaled scaledWidget) {
      return Objects.equals(scale, scaledWidget.scale);
    }
    return false;
  }

  public static class ScaledOptions {
    double scale = 1.0;

    public ScaledOptions scale(double scale) {
      this.scale = scale;
      return this;
    }
  }
}
