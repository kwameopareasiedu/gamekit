package dev.gamekit.ui.widgets;

import dev.gamekit.ui.BorderData;
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

  public BufferedImage fieldDefaultBackground;
  public BufferedImage fieldFocusBackground;
  public BorderData fieldDefaultBorder;
  public BorderData fieldFocusBorder;
  public Spacing fieldPadding;

  public BufferedImage checkboxDefaultBackground;
  public BufferedImage checkboxToggledBackground;
  public BufferedImage checkboxDefaultIcon;
  public BufferedImage checkboxToggledIcon;
  public BorderData checkboxDefaultBorder;
  public BorderData checkboxToggledBorder;
  public Integer checkboxSpacing;
  public Integer checkboxSize;

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

        Objects.equals(fieldDefaultBackground, themeWidget.fieldDefaultBackground) &&
        Objects.equals(fieldFocusBackground, themeWidget.fieldFocusBackground) &&
        Objects.equals(fieldDefaultBorder, themeWidget.fieldDefaultBorder) &&
        Objects.equals(fieldFocusBorder, themeWidget.fieldFocusBorder) &&
        Objects.equals(fieldPadding, themeWidget.fieldPadding) &&

        Objects.equals(checkboxDefaultBackground, themeWidget.checkboxDefaultBackground) &&
        Objects.equals(checkboxToggledBackground, themeWidget.checkboxToggledBackground) &&
        Objects.equals(checkboxDefaultIcon, themeWidget.checkboxDefaultIcon) &&
        Objects.equals(checkboxToggledIcon, themeWidget.checkboxToggledIcon) &&
        Objects.equals(checkboxDefaultBorder, themeWidget.checkboxDefaultBorder) &&
        Objects.equals(checkboxToggledBorder, themeWidget.checkboxToggledBorder) &&
        Objects.equals(checkboxSpacing, themeWidget.checkboxSpacing) &&
        Objects.equals(checkboxSize, themeWidget.checkboxSize);

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

    this.fieldDefaultBackground =
      coalesce(config.fieldDefaultBackground, theme.fieldDefaultBackground);
    this.fieldFocusBackground = coalesce(config.fieldFocusBackground, theme.fieldFocusBackground);
    this.fieldDefaultBorder = coalesce(config.fieldDefaultBorder, theme.fieldDefaultBorder);
    this.fieldFocusBorder = coalesce(config.fieldFocusBorder, theme.fieldFocusBorder);
    this.fieldPadding = coalesce(config.fieldPadding, theme.fieldPadding);

    this.checkboxDefaultBackground =
      coalesce(config.checkboxDefaultBackground, theme.checkboxDefaultBackground);
    this.checkboxToggledBackground =
      coalesce(config.checkboxToggledBackground, theme.checkboxToggledBackground);
    this.checkboxDefaultIcon = coalesce(config.checkboxDefaultIcon, theme.checkboxDefaultIcon);
    this.checkboxToggledIcon = coalesce(config.checkboxToggledIcon, theme.checkboxToggledIcon);
    this.checkboxDefaultBorder =
      coalesce(config.checkboxDefaultBorder, theme.checkboxDefaultBorder);
    this.checkboxToggledBorder =
      coalesce(config.checkboxToggledBorder, theme.checkboxToggledBorder);
    this.checkboxSpacing = coalesce(config.checkboxSpacing, theme.checkboxSpacing);
    this.checkboxSize = coalesce(config.checkboxSize, theme.checkboxSize);

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
    private Spacing buttonNinePatchSpacing;
    private BufferedImage buttonDefaultBackground;
    private BufferedImage buttonHoverBackground;
    private BufferedImage buttonPressedBackground;

    private Font textFont;
    private Integer textFontSize;
    private Integer textFontStyle;
    private Color textForegroundColor;
    private Color textBackgroundColor;
    private Alignment textHorizontalAlignment;
    private Alignment textVerticalAlignment;
    private Boolean textShadowEnabled;
    private Integer textShadowOffsetX;
    private Integer textShadowOffsetY;
    private Color textShadowColor;

    private BufferedImage fieldDefaultBackground;
    private BufferedImage fieldFocusBackground;
    private BorderData fieldDefaultBorder;
    private BorderData fieldFocusBorder;
    private Spacing fieldPadding;

    private BufferedImage checkboxDefaultBackground;
    private BufferedImage checkboxToggledBackground;
    private BufferedImage checkboxDefaultIcon;
    private BufferedImage checkboxToggledIcon;
    private BorderData checkboxDefaultBorder;
    private BorderData checkboxToggledBorder;
    private Integer checkboxSpacing;
    private Integer checkboxSize;

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

    public ThemeConfig fieldBackground(
      BufferedImage fieldDefaultBackground,
      BufferedImage fieldFocusBackground
    ) {
      this.fieldDefaultBackground = fieldDefaultBackground;
      this.fieldFocusBackground = fieldFocusBackground;
      return this;
    }

    public ThemeConfig fieldBorder(BorderData fieldDefaultBorder, BorderData fieldFocusBorder) {
      this.fieldDefaultBorder = fieldDefaultBorder;
      this.fieldFocusBorder = fieldFocusBorder;
      return this;
    }

    public ThemeConfig fieldPadding(Spacing fieldPadding) {
      this.fieldPadding = fieldPadding;
      return this;
    }

    public ThemeConfig checkboxBackground(
      BufferedImage checkboxDefaultBackground,
      BufferedImage checkboxToggledBackground
    ) {
      this.checkboxDefaultBackground = checkboxDefaultBackground;
      this.checkboxToggledBackground = checkboxToggledBackground;
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

    public ThemeConfig checkboxBorder(
      BorderData checkboxDefaultBorder,
      BorderData checkboxToggledBorder
    ) {
      this.checkboxDefaultBorder = checkboxDefaultBorder;
      this.checkboxToggledBorder = checkboxToggledBorder;
      return this;
    }

    public ThemeConfig checkboxSpacing(Integer checkboxSpacing) {
      this.checkboxSpacing = checkboxSpacing;
      return this;
    }

    public ThemeConfig checkboxSize(Integer checkboxSize) {
      this.checkboxSize = checkboxSize;
      return this;
    }
  }
}
