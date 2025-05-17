package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.Alignment;

import java.util.Objects;

/** A {@link SingleChildParent} which aligns its single child within itself */
public class Align extends SingleChildParent {
  protected final Alignment horizontalAlignment;
  protected final Alignment verticalAlignment;

  public Align(AlignOptions options, Widget child) {
    super(child);
    this.horizontalAlignment = options.horizontalAlignment;
    this.verticalAlignment = options.verticalAlignment;
  }

  public static Align create(AlignOptions options, Widget child) {
    return new Align(options, child);
  }

  public static AlignOptions options() {
    return new AlignOptions();
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

    double hOffset = switch (horizontalAlignment) {
      case CENTER -> computedBounds.width / 2 - intrinsicBounds.width / 2;
      case END -> computedBounds.width - intrinsicBounds.width;
      default -> 0;
    };

    double vOffset = switch (verticalAlignment) {
      case CENTER -> computedBounds.height / 2 - intrinsicBounds.height / 2;
      case END -> computedBounds.height - intrinsicBounds.height;
      default -> 0;
    };

    child.computedBounds.setPosition(hOffset, vOffset);
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Align alignWidget) {
      return Objects.equals(horizontalAlignment, alignWidget.horizontalAlignment)
        && Objects.equals(verticalAlignment, alignWidget.verticalAlignment);
    }

    return false;
  }

  public static class AlignOptions {
    Alignment horizontalAlignment = Alignment.START;
    Alignment verticalAlignment = Alignment.START;

    public AlignOptions horizontalAlignment(Alignment horizontalAlignment) {
      this.horizontalAlignment = horizontalAlignment;
      return this;
    }

    public AlignOptions verticalAlignment(Alignment verticalAlignment) {
      this.verticalAlignment = verticalAlignment;
      return this;
    }
  }
}
