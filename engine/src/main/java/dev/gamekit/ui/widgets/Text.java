package dev.gamekit.ui.widgets;

import dev.gamekit.core.Constants;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.Alignment;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Leaf} which renders text to the screen */
@SuppressWarnings("MagicConstant")
public class Text extends Leaf {
  protected final Config<?> config;
  protected final String text;
  protected Font font;
  protected int fontStyle;
  protected int fontSize;
  protected Color color;
  protected Color backgroundColor;
  protected Alignment alignment;
  protected Alignment verticalAlignment;
  protected boolean shadowEnabled;
  protected int shadowOffsetX;
  protected int shadowOffsetY;
  protected Color shadowColor;

  private Font renderFont;
  private FontMetrics fontMetrics;
  private String[] textLines;
  private double[] textOffsets;

  public Text(Config<?> config, String text) {
    this.config = config;
    this.text = text;
  }

  public static Text create(Config<?> params, String text) {
    return new Text(params, text);
  }

  public static Text create(String text) {
    return new Text(new Config<>(), text);
  }

  public static Config<?> config() {
    return new Config<>();
  }

  @Override
  protected void performMounted() {
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    font = coalesce(config.font, theme.textFont, Constants.DEFAULT_FONT);
    fontStyle = coalesce(config.fontStyle, theme.textFontStyle, Font.PLAIN);
    fontSize = coalesce(config.fontSize, theme.textFontSize, 20);
    color = coalesce(config.color, theme.textColor, Color.WHITE);
    backgroundColor =
      coalesce(config.backgroundColor, theme.textBackgroundColor, Constants.TRANSPARENT_COLOR);
    alignment = coalesce(config.alignment, theme.textAlignment, Alignment.START);
    verticalAlignment =
      coalesce(config.verticalAlignment, theme.textVerticalAlignment, Alignment.START);
    shadowEnabled = coalesce(config.shadowEnabled, theme.textShadowEnabled, false);
    shadowOffsetX = coalesce(config.shadowOffsetX, theme.textShadowOffsetX, 0);
    shadowOffsetY = coalesce(config.shadowOffsetY, theme.textShadowOffsetY, 0);
    shadowColor = coalesce(config.shadowColor, theme.textShadowColor, Color.WHITE);

    renderFont = font != null
      ? font.deriveFont(fontStyle, fontSize)
      : Constants.DEFAULT_FONT.deriveFont(fontStyle, fontSize);
    fontMetrics = uiBridge.getFontMetrics(renderFont);
    textLines = new String[0];
  }

  @Override
  protected void performLayout(Constraints constraints) {
    int textWidth = fontMetrics.stringWidth(text);
    int textHeight = fontMetrics.getHeight();

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
        words = text.split("");
        separator = "";
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

      textLines = lines.toArray(String[]::new);

      intrinsicBounds.setSize(maxLineWidth, textHeight * lines.size());

      computedBounds.setSize(
        constraints.constrainWidth(intrinsicBounds.width),
        constraints.constrainHeight(intrinsicBounds.height)
      );

      textOffsets = new double[lines.size()];

      for (int i = 0; i < lines.size(); i++) {
        String line1 = lines.get(i);
        int line1Width = fontMetrics.stringWidth(line1);

        double line1Offset = switch (alignment) {
          case CENTER -> computedBounds.width / 2 - line1Width / 2.0;
          case END -> computedBounds.width - line1Width;
          default -> 0;
        };

        textOffsets[i] = line1Offset;
      }
    } else {
      textLines = new String[]{ text };
      textOffsets = new double[]{
        switch (alignment) {
          case CENTER -> computedBounds.width / 2 - intrinsicBounds.width / 2.0;
          case END -> computedBounds.width - intrinsicBounds.width;
          default -> 0;
        }
      };
    }
  }

  @Override
  protected void performRender(Graphics2D g) {
    g.setFont(renderFont);

    double vOffset = absoluteBounds.y + switch (verticalAlignment) {
      case CENTER -> absoluteBounds.height / 2 - intrinsicBounds.height / 2;
      case END -> absoluteBounds.height - intrinsicBounds.height;
      default -> 0;
    };

    if (shadowEnabled) {
      g.setColor(shadowColor);

      for (int i = 0; i < textLines.length; i++) {
        String line = textLines[i];
        double offset = textOffsets[i];

        g.drawString(
          line,
          (int) (absoluteBounds.x + offset + shadowOffsetX),
          (int) ((i + 1) * fontSize + vOffset + shadowOffsetY)
        );
      }
    }

    g.setColor(color);

    for (int i = 0; i < textLines.length; i++) {
      String line = textLines[i];
      double offset = textOffsets[i];

      g.drawString(
        line,
        (int) (absoluteBounds.x + offset),
        (int) ((i + 1) * fontSize + vOffset)
      );
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

  @SuppressWarnings("unchecked")
  public static class Config<T extends Config<T>> {
    Font font;
    Integer fontStyle;
    Integer fontSize;
    Color color;
    Color backgroundColor;
    Alignment alignment;
    Alignment verticalAlignment;
    Boolean shadowEnabled;
    Integer shadowOffsetX;
    Integer shadowOffsetY;
    Color shadowColor;

    public T font(Font font) {
      this.font = font;
      return (T) this;
    }

    public T fontStyle(int fontStyle) {
      this.fontStyle = fontStyle;
      return (T) this;
    }

    public T fontSize(int fontSize) {
      this.fontSize = fontSize;
      return (T) this;
    }

    public T color(Color color) {
      this.color = color;
      return (T) this;
    }

    public T backgroundColor(Color backgroundColor) {
      this.backgroundColor = backgroundColor;
      return (T) this;
    }

    public T alignment(Alignment alignment) {
      this.alignment = alignment;
      return (T) this;
    }

    public T verticalAlignment(Alignment verticalAlignment) {
      this.verticalAlignment = verticalAlignment;
      return (T) this;
    }

    public T shadowEnabled(boolean shadowEnabled) {
      this.shadowEnabled = shadowEnabled;
      return (T) this;
    }

    public T shadowOffset(int shadowOffsetX, int shadowOffsetY) {
      this.shadowOffsetX = shadowOffsetX;
      this.shadowOffsetY = shadowOffsetY;
      return (T) this;
    }

    public T shadowColor(Color shadowColor) {
      this.shadowColor = shadowColor;
      return (T) this;
    }
  }
}
