package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.IO;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.EngineImage;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** A {@link SingleChildParent} which renders a 9-patch background or a solid color behind its children */
@WidgetBuilder
public class Panel extends SingleChildParent implements MouseEvent.Handler {
  public static final EngineImage DEFAULT_BG = IO.getImageSlice("default-sprites.png", 470, 64, 120, 120);

  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Panel.DEFAULT_BG")
  protected EngineImage background;
  @WidgetBuilderField
  protected Color color;
  @WidgetBuilderField(fallback = "0")
  protected Integer cornerRadius;
  @WidgetBuilderField(fallback = "true")
  protected Boolean clip;

  private Shape originalClip;
  private Color originalColor;

  public Panel(PanelConfig config, Widget child) {
    super(config, child);
  }

  public static Panel create(PanelConfig.Updater updater, Widget child) {
    return new Panel(Widgets.configurePanel(updater), child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicSize.set(child.computedBounds.width, child.computedBounds.height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    child.computedBounds.setPosition(0, 0);
  }

  @Override
  protected void preRender(Graphics2D g) {
    if (clip) {
      originalClip = g.getClip();

      g.setClip(
        new RoundRectangle2D.Double(
          absoluteBounds.x, absoluteBounds.y,
          absoluteBounds.width, absoluteBounds.height,
          cornerRadius, cornerRadius
        )
      );
    }

    originalColor = g.getColor();

    g.setColor(color);
  }

  @Override
  public void renderSelf(Graphics2D g) {
    if (color != null) {
      g.fillRoundRect(
        (int) absoluteBounds.x, (int) absoluteBounds.y,
        (int) absoluteBounds.width, (int) absoluteBounds.height,
        cornerRadius, cornerRadius
      );
    } else if (background != null) {
      background.render(g, absoluteBounds);
    }
  }

  @Override
  protected void postRender(Graphics2D g) {
    if (clip)
      g.setClip(originalClip);

    if (originalColor != null)
      g.setColor(originalColor);
  }

  @Override
  public void handleEvent(MouseEvent event) {
    // Prevent widgets behind the panel from receiving mouse events
    event.setHandled();
  }
}
