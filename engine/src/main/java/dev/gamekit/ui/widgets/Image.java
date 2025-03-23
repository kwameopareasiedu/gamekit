package dev.gamekit.ui.widgets;

import dev.gamekit.core.IO;
import dev.gamekit.ui.Node;
import dev.gamekit.utils.Constants;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Size;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.gamekit.utils.Math.clamp;

/**
 * A {@link Node} which loads an image from
 * a resource path and renders it to the screen
 */
public class Image extends Node {
  private static final Logger LOGGER = LogManager.getLogger();

  protected final String src;
  protected final BufferedImage srcImg;
  protected final Size size;
  protected final Fit fit;

  /* Draw bounds stored and only redrawn if they change */
  private int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

  public Image(String src) {
    this(src, Fit.FIT);
  }

  /** Creates a new Image node from the resource path and fit parameters */
  public Image(String src, Fit fit) {
    this.src = src;
    this.fit = fit;
    this.size = new Size(0, 0);
    srcImg = IO.loadImageResource(src);

    if (srcImg == null) {
      throw new NullPointerException(
        String.format("Unable to load image at %s", src)
      );
    }
  }

  @Override
  public void onLayout(Constraints constraints) {
    intrinsicSize.set(srcImg.getWidth(), srcImg.getHeight());

    int computedWidth = clamp(
      size.width > 0 ? size.width : intrinsicSize.width,
      constraints.minWidth, constraints.maxWidth
    );

    int computedHeight = clamp(
      size.height > 0 ? size.height : intrinsicSize.height,
      constraints.minHeight, constraints.maxHeight
    );

    computedSize.set(computedWidth, computedHeight);
  }

  @Override
  public Appearance getAppearance() {
    Appearance appearance = super.getAppearance();
    Graphics2D g = appearance.graphics;

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
      LOGGER.debug("Re-rendering image");
      g.setBackground(Constants.TRANSPARENT_COLOR);
      g.clearRect(0, 0, computedSize.width, computedSize.height);
      g.drawImage(srcImg, dx1, dy1, dx2, dy2, 0, 0, intrinsicSize.width, intrinsicSize.height, null);

      this.dx1 = dx1;
      this.dy1 = dy1;
      this.dx2 = dx2;
      this.dy2 = dy2;
    }

    return appearance;
  }

  /**
   * Returns the preferred size of this image.
   * @return the preferred size of this image
   */
  public Size getSize() { return size; }

  /** Determines how the image should be resized/fitted in its bounds */
  public enum Fit {
    FIT, CROP, STRETCH
  }
}
