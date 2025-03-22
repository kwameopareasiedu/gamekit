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
        text, padding.left + shadowOffset.x,
        fontSize + padding.top + shadowOffset.y
      );
    }

    graphics.setColor(color);
    graphics.drawString(text, padding.left, fontSize + padding.top);
    return image;
  }

  public void setText(String text) { this.text = text; }

  public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }

  public void setFontStyle(int fontStyle) { this.fontStyle = fontStyle; }

  public void setFontSize(int fontSize) { this.fontSize = fontSize; }

  public void setBgColor(Color bgColor) { this.bgColor = bgColor; }

  public void setColor(Color color) { this.color = color; }

  public void setFont(Font font) { this.font = font; }

  public void toggleShadow(boolean enabled) { shadowEnabled = enabled; }

  public void setShadowOffset(int x, int y) { shadowOffset.set(x, y); }

  public void setShadowColor(Color color) { shadowColor = color; }
}
