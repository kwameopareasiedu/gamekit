package dev.gamekit.ui.widgets;

import dev.gamekit.core.IO;
import dev.gamekit.core.UI;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.utils.Bounds;

import java.awt.*;
import java.util.*;
import java.util.List;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Leaf} which renders text to the screen */
@SuppressWarnings("MagicConstant")
public class Text extends Leaf {
  public static final Font DEFAULT_FONT = IO.getResourceFont("font-default.ttf");
  public static final int PLAIN = Font.PLAIN;
  public static final int BOLD = Font.BOLD;
  public static final int ITALIC = Font.ITALIC;

  protected String text;
  protected Font font;
  protected Integer fontSize;
  protected Integer fontStyle;
  protected Color color;
  protected Color backgroundColor;
  protected Alignment alignment;
  protected Boolean shadowEnabled;
  protected Integer shadowOffsetX;
  protected Integer shadowOffsetY;
  protected Color shadowColor;
  protected List<Symbol> symbols;

  private Font renderFont;
  private FontMetrics fontMetrics;

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
    return widget instanceof Text textWidget &&
      Objects.equals(text, textWidget.text) &&
      Objects.equals(font, textWidget.font) &&
      Objects.equals(fontSize, textWidget.fontSize) &&
      Objects.equals(fontStyle, textWidget.fontStyle) &&
      Objects.equals(color, textWidget.color) &&
      Objects.equals(backgroundColor, textWidget.backgroundColor) &&
      Objects.equals(alignment, textWidget.alignment) &&
      Objects.equals(shadowEnabled, textWidget.shadowEnabled) &&
      Objects.equals(shadowOffsetX, textWidget.shadowOffsetX) &&
      Objects.equals(shadowOffsetY, textWidget.shadowOffsetY) &&
      Objects.equals(shadowColor, textWidget.shadowColor);
  }

  @Override
  protected void performInit() {
    super.performInit();

    TextConfig<?> config = (TextConfig<?>) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    if (config.text == null)
      throw new IllegalArgumentException("Text text cannot be null");

    text = config.text;
    font = coalesce(config.font, theme.textFont, DEFAULT_FONT);
    fontSize = coalesce(config.fontSize, theme.textFontSize, 20);
    fontStyle = coalesce(config.fontStyle, theme.textFontStyle, PLAIN);
    color = coalesce(config.color, theme.textColor, Color.WHITE);
    backgroundColor = coalesce(config.backgroundColor, theme.textBackgroundColor, null);
    alignment = coalesce(config.alignment, theme.textAlignment, Alignment.START);
    shadowEnabled = coalesce(config.shadowEnabled, theme.textShadowEnabled, false);
    shadowOffsetX = coalesce(config.shadowOffsetX, theme.textShadowOffsetX, 0);
    shadowOffsetY = coalesce(config.shadowOffsetY, theme.textShadowOffsetY, 0);
    shadowColor = coalesce(config.shadowColor, theme.textShadowColor, Color.WHITE);

    renderFont = font != null
      ? font.deriveFont(fontStyle, fontSize)
      : DEFAULT_FONT.deriveFont(fontStyle, fontSize);
    fontMetrics = host.getFontMetrics(renderFont);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    List<String> lines = new ArrayList<>();
    List<Double> lineOffsets = new ArrayList<>();
    List<Symbol> symbols = new ArrayList<>();
    int textWidth = fontMetrics.stringWidth(text);
    int textHeight = fontSize;

    intrinsicBounds.setSize(textWidth, textHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    if (textWidth > computedBounds.width) {
      String[] tokens = text.split(" ");
      String separator = " ";

      boolean singleTokenExceedingComputedWidth = Arrays.stream(tokens).anyMatch(
        token -> fontMetrics.stringWidth(token) > computedBounds.width
      );

      if (singleTokenExceedingComputedWidth) {
        tokens = text.split("");
        separator = "";
      }

      StringBuilder lineBuilder = new StringBuilder();
      int maxLineWidth = 0;
      int currentLineWidth = 0;

      for (String token : tokens) {
        int tokenWidth = fontMetrics.stringWidth(token + separator);

        if (currentLineWidth + tokenWidth > computedBounds.width) {
          lines.add(lineBuilder.toString());
          lineBuilder.setLength(0);
          currentLineWidth = 0;
        }

        lineBuilder.append(token).append(separator);
        currentLineWidth += tokenWidth;

        if (currentLineWidth > maxLineWidth)
          maxLineWidth = currentLineWidth;
      }

      if (!lineBuilder.isEmpty())
        lines.add(lineBuilder.toString());

      intrinsicBounds.setSize(maxLineWidth, textHeight * lines.size());

      computedBounds.setSize(
        constraints.constrainWidth(intrinsicBounds.width),
        constraints.constrainHeight(intrinsicBounds.height)
      );

      for (String line : lines) {
        int lineWidth = fontMetrics.stringWidth(line);

        double lineOffset = switch (alignment) {
          case CENTER -> computedBounds.width / 2 - lineWidth / 2.0;
          case END -> computedBounds.width - lineWidth;
          default -> 0;
        };

        lineOffsets.add(lineOffset);
      }
    } else {
      lines.add(text);
      lineOffsets.add(
        switch (alignment) {
          case CENTER -> computedBounds.width / 2 - intrinsicBounds.width / 2.0;
          case END -> computedBounds.width - intrinsicBounds.width;
          default -> 0.0;
        }
      );
    }

    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.isEmpty())
        continue;

      double lineOffset = lineOffsets.get(i);
      String[] lineCharacters = line.split("");
      double lineYPosition = i * fontSize;

      for (String ch : lineCharacters) {
        int chWidth = fontMetrics.stringWidth(ch);

        symbols.add(
          new Symbol(
            ch.charAt(0),
            lineOffset, lineYPosition,
            chWidth, fontSize
          )
        );

        lineOffset += chWidth;
      }
    }

    this.symbols = Collections.unmodifiableList(symbols);
  }

  @Override
  protected void performPostLayout() {
    super.performPostLayout();

    for (Symbol symbol : symbols) {
      symbol.setPosition(
        absoluteBounds.x + symbol.x,
        absoluteBounds.y + symbol.y
      );
    }
  }

  @Override
  protected void performRender(Graphics2D g) {
    Stroke originalStroke = g.getStroke();
    Color originalColor = g.getColor();
    Font originalFont = g.getFont();

    if (backgroundColor != null) {
      g.setColor(backgroundColor);

      g.fillRect(
        (int) absoluteBounds.x,
        (int) absoluteBounds.y,
        (int) absoluteBounds.width,
        (int) absoluteBounds.height
      );
    }

    g.setFont(renderFont);

    if (shadowEnabled) {
      g.setColor(shadowColor);

      for (Symbol symbol : symbols) {
        g.drawString(
          String.valueOf(symbol.value),
          (int) symbol.x + shadowOffsetX,
          (int) (symbol.y + symbol.height + shadowOffsetY)
        );
      }
    }

    g.setColor(color);

    for (Symbol symbol : symbols) {
      g.drawString(
        String.valueOf(symbol.value),
        (int) symbol.x,
        (int) (symbol.y + symbol.height)
      );
    }

    if (Widget.DEBUG_DRAW) {
      g.setColor(UI.DEBUG_COLOR);
      g.setStroke(UI.DEBUG_STROKE);

      for (Symbol symbol : symbols) {
        g.drawRect(
          (int) symbol.x,
          (int) symbol.y,
          (int) symbol.width,
          (int) symbol.height
        );
      }
    }

    g.setFont(originalFont);
    g.setColor(originalColor);
    g.setStroke(originalStroke);
  }

  @SuppressWarnings("unchecked")
  public static class TextConfig<T extends TextConfig<T>> extends LeafConfig {
    protected String text;
    protected Font font;
    protected Integer fontStyle;
    protected Integer fontSize;
    protected Color color;
    protected Color backgroundColor;
    protected Alignment alignment;
    protected Boolean shadowEnabled;
    protected Integer shadowOffsetX;
    protected Integer shadowOffsetY;
    protected Color shadowColor;

    private T text(String text) {
      this.text = text;
      return (T) this;
    }

    public T font(Font font) {
      this.font = font;
      return (T) this;
    }

    public T fontSize(int fontSize) {
      this.fontSize = fontSize;
      return (T) this;
    }

    public T fontStyle(int fontStyle) {
      this.fontStyle = fontStyle;
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

  /** A store for a character symbol and its absolute bounds */
  public static class Symbol extends Bounds {
    public final char value;

    public Symbol(char value, double x, double y, double w, double h) {
      super(x, y, w, h);
      this.value = value;
    }
  }
}
