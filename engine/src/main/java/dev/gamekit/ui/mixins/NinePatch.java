package dev.gamekit.ui.mixins;

import dev.gamekit.core.Constants;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.widgets.Widget;
import dev.gamekit.utils.Bounds;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Mixin for {@link Widget Widgets} which provides functionality for rendering images using the
 * <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-patch scaling algorithm</a>
 */
public interface NinePatch {
  /** Called to render an image using the 9-patch algorithm */
  default void renderWith9PatchScaling(
    BufferedImage image,
    Bounds absoluteBounds,
    Spacing ninePatchSpacing,
    Graphics2D graphics
  ) {
    double iw = image.getWidth();
    double ih = image.getHeight();

    double nl = ninePatchSpacing.left;
    double nt = ninePatchSpacing.top;
    double nr = iw - ninePatchSpacing.right;
    double nb = ih - ninePatchSpacing.bottom;

    double[][] srcBounds = new double[][]{
      new double[]{ 0, 0, nl, nt },
      new double[]{ nl, 0, nr, nt },
      new double[]{ nr, 0, iw, nt },

      new double[]{ 0, nt, nl, nb },
      new double[]{ nl, nt, nr, nb },
      new double[]{ nr, nt, iw, nb },

      new double[]{ 0, nb, nl, ih },
      new double[]{ nl, nb, nr, ih },
      new double[]{ nr, nb, iw, ih },
    };

    double dx1 = absoluteBounds.x;
    double dy1 = absoluteBounds.y;
    double dx2 = dx1 + absoluteBounds.width;
    double dy2 = dy1 + absoluteBounds.height;

    nl = dx1 + ninePatchSpacing.left;
    nt = dy1 + ninePatchSpacing.top;
    nr = dx2 - ninePatchSpacing.right;
    nb = dy2 - ninePatchSpacing.bottom;

    double[][] destBounds = new double[][]{
      new double[]{ dx1, dy1, nl, nt },
      new double[]{ nl, dy1, nr, nt },
      new double[]{ nr, dy1, dx2, nt },

      new double[]{ dx1, nt, nl, nb },
      new double[]{ nl, nt, nr, nb },
      new double[]{ nr, nt, dx2, nb },

      new double[]{ dx1, nb, nl, dy2 },
      new double[]{ nl, nb, nr, dy2 },
      new double[]{ nr, nb, dx2, dy2 },
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

      graphics.setColor(Color.RED);
      graphics.setStroke(Constants.DEBUG_STROKE);
      graphics.drawRect(
        (int) (absoluteBounds.x + ninePatchSpacing.left),
        (int) (absoluteBounds.y + ninePatchSpacing.top),
        (int) (absoluteBounds.width - ninePatchSpacing.getHorizontal()),
        (int) (absoluteBounds.height - ninePatchSpacing.getVertical())
      );

      graphics.setColor(originalColor);
      graphics.setStroke(originalStroke);
    }
  }
}
