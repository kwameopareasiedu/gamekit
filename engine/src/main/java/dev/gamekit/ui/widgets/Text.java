package dev.gamekit.ui.widgets;

import dev.gamekit.core.Renderer;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Position;
import dev.gamekit.utils.Constants;

import java.awt.*;

import static dev.gamekit.utils.Math.clamp;

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
  protected boolean shadowEnabled;
  protected Position shadowOffset;
  protected Color shadowColor;

  private Font renderFont;
  private FontMetrics fontMetrics;

  protected Text(String text) {
    this.text = text;
    fontFamily = "FFF Forward";
    fontStyle = Font.PLAIN;
    fontSize = 16;
    color = Color.WHITE;
    backgroundColor = Constants.TRANSPARENT_COLOR;
    font = Constants.DEFAULT_FONT;
    shadowOffset = new Position(0, 0);
  }

  public static Text create(String text) {
    return new Text(text);
  }

  @Override
  protected void onLayout(Constraints constraints) {
    if (shouldUpdateRenderFont()) {
      logger.debug("Creating new render font");
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

  @Override
  public final void onRender(Graphics2D g) {
    g.setBackground(backgroundColor);
    g.clearRect(0, 0, computedSize.width, computedSize.height);
    g.setFont(renderFont);

    if (shadowEnabled) {
      g.setColor(shadowColor);
      g.drawString(text, shadowOffset.x, fontSize + shadowOffset.y);
    }

    g.setColor(color);
    g.drawString(text, 0, fontSize);
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

  public Text withShadow(boolean shadowEnabled) {
    this.shadowEnabled = shadowEnabled;
    return this;
  }

  public Text withShadowOffset(int x, int y) {
    this.shadowOffset.set(x, y);
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
