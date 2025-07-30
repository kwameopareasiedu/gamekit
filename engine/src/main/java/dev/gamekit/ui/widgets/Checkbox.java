package dev.gamekit.ui.widgets;

import dev.gamekit.ui.BorderData;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.events.ChangeEvent;
import dev.gamekit.ui.events.MouseEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} input component which toggles between two states */
public class Checkbox extends SingleChildParent implements MouseEvent.Handler {
  protected BorderData defaultBorder;
  protected BorderData activeBorder;
  protected BufferedImage background;
  protected Integer spacing;
  protected Integer size;
  protected Boolean value;
  protected ChangeEvent.Handler<Boolean> changeListener;

  private Stroke defaultBorderStroke;
  private Stroke activeBorderStroke;

  public Checkbox(CheckboxConfig config, Widget child) {
    super(config, child);
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
      return Objects.equals(defaultBorder, checkboxWidget.defaultBorder) &&
        Objects.equals(activeBorder, checkboxWidget.activeBorder) &&
        Objects.equals(background, checkboxWidget.background) &&
        Objects.equals(spacing, checkboxWidget.spacing) &&
        Objects.equals(size, checkboxWidget.size) &&
        Objects.equals(value, checkboxWidget.value);

    return false;
  }

  @Override
  protected void performInit() {
    CheckboxConfig config = (CheckboxConfig) super.config;

    this.defaultBorder = coalesce(config.defaultBorder, new BorderData(3, 12, Color.WHITE));
    this.activeBorder = coalesce(config.activeBorder, new BorderData(2, 12, Color.CYAN));
    this.background = coalesce(config.background, null);
    this.spacing = coalesce(config.spacing, 12);
    this.size = coalesce(config.size, 24);
    this.value = coalesce(config.value, false);
    this.changeListener = coalesce(config.changeListener, null);

    defaultBorderStroke = new BasicStroke(
      (float) defaultBorder.size(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
    );

    activeBorderStroke = new BasicStroke(
      (float) activeBorder.size(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
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
  protected void renderAppearance(Graphics2D g) {
    if (value && background != null)
      g.drawImage(background, 0, 0, null);

    BorderData resolvedBorder = defaultBorder;
    Stroke resolvedBorderStroke = defaultBorderStroke;

    if (value) {
      resolvedBorder = activeBorder;
      resolvedBorderStroke = activeBorderStroke;
    }

    g.setStroke(resolvedBorderStroke);
    g.setColor(resolvedBorder.color());

    g.drawRoundRect(
      (int) absoluteBounds.x, (int) absoluteBounds.y, size - 1, size - 1,
      (int) resolvedBorder.radius(), (int) resolvedBorder.radius()
    );
  }

  @Override
  public void handleEvent(MouseEvent ev) {
    switch (ev.type) {
      case CLICK -> {
        if (changeListener != null)
          changeListener.handleEvent(new ChangeEvent<>(!value));
      }
      case EXIT -> { }
    }

    uiBridge.triggerRender();
  }

  public static class CheckboxConfig extends SingleChildParentConfig {
    BorderData defaultBorder;
    BorderData activeBorder;
    BufferedImage background;
    Integer spacing;
    Integer size;
    Boolean value;
    ChangeEvent.Handler<Boolean> changeListener;

    public CheckboxConfig defaultBorder(BorderData defaultBorder) {
      this.defaultBorder = defaultBorder;
      return this;
    }

    public CheckboxConfig activeBorder(BorderData activeBorder) {
      this.activeBorder = activeBorder;
      return this;
    }

    public CheckboxConfig background(BufferedImage background) {
      this.background = background;
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
