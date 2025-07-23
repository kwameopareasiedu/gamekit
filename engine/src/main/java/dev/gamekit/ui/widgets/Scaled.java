package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which scales the computed size of its child */
public class Scaled extends SingleChildParent {
  protected double scale;

  private final Config config;

  public Scaled(Config config, Widget child) {
    super(child);
    this.config = config;
  }

  public static Scaled create(Config config, Widget child) {
    return new Scaled(config, child);
  }

  public static Config config() {
    return new Config();
  }

  @Override
  protected void performMounted() {
    scale = Math.max(0, coalesce(config.scale, 1.0));
    super.performMounted();
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

  public static class Config {
    Double scale;

    Config() { }

    public Config scale(double scale) {
      this.scale = scale;
      return this;
    }
  }
}
