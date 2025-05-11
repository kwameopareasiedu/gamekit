package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;

import java.util.Objects;

import static dev.gamekit.utils.Math.clamp;

/**
 * A {@link SingleChildParent} which enforces a size on its child. This can be a fixed size, the
 * child's intrinsic size or a fractional size relative to this {@link Sized}'s parent
 */
public class Sized extends SingleChildParent {
  protected final DimensionType widthType;
  protected final DimensionType heightType;
  protected final double width;
  protected final double height;

  public Sized(SizedOptions options, Widget child) {
    super(child);
    this.width = options.width;
    this.height = options.height;
    this.widthType = options.widthType;
    this.heightType = options.heightType;
  }

  public static Sized create(SizedOptions options, Widget child) {
    return new Sized(options, child);
  }

  public static SizedOptions options() {
    return new SizedOptions();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(constraints);

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

    intrinsicBounds.setSize(targetWidth, targetHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );

    child.layout(
      new Constraints(
        computedBounds.width, computedBounds.width,
        computedBounds.height, computedBounds.height
      )
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

  public static class SizedOptions {
    DimensionType widthType = DimensionType.FIXED;
    DimensionType heightType = DimensionType.FIXED;
    double width = 64;
    double height = 64;

    public SizedOptions width(int width) {
      this.widthType = DimensionType.FIXED;
      this.width = width;
      return this;
    }

    public SizedOptions height(int height) {
      this.heightType = heightType = DimensionType.FIXED;
      this.height = height;
      return this;
    }

    public SizedOptions intrinsicWidth() {
      this.widthType = DimensionType.INTRINSIC;
      return this;
    }

    public SizedOptions intrinsicHeight() {
      this.heightType = DimensionType.INTRINSIC;
      return this;
    }

    public SizedOptions fractionalWidth(double width) {
      this.widthType = DimensionType.FRACTIONAL;
      this.width = clamp(width, 0, 1);
      return this;
    }

    public SizedOptions fractionalHeight(double height) {
      this.heightType = DimensionType.FRACTIONAL;
      this.height = clamp(height, 0, 1);
      return this;
    }
  }

  protected enum DimensionType {
    FIXED, INTRINSIC, FRACTIONAL
  }
}
