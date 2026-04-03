package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;

/** {@link Measure} is a {@link SingleChildParent} which overlays a ruler on its children */
@WidgetBuilder
public class Measure extends SingleChildParent {
  private static final Color OVERLAY_COLOR = new Color(0x6F000000, true);

  @WidgetBuilderField(themable = false)
  public Boolean showRuler;
  @WidgetBuilderField(themable = false)
  public Integer rulerTicks;

  public Measure(MeasureConfig config, Widget child) {
    super(config, child);
  }

  public static Measure create(MeasureConfig.Updater updater, Widget child) {
    return new Measure(Widgets.configureMeasure(updater), child);
  }

  @Override
  protected void performInit() {
    if (rulerTicks == null)
      rulerTicks = 5;

    if (rulerTicks < 1)
      throw new IllegalArgumentException("Measure rulerTicks cannot be less than 1");

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(constraints);

    intrinsicSize.set(
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }

  @Override
  protected void postRender(Graphics2D g) {
    if (showRuler) {
      Color originalColor = g.getColor();

      g.setColor(OVERLAY_COLOR);
      g.fillRect(
        (int) absoluteBounds.x,
        (int) absoluteBounds.y,
        (int) absoluteBounds.width,
        (int) absoluteBounds.height
      );

      int absX = (int) absoluteBounds.x;
      int absY = (int) absoluteBounds.y;
      int absW = (int) absoluteBounds.width;
      int absH = (int) absoluteBounds.height;

      for (int x = rulerTicks; x < absW; x += rulerTicks) {
        g.setColor(Color.WHITE);
        g.drawLine(absX + x, absY, absX + x, absY + 10);
        g.drawString(String.valueOf(x), absX + x, absY + 32);
      }

      g.drawLine(absX + absW, absY, absX + absW, absY + 10);
      g.drawString(String.valueOf(absW), absX + absW - 32, absY + 64);

      for (int y = rulerTicks; y < absH; y += rulerTicks) {
        g.setColor(Color.WHITE);
        g.drawLine(absX, absY + y, absX + 10, absY + y);
        g.drawString(String.valueOf(y), absX + 16, absY + y + 8);
      }
      g.drawLine(absX, absY + absH, absX + 10, absY + absH);
      g.drawString(String.valueOf(absH), absX + 16, absY + absH);

      g.drawString(
        String.format("%d x %d", absW, absH),
        absX + absW / 2,
        absY + absH / 2
      );

      g.setColor(originalColor);
    }
  }
}
