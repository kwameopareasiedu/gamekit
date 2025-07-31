package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.enums.Alignment;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which provides theme variables to its child tree */
public class Theme extends SingleChildParent {
  private static final Theme DEFAULT_THEME = create(config(), Empty.create());

  public Spacing buttonNinePatchSpacing;
  public BufferedImage buttonDefaultBackground;
  public BufferedImage buttonHoverBackground;
  public BufferedImage buttonPressedBackground;

  public Font textFont;
  public Integer textFontSize;
  public Integer textFontStyle;
  public Color textForegroundColor;
  public Color textBackgroundColor;
  public Alignment textHorizontalAlignment;
  public Alignment textVerticalAlignment;
  public Boolean textShadowEnabled;
  public Integer textShadowOffsetX;
  public Integer textShadowOffsetY;
  public Color textShadowColor;

  public Spacing fieldNinePatchSpacing;
  public BufferedImage fieldDefaultBackground;
  public BufferedImage fieldFocusBackground;
  public Spacing fieldPadding;

  public Spacing checkboxNinePatchSpacing;
  public BufferedImage checkboxDefaultIcon;
  public BufferedImage checkboxToggledIcon;
  public Integer checkboxGapSize;
  public Integer checkboxIconSize;

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
    if (widget instanceof Theme themeWidget)
      return Objects.equals(buttonNinePatchSpacing, themeWidget.buttonNinePatchSpacing) &&
        Objects.equals(buttonDefaultBackground, themeWidget.buttonDefaultBackground) &&
        Objects.equals(buttonHoverBackground, themeWidget.buttonHoverBackground) &&
        Objects.equals(buttonPressedBackground, themeWidget.buttonPressedBackground) &&

        Objects.equals(textFont, themeWidget.textFont) &&
        Objects.equals(textFontStyle, themeWidget.textFontStyle) &&
        Objects.equals(textFontSize, themeWidget.textFontSize) &&
        Objects.equals(textForegroundColor, themeWidget.textForegroundColor) &&
        Objects.equals(textBackgroundColor, themeWidget.textBackgroundColor) &&
        Objects.equals(textHorizontalAlignment, themeWidget.textHorizontalAlignment) &&
        Objects.equals(textVerticalAlignment, themeWidget.textVerticalAlignment) &&
        Objects.equals(textShadowEnabled, themeWidget.textShadowEnabled) &&
        Objects.equals(textShadowOffsetX, themeWidget.textShadowOffsetX) &&
        Objects.equals(textShadowOffsetY, themeWidget.textShadowOffsetY) &&
        Objects.equals(textShadowColor, themeWidget.textShadowColor) &&

        Objects.equals(fieldNinePatchSpacing, themeWidget.fieldNinePatchSpacing) &&
        Objects.equals(fieldDefaultBackground, themeWidget.fieldDefaultBackground) &&
        Objects.equals(fieldFocusBackground, themeWidget.fieldFocusBackground) &&
        Objects.equals(fieldPadding, themeWidget.fieldPadding) &&

        Objects.equals(checkboxNinePatchSpacing, themeWidget.checkboxNinePatchSpacing) &&
        Objects.equals(checkboxDefaultIcon, themeWidget.checkboxDefaultIcon) &&
        Objects.equals(checkboxToggledIcon, themeWidget.checkboxToggledIcon) &&
        Objects.equals(checkboxGapSize, themeWidget.checkboxGapSize) &&
        Objects.equals(checkboxIconSize, themeWidget.checkboxIconSize);

    return false;
  }

  @Override
  protected void performInit() {
    ThemeConfig config = (ThemeConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.buttonNinePatchSpacing =
      coalesce(config.buttonNinePatchSpacing, theme.buttonNinePatchSpacing);
    this.buttonDefaultBackground =
      coalesce(config.buttonDefaultBackground, theme.buttonDefaultBackground);
    this.buttonHoverBackground =
      coalesce(config.buttonHoverBackground, theme.buttonHoverBackground);
    this.buttonPressedBackground =
      coalesce(config.buttonPressedBackground, theme.buttonPressedBackground);

    this.textFont = coalesce(config.textFont, theme.textFont);
    this.textFontSize = coalesce(config.textFontSize, theme.textFontSize);
    this.textFontStyle = coalesce(config.textFontStyle, theme.textFontStyle);
    this.textForegroundColor = coalesce(config.textForegroundColor, theme.textForegroundColor);
    this.textBackgroundColor = coalesce(config.textBackgroundColor, theme.textBackgroundColor);
    this.textHorizontalAlignment =
      coalesce(config.textHorizontalAlignment, theme.textHorizontalAlignment);
    this.textVerticalAlignment =
      coalesce(config.textVerticalAlignment, theme.textVerticalAlignment);
    this.textShadowEnabled = coalesce(config.textShadowEnabled, theme.textShadowEnabled);
    this.textShadowOffsetX = coalesce(config.textShadowOffsetX, theme.textShadowOffsetX);
    this.textShadowOffsetY = coalesce(config.textShadowOffsetY, theme.textShadowOffsetY);
    this.textShadowColor = coalesce(config.textShadowColor, theme.textShadowColor);

    this.fieldNinePatchSpacing =
      coalesce(config.fieldNinePatchSpacing, theme.fieldNinePatchSpacing);
    this.fieldDefaultBackground =
      coalesce(config.fieldDefaultBackground, theme.fieldDefaultBackground);
    this.fieldFocusBackground = coalesce(config.fieldFocusBackground, theme.fieldFocusBackground);
    this.fieldPadding = coalesce(config.fieldPadding, theme.fieldPadding);

    this.checkboxNinePatchSpacing =
      coalesce(config.checkboxNinePatchSpacing, theme.checkboxNinePatchSpacing);
    this.checkboxDefaultIcon = coalesce(config.checkboxDefaultIcon, theme.checkboxDefaultIcon);
    this.checkboxToggledIcon = coalesce(config.checkboxToggledIcon, theme.checkboxToggledIcon);
    this.checkboxGapSize = coalesce(config.checkboxGapSize, theme.checkboxGapSize);
    this.checkboxIconSize = coalesce(config.checkboxIconSize, theme.checkboxIconSize);

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(constraints);

    intrinsicBounds.setSize(
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );
  }

  public static class ThemeConfig extends SingleChildParentConfig {
    protected Spacing buttonNinePatchSpacing;
    protected BufferedImage buttonDefaultBackground;
    protected BufferedImage buttonHoverBackground;
    protected BufferedImage buttonPressedBackground;

    protected Font textFont;
    protected Integer textFontSize;
    protected Integer textFontStyle;
    protected Color textForegroundColor;
    protected Color textBackgroundColor;
    protected Alignment textHorizontalAlignment;
    protected Alignment textVerticalAlignment;
    protected Boolean textShadowEnabled;
    protected Integer textShadowOffsetX;
    protected Integer textShadowOffsetY;
    protected Color textShadowColor;

    protected Spacing fieldNinePatchSpacing;
    protected BufferedImage fieldDefaultBackground;
    protected BufferedImage fieldFocusBackground;
    protected Spacing fieldPadding;

    protected Spacing checkboxNinePatchSpacing;
    protected BufferedImage checkboxDefaultIcon;
    protected BufferedImage checkboxToggledIcon;
    protected Integer checkboxGapSize;
    protected Integer checkboxIconSize;

    public ThemeConfig buttonNinePatchSpacing(Spacing buttonNinePatchSpacing) {
      this.buttonNinePatchSpacing = buttonNinePatchSpacing;
      return this;
    }

    public ThemeConfig buttonBackground(
      BufferedImage buttonDefaultBackground,
      BufferedImage buttonHoverBackground,
      BufferedImage buttonPressedBackground
    ) {
      this.buttonDefaultBackground = buttonDefaultBackground;
      this.buttonHoverBackground = buttonHoverBackground;
      this.buttonPressedBackground = buttonPressedBackground;
      return this;
    }

    public ThemeConfig textFont(int textFontSize, int textFontStyle, Font textFont) {
      this.textFontSize = textFontSize;
      this.textFontStyle = textFontStyle;
      this.textFont = textFont;
      return this;
    }

    public ThemeConfig textFont(int fontSize, int fontStyle) {
      return textFont(fontSize, fontStyle, null);
    }

    public ThemeConfig textColor(Color textForegroundColor, Color textBackgroundColor) {
      this.textForegroundColor = textForegroundColor;
      this.textBackgroundColor = textBackgroundColor;
      return this;
    }

    public ThemeConfig textAlignment(
      Alignment textHorizontalAlignment,
      Alignment textVerticalAlignment
    ) {
      this.textHorizontalAlignment = textHorizontalAlignment;
      this.textVerticalAlignment = textVerticalAlignment;
      return this;
    }

    public ThemeConfig textShadow(
      boolean textShadowEnabled,
      int textShadowOffsetX,
      int textShadowOffsetY,
      Color textShadowColor
    ) {
      this.textShadowEnabled = textShadowEnabled;
      this.textShadowOffsetX = textShadowOffsetX;
      this.textShadowOffsetY = textShadowOffsetY;
      this.textShadowColor = textShadowColor;
      return this;
    }

    public ThemeConfig fieldNinePatchSpacing(Spacing fieldNinePatchSpacing) {
      this.fieldNinePatchSpacing = fieldNinePatchSpacing;
      return this;
    }

    public ThemeConfig fieldBackground(
      BufferedImage fieldDefaultBackground,
      BufferedImage fieldFocusBackground
    ) {
      this.fieldDefaultBackground = fieldDefaultBackground;
      this.fieldFocusBackground = fieldFocusBackground;
      return this;
    }

    public ThemeConfig fieldPadding(Spacing fieldPadding) {
      this.fieldPadding = fieldPadding;
      return this;
    }

    public ThemeConfig checkboxNinePatchSpacing(Spacing checkboxNinePatchSpacing) {
      this.checkboxNinePatchSpacing = checkboxNinePatchSpacing;
      return this;
    }

    public ThemeConfig checkboxIcon(
      BufferedImage checkboxDefaultIcon,
      BufferedImage checkboxToggledIcon
    ) {
      this.checkboxDefaultIcon = checkboxDefaultIcon;
      this.checkboxToggledIcon = checkboxToggledIcon;
      return this;
    }

    public ThemeConfig checkboxGapSize(Integer checkboxGapSize) {
      this.checkboxGapSize = checkboxGapSize;
      return this;
    }

    public ThemeConfig checkboxIconSize(Integer checkboxIconSize) {
      this.checkboxIconSize = checkboxIconSize;
      return this;
    }
  }
}
