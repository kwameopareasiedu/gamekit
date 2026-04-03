package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;

/** A {@link SingleChildParent} which adds padding around its single child */
@WidgetBuilder
public class Padding extends SingleChildParent {
  @WidgetBuilderField(fallback = "new dev.gamekit.utils.Spacing()")
  public Spacing padding;

  public Padding(PaddingConfig config, Widget child) {
    super(config, child);
  }

  public static Padding create(PaddingConfig.Updater updater, Widget child) {
    return new Padding(Widgets.configurePadding(updater), child);
  }

  public static Padding create(int padding, Widget child) {
    return Padding.create(props -> props.padding = new Spacing(padding), child);
  }

  public static Padding create(int vertical, int horizontal, Widget child) {
    return Padding.create(props -> props.padding = new Spacing(vertical, horizontal), child);
  }

  public static Padding create(int top, int right, int bottom, int left, Widget child) {
    return Padding.create(props -> props.padding = new Spacing(top, right, bottom, left), child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth() - padding.getHorizontal(),
        0, constraints.maxHeight() - padding.getVertical()
      )
    );

    intrinsicSize.set(
      child.computedBounds.width + padding.getHorizontal(),
      child.computedBounds.height + padding.getVertical()
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    child.computedBounds.setPosition(padding.left, padding.top);
  }
}
