package dev.gamekit.ui.widgets;

import dev.gamekit.core.UI;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.TextAlignment;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.util.Objects;

/** A {@link Widget} which renders text to the screen */
@SuppressWarnings("MagicConstant")
public class Text extends Widget {
  protected String text;
  protected String fontFamily;
  protected int fontStyle;
  protected int fontSize;
  protected Color color;
  protected Color backgroundColor;
  protected Font font;
  protected TextAlignment alignment;
  protected boolean shadowEnabled;
  protected int shadowOffsetX;
  protected int shadowOffsetY;
  protected Color shadowColor;

  private Font renderFont;
  private FontMetrics fontMetrics;

  protected Text(String text) {
    this.text = text;
    fontFamily = Constants.DEFAULT_FONT_NAME;
    fontStyle = Font.PLAIN;
    fontSize = 16;
    color = Color.WHITE;
    alignment = TextAlignment.START;
    backgroundColor = Constants.TRANSPARENT_COLOR;
    font = Constants.DEFAULT_FONT;
    shadowOffsetX = 0;
    shadowOffsetY = 0;
  }

  public static Text create(String text) {
    return new Text(text);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    if (shouldUpdateRenderFont()) {
      logger.debug("Creating new render font");
      renderFont = font != null
        ? font.deriveFont(fontStyle, fontSize)
        : new Font(fontFamily, fontStyle, fontSize);
      fontMetrics = UI.getFontMetrics(renderFont);
      fontFamily = renderFont.getFamily();
    }

    int textWidth = fontMetrics.stringWidth(text);
    int textHeight = (int) (1.5 * fontMetrics.getHeight());

    if (shadowEnabled) {
      textWidth += Math.abs(shadowOffsetX);
      textHeight += Math.abs(shadowOffsetY);
    }

    intrinsicBounds.setSize(textWidth, textHeight);

    int computedWidth = constraints.constrainWidth(textWidth);
    int computedHeight = constraints.constrainHeight(textHeight);
    computedBounds.setSize(computedWidth, computedHeight);
  }

  @Override
  public final void performRender(Graphics2D g) {
    g.setBackground(backgroundColor);
    g.clearRect(0, 0, computedBounds.width, computedBounds.height);
    g.setFont(renderFont);

    int textOffset = switch (alignment) {
      case CENTER -> computedBounds.width / 2 - intrinsicBounds.width / 2;
      case END -> computedBounds.width - intrinsicBounds.width;
      default -> 0;
    };

    if (shadowEnabled) {
      g.setColor(shadowColor);
      g.drawString(text, textOffset + shadowOffsetX, fontSize + shadowOffsetY);
    }

    g.setColor(color);
    g.drawString(text, textOffset, fontSize);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Text textWidget) {
      return Objects.equals(text, textWidget.text) &&
        Objects.equals(fontFamily, textWidget.fontFamily) &&
        Objects.equals(fontStyle, textWidget.fontStyle) &&
        Objects.equals(fontSize, textWidget.fontSize) &&
        Objects.equals(color, textWidget.color) &&
        Objects.equals(backgroundColor, textWidget.backgroundColor) &&
        Objects.equals(font, textWidget.font) &&
        Objects.equals(shadowEnabled, textWidget.shadowEnabled) &&
        Objects.equals(shadowOffsetX, textWidget.shadowOffsetX) &&
        Objects.equals(shadowOffsetY, textWidget.shadowOffsetY) &&
        Objects.equals(shadowColor, textWidget.shadowColor);
    }

    return false;
  }

  /**
   * Sets the font family of this text. The name of the
   * font should match an installed font on the system
   */
  public Text withFontFamily(String fontFamily) {
    this.fontFamily = fontFamily;
    return this;
  }

  public Text withFontStyle(int fontStyle) {
    this.fontStyle = fontStyle;
    return this;
  }

  public Text withFontSize(int fontSize) {
    this.fontSize = fontSize;
    return this;
  }

  public Text withBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
    return this;
  }

  public Text withColor(Color color) {
    this.color = color;
    return this;
  }

  /** Sets the font of this text. If set, this overrides {@link #fontFamily} value */
  public Text withFont(Font font) {
    this.font = font;
    return this;
  }

  public Text withAlignment(TextAlignment alignment) {
    this.alignment = alignment;
    return this;
  }

  public Text withShadow(boolean shadowEnabled) {
    this.shadowEnabled = shadowEnabled;
    return this;
  }

  public Text withShadowOffset(int x, int y) {
    this.shadowOffsetX = x;
    this.shadowOffsetY = y;
    return this;
  }

  public Text withShadowColor(Color color) {
    this.shadowColor = color;
    return this;
  }

  /**
   * Determines if the font should be updated.
   * This is done by checking if the {@link #fontFamily},
   * {@link #fontSize} or {@link #fontStyle} have changed
   */
  private boolean shouldUpdateRenderFont() {
    return renderFont == null ||
      !renderFont.getFamily().equals(fontFamily) ||
      renderFont.getSize() != fontSize ||
      renderFont.getStyle() != fontStyle;
  }
}
