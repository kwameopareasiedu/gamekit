package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.core.IO;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** A {@link Leaf} which renders text to the screen */
@SuppressWarnings("MagicConstant")
@WidgetBuilder
public class Text extends Leaf {
  public static final Font DEFAULT_FONT = IO.getFont("font-default.ttf");
  public static final int PLAIN = Font.PLAIN;
  public static final int BOLD = Font.BOLD;
  public static final int ITALIC = Font.ITALIC;

  @WidgetBuilderField(fallback = "\"Hello GameKit\"")
  public String text;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Text.DEFAULT_FONT")
  public Font font;
  @WidgetBuilderField(fallback = "20")
  public Integer fontSize;
  @WidgetBuilderField(fallback = "1.0")
  public Double fontHeightRatio;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.widgets.Text.PLAIN")
  public Integer fontStyle;
  @WidgetBuilderField(fallback = "java.awt.Color.WHITE")
  public Color color;
  @WidgetBuilderField
  public Color backgroundColor;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.enums.Alignment.START")
  public Alignment alignment;
  @WidgetBuilderField(fallback = "false")
  public Boolean shadowEnabled;
  @WidgetBuilderField(fallback = "0")
  public Integer shadowOffsetX;
  @WidgetBuilderField(fallback = "0")
  public Integer shadowOffsetY;
  @WidgetBuilderField(fallback = "java.awt.Color.WHITE")
  public Color shadowColor;

  protected List<Symbol> symbols;

  private Font renderFont;
  private FontMetrics fontMetrics;

  public Text(String key, TextConfig config) {
    super(key, config);
  }

  public static Text create(String key, TextConfig.Updater updater) {
    return new Text(key, Widgets.configureText(updater));
  }

  public static Text create(TextConfig.Updater updater) {
    return new Text(null, Widgets.configureText(updater));
  }

  public static Text create(String key, String text) {
    return Text.create(key, props -> props.text = text);
  }

  public static Text create(String text) {
    return Text.create(props -> props.text = text);
  }

  @Override
  protected void performInit() {
    super.performInit();

    if (text == null) throw new IllegalArgumentException("Text text cannot be null");
    if (font == null) throw new IllegalArgumentException("Text font cannot be null");

    renderFont = font.deriveFont(fontStyle, fontSize);
    fontMetrics = host.getFontMetrics(renderFont);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    List<String> lines = new ArrayList<>();
    List<Double> lineOffsets = new ArrayList<>();
    List<Symbol> symbols = new ArrayList<>();
    int textWidth = fontMetrics.stringWidth(text);
    int textHeight = (int) (fontSize * fontHeightRatio);

    intrinsicSize.set(textWidth, textHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    if (textWidth > computedBounds.width) {
      String[] tokens = text.split(" ");
      String separator = " ";

      boolean singleTokenExceedingComputedWidth =
        Arrays.stream(tokens).anyMatch(token -> fontMetrics.stringWidth(token) > computedBounds.width);

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

        if (currentLineWidth > maxLineWidth) maxLineWidth = currentLineWidth;
      }

      if (!lineBuilder.isEmpty()) lines.add(lineBuilder.toString());

      intrinsicSize.set(maxLineWidth, textHeight * lines.size());

      computedBounds.setSize(
        constraints.constrainWidth(intrinsicSize.width),
        constraints.constrainHeight(intrinsicSize.height)
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
      lineOffsets.add(switch (alignment) {
        case CENTER -> computedBounds.width / 2 - intrinsicSize.width / 2.0;
        case END -> computedBounds.width - intrinsicSize.width;
        default -> 0.0;
      });
    }

    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.isEmpty()) continue;

      double lineOffset = lineOffsets.get(i);
      String[] lineCharacters = line.split("");
      double lineYPosition = i * fontSize * fontHeightRatio;

      for (String ch : lineCharacters) {
        int chWidth = fontMetrics.stringWidth(ch);

        symbols.add(new Symbol(ch.charAt(0), lineOffset, lineYPosition, chWidth, fontSize * fontHeightRatio));

        lineOffset += chWidth;
      }
    }

    this.symbols = Collections.unmodifiableList(symbols);
  }

  @Override
  protected void performPostLayout() {
    super.performPostLayout();

    for (Symbol symbol : symbols) {
      symbol.setPosition(absoluteBounds.x + symbol.x, absoluteBounds.y + symbol.y);
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
      g.drawString(String.valueOf(symbol.value), (int) symbol.x, (int) (symbol.y + symbol.height));
    }

    if (Widget.DEBUG) {
      g.setColor(Widget.DEBUG_COLOR);
      g.setStroke(Widget.DEBUG_STROKE);

      for (Symbol symbol : symbols) {
        g.drawRect((int) symbol.x, (int) symbol.y, (int) symbol.width, (int) symbol.height);
      }
    }

    g.setFont(originalFont);
    g.setColor(originalColor);
    g.setStroke(originalStroke);
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
