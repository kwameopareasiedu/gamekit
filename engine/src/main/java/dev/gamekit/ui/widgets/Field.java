package dev.gamekit.ui.widgets;

import dev.gamekit.animation.Animation;
import dev.gamekit.core.IO;
import dev.gamekit.core.Input;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.*;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;
import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Text} widget extension which accepts text input */
public class Field extends Text
  implements NinePatch, FocusEvent.Handler, MouseEvent.Handler,
  KeyCharEvent.Handler, KeyCodeEvent.Handler {
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
  private final TextModel textModel;

  public Field(FieldConfig config, String text) {
    super(config, text);
    contentAbsoluteBounds = new Bounds();
    tempAbsoluteBounds = new Bounds();
    cursorAnimation = new Animation(500, Animation.RepeatMode.RESTART);
    textModel = new TextModel();

    cursorAnimation.setValueListener((val) -> {
      if (val == 1.0 && focused) {
        textModel.toggleCursorVisibility();

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

    textModel.updateSymbols(symbols);
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

    if (textModel.cursorVisible) {
      Color originalColor = g.getColor();
      g.setColor(Color.BLACK);

      Symbol cursorSymbol = textModel.getCursorSymbol();

      g.fillRect(
        (int) (cursorSymbol == null ? contentAbsoluteBounds.x : cursorSymbol.x + cursorSymbol.width),
        (int) (cursorSymbol == null ? contentAbsoluteBounds.y : cursorSymbol.y),
        TextModel.CURSOR_WIDTH, (int) contentAbsoluteBounds.height
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

    textModel.setCursorVisibility(focused);
    cursorAnimation.start();
    uiBridge.triggerRender();

    if (focusListener != null)
      focusListener.handleEvent(ev);
  }

  @Override
  public void handleEvent(MouseEvent ev) {
    if (ev.type == MouseEvent.Type.DOWN)
      textModel.updateCursorPosition(ev.x, ev.y);
  }

  @Override
  public void handleEvent(KeyCodeEvent ev) {
    switch (ev.keyCode) {
      case Input.KEY_LEFT -> {
        textModel.offsetCursor(-1);
        textModel.setCursorVisibility(true);
        uiBridge.triggerRender();
      }
      case Input.KEY_RIGHT -> {
        textModel.offsetCursor(1);
        textModel.setCursorVisibility(true);
        uiBridge.triggerRender();
      }
      case Input.KEY_HOME -> {
        textModel.offsetCursor(Integer.MIN_VALUE);
        textModel.setCursorVisibility(true);
        uiBridge.triggerRender();
      }
      case Input.KEY_END -> {
        textModel.offsetCursor(Integer.MAX_VALUE);
        textModel.setCursorVisibility(true);
        uiBridge.triggerRender();
      }
    }
  }

  @Override
  public void handleEvent(KeyCharEvent ev) {
    if (keyCharListener != null)
      keyCharListener.handleEvent(ev);

    if (!ev.isHandled() && changeListener != null) {
      boolean updatedTextModel;

      switch (ev.charPressed) {
        case Input.KEY_BACK_SPACE -> updatedTextModel = textModel.leftDeleteAtCursor();
        case Input.KEY_DELETE -> updatedTextModel = textModel.rightDeleteAtCursor();
        default -> updatedTextModel = textModel.insertSymbolAtCursor(ev.charPressed);
      }

      if (updatedTextModel)
        changeListener.handleEvent(new ChangeEvent<>(textModel.getTextFromSymbols()));
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
   * {@link TextModel} is a document model for {@link Field} widgets which manages the
   * text symbols and manipulations of these symbols based on a cursor.
   * <p>
   * Actions in the {@link Field} are submitted to this model which updates the symbols and cursor,
   * returning the resulting text from the symbols.
   * <p>
   * NB: <i>The cursor is always to the <b>right</b> of its current symbol and a null symbol
   * indicates no text in the associated {@link Field} widget</i>
   */
  public static class TextModel {
    public static final int CURSOR_WIDTH = 2;
    public static final int CURSOR_ORIGIN_INDEX = -1;
    public static final int PRINTABLE_ASCII_START = 32;
    public static final int PRINTABLE_ASCII_END = 126;

    private final List<Symbol> symbols;
    private boolean cursorVisible;
    private int cursorIndex;
    private final StringBuilder textBuilder;

    public TextModel() {
      symbols = new ArrayList<>();
      cursorVisible = false;
      cursorIndex = CURSOR_ORIGIN_INDEX;
      textBuilder = new StringBuilder();
    }

    /** Sets whether the cursor should be visible or not */
    public void setCursorVisibility(boolean visible) {
      this.cursorVisible = visible;
    }

    /** Toggles the cursor visibility */
    public void toggleCursorVisibility() {
      this.cursorVisible = !cursorVisible;
    }

    public void updateSymbols(List<Symbol> symbols) {
      this.symbols.clear();
      this.symbols.addAll(symbols);
    }

    /**
     * When the mouse is down in the associated {@link Field}, this method updates the cursor
     * position by determining which symbol is the closest to the event location
     */
    public void updateCursorPosition(int mouseX, int mouseY) {
      boolean mousePointInSymbolBounds = false;

      for (int symIdx = 0; symIdx < symbols.size(); symIdx++) {
        Symbol sym = symbols.get(symIdx);

        if (sym.contains(mouseX, mouseY)) {
          boolean mouseDownInLeftHalfOfSymbolBounds =
            sym.x < mouseX && mouseX < sym.x + 0.5 * sym.width;

          cursorIndex = mouseDownInLeftHalfOfSymbolBounds
            ? (symIdx > 0) ? symIdx - 1 : CURSOR_ORIGIN_INDEX
            : symIdx;

          mousePointInSymbolBounds = true;
          break;
        }
      }

      if (!mousePointInSymbolBounds) {
        if (!symbols.isEmpty()) {
          Symbol lastSymbol = symbols.get(symbols.size() - 1);

          boolean mousePointToRightOfLastSymbolBounds =
            mouseX > lastSymbol.x + lastSymbol.width;

          cursorIndex = mousePointToRightOfLastSymbolBounds
            ? symbols.size() - 1
            : CURSOR_ORIGIN_INDEX;
        } else {
          cursorIndex = CURSOR_ORIGIN_INDEX;
        }
      }
    }

    /**
     * Inserts a character symbol at the cursor. Returns {@code true} if the character is a
     * printable ASCII character and was successfully inserted and {@code false} otherwise
     */
    public boolean insertSymbolAtCursor(char ch) {
      boolean charIsPrintable = PRINTABLE_ASCII_START <= ch && ch <= PRINTABLE_ASCII_END;

      if (!charIsPrintable)
        return false;

      cursorIndex++;
      symbols.add(cursorIndex, new Symbol(ch, 0, 0, 0, 0));
      return true;
    }

    /** Moves the cursor by an offset without modifying the symbols */
    public void offsetCursor(int offset) {
      cursorIndex = clamp(cursorIndex + offset, CURSOR_ORIGIN_INDEX, symbols.size() - 1);
    }

    /**
     * Deletes the symbol at the cursor position and moves the cursor one unit to the left.
     * Returns {@code true} if the internal symbols were changed otherwise {@code false}
     */
    public boolean leftDeleteAtCursor() {
      if (cursorIndex == CURSOR_ORIGIN_INDEX)
        return false;

      symbols.remove(cursorIndex);
      cursorIndex--;
      return true;
    }

    /**
     * Deletes the symbol one unit after the cursor position. Returns {@code true} if the
     * internal symbols were changed otherwise {@code false}
     */
    public boolean rightDeleteAtCursor() {
      if (cursorIndex == symbols.size() - 1)
        return false;

      symbols.remove(cursorIndex + 1);
      return true;
    }

    /** Returns the symbol at the cursor */
    public Symbol getCursorSymbol() {
      if (cursorIndex == CURSOR_ORIGIN_INDEX)
        return null;

      return symbols.get(cursorIndex);
    }

    /** Computes the text from internal symbol list */
    public String getTextFromSymbols() {
      textBuilder.setLength(0);

      if (!symbols.isEmpty()) {
        for (Symbol sym : symbols)
          textBuilder.append(sym.value);
      }

      return textBuilder.toString();
    }
  }
}
