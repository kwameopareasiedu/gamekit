package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * A {@link Widget} which uses the 9-patch algorithm to render a
 * {@link BufferedImage} to the screen
 */
public class NinePatch extends Widget {
  protected final Spacing spacing;
  protected final BufferedImage image;

  protected NinePatch(BufferedImage image) {
    if (image == null)
      throw new NullPointerException("Image cannot be null");

    this.image = image;
    this.spacing = new Spacing(0, 0, 0, 0);
  }

  public static NinePatch create(BufferedImage image) {
    return new NinePatch(image);
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
    int dx2 = computedBounds.width, dy2 = computedBounds.height;

    g.setBackground(Constants.TRANSPARENT_COLOR);
    g.clearRect(0, 0, computedBounds.width, computedBounds.height);

    int nl = spacing.left;
    int nt = spacing.top;
    int nr = intrinsicBounds.width - spacing.right;
    int nb = intrinsicBounds.height - spacing.bottom;

    int[][] srcBounds = new int[][]{
      new int[]{ 0, 0, nl, nt },
      new int[]{ nl, 0, nr, nt },
      new int[]{ nr, 0, intrinsicBounds.width, nt },
      new int[]{ 0, nt, nl, nb },
      new int[]{ nl, nt, nr, nb },
      new int[]{ nr, nt, intrinsicBounds.width, nb },
      new int[]{ 0, nb, nl, intrinsicBounds.height },
      new int[]{ nl, nb, nr, intrinsicBounds.height },
      new int[]{ nr, nb, intrinsicBounds.width, intrinsicBounds.height },
    };

    nl = spacing.left;
    nt = spacing.top;
    nr = dx2 - spacing.right;
    nb = dy2 - spacing.bottom;

    int[][] destBounds = new int[][]{
      new int[]{ 0, 0, nl, nt },
      new int[]{ nl, 0, nr, nt },
      new int[]{ nr, 0, dx2, nt },
      new int[]{ 0, nt, nl, nb },
      new int[]{ nl, nt, nr, nb },
      new int[]{ nr, nt, dx2, nb },
      new int[]{ 0, nb, nl, dy2 },
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

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof NinePatch ninePatchWidget) {
      return Objects.equals(image, ninePatchWidget.image)
        && Objects.equals(spacing, ninePatchWidget.spacing);
    }

    return false;
  }

  public NinePatch withSpacing(int top, int right, int bottom, int left) {
    this.spacing.set(top, right, bottom, left);
    return this;
  }
}
