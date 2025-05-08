package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;

import java.util.Objects;

/** A {@link SingleChildParent} which adds padding around its single child */
public class Padding extends SingleChildParent {
  protected final Spacing padding;

  public Padding(PaddingOptions options, Widget child) {
    super(child);
    this.padding = options.padding;
  }

  public static Padding create(PaddingOptions options, Widget child) {
    return new Padding(options, child);
  }

  public static PaddingOptions options() {
    return new PaddingOptions();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
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

    if (intrinsicBounds.width > computedBounds.width ||
      intrinsicBounds.height > computedBounds.height) {
      child.layout(
        new Constraints(
          0, computedBounds.width - padding.getHorizontal(),
          0, computedBounds.height - padding.getVertical()
        )
      );
    }

    child.computedBounds.setPosition(padding.left, padding.top);
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Padding paddingWidget)
      return Objects.equals(padding, paddingWidget.padding);

    return false;
  }

  public static class PaddingOptions {
    Spacing padding = new Spacing();

    PaddingOptions() { }

    public PaddingOptions padding(Spacing padding) {
      this.padding = padding;
      return this;
    }

    public PaddingOptions padding(int padding) {
      this.padding = new Spacing(padding);
      return this;
    }

    public PaddingOptions padding(int horizontal, int vertical) {
      this.padding = new Spacing(horizontal, vertical);
      return this;
    }

    public PaddingOptions padding(int top, int right, int bottom, int left) {
      this.padding = new Spacing(top, right, bottom, left);
      return this;
    }
  }
}
