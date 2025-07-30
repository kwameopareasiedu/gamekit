package dev.gamekit.ui.widgets;

import dev.gamekit.core.Input;
import dev.gamekit.ui.BorderData;
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
  protected BorderData defaultBorder;
  protected BorderData focusBorder;
  protected Spacing padding;
  protected FocusEvent.Handler focusListener;
  protected KeyCharEvent.Handler keyCharListener;
  protected ChangeEvent.Handler<String> changeListener;
  protected boolean focused;

  private final Bounds absoluteContentBounds;
  private final Bounds tempAbsoluteBounds;
  private Stroke defaultBorderStroke;
  private Stroke focusBorderStroke;

  public Field(FieldConfig config, String text) {
    super(config, text);
    absoluteContentBounds = new Bounds();
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
        Objects.equals(defaultBorder, fieldWidget.defaultBorder) &&
        Objects.equals(focusBorder, fieldWidget.focusBorder) &&
        Objects.equals(padding, fieldWidget.padding);

    return false;
  }

  @Override
  protected void performInit() {
    super.performInit();

    FieldConfig config = (FieldConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    ninePatchSpacing = coalesce(config.ninePatchSpacing, theme.fieldNinePatchSpacing);
    defaultBackground = coalesce(config.defaultBackground, theme.fieldDefaultBackground);
    focusBackground = coalesce(config.focusBackground, theme.fieldFocusBackground);
    defaultBorder =
      coalesce(config.defaultBorder, theme.fieldDefaultBorder, BorderData.create(1, 12, Color.WHITE));
    focusBorder =
      coalesce(config.focusBorder, theme.fieldFocusBorder, BorderData.create(1, 12, Color.GREEN));
    padding = coalesce(config.padding, theme.fieldPadding, Spacing.create());
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
    BufferedImage background = defaultBackground;
    BorderData border = defaultBorder;
    Stroke borderStroke = defaultBorderStroke;
    Stroke originalStroke = g.getStroke();
    Color originalColor = g.getColor();

    if (focused) {
      background = focusBackground;
      border = focusBorder;
      borderStroke = focusBorderStroke;
    }

    if (background != null)
      renderWith9PatchScaling(background, absoluteBounds, ninePatchSpacing, g);

    tempAbsoluteBounds.set(absoluteBounds);
    absoluteBounds.set(absoluteContentBounds);
    super.performRender(g);
    absoluteBounds.set(tempAbsoluteBounds);

    g.setStroke(borderStroke);
    g.setColor(border.color());
    g.drawRoundRect(
      (int) absoluteBounds.x, (int) absoluteBounds.y,
      (int) absoluteBounds.width - 1, (int) absoluteBounds.height - 1,
      (int) border.radius(), (int) border.radius()
    );

    g.setStroke(originalStroke);
    g.setColor(originalColor);
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
    private Spacing ninePatchSpacing;
    private BufferedImage defaultBackground;
    private BufferedImage focusBackground;
    private BorderData defaultBorder;
    private BorderData focusBorder;
    private Spacing padding;
    private FocusEvent.Handler focusListener;
    private KeyCharEvent.Handler keyCharListener;
    private ChangeEvent.Handler<String> changeListener;

    public FieldConfig ninePatchSpacing(Spacing ninePatchSpacing) {
      this.ninePatchSpacing = ninePatchSpacing;
      return this;
    }

    public FieldConfig background(BufferedImage defaultBackground, BufferedImage focusBackground) {
      this.defaultBackground = defaultBackground;
      this.focusBackground = focusBackground;
      return this;
    }

    public FieldConfig border(BorderData defaultBorder, BorderData focusBorder) {
      this.defaultBorder = defaultBorder;
      this.focusBorder = focusBorder;
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
