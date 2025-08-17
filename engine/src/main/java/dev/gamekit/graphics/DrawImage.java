package dev.gamekit.graphics;

import dev.gamekit.settings.ImageInterpolation;

import java.awt.*;
import java.awt.image.BufferedImage;

/** {@link DrawImage} renders a <b>center-origin</b> image to the window */
public class DrawImage extends DrawCall<DrawImage> {
  private final BufferedImage image;
  private final int x, y;
  private final int width;
  private final int height;
  private final ImageInterpolation interpolation;

  public DrawImage(
    BufferedImage image, int x, int y, int width, int height, ImageInterpolation interpolation
  ) {
    this.image = image;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.interpolation = interpolation;
  }

  public DrawImage(BufferedImage image, int x, int y, int width, int height) {
    this(image, x, y, width, height, ImageInterpolation.DEFAULT);
  }

  @Override
  protected void draw(Graphics2D g) {
    ImageInterpolation originalInterpolation = ImageInterpolation.from(g);
    interpolation.apply(g);

    int x0 = x - width / 2, y0 = y + height / 2;
    int x1 = x0 + width, y1 = y0 - height;
    g.drawImage(image, x0, -y0, x1, -y1, 0, 0, image.getWidth(), image.getHeight(), null);

    if (originalInterpolation != null)
      originalInterpolation.apply(g);
  }
}
