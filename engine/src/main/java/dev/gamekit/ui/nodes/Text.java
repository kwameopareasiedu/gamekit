package dev.gamekit.ui.nodes;

import dev.gamekit.core.Renderer;
import dev.gamekit.ui.Node;
import dev.gamekit.utils.Constants;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Position;
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

  public static Builder create(String text) {
    return new Builder(text);
  }

  @Override
  protected void onLayout(Constraints constraints) {
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

  public static class Builder {
    private final Text instance;

    private Builder(String text) {
      instance = new Text(text);
    }

    public Text get() { return instance; }

    /**
     * Sets the font family of this text. The name of the
     * font should match an installed font on the system
     */
    public Builder withFontFamily(String fontFamily) {
      instance.fontFamily = fontFamily;
      return this;
    }

    public Builder withFontStyle(int fontStyle) {
      instance.fontStyle = fontStyle;
      return this;
    }

    public Builder withFontSize(int fontSize) {
      instance.fontSize = fontSize;
      return this;
    }

    public Builder withBackgroundColor(Color backgroundColor) {
      instance.backgroundColor = backgroundColor;
      return this;
    }

    public Builder withColor(Color color) {
      instance.color = color;
      return this;
    }

    /** Sets the font of this text. If set, this overrides {@link #fontFamily} value */
    public Builder withFont(Font font) {
      instance.font = font;
      return this;
    }

    public Builder withShadow(boolean shadowEnabled) {
      instance.shadowEnabled = shadowEnabled;
      return this;
    }

    public Builder withShadowOffset(int x, int y) {
      instance.shadowOffset.set(x, y);
      return this;
    }

    public Builder withShadowColor(Color color) {
      instance.shadowColor = color;
      return this;
    }
  }
}
