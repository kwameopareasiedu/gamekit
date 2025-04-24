package dev.gamekit.ui.widgets;

import dev.gamekit.core.UI;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dev.gamekit.utils.Constants.DEFAULT_FONT;

/** A {@link Leaf} which renders text to the screen */
@SuppressWarnings("MagicConstant")
public class Text extends Leaf {
  protected final String text;
  protected final Font font;
  protected final int fontStyle;
  protected final int fontSize;
  protected final Color color;
  protected final Color backgroundColor;
  protected final Alignment alignment;
  protected final Alignment verticalAlignment;
  protected final boolean shadowEnabled;
  protected final int shadowOffsetX;
  protected final int shadowOffsetY;
  protected final Color shadowColor;

  private final Font renderFont;
  private final FontMetrics fontMetrics;
  private String[] textLines;

  public Text(TextOptions options, String text) {
    this.text = text;
    this.font = options.font;
    this.fontStyle = options.fontStyle;
    this.fontSize = options.fontSize;
    this.color = options.color;
    this.backgroundColor = options.backgroundColor;
    this.alignment = options.alignment;
    this.verticalAlignment = options.verticalAlignment;
    this.shadowEnabled = options.shadowEnabled;
    this.shadowOffsetX = options.shadowOffsetX;
    this.shadowOffsetY = options.shadowOffsetY;
    this.shadowColor = options.shadowColor;

    renderFont = font != null
      ? font.deriveFont(fontStyle, fontSize)
      : DEFAULT_FONT.deriveFont(fontStyle, fontSize);
    fontMetrics = UI.getFontMetrics(renderFont);
    textLines = new String[0];
  }

  public static Text create(TextOptions params, String text) {
    return new Text(params, text);
  }

  public static Text create(String text) {
    return new Text(new TextOptions(), text);
  }

  public static TextOptions options() {
    return new TextOptions();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    int textWidth = fontMetrics.stringWidth(text);
    int textHeight =
      1 + fontMetrics.getAscent() + Math.abs(fontMetrics.getDescent());

    if (shadowEnabled) {
      textWidth += Math.abs(shadowOffsetX);
      textHeight += Math.abs(shadowOffsetY);
    }

    intrinsicBounds.setSize(textWidth, textHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    if (textWidth > computedBounds.width) {
      String[] words = text.split(" ");
      String separator = " ";

      boolean singleWordExceedingComputedWidth = Arrays.stream(words).anyMatch(
        word -> fontMetrics.stringWidth(word) > computedBounds.width
      );

      if (singleWordExceedingComputedWidth) {
        separator = "";
        words = text.split("");
      }

      List<String> lines = new ArrayList<>();
      StringBuilder line = new StringBuilder();
      int maxLineWidth = 0;
      int lineWidth = 0;

      for (String word : words) {
        int wordWidth = fontMetrics.stringWidth(word + separator);

        if (lineWidth + wordWidth > computedBounds.width) {
          lines.add(line.toString());
          line.setLength(0);
          lineWidth = 0;
        }

        line.append(word).append(separator);
        lineWidth += wordWidth;

        if (lineWidth > maxLineWidth)
          maxLineWidth = lineWidth;
      }

      if (!line.isEmpty())
        lines.add(line.toString());

      textLines = new String[lines.size()];
      textLines = lines.toArray(textLines);

      intrinsicBounds.setSize(maxLineWidth, textHeight * lines.size());

      computedBounds.setSize(
        constraints.constrainWidth(intrinsicBounds.width),
        constraints.constrainHeight(intrinsicBounds.height)
      );
    } else {
      textLines = new String[]{ text };
    }
  }

  @Override
  protected void performRender(Graphics2D g) {
    g.setFont(renderFont);

    int hOffset = absoluteBounds.x + switch (alignment) {
      case CENTER -> absoluteBounds.width / 2 - intrinsicBounds.width / 2;
      case END -> absoluteBounds.width - intrinsicBounds.width;
      default -> 0;
    };

    int vOffset = absoluteBounds.y + switch (verticalAlignment) {
      case CENTER -> absoluteBounds.height / 2 - intrinsicBounds.height / 2;
      case END -> absoluteBounds.height - intrinsicBounds.height;
      default -> 0;
    };

    if (shadowEnabled) {
      g.setColor(shadowColor);

      for (int i = 0; i < textLines.length; i++) {
        String word = textLines[i];

        g.drawString(
          word, hOffset + shadowOffsetX,
          (i + 1) * fontSize + vOffset + shadowOffsetY
        );
      }
    }

    g.setColor(color);

    for (int i = 0; i < textLines.length; i++) {
      String word = textLines[i];
      g.drawString(word, hOffset, (i + 1) * fontSize + vOffset);
    }
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Text textWidget) {
      return Objects.equals(text, textWidget.text) &&
        Objects.equals(font, textWidget.font) &&
        Objects.equals(fontStyle, textWidget.fontStyle) &&
        Objects.equals(fontSize, textWidget.fontSize) &&
        Objects.equals(color, textWidget.color) &&
        Objects.equals(backgroundColor, textWidget.backgroundColor) &&
        Objects.equals(shadowEnabled, textWidget.shadowEnabled) &&
        Objects.equals(shadowOffsetX, textWidget.shadowOffsetX) &&
        Objects.equals(shadowOffsetY, textWidget.shadowOffsetY) &&
        Objects.equals(shadowColor, textWidget.shadowColor);
    }

    return false;
  }

  public static class TextOptions {
    Font font = DEFAULT_FONT;
    int fontStyle = Font.PLAIN;
    int fontSize = 16;
    Color color = Color.WHITE;
    Color backgroundColor = Constants.TRANSPARENT_COLOR;
    Alignment alignment = Alignment.START;
    Alignment verticalAlignment = Alignment.START;
    boolean shadowEnabled = false;
    int shadowOffsetX = 0;
    int shadowOffsetY = 0;
    Color shadowColor = Color.WHITE;

    public TextOptions fontStyle(int fontStyle) {
      this.fontStyle = fontStyle;
      return this;
    }

    public TextOptions fontSize(int fontSize) {
      this.fontSize = fontSize;
      return this;
    }

    public TextOptions color(Color color) {
      this.color = color;
      return this;
    }

    public TextOptions backgroundColor(Color backgroundColor) {
      this.backgroundColor = backgroundColor;
      return this;
    }

    public TextOptions font(Font font) {
      this.font = font;
      return this;
    }

    public TextOptions alignment(Alignment alignment) {
      this.alignment = alignment;
      return this;
    }

    public TextOptions verticalAlignment(Alignment verticalAlignment) {
      this.verticalAlignment = verticalAlignment;
      return this;
    }

    public TextOptions shadowEnabled(boolean shadowEnabled) {
      this.shadowEnabled = shadowEnabled;
      return this;
    }

    public TextOptions shadowOffset(int shadowOffsetX, int shadowOffsetY) {
      this.shadowOffsetX = shadowOffsetX;
      this.shadowOffsetY = shadowOffsetY;
      return this;
    }

    public TextOptions shadowColor(Color shadowColor) {
      this.shadowColor = shadowColor;
      return this;
    }
  }
}
