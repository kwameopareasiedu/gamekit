package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;

/** A {@link SingleChildParent} which adds padding around its single child */
@WidgetBuilder
public class Padding extends SingleChildParent {
  @WidgetBuilderField(fallback = "new dev.gamekit.utils.Spacing()")
  protected Spacing padding;

  public Padding(PaddingConfig... config) {
    super(config);
  }

  public static Padding create(PaddingConfig... config) {
    return new Padding(config);
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
