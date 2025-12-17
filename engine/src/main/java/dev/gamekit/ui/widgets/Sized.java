package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;
import static dev.gamekit.utils.Misc.coalesce;

/**
 * A {@link SingleChildParent} which enforces a size on its child. This can be a fixed size, the
 * child's intrinsic size or a fractional size relative to this {@link Sized}'s parent
 */
public class Sized extends SingleChildParent {
  @WidgetBuilderField
  protected DimensionType widthType;
  @WidgetBuilderField
  protected DimensionType heightType;
  @WidgetBuilderField
  protected double width;
  @WidgetBuilderField
  protected double height;

  public Sized(SizedConfig config, Widget child) {
    super(config, child);
  }

  public static Sized create(SizedConfig config, Widget child) {
    return new Sized(config, child);
  }

  public static SizedConfig config() {
    return new SizedConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Sized sizedWidget &&
      Objects.equals(widthType, sizedWidget.widthType) &&
      Objects.equals(heightType, sizedWidget.heightType) &&
      Objects.equals(width, sizedWidget.width) &&
      Objects.equals(height, sizedWidget.height);
  }

  @Override
  protected void performInit() {
    SizedConfig config = (SizedConfig) super.config;

    this.width = coalesce(config.width, 64.0);
    this.height = coalesce(config.height, 64.0);
    this.widthType = coalesce(config.widthType, DimensionType.FIXED);
    this.heightType = coalesce(config.heightType, DimensionType.FIXED);

    super.performInit();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    if (widthType == DimensionType.INTRINSIC || heightType == DimensionType.INTRINSIC) {
      child.layout(
        new Constraints(
          0, constraints.maxWidth(),
          0, constraints.maxHeight()
        )
      );
    }

    double targetWidth = switch (widthType) {
      case FIXED -> width;
      case INTRINSIC -> child.computedBounds.width;
      case FRACTIONAL -> width * constraints.maxWidth();
    };

    double targetHeight = switch (heightType) {
      case FIXED -> height;
      case INTRINSIC -> child.computedBounds.height;
      case FRACTIONAL -> height * constraints.maxHeight();
    };

    if (widthType == DimensionType.INTRINSIC) {
      child.layout(
        new Constraints(
          0, constraints.maxWidth(),
          targetHeight, targetHeight
        )
      );
    } else if (heightType == DimensionType.INTRINSIC) {
      child.layout(
        new Constraints(
          targetWidth, targetWidth,
          0, constraints.maxHeight()
        )
      );
    } else {
      child.layout(
        new Constraints(
          targetWidth, targetWidth,
          targetHeight, targetHeight
        )
      );
    }

    intrinsicSize.set(
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }

  protected enum DimensionType {
    FIXED, INTRINSIC, FRACTIONAL
  }

  public static class SizedConfig extends SingleChildParentConfig {
    protected DimensionType widthType;
    protected DimensionType heightType;
    protected Double width;
    protected Double height;

    public SizedConfig width(int width) {
      this.widthType = DimensionType.FIXED;
      this.width = (double) width;
      return this;
    }

    public SizedConfig height(int height) {
      this.heightType = DimensionType.FIXED;
      this.height = (double) height;
      return this;
    }

    public SizedConfig intrinsicWidth() {
      this.widthType = DimensionType.INTRINSIC;
      this.width = (double) 0;
      return this;
    }

    public SizedConfig intrinsicHeight() {
      this.heightType = DimensionType.INTRINSIC;
      this.height = (double) 0;
      return this;
    }

    public SizedConfig fractionalWidth(double width) {
      this.widthType = DimensionType.FRACTIONAL;
      this.width = clamp(width, 0, 1);
      return this;
    }

    public SizedConfig fractionalHeight(double height) {
      this.heightType = DimensionType.FRACTIONAL;
      this.height = clamp(height, 0, 1);
      return this;
    }
  }
}
