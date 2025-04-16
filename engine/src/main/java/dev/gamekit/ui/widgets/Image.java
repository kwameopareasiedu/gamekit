package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;
import dev.gamekit.ui.enums.ImageFit;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/** A {@link Widget} which renders a {@link BufferedImage} to the screen */
public class Image extends Widget {
  protected final BufferedImage image;
  protected final ImageFit imageFit;

  /* Draw bounds stored and only redrawn if they change */
  private int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

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
  public void performRender(Graphics2D g) {
    int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

    switch (imageFit) {
      case FIT, CROP -> {
        double widthRatio = (double) computedBounds.width / intrinsicBounds.width;
        double heightRatio = (double) computedBounds.height / intrinsicBounds.height;

        double scaleRatio = imageFit == ImageFit.FIT ?
          intrinsicBounds.width > intrinsicBounds.height ? widthRatio : heightRatio :
          intrinsicBounds.width <= intrinsicBounds.height ? widthRatio : heightRatio;

        int scaledWidth = (int) (intrinsicBounds.width * scaleRatio);
        int scaledHeight = (int) (intrinsicBounds.height * scaleRatio);
        dx1 = (computedBounds.width - scaledWidth) / 2;
        dy1 = (computedBounds.height - scaledHeight) / 2;
        dx2 = dx1 + scaledWidth;
        dy2 = dy1 + scaledHeight;
      }
      case STRETCH -> {
        dx2 = computedBounds.width;
        dy2 = computedBounds.height;
      }
    }

    if (this.dx1 != dx1 || this.dy1 != dy1 || this.dx2 != dx2 || this.dy2 != dy2) {
      g.setBackground(Constants.TRANSPARENT_COLOR);
      g.clearRect(0, 0, computedBounds.width, computedBounds.height);
      g.drawImage(image, dx1, dy1, dx2, dy2, 0, 0, intrinsicBounds.width, intrinsicBounds.height, null);

      this.dx1 = dx1;
      this.dy1 = dy1;
      this.dx2 = dx2;
      this.dy2 = dy2;
    }
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
