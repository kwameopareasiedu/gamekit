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

  public UIContainer() {
    children = new ArrayList<>();
    computeSize();
    computePosition();
  }

  public void add(UINode child) {
    if (!children.contains(child)) {
      children.add(child);
    }
  }

  protected void computePosition() {
    x = margin.left;
    y = margin.top;
  }

  protected void computeSize() {
    width = padding.getHorizontal();
    height = padding.getVertical();
  }

  @Override
  public BufferedImage getAppearance() {
    if (img == null || img.getWidth() != width || img.getHeight() != height) {
      img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      g = img.createGraphics();
    }

    g.setBackground(Color.RED);
    g.fillRect(0, 0, width, height);
    return img;
  }

  @Override
  public void onUpdate() {
    computeSize();
    computePosition();
  }
}
