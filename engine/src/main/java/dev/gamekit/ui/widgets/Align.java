package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.utils.Constraints;

/** A {@link SingleChildParent} which aligns its single child within itself */
@WidgetBuilder
public class Align extends SingleChildParent {
  @WidgetBuilderField(fallback = "dev.gamekit.ui.enums.Alignment.START")
  protected Alignment horizontalAlignment;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.enums.Alignment.START")
  protected Alignment verticalAlignment;

  public Align(AlignConfig config, Widget child) {
    super(config, child);
  }

  public static Align create(AlignConfig.Updater updater, Widget child) {
    return new Align(Widgets.configureAlign(updater), child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicSize.set(
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.maxWidth(),
      constraints.maxHeight()
    );

    double hOffset = switch (horizontalAlignment) {
      case CENTER -> computedBounds.width / 2 - intrinsicSize.width / 2;
      case END -> computedBounds.width - intrinsicSize.width;
      default -> 0;
    };

    double vOffset = switch (verticalAlignment) {
      case CENTER -> computedBounds.height / 2 - intrinsicSize.height / 2;
      case END -> computedBounds.height - intrinsicSize.height;
      default -> 0;
    };

    child.computedBounds.setPosition(hOffset, vOffset);
  }
}
