package dev.gamekit.ui.widgets;

import dev.gamekit.core.Renderer;
import dev.gamekit.utils.Alignment;
import dev.gamekit.utils.Constraints;
import dev.gamekit.ui.Node;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

import static dev.gamekit.utils.Math.clamp;

/** A {@link Node} which renders text to the screen */
@SuppressWarnings("MagicConstant")
public class Text extends Node {
  private static final Logger LOGGER = LogManager.getLogger();

  protected String text;
  protected String fontFamily;
  protected int fontStyle;
  protected int fontSize;
  protected Color color;
  protected Color backgroundColor;
  protected Font font;
  protected Alignment alignment;
  protected boolean shadowEnabled;
  protected Position shadowOffset;
  protected Color shadowColor;

  private Font renderFont;
  private FontMetrics fontMetrics;

  public Text(String text) {
    this.text = text;
    fontFamily = "FFF Forward";
    fontStyle = Font.PLAIN;
    fontSize = 16;
    color = Color.WHITE;
    alignment = Alignment.TOP_LEFT;
    backgroundColor = Constants.TRANSPARENT_COLOR;
    font = Constants.DEFAULT_FONT;
    shadowOffset = new Position(0, 0);
  }

  @Override
  public void onLayout(Constraints constraints) {
    if (shouldUpdateRenderFont()) {
      LOGGER.debug("Creating new render font");
      renderFont = font != null
        ? font.deriveFont(fontStyle, fontSize)
        : new Font(fontFamily, fontStyle, fontSize);
      fontMetrics = Renderer.getFontMetrics(renderFont);
      fontFamily = renderFont.getFamily();
    }

    int intrinsicWidth = fontMetrics.stringWidth(text);
    int intrinsicHeight = fontMetrics.getHeight();

    if (shadowEnabled) {
      intrinsicWidth += Math.abs(shadowOffset.x);
      intrinsicHeight += Math.abs(shadowOffset.y);
    }

    intrinsicSize.set(intrinsicWidth, intrinsicHeight);

    int computedWidth = clamp(intrinsicSize.width, constraints.minWidth, constraints.maxWidth);
    int computedHeight = clamp(intrinsicSize.height, constraints.minHeight, constraints.maxHeight);
    computedSize.set(computedWidth, computedHeight);
  }

  @SuppressWarnings("DataFlowIssue")
  @Override
  public Appearance getAppearance() {
    Appearance appearance = super.getAppearance();
    Graphics2D g = appearance.graphics;

    g.setBackground(backgroundColor);
    g.clearRect(0, 0, computedSize.width, computedSize.height);
    g.setFont(renderFont);

    int drawX = 0, drawY = 0;

    switch (alignment) {
      case TOP_LEFT -> {
        drawX = 0;
        drawY = 0;
      }
      case TOP_CENTER -> {
        drawX = computedSize.width / 2 - intrinsicSize.width / 2;
        drawY = 0;
      }
      case TOP_RIGHT -> {
        drawX = computedSize.width - intrinsicSize.width;
        drawY = 0;
      }
      case LEFT -> {
        drawX = 0;
        drawY = computedSize.height / 2 - intrinsicSize.height / 2;
      }
      case CENTER -> {
        drawX = computedSize.width / 2 - intrinsicSize.width / 2;
        drawY = computedSize.height / 2 - intrinsicSize.height / 2;
      }
      case RIGHT -> {
        drawX = computedSize.width - intrinsicSize.width;
        drawY = computedSize.height / 2 - intrinsicSize.height / 2;
      }
      case BOTTOM_LEFT -> {
        drawX = 0;
        drawY = computedSize.height - intrinsicSize.height;
      }
      case BOTTOM_CENTER -> {
        drawX = computedSize.width / 2 - intrinsicSize.width / 2;
        drawY = computedSize.height - intrinsicSize.height;
      }
      case BOTTOM_RIGHT -> {
        drawX = computedSize.width - intrinsicSize.width;
        drawY = computedSize.height - intrinsicSize.height;
      }
    }

    if (shadowEnabled) {
      g.setColor(shadowColor);
      g.drawString(text, drawX + shadowOffset.x, drawY + fontSize + shadowOffset.y);
    }

    g.setColor(color);
    g.drawString(text, drawX, drawY + fontSize);
    return appearance;
  }

  /**
   * Sets the text of this text
   * @param text The text content
   */
  public void setText(String text) { this.text = text; }

  /**
   * Sets the font family of this text.
   * <p>
   * The name of the font should match an installed font on the system
   * @param fontFamily The text font family
   */
  public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }

  /**
   * Sets the font style of this text
   * @param fontStyle The text font style
   */
  public void setFontStyle(int fontStyle) { this.fontStyle = fontStyle; }

  /**
   * Sets the font size of this text
   * @param fontSize The text font size
   */
  public void setFontSize(int fontSize) { this.fontSize = fontSize; }

  /**
   * Sets the background color of this text
   * @param backgroundColor The text background color
   */
  public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }

  /**
   * Sets the color of this text
   * @param color The text color
   */
  public void setColor(Color color) { this.color = color; }

  /**
   * Sets the font of this text. If set, this overrides {@link #fontFamily} value
   * @param font The text font
   */
  public void setFont(Font font) { this.font = font; }

  /**
   * Sets the alignment of this text
   * @param alignment The text alignment
   */
  public void setAlignment(Alignment alignment) { this.alignment = alignment; }

  /**
   * Enables or disabled the drop shadow of this text
   * @param enabled The drop shadow state
   */
  public void toggleShadow(boolean enabled) { shadowEnabled = enabled; }

  /**
   * Sets the shadow offset of this text
   * @param x The horizontal offset. This can be negative
   * @param y The vertical offset. This can be negative
   */
  public void setShadowOffset(int x, int y) { shadowOffset.set(x, y); }

  /**
   * Sets the shadow color of this text
   * @param color The text shadow color
   */
  public void setShadowColor(Color color) { shadowColor = color; }

  /**
   * Indicates whether the {@link #renderFont} should be updated
   * @return Whether the render font should be updated
   */
  private boolean shouldUpdateRenderFont() {
    return renderFont == null ||
      !renderFont.getFamily().equals(fontFamily) ||
      renderFont.getSize() != fontSize ||
      renderFont.getStyle() != fontStyle;
  }
}
