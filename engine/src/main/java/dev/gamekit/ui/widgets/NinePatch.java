package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * A {@link SingleChildParent} which uses the 9-patch algorithm to render a
 * {@link BufferedImage} as a background to its descendants
 */
public class NinePatch extends SingleChildParent {
  protected final Spacing spacing;
  protected final BufferedImage image;

  protected NinePatch(BufferedImage image, Widget child) {
    super(child);

    if (image == null)
      throw new NullPointerException("Image cannot be null");

    this.image = image;
    this.spacing = new Spacing(0, 0, 0, 0);
  }

  public static NinePatch create(BufferedImage image, Widget child) {
    return new NinePatch(image, child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(
      Math.max(image.getWidth(), child.computedBounds.width),
      Math.max(image.getHeight(), child.computedBounds.height)
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.computedBounds.setPosition(
      computedBounds.width / 2 - child.computedBounds.width / 2,
      computedBounds.height / 2 - child.computedBounds.height / 2
    );
  }

  @Override
  public void renderBackground(Graphics2D g) {
    super.renderBackground(g);

    int dx2 = computedBounds.width, dy2 = computedBounds.height;

    g.setBackground(Constants.TRANSPARENT_COLOR);
    g.clearRect(0, 0, computedBounds.width, computedBounds.height);

    int nl = spacing.left;
    int nt = spacing.top;
    int iw = image.getWidth();
    int ih = image.getHeight();
    int nr = iw - spacing.right;
    int nb = ih - spacing.bottom;

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
