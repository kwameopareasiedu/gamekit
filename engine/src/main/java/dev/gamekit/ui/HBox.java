package dev.gamekit.ui;

import dev.gamekit.utils.Reusable;

import java.awt.*;

public class HBox extends Container {
  @Override
  protected void updateChildrenPositions() {
    int xpos = padding.left;

    for (var child : children) {
      xpos += child.margin.left;
      child.position.set(xpos, padding.top + child.margin.top);
      xpos += child.size.width + child.margin.right;
    }
  }

  @Override
  protected Dimension computeChildrenSize() {
    int combinedWidth = 0;
    int tallestHeight = 0;

    for (var child : children) {
      combinedWidth += child.size.width + child.margin.getHorizontal();
      tallestHeight = Math.max(tallestHeight, child.size.height + child.margin.getVertical());
    }

    Reusable.SIZE.setSize(combinedWidth, tallestHeight);
    return Reusable.SIZE;
  }
}
