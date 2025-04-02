package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.ImageFit;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/** A {@link Widget} which renders a {@link BufferedImage} to the screen */
public class Image extends Widget {
  protected final BufferedImage image;
  protected ImageFit imageFit;

  /* Draw bounds stored and only redrawn if they change */
  private int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

  protected Image(BufferedImage image) {
    if (image == null)
      throw new NullPointerException("Image cannot be null");

    this.image = image;
    this.imageFit = ImageFit.FIT;
  }

  public static Image create(BufferedImage image) {
    return new Image(image);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicBounds.setSize(image.getWidth(), image.getHeight());

    int computedWidth = constraints.constrainWidth(intrinsicBounds.width);
    int computedHeight = constraints.constrainHeight(intrinsicBounds.height);
    computedBounds.setSize(computedWidth, computedHeight);
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
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Image imageWidget) {
      return Objects.equals(image, imageWidget.image)
        && Objects.equals(imageFit, imageWidget.imageFit);
    }

    return false;
  }

  public Image withImageFit(ImageFit imageFit) {
    this.imageFit = imageFit;
    return this;
  }
}
