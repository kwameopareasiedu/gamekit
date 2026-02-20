package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.IO;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;

import java.awt.*;
import java.awt.image.BufferedImage;

/** A {@link SingleChildParent} which uses the 9-patch algorithm to render a background */
@WidgetBuilder
public class Panel extends SingleChildParent implements NinePatch, MouseEvent.Handler {
  public static final BufferedImage DEFAULT_BG = IO.getResourceImage("default-sprites.png", 470, 64, 120, 120);
  public static final Spacing DEFAULT_PADDING = new Spacing();

  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Panel.DEFAULT_BG")
  protected BufferedImage background;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Panel.DEFAULT_PADDING")
  protected Spacing padding;

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

    child.computedBounds.setPosition(
      computedBounds.width / 2 - intrinsicSize.width / 2,
      computedBounds.height / 2 - intrinsicSize.height / 2
    );
  }

  @Override
  public void renderSelf(Graphics2D g) {
    super.renderSelf(g);

    renderWith9PatchScaling(background, absoluteBounds, padding, g);
  }

  @Override
  public void handleEvent(MouseEvent event) {
    event.setHandled();
  }
}
