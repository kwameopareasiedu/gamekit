package dev.gamekit.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/** Base class for all UI containers in the engine */
public abstract class UIContainer extends UINode {
  protected final List<UINode> children;
  private BufferedImage img;
  private Graphics2D g;
  private Color bgColor;

  public UIContainer() {
    children = new ArrayList<>();
    bgColor = Color.GRAY;
    computeSize();
    computePosition();
  }

  public void addChild(UINode child) {
    if (!children.contains(child)) {
      children.add(child);
    }
  }

  public void setBgColor(Color bgColor) {
    this.bgColor = bgColor;
  }

  @Override
  public void onUpdate() {
    children.forEach(UINode::onUpdate);
    computeSize();
    computePosition();
  }

  @Override
  public BufferedImage getAppearance() {
    if (img == null || img.getWidth() != size.width || img.getHeight() != size.height) {
      img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
      g = img.createGraphics();
    }

    g.setColor(bgColor);
    g.fillRect(0, 0, size.width, size.height);

    // The children are drawn in the container's image instead of calling their onRender.
    // This allows for clipping if the child's bounds fall outside that of the container.
    children.forEach(child -> g.drawImage(child.getAppearance(), child.getPosition().x, child.getPosition().y, null));

    return img;
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
