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

  private ImageInterpolation interpolation;
  private ImageInterpolation prevInterpolation;

  public DrawImage(BufferedImage image, int x, int y, int width, int height) {
    this.image = image;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * A modifier which sets an {@link ImageInterpolation} strategy to the {@link Graphics2D} object.
   * <p>
   * This method returns the object on which it was called for further chaining
   */
  public final DrawImage withInterpolation(ImageInterpolation interpolation) {
    this.interpolation = interpolation;
    return this;
  }

  @Override
  protected void setup(Graphics2D g) {
    if (interpolation != null) {
      prevInterpolation = ImageInterpolation.from(g);

      interpolation.apply(g);
    }
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

  @Override
  protected void cleanup(Graphics2D g) {
    if (prevInterpolation != null)
      prevInterpolation.apply(g);
  }
}
