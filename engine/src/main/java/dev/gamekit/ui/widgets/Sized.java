package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

/**
 * A {@link SingleChildParent} which enforces a size on its child. This can be a fixed size, fractional size
 * relative to this {@link Sized Sized's} parent, or the child's intrinsic size
 */
@WidgetBuilder
public class Sized extends SingleChildParent {
  @WidgetBuilderField(fallback = "false")
  public Boolean useIntrinsicWidth;
  @WidgetBuilderField(fallback = "false")
  public Boolean useIntrinsicHeight;
  @WidgetBuilderField
  public Double fractionalWidth;
  @WidgetBuilderField
  public Double fractionalHeight;
  @WidgetBuilderField
  public Double fixedWidth;
  @WidgetBuilderField
  public Double fixedHeight;

  private DimensionType widthType;
  private DimensionType heightType;

  public Sized(SizedConfig config, Widget child) {
    super(config, child);
  }

  public static Sized create(SizedConfig.Updater updater, Widget child) {
    return new Sized(Widgets.configureSized(updater), child);
  }

  @Override
  protected void performInit() {
    if (useIntrinsicWidth == true)
      widthType = DimensionType.INTRINSIC;
    else if (fractionalWidth != null)
      widthType = DimensionType.FRACTIONAL;
    else if (fixedWidth != null)
      widthType = DimensionType.FIXED;
    else
      widthType = DimensionType.INTRINSIC;

    if (useIntrinsicHeight == true)
      heightType = DimensionType.INTRINSIC;
    else if (fractionalHeight != null)
      heightType = DimensionType.FRACTIONAL;
    else if (fixedHeight != null)
      heightType = DimensionType.FIXED;
    else
      heightType = DimensionType.INTRINSIC;

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
      case FIXED -> fixedWidth;
      case INTRINSIC -> child.computedBounds.width;
      case FRACTIONAL -> fractionalWidth * constraints.maxWidth();
    };

    double targetHeight = switch (heightType) {
      case FIXED -> fixedHeight;
      case INTRINSIC -> child.computedBounds.height;
      case FRACTIONAL -> fractionalHeight * constraints.maxHeight();
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

  private enum DimensionType {
    FIXED, INTRINSIC, FRACTIONAL
  }
}