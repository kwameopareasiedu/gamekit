package dev.gamekit.ui.widgets;

import dev.gamekit.animation.Animation;
import dev.gamekit.core.IO;
import dev.gamekit.core.Input;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.ChangeEvent;
import dev.gamekit.ui.events.FocusEvent;
import dev.gamekit.ui.events.KeyCharEvent;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Text} widget extension which accepts text input */
public class Field extends Text
  implements FocusEvent.Handler, MouseEvent.Handler, KeyCharEvent.Handler, NinePatch {
  public static final BufferedImage DEFAULT_BG =
    IO.getResourceImage("default-sprites.png", 646, 64, 96, 32);
  public static final BufferedImage FOCUS_BG =
    IO.getResourceImage("default-sprites.png", 646, 135, 96, 32);

  protected BufferedImage defaultBackground;
  protected BufferedImage focusBackground;
  protected Spacing edgeInsets;
  protected Spacing padding;
  protected FocusEvent.Handler focusListener;
  protected KeyCharEvent.Handler keyCharListener;
  protected ChangeEvent.Handler<String> changeListener;
  protected boolean focused;

  private final Bounds contentAbsoluteBounds;
  private final Bounds tempAbsoluteBounds;
  private final Animation cursorAnimation;
  private final Cursor cursor;

  public Field(FieldConfig config, String text) {
    super(config, text);
    contentAbsoluteBounds = new Bounds();
    tempAbsoluteBounds = new Bounds();
    cursorAnimation = new Animation(500, Animation.RepeatMode.RESTART);
    cursor = new Cursor();

    cursorAnimation.setValueListener((val) -> {
      if (val == 1.0 && focused) {
        cursor.setVisible(!cursor.visible);

        if (uiBridge != null)
          uiBridge.triggerRender();
      }
    });
  }

  public static Field create(FieldConfig config, String text) {
    return new Field(config, text);
  }

  public static FieldConfig config() {
    return new FieldConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Field fieldWidget &&
      super.stateEquals(widget) &&
      Objects.equals(defaultBackground, fieldWidget.defaultBackground) &&
      Objects.equals(focusBackground, fieldWidget.focusBackground) &&
      Objects.equals(edgeInsets, fieldWidget.edgeInsets) &&
      Objects.equals(padding, fieldWidget.padding);
  }

  @Override
  protected void performInit() {
    super.performInit();

    FieldConfig config = (FieldConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    color = coalesce(config.color, theme.textColor, Color.BLACK);
    edgeInsets = coalesce(config.edgeInsets, theme.fieldEdgeInsets, new Spacing(2));
    defaultBackground =
      coalesce(config.defaultBackground, theme.fieldDefaultBackground, DEFAULT_BG);
    focusBackground = coalesce(config.focusBackground, theme.fieldFocusBackground, FOCUS_BG);
    padding = coalesce(config.padding, theme.fieldPadding, new Spacing(4));
    focusListener = coalesce(config.focusListener, null);
    keyCharListener = coalesce(config.keyCharListener, null);
    changeListener = coalesce(config.changeListener, null);

    cursorAnimation.start();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    super.performLayout(constraints);

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
    double contentAbsoluteX = absoluteBounds.x + padding.left;
    double contentAbsoluteY = absoluteBounds.y + padding.top;
    double contentAbsoluteWidth = absoluteBounds.width - padding.getHorizontal();
    double contentAbsoluteHeight = absoluteBounds.height - padding.getVertical();

    contentAbsoluteBounds.set(
      contentAbsoluteX,
      contentAbsoluteY,
      contentAbsoluteWidth,
      contentAbsoluteHeight
    );

    tempAbsoluteBounds.set(absoluteBounds);
    absoluteBounds.set(contentAbsoluteBounds);
    super.performPostLayout();
    absoluteBounds.set(tempAbsoluteBounds);
  }

  @Override
  protected void performRender(Graphics2D g) {
    BufferedImage background = !focused ? defaultBackground : focusBackground;

    if (background != null)
      renderWith9PatchScaling(background, absoluteBounds, edgeInsets, g);

    if (cursor.visible) {
      Color originalColor = g.getColor();
      g.setColor(Color.BLACK);

      g.fillRect(
        (int) (cursor.symbol == null ? contentAbsoluteBounds.x :
          cursor.symbol.x + cursor.symbol.width),
        (int) (cursor.symbol == null ? contentAbsoluteBounds.y : cursor.symbol.y),
        Cursor.WIDTH, (int) contentAbsoluteBounds.height
      );

      g.setColor(originalColor);
    }

    super.performRender(g);
  }

  @Override
  public void handleEvent(FocusEvent ev) {
    switch (ev.type) {
      case FOCUS -> focused = true;
      case BLUR -> focused = false;
    }

    cursor.setVisible(focused);
    cursorAnimation.start();
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

  @Override
  public void handleEvent(MouseEvent ev) {
    if (ev.type == MouseEvent.Type.DOWN) {
      int checkedSymbols = 0;

      for (int i = 0; i < symbols.size(); i++) {
        Symbol sym = symbols.get(i);

        if (sym.contains(ev.x, ev.y)) {
          boolean mouseDownInLeftHalfOfSymbolBounds =
            sym.x < ev.x && ev.x < sym.x + 0.5 * sym.width;

          if (mouseDownInLeftHalfOfSymbolBounds) {
            cursor.symbol = (i > 0) ? symbols.get(i - 1) : null;
          } else {
            cursor.symbol = sym;
          }

          break;
        }

        checkedSymbols++;
      }

      if (checkedSymbols == symbols.size()) {
        if (!symbols.isEmpty()) {
          Symbol lastSymbol = symbols.get(symbols.size() - 1);
          boolean mouseDownToRightOfLastSymbolBounds = ev.x > lastSymbol.x + lastSymbol.width;
          cursor.symbol = mouseDownToRightOfLastSymbolBounds ? lastSymbol : null;
        } else {
          cursor.symbol = null;
        }
      }
    }
  }

  @Override
  protected void performUnmount() {
    super.performUnmount();
    cursorAnimation.end();
  }

  public static class FieldConfig extends TextConfig<FieldConfig> {
    protected BufferedImage defaultBackground;
    protected BufferedImage focusBackground;
    protected Spacing edgeInsets;
    protected Spacing padding;
    protected FocusEvent.Handler focusListener;
    protected KeyCharEvent.Handler keyCharListener;
    protected ChangeEvent.Handler<String> changeListener;

    public FieldConfig defaultBackground(BufferedImage defaultBackground) {
      this.defaultBackground = defaultBackground;
      return this;
    }

    public FieldConfig focusBackground(BufferedImage focusBackground) {
      this.focusBackground = focusBackground;
      return this;
    }

    public FieldConfig edgeInsets(int top, int right, int bottom, int left) {
      this.edgeInsets = new Spacing(top, right, bottom, left);
      return this;
    }

    public FieldConfig padding(int top, int right, int bottom, int left) {
      this.padding = new Spacing(top, right, bottom, left);
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

  /**
   * Stores information related to cursor management
   * <p>
   * NB: <i>The cursor is always to the <b>right</b> of its current symbol and a null symbol
   * indicates no text in the associated {@link Field} widget</i>
   */
  protected static class Cursor {
    protected static final int WIDTH = 2;

    protected Symbol symbol;
    protected boolean visible;

    public void setVisible(boolean visible) {
      this.visible = visible;
    }
  }
}
