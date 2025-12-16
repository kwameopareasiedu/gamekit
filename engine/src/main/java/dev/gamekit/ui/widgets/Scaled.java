package dev.gamekit.ui.widgets;

import dev.gamekit.utils.Constraints;

import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which scales the computed size of its child */
public class Scaled extends SingleChildParent {
  protected double scale;

  public Scaled(ScaledConfig config, Widget child) {
    super(config, child);
  }

  public static Scaled create(ScaledConfig config, Widget child) {
    return new Scaled(config, child);
  }

  public static ScaledConfig config() {
    return new ScaledConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Scaled scaledWidget &&
      Objects.equals(scale, scaledWidget.scale);
  }

  @Override
  protected void performInit() {
    ScaledConfig config = (ScaledConfig) super.config;

    scale = Math.max(0, coalesce(config.scale, 1.0));

    super.performInit();
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

    intrinsicSize.set(scaledWidth, scaledHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    child.layout(
      new Constraints(
        computedBounds.width, computedBounds.width,
        computedBounds.height, computedBounds.height
      )
    );
  }

  public static class ScaledConfig extends SingleChildParentConfig {
    protected Double scale;

    public ScaledConfig scale(double scale) {
      this.scale = scale;
      return this;
    }
  }
}
