package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.IO;
import dev.gamekit.ui.events.ChangeEvent;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;

import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} input widget which toggles between two states */
@WidgetBuilder
public class Checkbox extends SingleChildParent implements MouseEvent.Handler, NinePatch {
  public static final BufferedImage DEFAULT_ICON = IO.getResourceImage("default-sprites.png", 646, 206, 32, 32);
  public static final BufferedImage TOGGLED_ICON = IO.getResourceImage("default-sprites.png", 646, 277, 32, 32);

  @WidgetBuilderField
  protected BufferedImage defaultIcon;
  @WidgetBuilderField
  protected BufferedImage toggledIcon;
  @WidgetBuilderField
  protected Spacing iconEdgeInsets;
  @WidgetBuilderField
  protected Integer iconWidth;
  @WidgetBuilderField
  protected Integer iconHeight;
  @WidgetBuilderField
  protected Integer gapSize;
  @WidgetBuilderField
  protected Boolean toggled;
  @WidgetBuilderField(comparable = false, themable = false)
  protected ChangeEvent.Handler<Boolean> changeListener;

  private final Bounds iconAbsoluteBounds;

  public Checkbox(CheckboxConfig... config) {
    super(config);
    iconAbsoluteBounds = new Bounds();
  }

  public static Checkbox create(CheckboxConfig... config) {
    return new Checkbox(config);
  }

  @Override
  protected void performInit() {
    CheckboxConfig config = (CheckboxConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.defaultIcon = coalesce(config.defaultIcon, theme.checkboxDefaultIcon, DEFAULT_ICON);
    this.toggledIcon = coalesce(config.toggledIcon, theme.checkboxToggledIcon, TOGGLED_ICON);
    this.iconEdgeInsets = coalesce(config.iconEdgeInsets, theme.checkboxIconEdgeInsets, new Spacing(8));
    this.iconWidth = coalesce(config.iconWidth, theme.checkboxIconWidth, 24);
    this.iconHeight = coalesce(config.iconHeight, theme.checkboxIconHeight, 24);
    this.gapSize = coalesce(config.gapSize, theme.checkboxGapSize, 12);
    this.toggled = coalesce(config.toggled, false);
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

    intrinsicSize.set(
      iconWidth + gapSize + child.computedBounds.width,
      Math.max(iconHeight, child.computedBounds.height)
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    child.computedBounds.setPosition(
      iconWidth + gapSize,
      child.computedBounds.height <= iconHeight
        ? iconHeight / 2.0 - child.computedBounds.height / 2.0
        : 0
    );
  }

  @Override
  protected void performPostLayout() {
    super.performPostLayout();

    double iconPositionY = absoluteBounds.y + (absoluteBounds.height / 2.0 - iconHeight / 2.0);
    iconAbsoluteBounds.set(absoluteBounds.x, iconPositionY, iconWidth, iconHeight);
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
    if (ev.type == MouseEvent.Type.CLICK && changeListener != null) {
      changeListener.handleEvent(new ChangeEvent<>(!toggled));
      host.triggerRender();
    }
  }
}
