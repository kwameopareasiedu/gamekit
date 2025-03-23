package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Container;
import dev.gamekit.utils.Size;
import dev.gamekit.utils.Reusable;

/** Column container arranges its children horizontally */
public class Column extends Container {
  @Override
  protected Size getContentSize() {
    int widestWidth = 0;
    int combinedHeight = 0;

    for (var child : children) {
      widestWidth = Math.max(widestWidth, child.getComputedSize().width);
      combinedHeight += child.getComputedSize().height;
    }

    Reusable.SIZE.set(widestWidth, combinedHeight);
    return Reusable.SIZE;
  }

  @Override
  protected void updateContentPositions() {
    int ypos = 0;

    for (var child : children) {
      child.getComputedPosition().set(0, ypos);
      ypos += child.getComputedSize().height;
    }
  }
}
