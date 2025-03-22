package dev.gamekit.ui;

import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/** Base class for all UI containers in the engine */
public abstract class Container extends Node {
  protected final List<Node> children;

  public Container() {
    children = new ArrayList<>();
  }

  public void addChild(Node child) {
    if (!children.contains(child)) {
      children.add(child);
    }
  }

  @Override
  public void onUpdate() {
    children.forEach(Node::onUpdate);
    computeSize();
    computePosition();
  }

  @Override
  public BufferedImage getAppearance() {
    if (image == null || image.getWidth() != size.width || image.getHeight() != size.height) {
      image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
      graphics = image.createGraphics();
    }

    graphics.setColor(Constants.TRANSPARENT);
    graphics.fillRect(0, 0, size.width, size.height);

    // The children are drawn in the container's image
    // instead of calling their onRender. This allows
    // for clipping if the child's bounds fall outside
    // that of the container.
    children.forEach(child ->
      graphics.drawImage(
        child.getAppearance(),
        child.getPosition().x,
        child.getPosition().y,
        null
      )
    );

    return image;
  }

  protected void computePosition() {
    position.set(margin.left, margin.top);
    updateChildrenPositions();
  }

  protected void computeSize() {
    Dimension contentSize = computeChildrenSize();
    size.set(
      padding.getHorizontal() + contentSize.width,
      padding.getVertical() + contentSize.height
    );
  }

  protected abstract void updateChildrenPositions();

  protected abstract Dimension computeChildrenSize();
}
