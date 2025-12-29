package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.ui.events.ChangeEvent;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;

import java.awt.*;
import java.awt.image.BufferedImage;

/** A {@link SingleChildParent} input widget which toggles between two states */
@WidgetBuilder
public class Checkbox extends SingleChildParent implements MouseEvent.Handler, NinePatch {
  @WidgetBuilderField(fallback = "dev.gamekit.core.IO.getResourceImage(\"default-sprites.png\", 646, 206, 32, 32)")
  protected BufferedImage defaultIcon;
  @WidgetBuilderField(fallback = "dev.gamekit.core.IO.getResourceImage(\"default-sprites.png\", 646, 277, 32, 32)")
  protected BufferedImage toggledIcon;
  @WidgetBuilderField(fallback = "new dev.gamekit.utils.Spacing(8)")
  protected Spacing iconPadding;
  @WidgetBuilderField(fallback = "24")
  protected Integer iconWidth;
  @WidgetBuilderField(fallback = "24")
  protected Integer iconHeight;
  @WidgetBuilderField(fallback = "12")
  protected Integer gapSize;
  @WidgetBuilderField(fallback = "false")
  protected Boolean toggled;
  @WidgetBuilderField(comparable = false, themable = false)
  protected ChangeEvent.Handler<Boolean> changeListener;

  private final Bounds iconAbsoluteBounds;

  public Checkbox(CheckboxConfig config, Widget child) {
    super(config, child);
    iconAbsoluteBounds = new Bounds();
  }

  public static Checkbox create(CheckboxConfig.Updater updater, Widget child) {
    return new Checkbox(Widgets.configureCheckbox(updater), child);
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
      renderWith9PatchScaling(icon, iconAbsoluteBounds, iconPadding, g);
  }

  @Override
  public void handleEvent(MouseEvent ev) {
    if (ev.type == MouseEvent.Type.CLICK && changeListener != null) {
      changeListener.handleEvent(new ChangeEvent<>(!toggled));
      host.triggerRender();
    }
  }
}
