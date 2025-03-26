package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Alignment;
import dev.gamekit.ui.Constraints;

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

    int computedWidth = clamp(
      child.computedBounds.width,
      constraints.minWidth(),
      constraints.maxWidth()
    );

    int computedHeight = clamp(
      child.computedBounds.height,
      constraints.minHeight(),
      constraints.maxHeight()
    );

    intrinsicBounds.setSize(child.computedBounds.width, child.computedBounds.height);
    computedBounds.setSize(computedWidth, computedHeight);

    int drawX = 0, drawY = 0;

    switch (alignment) {
      case TOP_LEFT -> {
        drawX = 0;
        drawY = 0;
      }
      case TOP_CENTER -> {
        drawX = computedBounds.width / 2 - intrinsicBounds.width / 2;
        drawY = 0;
      }
      case TOP_RIGHT -> {
        drawX = computedBounds.width - intrinsicBounds.width;
        drawY = 0;
      }
      case LEFT -> {
        drawX = 0;
        drawY = computedBounds.height / 2 - intrinsicBounds.height / 2;
      }
      case CENTER -> {
        drawX = computedBounds.width / 2 - intrinsicBounds.width / 2;
        drawY = computedBounds.height / 2 - intrinsicBounds.height / 2;
      }
      case RIGHT -> {
        drawX = computedBounds.width - intrinsicBounds.width;
        drawY = computedBounds.height / 2 - intrinsicBounds.height / 2;
      }
      case BOTTOM_LEFT -> {
        drawX = 0;
        drawY = computedBounds.height - intrinsicBounds.height;
      }
      case BOTTOM_CENTER -> {
        drawX = computedBounds.width / 2 - intrinsicBounds.width / 2;
        drawY = computedBounds.height - intrinsicBounds.height;
      }
      case BOTTOM_RIGHT -> {
        drawX = computedBounds.width - intrinsicBounds.width;
        drawY = computedBounds.height - intrinsicBounds.height;
      }
    }

    child.computedBounds.setPosition(drawX, drawY);
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
