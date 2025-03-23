package dev.gamekit.ui;

import dev.gamekit.utils.Constants;

import java.awt.*;
import java.util.List;

/** Base class for all UI containers in the engine */
public abstract class Container extends Node {
  //  public void addChild(Node child) {
  //    if (!children.contains(child)) {
  //      children.add(child);
  //    }
  //  }
  //
  //  @Override
  //  public void onLayout(Constraints constraints) {
  //    List<Node> children = getChildren();
  //
  //    for (Node child : children) {
  //
  //    }
  //    children.forEach(Node::onLayout);
  //
  //    Size contentSize = getContentSize();
  //    computedSize.set(
  //      contentSize.width + padding.getHorizontal(),
  //      contentSize.height + padding.getVertical()
  //    );
  //
  //    computedPosition.set(margin.left, margin.top);
  //    updateContentPositions();
  //  }

  @Override
  public final Appearance getAppearance() {
    Appearance appearance = super.getAppearance();
    Graphics2D g = appearance.graphics;
    List<Node> children = getChildren();

    g.setColor(Constants.TRANSPARENT_COLOR);
    g.fillRect(0, 0, computedSize.width, computedSize.height);

    // The children are drawn in the container's image instead of calling their onRender.
    // This allows for clipping if the child's bounds fall outside that of the container.
    children.forEach(child ->
      g.drawImage(
        child.getAppearance().image,
        child.getComputedPosition().x,
        child.getComputedPosition().y,
        null
      )
    );

    return appearance;
  }

  /**
   * Abstract method which returns the list of children to iterate over.
   * This method exists because some containers may either have a
   * single child or multiple children.
   * @return the list of child nodes
   */
  protected abstract List<Node> getChildren();

  //  /**
  //   * Abstract method which measures the size of the smallest bounding box which contains all children
  //   * @return The size of the content
  //   */
  //  protected abstract Size getContentSize();
  //
  //  /** Abstract method which updates the positions of children within this container */
  //  protected abstract void updateContentPositions();
}
