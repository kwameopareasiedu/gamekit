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

  public Spacing buttonNinePatchBorder;
  public BufferedImage buttonDefaultBackground;
  public BufferedImage buttonHoverBackground;
  public BufferedImage buttonPressedBackground;

  public Font textFont;
  public Integer textFontStyle;
  public Integer textFontSize;
  public Color textColor;
  public Color textBackgroundColor;
  public Alignment textAlignment;
  public Alignment textVerticalAlignment;
  public Boolean textShadowEnabled;
  public Integer textShadowOffsetX;
  public Integer textShadowOffsetY;
  public Color textShadowColor;

  public BufferedImage fieldBackground;
  public Spacing fieldPadding;
  public BorderData fieldDefaultBorder;
  public BorderData fieldFocusBorder;

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
      return Objects.equals(buttonNinePatchBorder, themeWidget.buttonNinePatchBorder) &&
        Objects.equals(buttonDefaultBackground, themeWidget.buttonDefaultBackground) &&
        Objects.equals(buttonHoverBackground, themeWidget.buttonHoverBackground) &&
        Objects.equals(buttonPressedBackground, themeWidget.buttonPressedBackground) &&

        Objects.equals(textFont, themeWidget.textFont) &&
        Objects.equals(textFontStyle, themeWidget.textFontStyle) &&
        Objects.equals(textFontSize, themeWidget.textFontSize) &&
        Objects.equals(textColor, themeWidget.textColor) &&
        Objects.equals(textBackgroundColor, themeWidget.textBackgroundColor) &&
        Objects.equals(textAlignment, themeWidget.textAlignment) &&
        Objects.equals(textVerticalAlignment, themeWidget.textVerticalAlignment) &&
        Objects.equals(textShadowEnabled, themeWidget.textShadowEnabled) &&
        Objects.equals(textShadowOffsetX, themeWidget.textShadowOffsetX) &&
        Objects.equals(textShadowOffsetY, themeWidget.textShadowOffsetY) &&
        Objects.equals(textShadowColor, themeWidget.textShadowColor) &&

        Objects.equals(fieldBackground, themeWidget.fieldBackground) &&
        Objects.equals(fieldPadding, themeWidget.fieldPadding) &&
        Objects.equals(fieldDefaultBorder, themeWidget.fieldDefaultBorder) &&
        Objects.equals(fieldFocusBorder, themeWidget.fieldFocusBorder);

    return false;
  }

  @Override
  protected void performInit() {
    ThemeConfig config = (ThemeConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.buttonNinePatchBorder =
      coalesce(config.buttonNinePatchBorder, theme.buttonNinePatchBorder);
    this.buttonDefaultBackground =
      coalesce(config.buttonDefaultBackground, theme.buttonDefaultBackground);
    this.buttonHoverBackground =
      coalesce(config.buttonHoverBackground, theme.buttonHoverBackground);
    this.buttonPressedBackground =
      coalesce(config.buttonPressedBackground, theme.buttonPressedBackground);

    this.textFont = coalesce(config.textFont, theme.textFont);
    this.textFontStyle = coalesce(config.textFontStyle, theme.textFontStyle);
    this.textFontSize = coalesce(config.textFontSize, theme.textFontSize);
    this.textColor = coalesce(config.textColor, theme.textColor);
    this.textBackgroundColor = coalesce(config.textBackgroundColor, theme.textBackgroundColor);
    this.textAlignment = coalesce(config.textAlignment, theme.textAlignment);
    this.textVerticalAlignment =
      coalesce(config.textVerticalAlignment, theme.textVerticalAlignment);
    this.textShadowEnabled = coalesce(config.textShadowEnabled, theme.textShadowEnabled);
    this.textShadowOffsetX = coalesce(config.textShadowOffsetX, theme.textShadowOffsetX);
    this.textShadowOffsetY = coalesce(config.textShadowOffsetY, theme.textShadowOffsetY);
    this.textShadowColor = coalesce(config.textShadowColor, theme.textShadowColor);

    this.fieldBackground = coalesce(config.fieldBackground, theme.fieldBackground);
    this.fieldPadding = coalesce(config.fieldPadding, theme.fieldPadding);
    this.fieldDefaultBorder = coalesce(config.fieldDefaultBorder, theme.fieldDefaultBorder);
    this.fieldFocusBorder = coalesce(config.fieldFocusBorder, theme.fieldFocusBorder);

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
    Spacing buttonNinePatchBorder;
    BufferedImage buttonDefaultBackground;
    BufferedImage buttonHoverBackground;
    BufferedImage buttonPressedBackground;

    Font textFont;
    Integer textFontStyle;
    Integer textFontSize;
    Color textColor;
    Color textBackgroundColor;
    Alignment textAlignment;
    Alignment textVerticalAlignment;
    Boolean textShadowEnabled;
    Integer textShadowOffsetX;
    Integer textShadowOffsetY;
    Color textShadowColor;

    BufferedImage fieldBackground;
    Spacing fieldPadding;
    BorderData fieldDefaultBorder;
    BorderData fieldFocusBorder;

    public ThemeConfig ninePatch(Spacing border) {
      this.buttonNinePatchBorder = border;
      return this;
    }

    public ThemeConfig ninePatch(int all) {
      this.buttonNinePatchBorder = new Spacing(all);
      return this;
    }

    public ThemeConfig ninePatch(int horizontal, int vertical) {
      this.buttonNinePatchBorder = new Spacing(horizontal, vertical);
      return this;
    }

    public ThemeConfig ninePatch(int top, int right, int bottom, int left) {
      this.buttonNinePatchBorder = new Spacing(top, right, bottom, left);
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

    public ThemeConfig textFont(Font textFont) {
      this.textFont = textFont;
      return this;
    }

    public ThemeConfig textFontStyle(int textFontStyle) {
      this.textFontStyle = textFontStyle;
      return this;
    }

    public ThemeConfig textFontSize(int textFontSize) {
      this.textFontSize = textFontSize;
      return this;
    }

    public ThemeConfig textColor(Color textColor) {
      this.textColor = textColor;
      return this;
    }

    public ThemeConfig textBackgroundColor(Color textBackgroundColor) {
      this.textBackgroundColor = textBackgroundColor;
      return this;
    }

    public ThemeConfig textAlignment(Alignment textAlignment) {
      this.textAlignment = textAlignment;
      return this;
    }

    public ThemeConfig textVerticalAlignment(Alignment textVerticalAlignment) {
      this.textVerticalAlignment = textVerticalAlignment;
      return this;
    }

    public ThemeConfig textShadowEnabled(boolean textShadowEnabled) {
      this.textShadowEnabled = textShadowEnabled;
      return this;
    }

    public ThemeConfig textShadowOffset(int textShadowOffsetX, int textShadowOffsetY) {
      this.textShadowOffsetX = textShadowOffsetX;
      this.textShadowOffsetY = textShadowOffsetY;
      return this;
    }

    public ThemeConfig textShadowColor(Color textShadowColor) {
      this.textShadowColor = textShadowColor;
      return this;
    }

    public ThemeConfig fieldBackground(BufferedImage fieldBackground) {
      this.fieldBackground = fieldBackground;
      return this;
    }

    public ThemeConfig fieldPadding(Spacing fieldPadding) {
      this.fieldPadding = fieldPadding;
      return this;
    }

    public ThemeConfig fieldDefaultBorder(BorderData fieldDefaultBorder) {
      this.fieldDefaultBorder = fieldDefaultBorder;
      return this;
    }

    public ThemeConfig fieldFocusBorder(BorderData fieldFocusBorder) {
      this.fieldFocusBorder = fieldFocusBorder;
      return this;
    }
  }
}
