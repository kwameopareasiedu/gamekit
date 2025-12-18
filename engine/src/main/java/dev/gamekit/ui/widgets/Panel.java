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

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which uses the 9-patch algorithm to render a background */
@WidgetBuilder
public class Panel extends SingleChildParent implements NinePatch, MouseEvent.Handler {
  public static final BufferedImage DEFAULT_BG = IO.getResourceImage("default-sprites.png", 470, 64, 120, 120);

  @WidgetBuilderField
  protected BufferedImage background;
  @WidgetBuilderField
  protected Spacing edgeInsets;

  public Panel(PanelConfig... config) {
    super(config);
  }

  public static Panel create(PanelConfig... config) {
    return new Panel(config);
  }

  @Override
  protected void performInit() {
    PanelConfig config = (PanelConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.background = coalesce(config.background, theme.panelBackground, DEFAULT_BG);
    this.edgeInsets = coalesce(config.edgeInsets, theme.panelEdgeInsets, new Spacing());

    super.performInit();
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
  public void renderAppearance(Graphics2D g) {
    super.renderAppearance(g);

    renderWith9PatchScaling(background, absoluteBounds, edgeInsets, g);
  }

  @Override
  public void handleEvent(MouseEvent event) {
    event.setHandled();
  }
}
