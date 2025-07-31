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
  public static final int PLAIN = Font.PLAIN;
  public static final int BOLD = Font.BOLD;
  public static final int ITALIC = Font.ITALIC;

  protected String text;
  protected Font font;
  protected int fontSize;
  protected int fontStyle;
  protected Color foregroundColor;
  protected Color backgroundColor;
  protected Alignment horizontalAlignment;
  protected Alignment verticalAlignment;
  protected boolean shadowEnabled;
  protected int shadowOffsetX;
  protected int shadowOffsetY;
  protected Color shadowColor;

  private Font renderFont;
  private FontMetrics fontMetrics;
  private String[] textLines;
  private double[] textOffsets;

  public Text(TextConfig<?> config, String text) {
    super(config.text(text));
  }

  public static Text create(TextConfig<?> params, String text) {
    return new Text(params, text);
  }

  public static Text create(String text) {
    return new Text(new TextConfig<>(), text);
  }

  public static TextConfig<?> config() {
    return new TextConfig<>();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Text textWidget)
      return Objects.equals(text, textWidget.text) &&
        Objects.equals(font, textWidget.font) &&
        Objects.equals(fontSize, textWidget.fontSize) &&
        Objects.equals(fontStyle, textWidget.fontStyle) &&
        Objects.equals(foregroundColor, textWidget.foregroundColor) &&
        Objects.equals(backgroundColor, textWidget.backgroundColor) &&
        Objects.equals(horizontalAlignment, textWidget.horizontalAlignment) &&
        Objects.equals(verticalAlignment, textWidget.verticalAlignment) &&
        Objects.equals(shadowEnabled, textWidget.shadowEnabled) &&
        Objects.equals(shadowOffsetX, textWidget.shadowOffsetX) &&
        Objects.equals(shadowOffsetY, textWidget.shadowOffsetY) &&
        Objects.equals(shadowColor, textWidget.shadowColor);

    return false;
  }

  @Override
  protected void performInit() {
    TextConfig<?> config = (TextConfig<?>) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    if (config.text == null)
      throw new IllegalArgumentException("Text text cannot be null");

    text = coalesce(config.text, "");
    font = coalesce(config.font, theme.textFont, Constants.DEFAULT_FONT);
    fontSize = coalesce(config.fontSize, theme.textFontSize, 20);
    fontStyle = coalesce(config.fontStyle, theme.textFontStyle, Font.PLAIN);
    foregroundColor = coalesce(config.foregroundColor, theme.textForegroundColor, Color.WHITE);
    backgroundColor =
      coalesce(config.backgroundColor, theme.textBackgroundColor, Constants.TRANSPARENT_COLOR);
    horizontalAlignment =
      coalesce(config.horizontalAlignment, theme.textHorizontalAlignment, Alignment.START);
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

    super.performInit();
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

        double line1Offset = switch (horizontalAlignment) {
          case CENTER -> computedBounds.width / 2 - line1Width / 2.0;
          case END -> computedBounds.width - line1Width;
          default -> 0;
        };

        textOffsets[i] = line1Offset;
      }
    } else {
      textLines = new String[]{ text };
      textOffsets = new double[]{
        switch (horizontalAlignment) {
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

    g.setColor(foregroundColor);

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

  @SuppressWarnings("unchecked")
  public static class TextConfig<T extends TextConfig<T>> extends LeafConfig {
    protected String text;
    protected Font font;
    protected Integer fontStyle;
    protected Integer fontSize;
    protected Color foregroundColor;
    protected Color backgroundColor;
    protected Alignment horizontalAlignment;
    protected Alignment verticalAlignment;
    protected Boolean shadowEnabled;
    protected Integer shadowOffsetX;
    protected Integer shadowOffsetY;
    protected Color shadowColor;

    private T text(String text) {
      this.text = text;
      return (T) this;
    }

    public T font(int fontSize, int fontStyle, Font font) {
      this.fontSize = fontSize;
      this.fontStyle = fontStyle;
      this.font = font;
      return (T) this;
    }

    public T font(int fontSize, int fontStyle) {
      return font(fontSize, fontStyle, null);
    }

    public T color(Color color, Color backgroundColor) {
      this.foregroundColor = color;
      this.backgroundColor = backgroundColor;
      return (T) this;
    }

    public T alignment(Alignment horizontalAlignment, Alignment verticalAlignment) {
      this.horizontalAlignment = horizontalAlignment;
      this.verticalAlignment = verticalAlignment;
      return (T) this;
    }

    public T shadow(
      boolean shadowEnabled,
      int shadowOffsetX,
      int shadowOffsetY,
      Color shadowColor
    ) {
      this.shadowEnabled = shadowEnabled;
      this.shadowOffsetX = shadowOffsetX;
      this.shadowOffsetY = shadowOffsetY;
      this.shadowColor = shadowColor;
      return (T) this;
    }
  }
}
