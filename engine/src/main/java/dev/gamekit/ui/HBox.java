package dev.gamekit.ui;

import dev.gamekit.utils.Reusable;

import java.awt.*;

public class HBox extends UIContainer {
  @Override
  protected void updateChildrenPositions() {
    int x = padding.left;

    for (var child : children) {
      x += child.margin.left;
      child.x = x;
      child.y = padding.top + child.margin.top;
      x += child.width + child.margin.right;
    }
  }

  @Override
  protected Dimension computeChildrenSize() {
    int combinedWidth = 0;
    int tallestHeight = 0;

    for (var child:children) {
      combinedWidth += child.width + child.margin.getHorizontal();
      tallestHeight = Math.max(tallestHeight, child.height + child.margin.getVertical());
    }

    Reusable.SIZE.setSize(combinedWidth, tallestHeight);
    return Reusable.SIZE;
  }
}
