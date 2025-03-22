package dev.gamekit.ui;

import dev.gamekit.core.Renderer;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;

/** A {@link Node} which renders text to the screen */
@SuppressWarnings("MagicConstant")
public class Text extends Node {
  protected String text;
  protected String fontFamily;
  protected int fontStyle;
  protected int fontSize;
  protected Color color;
  protected Color bgColor;
  protected Font font;
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
    bgColor = Constants.TRANSPARENT;
    font = Constants.DEFAULT_FONT;
    shadowOffset = new Position(0, 0);
    padding.set(0);
  }

  @Override
  public void onUpdate() {
    boolean shouldUpdateFont = renderFont == null ||
      !renderFont.getFamily().equals(fontFamily) ||
      renderFont.getSize() != fontSize ||
      renderFont.getStyle() != fontStyle;

    if (shouldUpdateFont) {
      renderFont = font != null
        ? font.deriveFont(fontStyle, fontSize)
        : new Font(fontFamily, fontStyle, fontSize);

      fontMetrics = Renderer.getFontMetrics(renderFont);
    }

    int contentWidth = fontMetrics.stringWidth(text) + padding.getHorizontal();
    int contentHeight = fontMetrics.getHeight() + padding.getVertical();

    if (shadowEnabled) {
      contentWidth += Math.abs(shadowOffset.x);
      contentHeight += Math.abs(shadowOffset.y);
    }

    size.set(contentWidth, contentHeight);
  }

  @Override
  public BufferedImage getAppearance() {
    if (image == null || image.getWidth() != size.width || image.getHeight() != size.height) {
      image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
      graphics = image.createGraphics();
    }

    graphics.setBackground(bgColor);
    graphics.clearRect(0, 0, size.width, size.height);
    graphics.setFont(renderFont);

    if (shadowEnabled) {
      graphics.setColor(shadowColor);
      graphics.drawString(
        text,
        padding.left + shadowOffset.x,
        fontSize + padding.top + shadowOffset.y
      );
    }

    graphics.setColor(color);
    graphics.drawString(text, padding.left, fontSize + padding.top);
    return image;
  }

  /**
   * Sets the text of this text
   * @param text The text of this text
   */
  public void setText(String text) { this.text = text; }

  /**
   * Sets the font family of this text.
   * <p>
   * The name of the font should match an installed font on the system
   * @param fontFamily The font family of this text
   */
  public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }

  /**
   * Sets the font style of this text
   * @param fontStyle The font style of this text
   */
  public void setFontStyle(int fontStyle) { this.fontStyle = fontStyle; }

  /**
   * Sets the font size of this text
   * @param fontSize The font size of this text
   */
  public void setFontSize(int fontSize) { this.fontSize = fontSize; }

  /**
   * Sets the background color of this text
   * @param bgColor The background color of this text
   */
  public void setBgColor(Color bgColor) { this.bgColor = bgColor; }

  /**
   * Sets the color of this text
   * @param color The color of this text
   */
  public void setColor(Color color) { this.color = color; }

  /**
   * Sets the font of this text. If set, this overrides {@link #fontFamily} value
   * @param font The font of this text
   */
  public void setFont(Font font) { this.font = font; }

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
   * @param color The shadow color of this text
   */
  public void setShadowColor(Color color) { shadowColor = color; }
}
