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

  protected BufferedImage defaultIcon;
  protected BufferedImage toggledIcon;
  protected Spacing iconEdgeInsets;
  protected Integer iconWidth;
  protected Integer iconHeight;
  protected Integer gapSize;
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
    return widget instanceof Checkbox checkboxWidget &&
      Objects.equals(defaultIcon, checkboxWidget.defaultIcon) &&
      Objects.equals(toggledIcon, checkboxWidget.toggledIcon) &&
      Objects.equals(iconEdgeInsets, checkboxWidget.iconEdgeInsets) &&
      Objects.equals(iconWidth, checkboxWidget.iconWidth) &&
      Objects.equals(iconHeight, checkboxWidget.iconHeight) &&
      Objects.equals(gapSize, checkboxWidget.gapSize) &&
      Objects.equals(toggled, checkboxWidget.toggled);
  }

  @Override
  protected void performInit() {
    CheckboxConfig config = (CheckboxConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.defaultIcon = coalesce(config.defaultIcon, theme.checkboxDefaultIcon, DEFAULT_ICON);
    this.toggledIcon = coalesce(config.toggledIcon, theme.checkboxToggledIcon, TOGGLED_ICON);
    this.iconEdgeInsets =
      coalesce(config.iconEdgeInsets, theme.checkboxIconEdgeInsets, Spacing.create(8));
    this.iconWidth = coalesce(config.iconWidth, theme.checkboxIconWidth, 24);
    this.iconHeight = coalesce(config.iconHeight, theme.checkboxIconHeight, 24);
    this.gapSize = coalesce(config.gapSize, theme.checkboxGapSize, 12);
    this.toggled = coalesce(config.value, false);
    this.changeListener = coalesce(config.changeListener, null);

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth() - iconWidth - gapSize,
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(
      iconWidth + gapSize + child.computedBounds.width,
      Math.max(iconHeight, child.computedBounds.height)
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.computedBounds.setPosition(
      iconWidth + gapSize,
      child.computedBounds.height <= iconHeight 
        ? (int) (0.5 * (iconHeight - child.computedBounds.height)) 
        : 0
    );
  }

  @Override
  protected void performPostLayout() {
    super.performPostLayout();
    iconAbsoluteBounds.set(absoluteBounds.x, absoluteBounds.y, iconWidth, iconHeight);
  }

  @Override
  protected void renderAppearance(Graphics2D g) {
    BufferedImage icon = defaultIcon;

    if (toggled)
      icon = toggledIcon;

    if (icon != null)
      renderWith9PatchScaling(icon, iconAbsoluteBounds, iconEdgeInsets, g);
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
    protected BufferedImage defaultIcon;
    protected BufferedImage toggledIcon;
    protected Spacing iconEdgeInsets;
    protected Integer iconWidth;
    protected Integer iconHeight;
    protected Integer gapSize;
    protected Boolean value;
    protected ChangeEvent.Handler<Boolean> changeListener;

    public CheckboxConfig defaultIcon(BufferedImage defaultIcon) {
      this.defaultIcon = defaultIcon;
      return this;
    }

    public CheckboxConfig toggledIcon(BufferedImage toggledIcon) {
      this.toggledIcon = toggledIcon;
      return this;
    }

    public CheckboxConfig iconEdgeInsets(Spacing iconEdgeInsets) {
      this.iconEdgeInsets = iconEdgeInsets;
      return this;
    }

    public CheckboxConfig iconSize(Integer iconWidth, Integer iconHeight) {
      this.iconWidth = iconWidth;
      this.iconHeight = iconHeight;
      return this;
    }

    public CheckboxConfig gapSize(Integer gapSize) {
      this.gapSize = gapSize;
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
