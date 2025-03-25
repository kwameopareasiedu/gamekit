package dev.gamekit.ui.widgets;

import dev.gamekit.core.IO;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Size;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;

/** A {@link Widget} which loads a <b>resource image</b> and renders it to the screen */
public class Image extends Widget {
  protected final String src;
  protected Size size;
  protected Fit fit;

  private final BufferedImage srcImg;

  /* Draw bounds stored and only redrawn if they change */
  private int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

  protected Image(String src) {
    this.src = src;
    this.fit = Fit.FIT;
    this.size = new Size(0, 0);
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
    intrinsicSize.set(srcImg.getWidth(), srcImg.getHeight());

    int computedWidth = clamp(
      size.width > 0 ? size.width : intrinsicSize.width,
      constraints.minWidth(), constraints.maxWidth()
    );

    int computedHeight = clamp(
      size.height > 0 ? size.height : intrinsicSize.height,
      constraints.minHeight(), constraints.maxHeight()
    );

    computedSize.set(computedWidth, computedHeight);
  }

  @Override
  public final void performRender(Graphics2D g) {
    int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

    switch (fit) {
      case FIT, CROP -> {
        double widthRatio = (double) computedSize.width / intrinsicSize.width;
        double heightRatio = (double) computedSize.height / intrinsicSize.height;

        double scaleRatio = fit == Fit.FIT ?
          intrinsicSize.width > intrinsicSize.height ? widthRatio : heightRatio :
          intrinsicSize.width <= intrinsicSize.height ? widthRatio : heightRatio;

        int scaledWidth = (int) (intrinsicSize.width * scaleRatio);
        int scaledHeight = (int) (intrinsicSize.height * scaleRatio);
        dx1 = (computedSize.width - scaledWidth) / 2;
        dy1 = (computedSize.height - scaledHeight) / 2;
        dx2 = dx1 + scaledWidth;
        dy2 = dy1 + scaledHeight;
      }
      case STRETCH -> {
        dx2 = computedSize.width;
        dy2 = computedSize.height;
      }
    }

    if (this.dx1 != dx1 || this.dy1 != dy1 || this.dx2 != dx2 || this.dy2 != dy2) {
      logger.debug("Rendering");
      g.setBackground(Constants.TRANSPARENT_COLOR);
      g.clearRect(0, 0, computedSize.width, computedSize.height);
      g.drawImage(srcImg, dx1, dy1, dx2, dy2, 0, 0, intrinsicSize.width, intrinsicSize.height, null);

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
        && Objects.equals(fit, imageWidget.fit)
        && Objects.equals(size, imageWidget.size);
    }

    return false;
  }

  public Image withSize(int width, int height) {
    this.size.set(width, height);
    return this;
  }

  public Image withFit(Fit fit) {
    this.fit = fit;
    return this;
  }

  /** Determines how the image should be resized/fitted in its bounds */
  public enum Fit {
    /** Resize the image to fit within the bounds */
    FIT,
    /** Cutout the portions of the image which are outside the bounds */
    CROP,
    /** Stretch the image to completely cover the bounds */
    STRETCH
  }
}
