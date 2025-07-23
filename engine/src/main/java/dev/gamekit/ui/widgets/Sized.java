package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;
import static dev.gamekit.utils.Misc.coalesce;

/**
 * A {@link SingleChildParent} which enforces a size on its child. This can be a fixed size, the
 * child's intrinsic size or a fractional size relative to this {@link Sized}'s parent
 */
public class Sized extends SingleChildParent {
  protected DimensionType widthType;
  protected DimensionType heightType;
  protected double width;
  protected double height;

  private final Config config;

  public Sized(Config config, Widget child) {
    super(child);
    this.config = config;
  }

  public static Sized create(Config config, Widget child) {
    return new Sized(config, child);
  }

  public static Config config() {
    return new Config();
  }

  @Override
  protected void performMounted() {
    this.width = coalesce(config.width, 64.0);
    this.height = coalesce(config.height, 64.0);
    this.widthType = coalesce(config.widthType, DimensionType.FIXED);
    this.heightType = coalesce(config.heightType, DimensionType.FIXED);
    super.performMounted();
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
    if (widget instanceof Sized sizedWidget) {
      return Objects.equals(widthType, sizedWidget.widthType) &&
        Objects.equals(heightType, sizedWidget.heightType) &&
        Objects.equals(width, sizedWidget.width) &&
        Objects.equals(height, sizedWidget.height);
    }
    return false;
  }

  public static class Config {
    DimensionType widthType;
    DimensionType heightType;
    Double width;
    Double height;

    Config() { }

    public Config width(int width) {
      this.widthType = DimensionType.FIXED;
      this.width = (double) width;
      return this;
    }

    public Config height(int height) {
      this.heightType = DimensionType.FIXED;
      this.height = (double) height;
      return this;
    }

    public Config intrinsicWidth() {
      this.widthType = DimensionType.INTRINSIC;
      this.width = (double) 0;
      return this;
    }

    public Config intrinsicHeight() {
      this.heightType = DimensionType.INTRINSIC;
      this.height = (double) 0;
      return this;
    }

    public Config fractionalWidth(double width) {
      this.widthType = DimensionType.FRACTIONAL;
      this.width = clamp(width, 0, 1);
      return this;
    }

    public Config fractionalHeight(double height) {
      this.heightType = DimensionType.FRACTIONAL;
      this.height = clamp(height, 0, 1);
      return this;
    }
  }

  protected enum DimensionType {
    FIXED, INTRINSIC, FRACTIONAL
  }
}
