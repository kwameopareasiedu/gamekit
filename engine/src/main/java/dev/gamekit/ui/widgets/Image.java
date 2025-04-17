package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;
import dev.gamekit.ui.enums.ImageFit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/** A {@link Leaf} which renders a {@link BufferedImage} to the screen */
public class Image extends Leaf {
  protected final BufferedImage image;
  protected final ImageFit imageFit;

  public Image(BufferedImage image, ImageFit imageFit) {
    if (image == null)
      throw new NullPointerException("Image cannot be null");

    this.image = image;
    this.imageFit = imageFit;
  }

  @SafeVarargs
  public static Image create(Param<? super ImageParam>... params) {
    return new Image(
      Param.getValue(params, "image", null),
      Param.getValue(params, "imageFit", ImageFit.FIT)
    );
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
    int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

    switch (imageFit) {
      case FIT, CROP -> {
        double widthRatio = (double) absoluteBounds.width / intrinsicBounds.width;
        double heightRatio = (double) absoluteBounds.height / intrinsicBounds.height;

        double scaleRatio = imageFit == ImageFit.FIT ?
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

    g.drawImage(image, dx1, dy1, dx2, dy2, 0, 0, intrinsicBounds.width, intrinsicBounds.height, null);
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Image imageWidget) {
      return Objects.equals(image, imageWidget.image)
        && Objects.equals(imageFit, imageWidget.imageFit);
    }

    return false;
  }
}
