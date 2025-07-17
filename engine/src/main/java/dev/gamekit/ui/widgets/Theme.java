package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.Alignment;

import java.awt.*;
import java.awt.image.BufferedImage;

/** A {@link SingleChildParent} which provides theme variables to its child tree */
public class Theme extends SingleChildParent {
  private static final Theme DEFAULT_THEME = create(options(), Empty.create());

  public final BufferedImage buttonDefaultBackground;
  public final BufferedImage buttonHoverBackground;
  public final BufferedImage buttonPressedBackground;

  public final Font textFont;
  public final Integer textFontStyle;
  public final Integer textFontSize;
  public final Color textColor;
  public final Color textBackgroundColor;
  public final Alignment textAlignment;
  public final Alignment textVerticalAlignment;
  public final Boolean textShadowEnabled;
  public final Integer textShadowOffsetX;
  public final Integer textShadowOffsetY;
  public final Color textShadowColor;

  public Theme(ThemeOptions options, Widget child) {
    super(child);
    this.buttonDefaultBackground = options.buttonDefaultBackground;
    this.buttonHoverBackground = options.buttonHoverBackground;
    this.buttonPressedBackground = options.buttonPressedBackground;

    this.textFont = options.textFont;
    this.textFontStyle = options.textFontStyle;
    this.textFontSize = options.textFontSize;
    this.textColor = options.textColor;
    this.textBackgroundColor = options.textBackgroundColor;
    this.textAlignment = options.textAlignment;
    this.textVerticalAlignment = options.textVerticalAlignment;
    this.textShadowEnabled = options.textShadowEnabled;
    this.textShadowOffsetX = options.textShadowOffsetX;
    this.textShadowOffsetY = options.textShadowOffsetY;
    this.textShadowColor = options.textShadowColor;
  }

  public static Theme create(ThemeOptions params, Widget child) {
    return new Theme(params, child);
  }

  public static ThemeOptions options() {
    return new ThemeOptions();
  }

  public static Theme getDefault() {
    return DEFAULT_THEME;
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

  public static class ThemeOptions {
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

    public ThemeOptions buttonDefaultBackground(BufferedImage buttonDefaultBackground) {
      this.buttonDefaultBackground = buttonDefaultBackground;
      return this;
    }

    public ThemeOptions buttonHoverBackground(BufferedImage buttonHoverBackground) {
      this.buttonHoverBackground = buttonHoverBackground;
      return this;
    }

    public ThemeOptions buttonPressedBackground(BufferedImage buttonPressedBackground) {
      this.buttonPressedBackground = buttonPressedBackground;
      return this;
    }

    public ThemeOptions textFont(Font textFont) {
      this.textFont = textFont;
      return this;
    }

    public ThemeOptions textFontStyle(int textFontStyle) {
      this.textFontStyle = textFontStyle;
      return this;
    }

    public ThemeOptions textFontSize(int textFontSize) {
      this.textFontSize = textFontSize;
      return this;
    }

    public ThemeOptions textColor(Color textColor) {
      this.textColor = textColor;
      return this;
    }

    public ThemeOptions textBackgroundColor(Color textBackgroundColor) {
      this.textBackgroundColor = textBackgroundColor;
      return this;
    }

    public ThemeOptions textAlignment(Alignment textAlignment) {
      this.textAlignment = textAlignment;
      return this;
    }

    public ThemeOptions textVerticalAlignment(Alignment textVerticalAlignment) {
      this.textVerticalAlignment = textVerticalAlignment;
      return this;
    }

    public ThemeOptions textShadowEnabled(boolean textShadowEnabled) {
      this.textShadowEnabled = textShadowEnabled;
      return this;
    }

    public ThemeOptions textShadowOffset(int textShadowOffsetX, int textShadowOffsetY) {
      this.textShadowOffsetX = textShadowOffsetX;
      this.textShadowOffsetY = textShadowOffsetY;
      return this;
    }

    public ThemeOptions textShadowColor(Color textShadowColor) {
      this.textShadowColor = textShadowColor;
      return this;
    }
  }
}
