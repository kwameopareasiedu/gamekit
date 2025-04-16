package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;
import dev.gamekit.ui.Spacing;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * A {@link SingleChildParent} which uses the 9-patch algorithm to render a
 * {@link BufferedImage} as a background to its descendants
 */
public class Panel extends SingleChildParent {
  protected final BufferedImage background;
  protected final Spacing spacing;

  public Panel(BufferedImage background, Spacing spacing, Widget child) {
    super(child);

    if (background == null)
      throw new NullPointerException("Image cannot be null");

    this.background = background;
    this.spacing = spacing;
  }

  @SafeVarargs
  public static Panel create(Param<? super PanelParam>... params) {
    return new Panel(
      Param.getValue(params, "background", Constants.DEFAULT_PANEL_BG),
      Param.getValue(params, "spacing", new Spacing(12)),
      Param.getValue(params, "child", null)
    );
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
      Math.max(background.getWidth(), child.computedBounds.width),
      Math.max(background.getHeight(), child.computedBounds.height)
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
    int iw = background.getWidth();
    int ih = background.getHeight();
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
        background, dest[0], dest[1], dest[2], dest[3],
        src[0], src[1], src[2], src[3], null
      );
    }
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Panel panelWidget) {
      return Objects.equals(background, panelWidget.background)
        && Objects.equals(spacing, panelWidget.spacing);
    }

    return false;
  }
}
