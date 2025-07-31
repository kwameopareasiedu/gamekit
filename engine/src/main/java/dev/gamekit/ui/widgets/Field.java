package dev.gamekit.ui.widgets;

import dev.gamekit.core.Constants;
import dev.gamekit.core.Input;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.ChangeEvent;
import dev.gamekit.ui.events.FocusEvent;
import dev.gamekit.ui.events.KeyCharEvent;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Text} widget extension which accepts text input */
public class Field extends Text implements FocusEvent.Handler, KeyCharEvent.Handler, NinePatch {
  protected Spacing ninePatchSpacing;
  protected BufferedImage defaultBackground;
  protected BufferedImage focusBackground;
  protected Spacing padding;
  protected FocusEvent.Handler focusListener;
  protected KeyCharEvent.Handler keyCharListener;
  protected ChangeEvent.Handler<String> changeListener;
  protected boolean focused;

  private final Bounds contentAbsoluteBounds;
  private final Bounds tempAbsoluteBounds;

  public Field(FieldConfig config, String text) {
    super(config, text);
    contentAbsoluteBounds = new Bounds();
    tempAbsoluteBounds = new Bounds();
  }

  public static Field create(FieldConfig config, String text) {
    return new Field(config, text);
  }

  public static FieldConfig config() {
    return new FieldConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Field fieldWidget)
      return super.stateEquals(widget) &&
        Objects.equals(ninePatchSpacing, fieldWidget.ninePatchSpacing) &&
        Objects.equals(defaultBackground, fieldWidget.defaultBackground) &&
        Objects.equals(focusBackground, fieldWidget.focusBackground) &&
        Objects.equals(padding, fieldWidget.padding);

    return false;
  }

  @Override
  protected void performInit() {
    super.performInit();

    FieldConfig config = (FieldConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    foregroundColor = coalesce(config.foregroundColor, theme.textForegroundColor, Color.BLACK);
    ninePatchSpacing =
      coalesce(config.ninePatchSpacing, theme.fieldNinePatchSpacing, Spacing.create(2));
    defaultBackground =
      coalesce(config.defaultBackground, theme.fieldDefaultBackground, Constants.FIELD_DEFAULT_BG);
    focusBackground =
      coalesce(config.focusBackground, theme.fieldFocusBackground, Constants.FIELD_FOCUS_BG);
    padding = coalesce(config.padding, theme.fieldPadding, Spacing.create());
    focusListener = coalesce(config.focusListener, null);
    keyCharListener = coalesce(config.keyCharListener, null);
    changeListener = coalesce(config.changeListener, null);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    super.performLayout(
      new Constraints(
        constraints.minWidth(), constraints.maxWidth() - padding.getHorizontal(),
        constraints.minHeight(), constraints.maxHeight() - padding.getVertical()
      )
    );

    intrinsicBounds.setSize(
      intrinsicBounds.width + padding.getHorizontal(),
      intrinsicBounds.height + padding.getVertical()
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );
  }

  @Override
  protected void performPostLayout() {
    contentAbsoluteBounds.set(absoluteBounds);

    double absoluteContentX = contentAbsoluteBounds.x + padding.left;
    double absoluteContentY = contentAbsoluteBounds.y + padding.top;
    double absoluteContentWidth = contentAbsoluteBounds.width - padding.getHorizontal();
    double absoluteContentHeight = contentAbsoluteBounds.height - padding.getVertical();

    contentAbsoluteBounds.set(
      absoluteContentX,
      absoluteContentY,
      absoluteContentWidth,
      absoluteContentHeight
    );
  }

  @Override
  protected void performRender(Graphics2D g) {
    BufferedImage background = defaultBackground;

    if (focused)
      background = focusBackground;

    if (background != null)
      renderWith9PatchScaling(background, absoluteBounds, ninePatchSpacing, g);

    tempAbsoluteBounds.set(absoluteBounds);
    absoluteBounds.set(contentAbsoluteBounds);
    super.performRender(g);
    absoluteBounds.set(tempAbsoluteBounds);
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
        case Input.KEY_BACK_SPACE -> !text.isEmpty() ? text.substring(0, text.length() - 1) : text;
        default -> {
          boolean charIsPrintable = 32 <= charKeyCode && charKeyCode <= 126;
          yield charIsPrintable ? text + ev.charPressed : text;
        }
      };

      changeListener.handleEvent(new ChangeEvent<>(newValue));
    }
  }

  public static class FieldConfig extends TextConfig<FieldConfig> {
    protected Spacing ninePatchSpacing;
    protected BufferedImage defaultBackground;
    protected BufferedImage focusBackground;
    protected Spacing padding;
    protected FocusEvent.Handler focusListener;
    protected KeyCharEvent.Handler keyCharListener;
    protected ChangeEvent.Handler<String> changeListener;

    public FieldConfig ninePatchSpacing(Spacing ninePatchSpacing) {
      this.ninePatchSpacing = ninePatchSpacing;
      return this;
    }

    public FieldConfig background(BufferedImage defaultBackground, BufferedImage focusBackground) {
      this.defaultBackground = defaultBackground;
      this.focusBackground = focusBackground;
      return this;
    }

    public FieldConfig padding(Spacing padding) {
      this.padding = padding;
      return this;
    }

    public FieldConfig focusListener(FocusEvent.Handler focusListener) {
      this.focusListener = focusListener;
      return this;
    }

    public FieldConfig keyCharListener(KeyCharEvent.Handler keyCharListener) {
      this.keyCharListener = keyCharListener;
      return this;
    }

    public FieldConfig changeListener(ChangeEvent.Handler<String> changeListener) {
      this.changeListener = changeListener;
      return this;
    }
  }
}
