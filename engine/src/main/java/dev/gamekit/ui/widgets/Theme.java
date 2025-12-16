package dev.gamekit.ui.widgets;

import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;
import dev.gamekit.ui.enums.Alignment;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which provides theme variables to its child tree */
public class Theme extends SingleChildParent {
  private static final Theme DEFAULT_THEME = create(config(), Empty.create());

  public BufferedImage panelBackground;
  public Spacing panelEdgeInsets;

  public BufferedImage buttonDefaultBackground;
  public BufferedImage buttonHoverBackground;
  public BufferedImage buttonPressedBackground;
  public Spacing buttonEdgeInsets;

  public Font textFont;
  public Integer textFontSize;
  public Integer textFontStyle;
  public Color textColor;
  public Color textBackgroundColor;
  public Alignment textAlignment;
  public Boolean textShadowEnabled;
  public Integer textShadowOffsetX;
  public Integer textShadowOffsetY;
  public Color textShadowColor;

  public BufferedImage fieldDefaultBackground;
  public BufferedImage fieldFocusBackground;
  public Spacing fieldEdgeInsets;
  public Spacing fieldPadding;

  public BufferedImage checkboxDefaultIcon;
  public BufferedImage checkboxToggledIcon;
  public Spacing checkboxIconEdgeInsets;
  public Integer checkboxIconWidth;
  public Integer checkboxIconHeight;
  public Integer checkboxGapSize;

  public BufferedImage progressTrackBackground;
  public BufferedImage progressFillBackground;
  public Spacing progressTrackEdgeInsets;
  public Spacing progressFillEdgeInsets;
  public Spacing progressFillMargin;
  public Progress.FillMode progressFillMode;

  public BufferedImage sliderThumbBackground;
  public Spacing sliderThumbEdgeInsets;
  public Integer sliderThumbWidth;
  public Integer sliderThumbHeight;

  public Theme(ThemeConfig config, Widget child) {
    super(config, child);
  }

  public static Theme create(ThemeConfig params, Widget child) {
    return new Theme(params, child);
  }

  public static ThemeConfig config() {
    return new ThemeConfig();
  }

  public static Theme getDefault() {
    return DEFAULT_THEME;
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Theme themeWidget &&

      Objects.equals(panelBackground, themeWidget.panelBackground) &&
      Objects.equals(panelEdgeInsets, themeWidget.panelEdgeInsets) &&

      Objects.equals(buttonDefaultBackground, themeWidget.buttonDefaultBackground) &&
      Objects.equals(buttonHoverBackground, themeWidget.buttonHoverBackground) &&
      Objects.equals(buttonPressedBackground, themeWidget.buttonPressedBackground) &&
      Objects.equals(buttonEdgeInsets, themeWidget.buttonEdgeInsets) &&

      Objects.equals(textFont, themeWidget.textFont) &&
      Objects.equals(textFontStyle, themeWidget.textFontStyle) &&
      Objects.equals(textFontSize, themeWidget.textFontSize) &&
      Objects.equals(textColor, themeWidget.textColor) &&
      Objects.equals(textBackgroundColor, themeWidget.textBackgroundColor) &&
      Objects.equals(textAlignment, themeWidget.textAlignment) &&
      Objects.equals(textShadowEnabled, themeWidget.textShadowEnabled) &&
      Objects.equals(textShadowOffsetX, themeWidget.textShadowOffsetX) &&
      Objects.equals(textShadowOffsetY, themeWidget.textShadowOffsetY) &&
      Objects.equals(textShadowColor, themeWidget.textShadowColor) &&

      Objects.equals(fieldDefaultBackground, themeWidget.fieldDefaultBackground) &&
      Objects.equals(fieldFocusBackground, themeWidget.fieldFocusBackground) &&
      Objects.equals(fieldEdgeInsets, themeWidget.fieldEdgeInsets) &&
      Objects.equals(fieldPadding, themeWidget.fieldPadding) &&

      Objects.equals(checkboxDefaultIcon, themeWidget.checkboxDefaultIcon) &&
      Objects.equals(checkboxToggledIcon, themeWidget.checkboxToggledIcon) &&
      Objects.equals(checkboxIconEdgeInsets, themeWidget.checkboxIconEdgeInsets) &&
      Objects.equals(checkboxIconWidth, themeWidget.checkboxIconWidth) &&
      Objects.equals(checkboxIconHeight, themeWidget.checkboxIconHeight) &&
      Objects.equals(checkboxGapSize, themeWidget.checkboxGapSize) &&

      Objects.equals(progressTrackBackground, themeWidget.progressTrackBackground) &&
      Objects.equals(progressFillBackground, themeWidget.progressFillBackground) &&
      Objects.equals(progressTrackEdgeInsets, themeWidget.progressTrackEdgeInsets) &&
      Objects.equals(progressFillEdgeInsets, themeWidget.progressFillEdgeInsets) &&
      Objects.equals(progressFillMargin, themeWidget.progressFillMargin) &&
      Objects.equals(progressFillMode, themeWidget.progressFillMode) &&

      Objects.equals(sliderThumbBackground, themeWidget.sliderThumbBackground) &&
      Objects.equals(sliderThumbEdgeInsets, themeWidget.sliderThumbEdgeInsets) &&
      Objects.equals(sliderThumbWidth, themeWidget.sliderThumbWidth) &&
      Objects.equals(sliderThumbHeight, themeWidget.sliderThumbHeight);
  }

  @Override
  protected void performInit() {
    ThemeConfig config = (ThemeConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.panelBackground = coalesce(config.panelBackground, theme.panelBackground);
    this.panelEdgeInsets = coalesce(config.panelEdgeInsets, theme.panelEdgeInsets);

    this.buttonDefaultBackground =
      coalesce(config.buttonDefaultBackground, theme.buttonDefaultBackground);
    this.buttonHoverBackground =
      coalesce(config.buttonHoverBackground, theme.buttonHoverBackground);
    this.buttonPressedBackground =
      coalesce(config.buttonPressedBackground, theme.buttonPressedBackground);
    this.buttonEdgeInsets =
      coalesce(config.buttonEdgeInsets, theme.buttonEdgeInsets);

    this.textFont = coalesce(config.textFont, theme.textFont);
    this.textFontSize = coalesce(config.textFontSize, theme.textFontSize);
    this.textFontStyle = coalesce(config.textFontStyle, theme.textFontStyle);
    this.textColor = coalesce(config.textColor, theme.textColor);
    this.textBackgroundColor = coalesce(config.textBackgroundColor, theme.textBackgroundColor);
    this.textAlignment = coalesce(config.textAlignment, theme.textAlignment);
    this.textShadowEnabled = coalesce(config.textShadowEnabled, theme.textShadowEnabled);
    this.textShadowOffsetX = coalesce(config.textShadowOffsetX, theme.textShadowOffsetX);
    this.textShadowOffsetY = coalesce(config.textShadowOffsetY, theme.textShadowOffsetY);
    this.textShadowColor = coalesce(config.textShadowColor, theme.textShadowColor);

    this.fieldDefaultBackground =
      coalesce(config.fieldDefaultBackground, theme.fieldDefaultBackground);
    this.fieldFocusBackground = coalesce(config.fieldFocusBackground, theme.fieldFocusBackground);
    this.fieldEdgeInsets = coalesce(config.fieldEdgeInsets, theme.fieldEdgeInsets);
    this.fieldPadding = coalesce(config.fieldPadding, theme.fieldPadding);

    this.checkboxDefaultIcon = coalesce(config.checkboxDefaultIcon, theme.checkboxDefaultIcon);
    this.checkboxToggledIcon = coalesce(config.checkboxToggledIcon, theme.checkboxToggledIcon);
    this.checkboxIconEdgeInsets = coalesce(config.checkboxEdgeInsets, theme.checkboxIconEdgeInsets);
    this.checkboxIconWidth = coalesce(config.checkboxIconWidth, theme.checkboxIconWidth);
    this.checkboxIconHeight = coalesce(config.checkboxIconHeight, theme.checkboxIconHeight);
    this.checkboxGapSize = coalesce(config.checkboxGapSize, theme.checkboxGapSize);

    this.progressTrackBackground =
      coalesce(config.progressTrackBackground, theme.progressTrackBackground);
    this.progressFillBackground =
      coalesce(config.progressFillBackground, theme.progressFillBackground);
    this.progressTrackEdgeInsets =
      coalesce(config.progressTrackEdgeInsets, theme.progressTrackEdgeInsets);
    this.progressFillEdgeInsets =
      coalesce(config.progressFillEdgeInsets, theme.progressFillEdgeInsets);
    this.progressFillMargin = coalesce(config.progressFillMargin, theme.progressFillMargin);
    this.progressFillMode = coalesce(config.progressFillMode, theme.progressFillMode);

    this.sliderThumbBackground =
      coalesce(config.sliderThumbBackground, theme.sliderThumbBackground);
    this.sliderThumbEdgeInsets =
      coalesce(config.sliderThumbEdgeInsets, theme.sliderThumbEdgeInsets);
    this.sliderThumbWidth = coalesce(config.sliderThumbWidth, theme.sliderThumbWidth);
    this.sliderThumbHeight = coalesce(config.sliderThumbHeight, theme.sliderThumbHeight);

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(constraints);

    intrinsicSize.set(
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }

  public static class ThemeConfig extends SingleChildParentConfig {
    protected BufferedImage panelBackground;
    protected Spacing panelEdgeInsets;

    protected BufferedImage buttonDefaultBackground;
    protected BufferedImage buttonHoverBackground;
    protected BufferedImage buttonPressedBackground;
    protected Spacing buttonEdgeInsets;

    protected Font textFont;
    protected Integer textFontSize;
    protected Integer textFontStyle;
    protected Color textColor;
    protected Color textBackgroundColor;
    protected Alignment textAlignment;
    protected Boolean textShadowEnabled;
    protected Integer textShadowOffsetX;
    protected Integer textShadowOffsetY;
    protected Color textShadowColor;

    protected BufferedImage fieldDefaultBackground;
    protected BufferedImage fieldFocusBackground;
    protected Spacing fieldEdgeInsets;
    protected Spacing fieldPadding;

    protected BufferedImage checkboxDefaultIcon;
    protected BufferedImage checkboxToggledIcon;
    protected Spacing checkboxEdgeInsets;
    protected Integer checkboxIconWidth;
    protected Integer checkboxIconHeight;
    protected Integer checkboxGapSize;

    protected BufferedImage progressTrackBackground;
    protected BufferedImage progressFillBackground;
    protected Spacing progressTrackEdgeInsets;
    protected Spacing progressFillEdgeInsets;
    protected Spacing progressFillMargin;
    protected Progress.FillMode progressFillMode;

    protected BufferedImage sliderThumbBackground;
    protected Spacing sliderThumbEdgeInsets;
    protected Integer sliderThumbWidth;
    protected Integer sliderThumbHeight;

    public ThemeConfig panelBackground(BufferedImage panelBackground) {
      this.panelBackground = panelBackground;
      return this;
    }

    public ThemeConfig panelEdgeInsets(int top, int right, int bottom, int left) {
      this.panelEdgeInsets = new Spacing(top, right, bottom, left);
      return this;
    }

    public ThemeConfig buttonDefaultBackground(BufferedImage buttonDefaultBackground) {
      this.buttonDefaultBackground = buttonDefaultBackground;
      return this;
    }

    public ThemeConfig buttonHoverBackground(BufferedImage buttonHoverBackground) {
      this.buttonHoverBackground = buttonHoverBackground;
      return this;
    }

    public ThemeConfig buttonPressedBackground(BufferedImage buttonPressedBackground) {
      this.buttonPressedBackground = buttonPressedBackground;
      return this;
    }

    public ThemeConfig buttonEdgeInsets(int top, int right, int bottom, int left) {
      this.buttonEdgeInsets = new Spacing(top, right, bottom, left);
      return this;
    }

    public ThemeConfig textFont(Font font) {
      this.textFont = font;
      return this;
    }

    public ThemeConfig textFontSize(int fontSize) {
      this.textFontSize = fontSize;
      return this;
    }

    public ThemeConfig textFontStyle(int fontStyle) {
      this.textFontStyle = fontStyle;
      return this;
    }

    public ThemeConfig textColor(Color color) {
      this.textColor = color;
      return this;
    }

    public ThemeConfig textBackgroundColor(Color backgroundColor) {
      this.textBackgroundColor = backgroundColor;
      return this;
    }

    public ThemeConfig textAlignment(Alignment textAlignment) {
      this.textAlignment = textAlignment;
      return this;
    }

    public ThemeConfig textShadow(
      boolean shadowEnabled,
      int shadowOffsetX,
      int shadowOffsetY,
      Color shadowColor
    ) {
      this.textShadowEnabled = shadowEnabled;
      this.textShadowOffsetX = shadowOffsetX;
      this.textShadowOffsetY = shadowOffsetY;
      this.textShadowColor = shadowColor;
      return this;
    }

    public ThemeConfig fieldDefaultBackground(BufferedImage fieldDefaultBackground) {
      this.fieldDefaultBackground = fieldDefaultBackground;
      return this;
    }

    public ThemeConfig fieldFocusBackground(BufferedImage fieldFocusBackground) {
      this.fieldFocusBackground = fieldFocusBackground;
      return this;
    }

    public ThemeConfig fieldEdgeInsets(int top, int right, int bottom, int left) {
      this.fieldEdgeInsets = new Spacing(top, right, bottom, left);
      return this;
    }

    public ThemeConfig fieldPadding(int top, int right, int bottom, int left) {
      this.fieldPadding = new Spacing(top, right, bottom, left);
      return this;
    }

    public ThemeConfig checkboxDefaultIcon(BufferedImage checkboxDefaultIcon) {
      this.checkboxDefaultIcon = checkboxDefaultIcon;
      return this;
    }

    public ThemeConfig checkboxToggledIcon(BufferedImage checkboxToggledIcon) {
      this.checkboxToggledIcon = checkboxToggledIcon;
      return this;
    }

    public ThemeConfig checkboxEdgeInsets(int top, int right, int bottom, int left) {
      this.checkboxEdgeInsets = new Spacing(top, right, bottom, left);
      return this;
    }

    public ThemeConfig checkboxIconSize(Integer checkboxIconWidth, Integer checkboxIconHeight) {
      this.checkboxIconWidth = checkboxIconWidth;
      this.checkboxIconHeight = checkboxIconHeight;
      return this;
    }

    public ThemeConfig checkboxGapSize(Integer checkboxGapSize) {
      this.checkboxGapSize = checkboxGapSize;
      return this;
    }

    public ThemeConfig progressTrackBackground(BufferedImage progressTrackBackground) {
      this.progressTrackBackground = progressTrackBackground;
      return this;
    }

    public ThemeConfig progressTrackEdgeInsets(int top, int right, int bottom, int left) {
      this.progressTrackEdgeInsets = new Spacing(top, right, bottom, left);
      return this;
    }

    public ThemeConfig progressFillBackground(BufferedImage progressFillBackground) {
      this.progressFillBackground = progressFillBackground;
      return this;
    }

    public ThemeConfig progressFillEdgeInsets(int top, int right, int bottom, int left) {
      this.progressFillEdgeInsets = new Spacing(top, right, bottom, left);
      return this;
    }

    public ThemeConfig progressFillMargin(int top, int right, int bottom, int left) {
      this.progressFillMargin = new Spacing(top, right, bottom, left);
      return this;
    }

    public ThemeConfig progressFillMode(Progress.FillMode progressFillMode) {
      this.progressFillMode = progressFillMode;
      return this;
    }

    public ThemeConfig sliderThumbBackground(BufferedImage sliderThumbBackground) {
      this.sliderThumbBackground = sliderThumbBackground;
      return this;
    }

    public ThemeConfig sliderThumbEdgeInsets(int top, int right, int bottom, int left) {
      this.sliderThumbEdgeInsets = new Spacing(top, right, bottom, left);
      return this;
    }

    public ThemeConfig sliderThumbSize(int sliderThumbWidth, int sliderThumbHeight) {
      this.sliderThumbWidth = sliderThumbWidth;
      this.sliderThumbHeight = sliderThumbHeight;
      return this;
    }
  }
}
