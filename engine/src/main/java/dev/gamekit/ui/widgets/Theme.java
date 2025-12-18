package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Spacing;

import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which provides theme variables to its child tree */
@WidgetBuilder
public class Theme extends SingleChildParent {
  private static final Theme DEFAULT_THEME = create(ThemeConfig.child(Empty.create()));

  @WidgetBuilderField
  public BufferedImage panelBackground;
  @WidgetBuilderField
  public Spacing panelEdgeInsets;

  @WidgetBuilderField
  public BufferedImage buttonDefaultBackground;
  @WidgetBuilderField
  public BufferedImage buttonHoverBackground;
  @WidgetBuilderField
  public BufferedImage buttonPressedBackground;
  @WidgetBuilderField
  public Spacing buttonEdgeInsets;

  @WidgetBuilderField
  public Font textFont;
  @WidgetBuilderField
  public Integer textFontSize;
  @WidgetBuilderField
  public Integer textFontStyle;
  @WidgetBuilderField
  public Color textColor;
  @WidgetBuilderField
  public Color textBackgroundColor;
  @WidgetBuilderField
  public Alignment textAlignment;
  @WidgetBuilderField
  public Boolean textShadowEnabled;
  @WidgetBuilderField
  public Integer textShadowOffsetX;
  @WidgetBuilderField
  public Integer textShadowOffsetY;
  @WidgetBuilderField
  public Color textShadowColor;

  @WidgetBuilderField
  public BufferedImage fieldDefaultBackground;
  @WidgetBuilderField
  public BufferedImage fieldFocusBackground;
  @WidgetBuilderField
  public Spacing fieldEdgeInsets;
  @WidgetBuilderField
  public Spacing fieldPadding;

  @WidgetBuilderField
  public BufferedImage checkboxDefaultIcon;
  @WidgetBuilderField
  public BufferedImage checkboxToggledIcon;
  @WidgetBuilderField
  public Spacing checkboxIconEdgeInsets;
  @WidgetBuilderField
  public Integer checkboxIconWidth;
  @WidgetBuilderField
  public Integer checkboxIconHeight;
  @WidgetBuilderField
  public Integer checkboxGapSize;

  @WidgetBuilderField
  public BufferedImage progressTrackBackground;
  @WidgetBuilderField
  public BufferedImage progressFillBackground;
  @WidgetBuilderField
  public Spacing progressTrackEdgeInsets;
  @WidgetBuilderField
  public Spacing progressFillEdgeInsets;
  @WidgetBuilderField
  public Spacing progressFillMargin;
  @WidgetBuilderField
  public Progress.FillMode progressFillMode;

  @WidgetBuilderField
  public BufferedImage sliderThumbBackground;
  @WidgetBuilderField
  public Spacing sliderThumbEdgeInsets;
  @WidgetBuilderField
  public Integer sliderThumbWidth;
  @WidgetBuilderField
  public Integer sliderThumbHeight;

  public Theme(ThemeConfig... config) {
    super(config);
  }

  public static Theme create(ThemeConfig... params) {
    return new Theme(params);
  }

  public static Theme getDefault() {
    return DEFAULT_THEME;
  }

  @Override
  protected void performInit() {
    ThemeConfig config = (ThemeConfig) super.config;
    Theme theme = coalesce(getAncestorOfType(Theme.class), Theme.getDefault());

    this.panelBackground = coalesce(config.panelBackground, theme.panelBackground);
    this.panelEdgeInsets = coalesce(config.panelEdgeInsets, theme.panelEdgeInsets);

    this.buttonDefaultBackground = coalesce(config.buttonDefaultBackground, theme.buttonDefaultBackground);
    this.buttonHoverBackground = coalesce(config.buttonHoverBackground, theme.buttonHoverBackground);
    this.buttonPressedBackground = coalesce(config.buttonPressedBackground, theme.buttonPressedBackground);
    this.buttonEdgeInsets = coalesce(config.buttonEdgeInsets, theme.buttonEdgeInsets);

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

    this.fieldDefaultBackground = coalesce(config.fieldDefaultBackground, theme.fieldDefaultBackground);
    this.fieldFocusBackground = coalesce(config.fieldFocusBackground, theme.fieldFocusBackground);
    this.fieldEdgeInsets = coalesce(config.fieldEdgeInsets, theme.fieldEdgeInsets);
    this.fieldPadding = coalesce(config.fieldPadding, theme.fieldPadding);

    this.checkboxDefaultIcon = coalesce(config.checkboxDefaultIcon, theme.checkboxDefaultIcon);
    this.checkboxToggledIcon = coalesce(config.checkboxToggledIcon, theme.checkboxToggledIcon);
    this.checkboxIconEdgeInsets = coalesce(config.checkboxIconEdgeInsets, theme.checkboxIconEdgeInsets);
    this.checkboxIconWidth = coalesce(config.checkboxIconWidth, theme.checkboxIconWidth);
    this.checkboxIconHeight = coalesce(config.checkboxIconHeight, theme.checkboxIconHeight);
    this.checkboxGapSize = coalesce(config.checkboxGapSize, theme.checkboxGapSize);

    this.progressTrackBackground = coalesce(config.progressTrackBackground, theme.progressTrackBackground);
    this.progressFillBackground = coalesce(config.progressFillBackground, theme.progressFillBackground);
    this.progressTrackEdgeInsets = coalesce(config.progressTrackEdgeInsets, theme.progressTrackEdgeInsets);
    this.progressFillEdgeInsets = coalesce(config.progressFillEdgeInsets, theme.progressFillEdgeInsets);
    this.progressFillMargin = coalesce(config.progressFillMargin, theme.progressFillMargin);
    this.progressFillMode = coalesce(config.progressFillMode, theme.progressFillMode);

    this.sliderThumbBackground = coalesce(config.sliderThumbBackground, theme.sliderThumbBackground);
    this.sliderThumbEdgeInsets = coalesce(config.sliderThumbEdgeInsets, theme.sliderThumbEdgeInsets);
    this.sliderThumbWidth = coalesce(config.sliderThumbWidth, theme.sliderThumbWidth);
    this.sliderThumbHeight = coalesce(config.sliderThumbHeight, theme.sliderThumbHeight);

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(constraints);

    intrinsicSize.set(child.computedBounds.width, child.computedBounds.height);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }
}
