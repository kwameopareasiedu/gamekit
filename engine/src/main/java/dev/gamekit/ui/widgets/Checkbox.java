package dev.gamekit.ui.widgets;

import dev.gamekit.core.IO;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.ChangeEvent;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} input widget which toggles between two states */
public class Checkbox extends SingleChildParent implements MouseEvent.Handler, NinePatch {
  public static final BufferedImage DEFAULT_ICON =
    IO.getResourceImage("default-sprites.png", 646, 206, 32, 32);
  public static final BufferedImage TOGGLED_ICON =
    IO.getResourceImage("default-sprites.png", 646, 277, 32, 32);

  protected Spacing ninePatchSpacing;
  protected BufferedImage defaultIcon;
  protected BufferedImage toggledIcon;
  protected Integer gapSize;
  protected Integer iconSize;
  protected Boolean toggled;
  protected ChangeEvent.Handler<Boolean> changeListener;

  private final Bounds iconAbsoluteBounds;

  public Checkbox(CheckboxConfig config, Widget child) {
    super(config, child);
    iconAbsoluteBounds = new Bounds();
  }

  public static Checkbox create(CheckboxConfig config, Widget child) {
    return new Checkbox(config, child);
  }

  public static CheckboxConfig config() {
    return new CheckboxConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Checkbox checkboxWidget)
      return Objects.equals(ninePatchSpacing, checkboxWidget.ninePatchSpacing) &&
        Objects.equals(defaultIcon, checkboxWidget.defaultIcon) &&
        Objects.equals(toggledIcon, checkboxWidget.toggledIcon) &&
        Objects.equals(gapSize, checkboxWidget.gapSize) &&
        Objects.equals(iconSize, checkboxWidget.iconSize) &&
        Objects.equals(toggled, checkboxWidget.toggled);

    return false;
  }

  @Override
  protected void performInit() {
    CheckboxConfig config = (CheckboxConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.ninePatchSpacing =
      coalesce(config.ninePatchSpacing, theme.checkboxNinePatchSpacing, Spacing.create(8));
    this.defaultIcon = coalesce(config.defaultIcon, theme.checkboxDefaultIcon, DEFAULT_ICON);
    this.toggledIcon = coalesce(config.toggledIcon, theme.checkboxToggledIcon, TOGGLED_ICON);
    this.gapSize = coalesce(config.gapSize, theme.checkboxGapSize, 12);
    this.iconSize = coalesce(config.iconSize, theme.checkboxIconSize, 24);
    this.toggled = coalesce(config.value, false);
    this.changeListener = coalesce(config.changeListener, null);

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth() - iconSize - gapSize,
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(
      iconSize + gapSize + child.computedBounds.width,
      Math.max(iconSize, child.computedBounds.height)
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.computedBounds.setPosition(
      iconSize + gapSize,
      child.computedBounds.height > iconSize ? 0 : (int) (0.5 * (iconSize - child.computedBounds.height))
    );
  }

  @Override
  protected void performPostLayout() {
    iconAbsoluteBounds.set(absoluteBounds.x, absoluteBounds.y, iconSize, iconSize);

    super.performPostLayout();
  }

  @Override
  protected void renderAppearance(Graphics2D g) {
    BufferedImage icon = defaultIcon;

    if (toggled)
      icon = toggledIcon;

    if (icon != null)
      renderWith9PatchScaling(icon, iconAbsoluteBounds, ninePatchSpacing, g);
  }

  @Override
  public void handleEvent(MouseEvent ev) {
    switch (ev.type) {
      case CLICK -> {
        if (changeListener != null)
          changeListener.handleEvent(new ChangeEvent<>(!toggled));
      }
      case EXIT -> { }
    }

    uiBridge.triggerRender();
  }

  public static class CheckboxConfig extends SingleChildParentConfig {
    protected Spacing ninePatchSpacing;
    protected BufferedImage defaultIcon;
    protected BufferedImage toggledIcon;
    protected Integer gapSize;
    protected Integer iconSize;
    protected Boolean value;
    protected ChangeEvent.Handler<Boolean> changeListener;

    public CheckboxConfig ninePatchSpacing(Spacing ninePatchSpacing) {
      this.ninePatchSpacing = ninePatchSpacing;
      return this;
    }

    public CheckboxConfig icon(BufferedImage defaultIcon, BufferedImage toggledIcon) {
      this.defaultIcon = defaultIcon;
      this.toggledIcon = toggledIcon;
      return this;
    }

    public CheckboxConfig gapSize(Integer gapSize) {
      this.gapSize = gapSize;
      return this;
    }

    public CheckboxConfig iconSize(Integer iconSize) {
      this.iconSize = iconSize;
      return this;
    }

    public CheckboxConfig value(Boolean value) {
      this.value = value;
      return this;
    }

    public CheckboxConfig changeListener(ChangeEvent.Handler<Boolean> changeListener) {
      this.changeListener = changeListener;
      return this;
    }
  }
}
