package dev.gamekit.ui.widgets;

import dev.gamekit.core.Constants;
import dev.gamekit.core.UI;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.InputEvent;
import dev.gamekit.ui.events.InputEventHandler;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/**
 * A {@link SingleChildParent} which uses the 9-patch algorithm to render a {@link BufferedImage}
 * as a background to its descendants
 */
public class Panel extends SingleChildParent implements NinePatch, InputEventHandler {
  protected BufferedImage background;
  protected Spacing ninePatchBorder;

  private final Config config;

  public Panel(Config config, Widget child) {
    super(child);
    this.config = config;
  }

  public static Panel create(Config config, Widget child) {
    return new Panel(config, child);
  }

  public static Config config() {
    return new Config();
  }

  @Override
  protected void performMounted() {
    this.background = coalesce(config.background, Constants.DEFAULT_PANEL_BG);
    this.ninePatchBorder = coalesce(config.ninePatchBorder, new Spacing());
    super.performMounted();
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

    renderNinePatch(
      background,
      absoluteBounds,
      ninePatchBorder,
      g
    );
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Panel panelWidget) {
      return Objects.equals(background, panelWidget.background)
        && Objects.equals(ninePatchBorder, panelWidget.ninePatchBorder);
    }

    return false;
  }

  @Override
  public UI.WidgetBridge getUiBridge() {
    return uiBridge;
  }

  @Override
  public void handleEvent(InputEvent event) {
    event.setHandled();
  }

  public static class Config {
    public BufferedImage background;
    public Spacing ninePatchBorder;

    Config() { }

    public Config background(BufferedImage background) {
      this.background = background;
      return this;
    }

    public Config ninePatch(Spacing border) {
      this.ninePatchBorder = border;
      return this;
    }

    public Config ninePatch(int all) {
      this.ninePatchBorder = new Spacing(all);
      return this;
    }

    public Config ninePatch(int horizontal, int vertical) {
      this.ninePatchBorder = new Spacing(horizontal, vertical);
      return this;
    }

    public Config ninePatch(int top, int right, int bottom, int left) {
      this.ninePatchBorder = new Spacing(top, right, bottom, left);
      return this;
    }
  }
}
