package dev.gamekit.ui.mixins;

import dev.gamekit.core.UI;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.widgets.Widget;
import dev.gamekit.utils.Bounds;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Mixin for {@link Widget Widgets} which provides functionality for rendering images using the
 * <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-patch scaling algorithm</a>
 */
@SuppressWarnings("ExtractMethodRecommender")
public interface NinePatch {
  /** Called to render an image using the 9-patch algorithm */
  default void renderWith9PatchScaling(
    BufferedImage image,
    Bounds absoluteBounds,
    Spacing edgeInsets,
    Graphics2D graphics
  ) {
    double iw = image.getWidth();
    double ih = image.getHeight();

    double snl = edgeInsets.left;
    double snt = edgeInsets.top;
    double snr = iw - edgeInsets.right;
    double snb = ih - edgeInsets.bottom;

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

    double dnl = dx1 + edgeInsets.left;
    double dnt = dy1 + edgeInsets.top;
    double dnr = dx2 - edgeInsets.right;
    double dnb = dy2 - edgeInsets.bottom;

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
        image, (int) dest[0], (int) dest[1], (int) dest[2], (int) dest[3],
        (int) src[0], (int) src[1], (int) src[2], (int) src[3], null
      );
    }

    if (Widget.DEBUG_DRAW) {
      Color originalColor = graphics.getColor();
      Stroke originalStroke = graphics.getStroke();

      graphics.setColor(UI.DEBUG_COLOR);
      graphics.setStroke(UI.DEBUG_STROKE);
      graphics.drawRect(
        (int) (absoluteBounds.x + edgeInsets.left),
        (int) (absoluteBounds.y + edgeInsets.top),
        (int) (absoluteBounds.width - edgeInsets.getHorizontal()),
        (int) (absoluteBounds.height - edgeInsets.getVertical())
      );

      graphics.setColor(originalColor);
      graphics.setStroke(originalStroke);
    }
  }
}
