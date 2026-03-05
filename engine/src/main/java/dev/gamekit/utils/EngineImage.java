package dev.gamekit.utils;

import dev.gamekit.core.UI;
import dev.gamekit.ui.widgets.Widget;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * {@link EngineImage} is an extension {@link BufferedImage} with engine-specific features such as:
 * <ul>
 *   <li>Insets for <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-patch</a> rendering</li>
 * </ul>
 */
public class EngineImage extends BufferedImage {
  public final int topInset;
  public final int rightInset;
  public final int bottomInset;
  public final int leftInset;
  public final int verticalInset;
  public final int horizontalInset;

  public EngineImage(BufferedImage src) {
    this(src, 0, 0, 0, 0);
  }

  public EngineImage(BufferedImage src, int topInset, int rightInset, int bottomInset, int leftInset) {
    this(src, 0, 0, src.getWidth(), src.getHeight(), topInset, rightInset, bottomInset, leftInset);
  }

  public EngineImage(
    BufferedImage src,
    int sliceX, int sliceY, int sliceWidth, int sliceHeight,
    int topInset, int rightInset, int bottomInset, int leftInset
  ) {
    super(sliceWidth, sliceHeight, src.getType());

    this.topInset = topInset;
    this.rightInset = rightInset;
    this.bottomInset = bottomInset;
    this.leftInset = leftInset;
    this.verticalInset = topInset + bottomInset;
    this.horizontalInset = leftInset + rightInset;

    Graphics g = createGraphics();
    g.drawImage(src, 0, 0, sliceWidth, sliceHeight, sliceX, sliceY, sliceX + sliceWidth, sliceY + sliceHeight, null);
    g.dispose();
  }

  /** Renders this image to the provided {@link Graphics2D} target with the specified {@link Spacing} bounds */
  public void render(Graphics2D graphics, Bounds absoluteBounds) {
    double iw = getWidth();
    double ih = getHeight();

    double snl = leftInset;
    double snt = topInset;
    double snr = iw - rightInset;
    double snb = ih - bottomInset;

    double[][] srcBounds = new double[][]{
      new double[]{ 0, 0, snl, snt },
      new double[]{ snl, 0, snr, snt },
      new double[]{ snr, 0, iw, snt },

      new double[]{ 0, snt, snl, snb },
      new double[]{ snl, snt, snr, snb },
      new double[]{ snr, snt, iw, snb },

      new double[]{ 0, snb, snl, ih },
      new double[]{ snl, snb, snr, ih },
      new double[]{ snr, snb, iw, ih },
    };

    double dx1 = absoluteBounds.x;
    double dy1 = absoluteBounds.y;
    double dx2 = dx1 + absoluteBounds.width;
    double dy2 = dy1 + absoluteBounds.height;

    double dnl = dx1 + leftInset;
    double dnt = dy1 + topInset;
    double dnr = dx2 - rightInset;
    double dnb = dy2 - bottomInset;

    double[][] destBounds = new double[][]{
      new double[]{ dx1, dy1, dnl, dnt },
      new double[]{ dnl, dy1, dnr, dnt },
      new double[]{ dnr, dy1, dx2, dnt },

      new double[]{ dx1, dnt, dnl, dnb },
      new double[]{ dnl, dnt, dnr, dnb },
      new double[]{ dnr, dnt, dx2, dnb },

      new double[]{ dx1, dnb, dnl, dy2 },
      new double[]{ dnl, dnb, dnr, dy2 },
      new double[]{ dnr, dnb, dx2, dy2 },
    };

    for (int i = 0; i < srcBounds.length; i++) {
      double[] src = srcBounds[i];
      double[] dest = destBounds[i];

      graphics.drawImage(
        this, (int) dest[0], (int) dest[1], (int) dest[2], (int) dest[3],
        (int) src[0], (int) src[1], (int) src[2], (int) src[3], null
      );
    }

    if (Widget.DEBUG_DRAW) {
      Color originalColor = graphics.getColor();
      Stroke originalStroke = graphics.getStroke();

      graphics.setColor(UI.DEBUG_COLOR);
      graphics.setStroke(UI.DEBUG_STROKE);
      graphics.drawRect(
        (int) (absoluteBounds.x + leftInset),
        (int) (absoluteBounds.y + topInset),
        (int) (absoluteBounds.width - horizontalInset),
        (int) (absoluteBounds.height - verticalInset)
      );

      graphics.setColor(originalColor);
      graphics.setStroke(originalStroke);
    }
  }
}
