package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Alignment;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Size;

import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;

/** A {@link Parent} which aligns its single child within itself */
public class Align extends SingleChildParent {
  protected Alignment alignment;

  protected Align(Widget child, Alignment alignment) {
    super(child);
    this.alignment = alignment;
  }

  public static Align create(Widget child, Alignment alignment) {
    return new Align(child, alignment);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.computeLayout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    Size childSize = child.getComputedSize();
    int computedWidth = clamp(childSize.width, constraints.minWidth(), constraints.maxWidth());
    int computedHeight = clamp(childSize.height, constraints.minHeight(), constraints.maxHeight());

    intrinsicSize.set(childSize.width, childSize.height);
    computedSize.set(computedWidth, computedHeight);

    int drawX = 0, drawY = 0;

    switch (alignment) {
      case TOP_LEFT -> {
        drawX = 0;
        drawY = 0;
      }
      case TOP_CENTER -> {
        drawX = computedSize.width / 2 - intrinsicSize.width / 2;
        drawY = 0;
      }
      case TOP_RIGHT -> {
        drawX = computedSize.width - intrinsicSize.width;
        drawY = 0;
      }
      case LEFT -> {
        drawX = 0;
        drawY = computedSize.height / 2 - intrinsicSize.height / 2;
      }
      case CENTER -> {
        drawX = computedSize.width / 2 - intrinsicSize.width / 2;
        drawY = computedSize.height / 2 - intrinsicSize.height / 2;
      }
      case RIGHT -> {
        drawX = computedSize.width - intrinsicSize.width;
        drawY = computedSize.height / 2 - intrinsicSize.height / 2;
      }
      case BOTTOM_LEFT -> {
        drawX = 0;
        drawY = computedSize.height - intrinsicSize.height;
      }
      case BOTTOM_CENTER -> {
        drawX = computedSize.width / 2 - intrinsicSize.width / 2;
        drawY = computedSize.height - intrinsicSize.height;
      }
      case BOTTOM_RIGHT -> {
        drawX = computedSize.width - intrinsicSize.width;
        drawY = computedSize.height - intrinsicSize.height;
      }
    }

    child.getComputedPosition().set(drawX, drawY);
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
