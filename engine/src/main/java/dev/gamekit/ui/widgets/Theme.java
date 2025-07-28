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

  private final Config config;

  public Theme(Config config, Widget child) {
    super(child);
    this.config = config;
  }

  public static Theme create(Config params, Widget child) {
    return new Theme(params, child);
  }

  public static Config config() {
    return new Config();
  }

  public static Theme getDefault() {
    return DEFAULT_THEME;
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Theme themeWidget) {
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
    }

    return false;
  }

  @Override
  protected void performUpdateState(Widget widget) {
    this.buttonNinePatchBorder = ((Theme) widget).buttonNinePatchBorder;
    this.buttonDefaultBackground = ((Theme) widget).buttonDefaultBackground;
    this.buttonHoverBackground = ((Theme) widget).buttonHoverBackground;
    this.buttonPressedBackground = ((Theme) widget).buttonPressedBackground;

    this.textFont = ((Theme) widget).textFont;
    this.textFontStyle = ((Theme) widget).textFontStyle;
    this.textFontSize = ((Theme) widget).textFontSize;
    this.textColor = ((Theme) widget).textColor;
    this.textBackgroundColor = ((Theme) widget).textBackgroundColor;
    this.textAlignment = ((Theme) widget).textAlignment;
    this.textVerticalAlignment = ((Theme) widget).textVerticalAlignment;
    this.textShadowEnabled = ((Theme) widget).textShadowEnabled;
    this.textShadowOffsetX = ((Theme) widget).textShadowOffsetX;
    this.textShadowOffsetY = ((Theme) widget).textShadowOffsetY;
    this.textShadowColor = ((Theme) widget).textShadowColor;

    this.fieldBackground = ((Theme) widget).fieldBackground;
    this.fieldPadding = ((Theme) widget).fieldPadding;
    this.fieldDefaultBorder = ((Theme) widget).fieldDefaultBorder;
    this.fieldFocusBorder = ((Theme) widget).fieldFocusBorder;
  }

  @Override
  protected void performMounted() {
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

    super.performMounted();
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

  public static class Config {
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

    Config() { }

    public Config ninePatch(Spacing border) {
      this.buttonNinePatchBorder = border;
      return this;
    }

    public Config ninePatch(int all) {
      this.buttonNinePatchBorder = new Spacing(all);
      return this;
    }

    public Config ninePatch(int horizontal, int vertical) {
      this.buttonNinePatchBorder = new Spacing(horizontal, vertical);
      return this;
    }

    public Config ninePatch(int top, int right, int bottom, int left) {
      this.buttonNinePatchBorder = new Spacing(top, right, bottom, left);
      return this;
    }

    public Config buttonDefaultBackground(BufferedImage buttonDefaultBackground) {
      this.buttonDefaultBackground = buttonDefaultBackground;
      return this;
    }

    public Config buttonHoverBackground(BufferedImage buttonHoverBackground) {
      this.buttonHoverBackground = buttonHoverBackground;
      return this;
    }

    public Config buttonPressedBackground(BufferedImage buttonPressedBackground) {
      this.buttonPressedBackground = buttonPressedBackground;
      return this;
    }

    public Config textFont(Font textFont) {
      this.textFont = textFont;
      return this;
    }

    public Config textFontStyle(int textFontStyle) {
      this.textFontStyle = textFontStyle;
      return this;
    }

    public Config textFontSize(int textFontSize) {
      this.textFontSize = textFontSize;
      return this;
    }

    public Config textColor(Color textColor) {
      this.textColor = textColor;
      return this;
    }

    public Config textBackgroundColor(Color textBackgroundColor) {
      this.textBackgroundColor = textBackgroundColor;
      return this;
    }

    public Config textAlignment(Alignment textAlignment) {
      this.textAlignment = textAlignment;
      return this;
    }

    public Config textVerticalAlignment(Alignment textVerticalAlignment) {
      this.textVerticalAlignment = textVerticalAlignment;
      return this;
    }

    public Config textShadowEnabled(boolean textShadowEnabled) {
      this.textShadowEnabled = textShadowEnabled;
      return this;
    }

    public Config textShadowOffset(int textShadowOffsetX, int textShadowOffsetY) {
      this.textShadowOffsetX = textShadowOffsetX;
      this.textShadowOffsetY = textShadowOffsetY;
      return this;
    }

    public Config textShadowColor(Color textShadowColor) {
      this.textShadowColor = textShadowColor;
      return this;
    }

    public Config fieldBackground(BufferedImage fieldBackground) {
      this.fieldBackground = fieldBackground;
      return this;
    }

    public Config fieldPadding(Spacing fieldPadding) {
      this.fieldPadding = fieldPadding;
      return this;
    }

    public Config fieldDefaultBorder(BorderData fieldDefaultBorder) {
      this.fieldDefaultBorder = fieldDefaultBorder;
      return this;
    }

    public Config fieldFocusBorder(BorderData fieldFocusBorder) {
      this.fieldFocusBorder = fieldFocusBorder;
      return this;
    }
  }
}
