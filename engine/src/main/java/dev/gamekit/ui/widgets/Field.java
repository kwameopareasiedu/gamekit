package dev.gamekit.ui.widgets;

import dev.gamekit.animation.Animation;
import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.Input;
import dev.gamekit.ui.events.*;
import dev.gamekit.ui.mixins.NinePatch;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static dev.gamekit.utils.Math.clamp;

/** A {@link Text} widget extension which accepts text input */
// TODO: Implement sliding window for text rendering
// TODO: Implement text selection
// TODO: Implement key char and key code modifier detection
@WidgetBuilder
public class Field extends Text
  implements NinePatch, FocusEvent.Handler, MouseEvent.Handler, KeyCharEvent.Handler, KeyCodeEvent.Handler {
  @WidgetBuilderField(fallback = "dev.gamekit.core.IO.getResourceImage(\"default-sprites.png\", 646, 64, 96, 32)")
  protected BufferedImage defaultBackground;
  @WidgetBuilderField(fallback = "dev.gamekit.core.IO.getResourceImage(\"default-sprites.png\", 646, 135, 96, 32)")
  protected BufferedImage focusBackground;
  @WidgetBuilderField(fallback = "new dev.gamekit.utils.Spacing(2)")
  protected Spacing edgeInsets;
  @WidgetBuilderField(fallback = "new dev.gamekit.utils.Spacing(4)")
  protected Spacing padding;
  @WidgetBuilderField(comparable = false, themable = false)
  protected FocusEvent.Handler focusListener;
  @WidgetBuilderField(comparable = false, themable = false)
  protected KeyCharEvent.Handler keyCharListener;
  @WidgetBuilderField(comparable = false, themable = false)
  protected ChangeEvent.Handler<String> changeListener;

  protected boolean focused;

  private final Bounds contentAbsoluteBounds;
  private final Bounds tempAbsoluteBounds;
  private final Animation cursorAnimation;
  private final TextModel textModel;

  public Field(FieldConfig... config) {
    super(config);

    contentAbsoluteBounds = new Bounds();
    tempAbsoluteBounds = new Bounds();
    cursorAnimation = new Animation(500, Animation.RepeatMode.RESTART);
    textModel = new TextModel();

    cursorAnimation.setValueListener((val) -> {
      if (val == 1.0 && focused) {
        textModel.toggleCursorVisibility();

        if (host != null)
          host.triggerRender();
      }
    });
  }

  public static Field create(FieldConfig... config) {
    return new Field(config);
  }

  @Override
  protected void performInit() {
    super.performInit();

    cursorAnimation.start();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    super.performLayout(constraints);

    intrinsicSize.set(
      intrinsicSize.width + padding.getHorizontal(),
      intrinsicSize.height + padding.getVertical()
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    textModel.updateSymbols(symbols);
  }

  @Override
  protected void performPostLayout() {
    double contentAbsoluteX = absoluteBounds.x + padding.left;
    double contentAbsoluteY = absoluteBounds.y + padding.top;
    double contentAbsoluteWidth = absoluteBounds.width - padding.getHorizontal();
    double contentAbsoluteHeight = absoluteBounds.height - padding.getVertical();

    contentAbsoluteBounds.set(contentAbsoluteX, contentAbsoluteY, contentAbsoluteWidth, contentAbsoluteHeight);
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
    host.triggerRender();

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
        host.triggerRender();
      }
      case Input.KEY_RIGHT -> {
        textModel.offsetCursor(1);
        textModel.setCursorVisibility(true);
        host.triggerRender();
      }
      case Input.KEY_HOME -> {
        textModel.offsetCursor(-symbols.size());
        textModel.setCursorVisibility(true);
        host.triggerRender();
      }
      case Input.KEY_END -> {
        textModel.offsetCursor(symbols.size());
        textModel.setCursorVisibility(true);
        host.triggerRender();
      }
    }
  }

  @Override
  public void handleEvent(KeyCharEvent ev) {
    if (keyCharListener != null)
      keyCharListener.handleEvent(ev);

    if (!ev.isHandled() && changeListener != null) {
      boolean textModelUpdated;

      switch (ev.charPressed) {
        case Input.KEY_BACK_SPACE -> textModelUpdated = textModel.leftDeleteAtCursor();
        case Input.KEY_DELETE -> textModelUpdated = textModel.rightDeleteAtCursor();
        default -> textModelUpdated = textModel.insertSymbolAtCursor(ev.charPressed);
      }

      if (textModelUpdated)
        changeListener.handleEvent(new ChangeEvent<>(textModel.getTextFromSymbols()));
    }
  }

  @Override
  protected void performUnmount() {
    super.performUnmount();
    cursorAnimation.end();
  }

  /**
   * {@link TextModel} is a document model for {@link Field} widgets which manages the text symbols and manipulations
   * of these symbols based on a cursor.
   * <p>
   * Actions in the {@link Field} are submitted to this model which updates the symbols and cursor, returning the
   * resulting text from the symbols.
   * <p>
   * NB: <i>The cursor is always to the <b>right</b> of its current symbol and a null symbol indicates no text in the
   * {@link Field} widget</i>
   */
  public static class TextModel {
    public static final int CURSOR_WIDTH = 2;
    public static final int CURSOR_ORIGIN_INDEX = -1;
    public static final int PRINTABLE_ASCII_START = 32;
    public static final int PRINTABLE_ASCII_END = 126;

    private final List<Symbol> symbols;
    private final StringBuilder textBuilder;
    private boolean cursorVisible;
    private int cursorIndex;

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

    /** Updates the symbols of this text model after a layout in the associated {@link Field} */
    public void updateSymbols(List<Symbol> symbols) {
      this.symbols.clear();
      this.symbols.addAll(symbols);
    }

    /**
     * When the mouse is down in the associated {@link Field}, this method updates the cursor position by determining
     * which symbol is the closest to the event location
     */
    public void updateCursorPosition(int mouseX, int mouseY) {
      boolean mousePointInSymbolBounds = false;

      for (int symIdx = 0; symIdx < symbols.size(); symIdx++) {
        Symbol sym = symbols.get(symIdx);

        if (sym.contains(mouseX, mouseY)) {
          boolean mouseDownInLeftHalfOfSymbolBounds = sym.x < mouseX && mouseX < sym.x + 0.5 * sym.width;
          cursorIndex = mouseDownInLeftHalfOfSymbolBounds ? (symIdx > 0) ? symIdx - 1 : CURSOR_ORIGIN_INDEX : symIdx;
          mousePointInSymbolBounds = true;

          break;
        }
      }

      if (!mousePointInSymbolBounds) {
        if (!symbols.isEmpty()) {
          Symbol lastSymbol = symbols.get(symbols.size() - 1);
          boolean mousePointToRightOfLastSymbolBounds = mouseX > lastSymbol.x + lastSymbol.width;
          cursorIndex = mousePointToRightOfLastSymbolBounds ? symbols.size() - 1 : CURSOR_ORIGIN_INDEX;
        } else {
          cursorIndex = CURSOR_ORIGIN_INDEX;
        }
      }
    }

    /**
     * Inserts a character symbol at the cursor. Returns {@code true} if the character is a printable ASCII character
     * and was successfully inserted and {@code false} otherwise
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
