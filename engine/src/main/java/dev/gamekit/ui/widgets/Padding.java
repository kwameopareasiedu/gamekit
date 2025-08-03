package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;

import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which adds padding around its single child */
public class Padding extends SingleChildParent {
  protected Spacing padding;

  public Padding(PaddingConfig config, Widget child) {
    super(config, child);
  }

  public static Padding create(PaddingConfig config, Widget child) {
    return new Padding(config, child);
  }

  public static PaddingConfig config() {
    return new PaddingConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Padding paddingWidget &&
        Objects.equals(padding, paddingWidget.padding);
  }

  @Override
  protected void performInit() {
    PaddingConfig config = (PaddingConfig) super.config;

    padding = coalesce(config.padding, new Spacing());

    super.performInit();
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

  public static class PaddingConfig extends SingleChildParentConfig {
    protected Spacing padding;

    public PaddingConfig padding(int top, int right, int bottom, int left) {
      this.padding = new Spacing(top, right, bottom, left);
      return this;
    }
  }
}
