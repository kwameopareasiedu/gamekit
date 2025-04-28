package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.ImageFit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/** A {@link Leaf} which renders a {@link BufferedImage} to the screen */
public class Image extends Leaf {
  protected final BufferedImage image;
  protected final ImageFit fit;

  public Image(ImageOptions options, BufferedImage image) {
    if (image == null)
      throw new NullPointerException("Image cannot be null");

    this.image = image;
    this.fit = options.fit;
  }

  public static Image create(ImageOptions options, BufferedImage image) {
    return new Image(options, image);
  }

  public static Image create(BufferedImage image) {
    return new Image(new ImageOptions(), image);
  }

  public static ImageOptions options() {
    return new ImageOptions();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicBounds.setSize(image.getWidth(), image.getHeight());

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );
  }

  @Override
  protected void performRender(Graphics2D g) {
    double dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

    switch (fit) {
      case FIT, CROP -> {
        double widthRatio = absoluteBounds.width / intrinsicBounds.width;
        double heightRatio = absoluteBounds.height / intrinsicBounds.height;

        double scaleRatio = fit == ImageFit.FIT ?
          intrinsicBounds.width > intrinsicBounds.height ? widthRatio : heightRatio :
          intrinsicBounds.width <= intrinsicBounds.height ? widthRatio : heightRatio;

        int scaledWidth = (int) (intrinsicBounds.width * scaleRatio);
        int scaledHeight = (int) (intrinsicBounds.height * scaleRatio);
        dx1 = absoluteBounds.x + (absoluteBounds.width - scaledWidth) / 2;
        dy1 = absoluteBounds.y + (absoluteBounds.height - scaledHeight) / 2;
        dx2 = dx1 + scaledWidth;
        dy2 = dy1 + scaledHeight;
      }
      case STRETCH -> {
        dx2 = absoluteBounds.width;
        dy2 = absoluteBounds.height;
      }
    }

    g.drawImage(
      image, (int) dx1, (int) dy1, (int) dx2, (int) dy2,
      0, 0, (int) intrinsicBounds.width, (int) intrinsicBounds.height, null
    );
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Image imageWidget) {
      return Objects.equals(image, imageWidget.image)
        && Objects.equals(fit, imageWidget.fit);
    }

    return false;
  }

  public static class ImageOptions {
    public ImageFit fit = ImageFit.FIT;

    public ImageOptions fit(ImageFit fit) {
      this.fit = fit;
      return this;
    }
  }
}
