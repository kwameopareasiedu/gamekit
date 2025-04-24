package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Bounds;
import dev.gamekit.ui.Spacing;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Mixin for {@link Widget Widgets} which render a 9-patch background */
public interface NinePatch {
  /** Called to render an image using the 9-patch algorithm */
  default void renderNinePatch(
    BufferedImage image,
    Bounds absoluteBounds,
    Spacing padding,
    Graphics2D g
  ) {
    int dx1 = absoluteBounds.x, dy1 = absoluteBounds.y;
    int dx2 = dx1 + absoluteBounds.width, dy2 = dy1 + absoluteBounds.height;

    int nl = padding.left;
    int nt = padding.top;
    int iw = image.getWidth();
    int ih = image.getHeight();
    int nr = iw - padding.right;
    int nb = ih - padding.bottom;

    int[][] srcBounds = new int[][]{
      new int[]{ 0, 0, nl, nt },
      new int[]{ nl, 0, nr, nt },
      new int[]{ nr, 0, iw, nt },
      new int[]{ 0, nt, nl, nb },
      new int[]{ nl, nt, nr, nb },
      new int[]{ nr, nt, iw, nb },
      new int[]{ 0, nb, nl, ih },
      new int[]{ nl, nb, nr, ih },
      new int[]{ nr, nb, iw, ih },
    };

    nl = dx1 + padding.left;
    nt = dy1 + padding.top;
    nr = dx2 - padding.right;
    nb = dy2 - padding.bottom;

    int[][] destBounds = new int[][]{
      new int[]{ dx1, dy1, nl, nt },
      new int[]{ nl, dy1, nr, nt },
      new int[]{ nr, dy1, dx2, nt },
      new int[]{ dx1, nt, nl, nb },
      new int[]{ nl, nt, nr, nb },
      new int[]{ nr, nt, dx2, nb },
      new int[]{ dx1, nb, nl, dy2 },
      new int[]{ nl, nb, nr, dy2 },
      new int[]{ nr, nb, dx2, dy2 },
    };

    for (int i = 0; i < srcBounds.length; i++) {
      int[] src = srcBounds[i];
      int[] dest = destBounds[i];

      g.drawImage(
        image, dest[0], dest[1], dest[2], dest[3],
        src[0], src[1], src[2], src[3], null
      );
    }
  }
}
