package dev.gamekit.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

/** {@link DrawImage} renders an image to the window */
public class DrawImage extends DrawCall {
  private final BufferedImage image;
  private final int x, y;
  private final int width;
  private final int height;

  public DrawImage(BufferedImage image, int x, int y, int width, int height) {
    this.image = image;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  @Override
  protected void draw(Graphics2D g) {
    int x0 = x - width / 2, y0 = y + height / 2;
    int x1 = x0 + width, y1 = y0 - height;
    g.drawImage(image, x0, -y0, x1, -y1, 0, 0, image.getWidth(), image.getHeight(), null);
  }
}
