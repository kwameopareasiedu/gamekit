package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Container;
import dev.gamekit.ui.Node;
import dev.gamekit.utils.Alignment;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Size;

import java.util.List;

import static dev.gamekit.utils.Math.clamp;

/** A {@link Node} which aligns its single child based on an {@link Alignment} */
public class Align extends Container {
  protected final Node child;
  protected final List<Node> children;
  protected Alignment alignment;

  protected Align(Node child, Alignment alignment) {
    this.child = child;
    this.children = List.of(child);
    this.alignment = alignment;
  }

  public static Builder create(Node child, Alignment alignment) {
    return new Builder(child, alignment);
  }

  @Override
  protected List<Node> getChildren() { return children; }

  @Override
  public void onLayout(Constraints constraints) {
    Constraints c = constraints.update(
      0, constraints.maxWidth, 0, constraints.maxHeight
    );

    child.onLayout(c);

    Size childSize = child.getComputedSize();
    int computedWidth = clamp(childSize.width, constraints.minWidth, constraints.maxWidth);
    int computedHeight = clamp(childSize.height, constraints.minHeight, constraints.maxHeight);

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

  public static class Builder {
    private final Align instance;

    private Builder(Node child, Alignment alignment) {
      instance = new Align(child, alignment);
    }

    public Align get() { return instance; }

    public Builder withAlignment(Alignment alignment) {
      instance.alignment = alignment;
      return this;
    }
  }
}
