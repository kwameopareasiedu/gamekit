package dev.gamekit.ui.widgets;

import dev.gamekit.core.UI;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Param;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.utils.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** A {@link Widget} which renders text to the screen */
@SuppressWarnings("MagicConstant")
public class Text extends Widget {
  protected final String text;
  protected final String fontFamily;
  protected final int fontStyle;
  protected final int fontSize;
  protected final Color color;
  protected final Color backgroundColor;
  protected final Font font;
  protected final Alignment alignment;
  protected final Alignment verticalAlignment;
  protected final boolean shadowEnabled;
  protected final int shadowOffsetX;
  protected final int shadowOffsetY;
  protected final Color shadowColor;

  private final Font renderFont;
  private final FontMetrics fontMetrics;
  private String[] multilineText;

  public Text(
    String text,
    String fontFamily,
    int fontStyle,
    int fontSize,
    Color color,
    Color backgroundColor,
    Font font,
    Alignment alignment,
    Alignment verticalAlignment,
    boolean shadowEnabled,
    int shadowOffsetX,
    int shadowOffsetY,
    Color shadowColor
  ) {
    this.text = text;
    this.fontFamily = font != null ? font.getFamily() : fontFamily;
    this.fontStyle = fontStyle;
    this.fontSize = fontSize;
    this.color = color;
    this.backgroundColor = backgroundColor;
    this.font = font;
    this.alignment = alignment;
    this.verticalAlignment = verticalAlignment;
    this.shadowEnabled = shadowEnabled;
    this.shadowOffsetX = shadowOffsetX;
    this.shadowOffsetY = shadowOffsetY;
    this.shadowColor = shadowColor;

    renderFont = font != null
      ? font.deriveFont(fontStyle, fontSize)
      : new Font(fontFamily, fontStyle, fontSize);
    fontMetrics = UI.getFontMetrics(renderFont);
  }

  @SafeVarargs
  public static Text create(Param<? super TextParam>... params) {
    return new Text(
      Param.getValue(params, "text", "Text"),
      Param.getValue(params, "fontFamily", Constants.DEFAULT_FONT_NAME),
      Param.getValue(params, "fontStyle", Font.PLAIN),
      Param.getValue(params, "fontSize", 16),
      Param.getValue(params, "color", Color.WHITE),
      Param.getValue(params, "backgroundColor", Constants.TRANSPARENT_COLOR),
      Param.getValue(params, "font", Constants.DEFAULT_FONT),
      Param.getValue(params, "alignment", Alignment.START),
      Param.getValue(params, "verticalAlignment", Alignment.START),
      Param.getValue(params, "shadowEnabled", false),
      Param.getValue(params, "shadowOffsetX", 0),
      Param.getValue(params, "shadowOffsetY", 0),
      Param.getValue(params, "shadowColor", Color.WHITE)
    );
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

      multilineText = new String[lines.size()];
      multilineText = lines.toArray(multilineText);

      intrinsicBounds.setSize(maxLineWidth, textHeight * lines.size());

      computedBounds.setSize(
        constraints.constrainWidth(intrinsicBounds.width),
        constraints.constrainHeight(intrinsicBounds.height)
      );
    }
  }

  @Override
  public void performRender(Graphics2D g) {
    g.setBackground(backgroundColor);
    g.clearRect(0, 0, computedBounds.width, computedBounds.height);
    g.setFont(renderFont);

    int hOffset = switch (alignment) {
      case CENTER -> computedBounds.width / 2 - intrinsicBounds.width / 2;
      case END -> computedBounds.width - intrinsicBounds.width;
      default -> 0;
    };

    int vOffset = switch (verticalAlignment) {
      case CENTER -> computedBounds.height / 2 - intrinsicBounds.height / 2;
      case END -> computedBounds.height - intrinsicBounds.height;
      default -> 0;
    };

    if (shadowEnabled) {
      g.setColor(shadowColor);

      if (multilineText != null) {
        for (int i = 0; i < multilineText.length; i++) {
          String word = multilineText[i];

          g.drawString(
            word, hOffset + shadowOffsetX,
            (i + 1) * fontSize + vOffset + shadowOffsetY
          );
        }
      } else {
        g.drawString(
          text, hOffset + shadowOffsetX,
          fontSize + vOffset + shadowOffsetY
        );
      }
    }

    g.setColor(color);

    if (multilineText != null) {
      for (int i = 0; i < multilineText.length; i++) {
        String word = multilineText[i];
        g.drawString(word, hOffset, (i + 1) * fontSize + vOffset);
      }
    } else g.drawString(text, hOffset, fontSize + vOffset);
  }

  @Override
  public boolean stateEquals(Widget widget) {
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
}
