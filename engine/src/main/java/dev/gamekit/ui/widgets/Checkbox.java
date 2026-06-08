package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.IO;
import dev.gamekit.ui.events.ChangeEvent;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Picture;

import java.awt.*;

/** A {@link SingleChildParent} input widget which toggles between two states */
@WidgetBuilder
public class Checkbox extends SingleChildParent implements MouseEvent.Handler {
  public static final Picture DEFAULT_ICON =
    IO.getImageSliceWithInsets("default-sprites.png", 646, 206, 32, 32, 8, 8, 8, 8);
  public static final Picture DEFAULT_TOGGLED_ICON =
    IO.getImageSliceWithInsets("default-sprites.png", 646, 277, 32, 32, 8, 8, 8, 8);

  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Checkbox.DEFAULT_ICON")
  public Picture defaultIcon;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Checkbox.DEFAULT_TOGGLED_ICON")
  public Picture toggledIcon;
  @WidgetBuilderField(fallback = "24")
  public Integer iconWidth;
  @WidgetBuilderField(fallback = "24")
  public Integer iconHeight;
  @WidgetBuilderField(fallback = "12")
  public Integer gapSize;
  @WidgetBuilderField(fallback = "false")
  public Boolean toggled;
  @WidgetBuilderField(comparable = false, themable = false)
  public ChangeEvent.Handler<Boolean> changeListener;

  private final Bounds iconAbsoluteBounds;

  public Checkbox(String key, CheckboxConfig config, Widget child) {
    super(key, config, child);
    iconAbsoluteBounds = new Bounds();
  }

  public static Checkbox create(String key, CheckboxConfig.Updater updater, Widget child) {
    return new Checkbox(key, Widgets.configureCheckbox(updater), child);
  }

  public static Checkbox create(CheckboxConfig.Updater updater, Widget child) {
    return new Checkbox(null, Widgets.configureCheckbox(updater), child);
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
  protected void renderSelf(Graphics2D g) {
    Picture icon = defaultIcon;

    if (toggled)
      icon = toggledIcon;

    if (icon != null)
      icon.render(g, iconAbsoluteBounds);
  }

  @Override
  public void handleEvent(MouseEvent ev) {
    if (ev.type == MouseEvent.Type.CLICK && changeListener != null) {
      changeListener.handleEvent(new ChangeEvent<>(!toggled));
      host.triggerRender();
    }
  }
}
