package dev.gamekit.ui;

import dev.gamekit.utils.Reusable;

public class VBox extends Container {
  @Override
  protected Size getContentSize() {
    int widestWidth = 0;
    int combinedHeight = 0;

    for (var child : children) {
      widestWidth = Math.max(widestWidth, child.size.width + child.margin.getHorizontal());
      combinedHeight += child.size.height + child.margin.getVertical();
    }

    Reusable.SIZE.set(widestWidth, combinedHeight);
    return Reusable.SIZE;
  }

  @Override
  protected void updateContentPositions() {
    int ypos = padding.top;

    for (var child : children) {
      ypos += child.margin.top;
      child.position.set(padding.left + child.margin.left, ypos);
      ypos += child.size.height + child.margin.bottom;
    }
  }
}
