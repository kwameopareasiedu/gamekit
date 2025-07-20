package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.enums.Alignment;

import java.awt.*;
import java.awt.image.BufferedImage;

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
  protected void performMounted() {
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.buttonNinePatchBorder = coalesce(config.buttonNinePatchBorder, theme.buttonNinePatchBorder);
    this.buttonDefaultBackground = coalesce(config.buttonDefaultBackground, theme.buttonDefaultBackground);
    this.buttonHoverBackground = coalesce(config.buttonHoverBackground, theme.buttonHoverBackground);
    this.buttonPressedBackground = coalesce(config.buttonPressedBackground, theme.buttonPressedBackground);
    this.textFont = coalesce(config.textFont, theme.textFont);
    this.textFontStyle = coalesce(config.textFontStyle, theme.textFontStyle);
    this.textFontSize = coalesce(config.textFontSize, theme.textFontSize);
    this.textColor = coalesce(config.textColor, theme.textColor);
    this.textBackgroundColor = coalesce(config.textBackgroundColor, theme.textBackgroundColor);
    this.textAlignment = coalesce(config.textAlignment, theme.textAlignment);
    this.textVerticalAlignment = coalesce(config.textVerticalAlignment, theme.textVerticalAlignment);
    this.textShadowEnabled = coalesce(config.textShadowEnabled, theme.textShadowEnabled);
    this.textShadowOffsetX = coalesce(config.textShadowOffsetX, theme.textShadowOffsetX);
    this.textShadowOffsetY = coalesce(config.textShadowOffsetY, theme.textShadowOffsetY);
    this.textShadowColor = coalesce(config.textShadowColor, theme.textShadowColor);
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

  @Override
  public boolean stateEquals(Widget widget) {
    return false;
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
  }
}
