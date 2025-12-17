package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;
import dev.gamekit.ui.enums.Alignment;

import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which aligns its single child within itself */
@WidgetBuilder
public class Align extends SingleChildParent {
  @WidgetBuilderField
  protected Alignment horizontalAlignment;
  @WidgetBuilderField
  protected Alignment verticalAlignment;

  public Align(AlignConfig... config) {
    super(config);
  }

  public static Align create(AlignConfig... config) {
    return new Align(config);
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Align alignWidget &&
      Objects.equals(horizontalAlignment, alignWidget.horizontalAlignment)
      && Objects.equals(verticalAlignment, alignWidget.verticalAlignment);
  }

  @Override
  protected void performInit() {
    AlignConfig config = (AlignConfig) super.config;

    this.horizontalAlignment = coalesce(config.horizontalAlignment, Alignment.START);
    this.verticalAlignment = coalesce(config.verticalAlignment, Alignment.START);

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
