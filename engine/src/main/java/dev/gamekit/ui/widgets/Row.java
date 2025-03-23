package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Container;
import dev.gamekit.utils.Size;
import dev.gamekit.utils.Reusable;

/** Column container arranges its children vertically */
public class Row extends Container {
  @Override
  protected Size getContentSize() {
    int combinedWidth = 0;
    int tallestHeight = 0;

    for (var child : children) {
      combinedWidth += child.getComputedSize().width;
      tallestHeight = Math.max(tallestHeight, child.getComputedSize().height);
    }

    Reusable.SIZE.set(combinedWidth, tallestHeight);
    return Reusable.SIZE;
  }

  @Override
  protected void updateContentPositions() {
    int xpos = 0;

    for (var child : children) {
      child.getComputedPosition().set(xpos, 0);
      xpos += child.getComputedSize().width;
    }
  }
}
