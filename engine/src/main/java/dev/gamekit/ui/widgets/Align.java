package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;
import dev.gamekit.ui.enums.Alignment;

import java.util.Objects;

/** A {@link SingleChildParent} which aligns its single child within itself */
public class Align extends SingleChildParent {
  protected final Alignment horizontalAlignment;
  protected final Alignment verticalAlignment;

  public Align(
    Alignment horizontalAlignment,
    Alignment verticalAlignment,
    Widget child
  ) {
    super(child);
    this.horizontalAlignment = horizontalAlignment;
    this.verticalAlignment = verticalAlignment;
  }

  @SafeVarargs
  public static Align create(Param<? super AlignParam>... params) {
    return new Align(
      Param.getValue(params, "horizontalAlignment", Alignment.START),
      Param.getValue(params, "verticalAlignment", Alignment.START),
      Param.getValue(params, "child", null)
    );
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
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.maxWidth(),
      constraints.maxHeight()
    );

    int hOffset = switch (horizontalAlignment) {
      case CENTER -> computedBounds.width / 2 - intrinsicBounds.width / 2;
      case END -> computedBounds.width - intrinsicBounds.width;
      default -> 0;
    };

    int vOffset = switch (verticalAlignment) {
      case CENTER -> computedBounds.height / 2 - intrinsicBounds.height / 2;
      case END -> computedBounds.height - intrinsicBounds.height;
      default -> 0;
    };

    child.computedBounds.setPosition(hOffset, vOffset);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Align alignWidget) {
      return Objects.equals(horizontalAlignment, alignWidget.horizontalAlignment)
        && Objects.equals(verticalAlignment, alignWidget.verticalAlignment);
    }

    return false;
  }
}
