package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;

import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which adds padding around its single child */
public class Padding extends SingleChildParent {
  protected Spacing padding;

  private final Config config;

  public Padding(Config config, Widget child) {
    super(child);
    this.config = config;
  }

  public static Padding create(Config config, Widget child) {
    return new Padding(config, child);
  }

  public static Config config() {
    return new Config();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Padding paddingWidget)
      return Objects.equals(padding, paddingWidget.padding);

    return false;
  }

  @Override
  protected void performUpdateState(Widget widget) {
    this.padding = ((Padding) widget).padding;
  }

  @Override
  protected void performMounted() {
    padding = coalesce(config.padding, new Spacing());
    super.performMounted();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth() - padding.getHorizontal(),
        0, constraints.maxHeight() - padding.getVertical()
      )
    );

    intrinsicBounds.setSize(
      child.computedBounds.width + padding.getHorizontal(),
      child.computedBounds.height + padding.getVertical()
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.computedBounds.setPosition(padding.left, padding.top);
  }

  public static class Config {
    Spacing padding = new Spacing();

    Config() { }

    public Config padding(Spacing padding) {
      this.padding = padding;
      return this;
    }

    public Config padding(int padding) {
      this.padding = new Spacing(padding);
      return this;
    }

    public Config padding(int horizontal, int vertical) {
      this.padding = new Spacing(horizontal, vertical);
      return this;
    }

    public Config padding(int top, int right, int bottom, int left) {
      this.padding = new Spacing(top, right, bottom, left);
      return this;
    }
  }
}
