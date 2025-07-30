package dev.gamekit.ui.widgets;

import dev.gamekit.core.Constants;
import dev.gamekit.ui.BorderData;
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

/** A {@link SingleChildParent} input component which toggles between two states */
public class Checkbox extends SingleChildParent implements MouseEvent.Handler, NinePatch {
  protected Spacing backgroundNinePatchSpacing;
  protected Spacing iconNinePatchSpacing;
  protected BufferedImage defaultBackground;
  protected BufferedImage toggledBackground;
  protected BufferedImage defaultIcon;
  protected BufferedImage toggledIcon;
  protected BorderData defaultBorder;
  protected BorderData toggledBorder;
  protected Integer spacing;
  protected Integer size;
  protected Boolean toggled;
  protected ChangeEvent.Handler<Boolean> changeListener;

  private final Bounds iconAbsoluteBounds;
  private Stroke defaultBorderStroke;
  private Stroke toggledBorderStroke;

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
      return Objects.equals(backgroundNinePatchSpacing, checkboxWidget.backgroundNinePatchSpacing) &&
        Objects.equals(iconNinePatchSpacing, checkboxWidget.iconNinePatchSpacing) &&
        Objects.equals(defaultBackground, checkboxWidget.defaultBackground) &&
        Objects.equals(toggledBackground, checkboxWidget.toggledBackground) &&
        Objects.equals(defaultIcon, checkboxWidget.defaultIcon) &&
        Objects.equals(toggledIcon, checkboxWidget.toggledIcon) &&
        Objects.equals(defaultBorder, checkboxWidget.defaultBorder) &&
        Objects.equals(toggledBorder, checkboxWidget.toggledBorder) &&
        Objects.equals(spacing, checkboxWidget.spacing) &&
        Objects.equals(size, checkboxWidget.size) &&
        Objects.equals(toggled, checkboxWidget.toggled);

    return false;
  }

  @Override
  protected void performInit() {
    CheckboxConfig config = (CheckboxConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.backgroundNinePatchSpacing =
      coalesce(config.backgroundNinePatchSpacing, theme.checkboxBackgroundNinePatchSpacing, null);
    this.iconNinePatchSpacing =
      coalesce(config.iconNinePatchSpacing, theme.checkboxIconNinePatchSpacing, null);
    this.defaultBackground =
      coalesce(config.defaultBackground, theme.checkboxDefaultBackground, null);
    this.toggledBackground =
      coalesce(config.toggledBackground, theme.checkboxToggledBackground, null);
    this.defaultIcon = coalesce(config.defaultIcon, theme.checkboxDefaultIcon, null);
    this.toggledIcon =
      coalesce(config.toggledIcon, theme.checkboxToggledIcon, Constants.DEFAULT_CHECK_ICON);
    this.defaultBorder =
      coalesce(config.defaultBorder, theme.checkboxDefaultBorder, BorderData.create(1, 4, Color.WHITE));
    this.toggledBorder =
      coalesce(config.toggledBorder, theme.checkboxToggledBorder, BorderData.create(1, 4, Color.CYAN));
    this.spacing = coalesce(config.spacing, theme.checkboxSpacing, 12);
    this.size = coalesce(config.size, theme.checkboxSize, 24);
    this.toggled = coalesce(config.value, false);
    this.changeListener = coalesce(config.changeListener, null);

    defaultBorderStroke = new BasicStroke(
      (float) defaultBorder.size(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
    );

    toggledBorderStroke = new BasicStroke(
      (float) toggledBorder.size(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
    );

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth() - size - spacing,
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(
      size + spacing + child.computedBounds.width,
      Math.max(size, child.computedBounds.height)
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.computedBounds.setPosition(
      size + spacing,
      child.computedBounds.height > size ? 0 : (int) (0.5 * (size - child.computedBounds.height))
    );
  }

  @Override
  protected void performPostLayout() {
    iconAbsoluteBounds.set(absoluteBounds.x, absoluteBounds.y, size, size);

    super.performPostLayout();
  }

  @Override
  protected void renderAppearance(Graphics2D g) {
    BufferedImage background = defaultBackground;
    BufferedImage icon = defaultIcon;
    BorderData border = defaultBorder;
    Stroke borderStroke = defaultBorderStroke;
    Stroke originalStroke = g.getStroke();
    Color originalColor = g.getColor();

    if (toggled) {
      background = toggledBackground;
      icon = toggledIcon;
      border = toggledBorder;
      borderStroke = toggledBorderStroke;
    }

    if (background != null)
      renderWith9PatchScaling(background, absoluteBounds, backgroundNinePatchSpacing, g);

    if (icon != null)
      renderWith9PatchScaling(icon, iconAbsoluteBounds, iconNinePatchSpacing, g);

    g.setStroke(borderStroke);
    g.setColor(border.color());

    g.drawRoundRect(
      (int) absoluteBounds.x, (int) absoluteBounds.y, size - 1, size - 1,
      (int) border.radius(), (int) border.radius()
    );

    g.setStroke(originalStroke);
    g.setColor(originalColor);
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
    private Spacing backgroundNinePatchSpacing;
    private Spacing iconNinePatchSpacing;
    private BufferedImage defaultBackground;
    private BufferedImage toggledBackground;
    private BufferedImage defaultIcon;
    private BufferedImage toggledIcon;
    private BorderData defaultBorder;
    private BorderData toggledBorder;
    private Integer spacing;
    private Integer size;
    private Boolean value;
    private ChangeEvent.Handler<Boolean> changeListener;

    public CheckboxConfig ninePatchSpacing(
      Spacing backgroundNinePatchSpacing,
      Spacing iconNinePatchSpacing
    ) {
      this.backgroundNinePatchSpacing = backgroundNinePatchSpacing;
      this.iconNinePatchSpacing = iconNinePatchSpacing;
      return this;
    }

    public CheckboxConfig background(
      BufferedImage defaultBackground,
      BufferedImage toggledBackground
    ) {
      this.defaultBackground = defaultBackground;
      this.toggledBackground = toggledBackground;
      return this;
    }

    public CheckboxConfig icon(BufferedImage defaultIcon, BufferedImage toggledIcon) {
      this.defaultIcon = defaultIcon;
      this.toggledIcon = toggledIcon;
      return this;
    }

    public CheckboxConfig border(BorderData defaultBorder, BorderData toggledBorder) {
      this.defaultBorder = defaultBorder;
      this.toggledBorder = toggledBorder;
      return this;
    }

    public CheckboxConfig spacing(Integer spacing) {
      this.spacing = spacing;
      return this;
    }

    public CheckboxConfig size(Integer size) {
      this.size = size;
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
