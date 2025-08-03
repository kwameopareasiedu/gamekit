package dev.gamekit.ui.widgets;

import dev.gamekit.core.IO;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which uses the 9-patch algorithm to render a background */
public class Panel extends SingleChildParent implements NinePatch, MouseEvent.Handler {
  public static final BufferedImage DEFAULT_BG =
    IO.getResourceImage("default-sprites.png", 470, 64, 120, 120);

  protected BufferedImage background;
  protected Spacing edgeInsets;

  public Panel(PanelConfig config, Widget child) {
    super(config, child);
  }

  public static Panel create(PanelConfig config, Widget child) {
    return new Panel(config, child);
  }

  public static PanelConfig config() {
    return new PanelConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Panel panelWidget &&
      Objects.equals(background, panelWidget.background)
      && Objects.equals(edgeInsets, panelWidget.edgeInsets);
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

    intrinsicBounds.setSize(child.computedBounds.width, child.computedBounds.height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.computedBounds.setPosition(
      computedBounds.width / 2 - intrinsicBounds.width / 2,
      computedBounds.height / 2 - intrinsicBounds.height / 2
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

  public static class PanelConfig extends SingleChildParentConfig {
    protected BufferedImage background;
    protected Spacing edgeInsets;

    public PanelConfig background(BufferedImage background) {
      this.background = background;
      return this;
    }

    public PanelConfig edgeInsets(int top, int right, int bottom, int left) {
      this.edgeInsets = new Spacing(top, right, bottom, left);
      return this;
    }
  }
}
