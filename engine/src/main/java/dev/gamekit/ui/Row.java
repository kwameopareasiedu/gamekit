package dev.gamekit.ui;

import dev.gamekit.utils.Reusable;

public class HBox extends Container {
  @Override
  protected Size getContentSize() {
    int combinedWidth = 0;
    int tallestHeight = 0;

    for (var child : children) {
      combinedWidth += child.size.width + child.margin.getHorizontal();
      tallestHeight = Math.max(tallestHeight, child.size.height + child.margin.getVertical());
    }

    Reusable.SIZE.set(combinedWidth, tallestHeight);
    return Reusable.SIZE;
  }

  @Override
  protected void updateContentPositions() {
    int xpos = padding.left;

    for (var child : children) {
      xpos += child.margin.left;
      child.position.set(xpos, padding.top + child.margin.top);
      xpos += child.size.width + child.margin.right;
    }
  }
}
