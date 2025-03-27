package dev.gamekit.ui.widgets;

import dev.gamekit.core.IO;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.ImageFit;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/** A {@link Widget} which loads a <b>resource image</b> and renders it to the screen */
public class Image extends Widget {
  protected final String src;
  protected int width;
  protected int height;
  protected ImageFit imageFit;

  private final BufferedImage srcImg;

  /* Draw bounds stored and only redrawn if they change */
  private int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

  protected Image(String src) {
    this.src = src;
    this.imageFit = ImageFit.FIT;
    this.width = 0;
    this.height = 0;
    srcImg = IO.loadImageResource(src);

    if (srcImg == null) {
      throw new NullPointerException(
        String.format("Unable to load image at %s", src)
      );
    }
  }

  public static Image create(String src) {
    return new Image(src);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicBounds.setSize(srcImg.getWidth(), srcImg.getHeight());

    int computedWidth = constraints.constrainWidth(width > 0 ? width : intrinsicBounds.width);
    int computedHeight = constraints.constrainHeight(height > 0 ? height : intrinsicBounds.height);
    computedBounds.setSize(computedWidth, computedHeight);
  }

  @Override
  public final void performRender(Graphics2D g) {
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
      g.drawImage(srcImg, dx1, dy1, dx2, dy2, 0, 0, intrinsicBounds.width, intrinsicBounds.height, null);

      this.dx1 = dx1;
      this.dy1 = dy1;
      this.dx2 = dx2;
      this.dy2 = dy2;
    }
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Image imageWidget) {
      return Objects.equals(src, imageWidget.src)
        && Objects.equals(imageFit, imageWidget.imageFit)
        && Objects.equals(width, imageWidget.width)
        && Objects.equals(height, imageWidget.height);
    }

    return false;
  }

  public Image withSize(int width, int height) {
    this.width = width;
    this.height = height;
    return this;
  }

  public Image withImageFit(ImageFit imageFit) {
    this.imageFit = imageFit;
    return this;
  }
}
