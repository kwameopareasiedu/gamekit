package dev.gamekit.ui.widgets;

import dev.gamekit.core.Input;
import dev.gamekit.ui.BorderData;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.ChangeEvent;
import dev.gamekit.ui.events.FocusEvent;
import dev.gamekit.ui.events.KeyCharEvent;
import dev.gamekit.utils.Bounds;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Text} widget extension which accepts text input */
public class Field extends Text implements FocusEvent.Handler, KeyCharEvent.Handler {
  protected BufferedImage background;
  protected Spacing padding;
  protected BorderData defaultBorder;
  protected BorderData focusBorder;
  protected FocusEvent.Handler focusListener;
  protected KeyCharEvent.Handler keyCharListener;
  protected ChangeEvent.Handler changeListener;
  protected boolean focused;

  private final Bounds absoluteContentBounds;
  private final Bounds tempAbsoluteBounds;
  private Stroke defaultBorderStroke;
  private Stroke focusBorderStroke;

  public Field(Config config, String text) {
    super(config, text);
    absoluteContentBounds = new Bounds();
    tempAbsoluteBounds = new Bounds();
  }

  public static Field create(Config config, String text) {
    return new Field(config, text);
  }

  public static Config config() {
    return new Config();
  }

  @Override
  protected void performMounted() {
    super.performMounted();

    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());
    Config config = (Config) super.config;

    background = coalesce(config.background, theme.fieldBackground);
    padding = coalesce(config.padding, theme.fieldPadding, new Spacing());
    defaultBorder =
      coalesce(config.defaultBorder, theme.fieldDefaultBorder, new BorderData(1, 12, Color.WHITE));
    focusBorder =
      coalesce(config.focusBorder, theme.fieldFocusBorder, new BorderData(1, 12, Color.GREEN));
    focusListener = coalesce(config.focusListener, null);
    keyCharListener = coalesce(config.keyCharListener, null);
    changeListener = coalesce(config.changeListener, null);

    defaultBorderStroke = new BasicStroke(
      (float) defaultBorder.size(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
    );

    focusBorderStroke = new BasicStroke(
      (float) focusBorder.size(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
    );
  }

  @Override
  protected void performLayout(Constraints constraints) {
    double horizontalSpacing = 2 * defaultBorder.size() + padding.getHorizontal();
    double verticalSpacing = 2 * defaultBorder.size() + padding.getVertical();

    super.performLayout(
      new Constraints(
        constraints.minWidth(), constraints.maxWidth() - horizontalSpacing,
        constraints.minHeight(), constraints.maxHeight() - verticalSpacing
      )
    );

    intrinsicBounds.setSize(
      intrinsicBounds.width + horizontalSpacing,
      intrinsicBounds.height + verticalSpacing
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );
  }

  @Override
  protected void performPostLayout() {
    absoluteContentBounds.set(absoluteBounds);

    double horizontalSpacing = 2 * defaultBorder.size() + padding.getHorizontal();
    double verticalSpacing = 2 * defaultBorder.size() + padding.getVertical();
    double absoluteContentX = absoluteContentBounds.x + defaultBorder.size() + padding.left;
    double absoluteContentY = absoluteContentBounds.y + defaultBorder.size() + padding.top;
    double absoluteContentWidth = absoluteContentBounds.width - horizontalSpacing;
    double absoluteContentHeight = absoluteContentBounds.height - verticalSpacing;

    absoluteContentBounds.set(
      absoluteContentX,
      absoluteContentY,
      absoluteContentWidth,
      absoluteContentHeight
    );
  }

  @Override
  protected void performRender(Graphics2D g) {
    if (background != null)
      g.drawImage(
        background,
        (int) absoluteBounds.x, (int) absoluteBounds.y,
        (int) absoluteBounds.width, (int) absoluteBounds.height,
        null
      );

    tempAbsoluteBounds.set(absoluteBounds);
    absoluteBounds.set(absoluteContentBounds);
    super.performRender(g);
    absoluteBounds.set(tempAbsoluteBounds);

    BorderData resolvedBorder = defaultBorder;
    Stroke resolvedBorderStroke = defaultBorderStroke;

    if (focused) {
      resolvedBorder = focusBorder;
      resolvedBorderStroke = focusBorderStroke;
    }

    Stroke tempStroke = g.getStroke();
    Color tempColor = g.getColor();

    g.setStroke(resolvedBorderStroke);
    g.setColor(resolvedBorder.color());
    g.drawRoundRect(
      (int) absoluteBounds.x, (int) absoluteBounds.y,
      (int) absoluteBounds.width - 1, (int) absoluteBounds.height - 1,
      (int) resolvedBorder.radius(), (int) resolvedBorder.radius()
    );

    g.setStroke(tempStroke);
    g.setColor(tempColor);
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Field fieldWidget)
      return super.stateEquals(widget) &&
        Objects.equals(padding, fieldWidget.padding) &&
        Objects.equals(background, fieldWidget.background);

    return false;
  }

  @Override
  public void handleEvent(FocusEvent ev) {
    switch (ev.type) {
      case FOCUS -> focused = true;
      case BLUR -> focused = false;
    }

    uiBridge.triggerRender();

    if (focusListener != null)
      focusListener.handleEvent(ev);
  }

  @Override
  public void handleEvent(KeyCharEvent ev) {
    if (keyCharListener != null)
      keyCharListener.handleEvent(ev);

    if (!ev.isHandled() && changeListener != null) {
      int charKeyCode = KeyEvent.getExtendedKeyCodeForChar(ev.charPressed);

      String newValue = switch (charKeyCode) {
        case Input.KEY_BACK_SPACE -> text.substring(0, text.length() - 1);
        default -> KeyEvent.getKeyText(charKeyCode).length() == 1 ? text + ev.charPressed : text;
      };

      changeListener.handleEvent(new ChangeEvent(newValue));
    }
  }

  public static class Config extends Text.Config<Config> {
    BufferedImage background;
    Spacing padding;
    BorderData defaultBorder;
    BorderData focusBorder;
    FocusEvent.Handler focusListener;
    KeyCharEvent.Handler keyCharListener;
    ChangeEvent.Handler changeListener;

    Config() { }

    public Config background(BufferedImage background) {
      this.background = background;
      return this;
    }

    public Config padding(Spacing padding) {
      this.padding = padding;
      return this;
    }

    public Config padding(int padding) {
      this.padding = new Spacing(padding);
      return this;
    }

    public Config padding(int horizontal, int vertical) {
      this.padding = new Spacing(horizontal, vertical);
      return this;
    }

    public Config padding(int top, int right, int bottom, int left) {
      this.padding = new Spacing(top, right, bottom, left);
      return this;
    }

    public Config border(BorderData border) {
      this.defaultBorder = border;
      return this;
    }

    public Config defaultBorder(BorderData defaultBorder) {
      this.defaultBorder = defaultBorder;
      return this;
    }

    public Config focusBorder(BorderData focusBorder) {
      this.focusBorder = focusBorder;
      return this;
    }

    public Config focusListener(FocusEvent.Handler focusListener) {
      this.focusListener = focusListener;
      return this;
    }

    public Config keyCharListener(KeyCharEvent.Handler keyCharListener) {
      this.keyCharListener = keyCharListener;
      return this;
    }

    public Config changeListener(ChangeEvent.Handler changeListener) {
      this.changeListener = changeListener;
      return this;
    }
  }
}
