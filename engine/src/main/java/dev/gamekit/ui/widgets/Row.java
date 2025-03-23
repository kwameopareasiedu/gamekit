package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Container;
import dev.gamekit.ui.Node;
import dev.gamekit.utils.Constraints;

import java.util.List;

/** Column container arranges its children vertically */
public class Row extends Container {
  @Override
  protected List<Node> getChildren() {
    return List.of();
  }

  @Override
  public void onLayout(Constraints constraints) {

  }

  //  @Override
  //  protected Size getContentSize() {
  //    int combinedWidth = 0;
  //    int tallestHeight = 0;
  //
  //    for (var child : children) {
  //      combinedWidth += child.getComputedSize().width;
  //      tallestHeight = Math.max(tallestHeight, child.getComputedSize().height);
  //    }
  //
  //    Reusable.SIZE.set(combinedWidth, tallestHeight);
  //    return Reusable.SIZE;
  //  }
  //
  //  @Override
  //  protected void updateContentPositions() {
  //    int xpos = 0;
  //
  //    for (var child : children) {
  //      child.getComputedPosition().set(xpos, 0);
  //      xpos += child.getComputedSize().width;
  //    }
  //  }
}
