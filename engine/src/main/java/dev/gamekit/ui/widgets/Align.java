package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.Alignment;

import java.util.Objects;

/** A {@link SingleChildParent} which aligns its single child within itself */
public class Align extends SingleChildParent {
  protected Alignment alignment;

  protected Align(Alignment alignment, Widget child) {
    super(child);
    this.alignment = alignment;
  }

  public static Align create(Alignment alignment, Widget child) {
    return new Align(alignment, child);
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

    int hOffset = 0, vOffset = 0;

    switch (alignment) {
      case TOP_LEFT -> {
        hOffset = 0;
        vOffset = 0;
      }
      case TOP_CENTER -> {
        hOffset = computedBounds.width / 2 - intrinsicBounds.width / 2;
        vOffset = 0;
      }
      case TOP_RIGHT -> {
        hOffset = computedBounds.width - intrinsicBounds.width;
        vOffset = 0;
      }
      case LEFT -> {
        hOffset = 0;
        vOffset = computedBounds.height / 2 - intrinsicBounds.height / 2;
      }
      case CENTER -> {
        hOffset = computedBounds.width / 2 - intrinsicBounds.width / 2;
        vOffset = computedBounds.height / 2 - intrinsicBounds.height / 2;
      }
      case RIGHT -> {
        hOffset = computedBounds.width - intrinsicBounds.width;
        vOffset = computedBounds.height / 2 - intrinsicBounds.height / 2;
      }
      case BOTTOM_LEFT -> {
        hOffset = 0;
        vOffset = computedBounds.height - intrinsicBounds.height;
      }
      case BOTTOM_CENTER -> {
        hOffset = computedBounds.width / 2 - intrinsicBounds.width / 2;
        vOffset = computedBounds.height - intrinsicBounds.height;
      }
      case BOTTOM_RIGHT -> {
        hOffset = computedBounds.width - intrinsicBounds.width;
        vOffset = computedBounds.height - intrinsicBounds.height;
      }
    }

    child.computedBounds.setPosition(hOffset, vOffset);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Align alignWidget) {
      return Objects.equals(child, alignWidget.child)
        && Objects.equals(alignment, alignWidget.alignment);
    }

    return false;
  }
}
