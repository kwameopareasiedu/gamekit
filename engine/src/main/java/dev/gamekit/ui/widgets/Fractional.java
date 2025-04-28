package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.Alignment;

import java.util.Objects;

/**
 * A {@link SingleChildParent} which enforces a fractional size on its child relative to its
 * width and height
 */
public class Fractional extends SingleChildParent {
  protected final double widthFactor;
  protected final double heightFactor;
  protected final Alignment horizontalAlignment;
  protected final Alignment verticalAlignment;

  public Fractional(FractionalOptions options, Widget child) {
    super(child);
    this.widthFactor = options.widthFactor;
    this.heightFactor = options.heightFactor;
    this.horizontalAlignment = options.horizontalAlignment;
    this.verticalAlignment = options.verticalAlignment;
  }

  public static Fractional create(FractionalOptions options, Widget child) {
    return new Fractional(options, child);
  }

  public static FractionalOptions options() {
    return new FractionalOptions();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    computedBounds.setSize(
      constraints.maxWidth(),
      constraints.maxHeight()
    );

    double fractionalWidth = widthFactor * computedBounds.width;
    double fractionalHeight = heightFactor * computedBounds.height;

    child.layout(
      new Constraints(
        fractionalWidth, fractionalWidth,
        fractionalHeight, fractionalHeight
      )
    );

    intrinsicBounds.setSize(
      child.computedBounds.width,
      child.computedBounds.height
    );

    double hOffset = switch (horizontalAlignment) {
      case CENTER -> computedBounds.width / 2 - intrinsicBounds.width / 2;
      case END -> computedBounds.width - intrinsicBounds.width;
      default -> 0;
    };

    double vOffset = switch (verticalAlignment) {
      case CENTER -> computedBounds.height / 2 - intrinsicBounds.height / 2;
      case END -> computedBounds.height - intrinsicBounds.height;
      default -> 0;
    };

    child.computedBounds.setPosition(hOffset, vOffset);
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Fractional fractionalWidget) {
      return Objects.equals(widthFactor, fractionalWidget.widthFactor) &&
        Objects.equals(heightFactor, fractionalWidget.heightFactor);
    }
    return false;
  }

  public static class FractionalOptions {
    double widthFactor = 1.0;
    double heightFactor = 1.0;
    Alignment horizontalAlignment = Alignment.START;
    Alignment verticalAlignment = Alignment.START;

    public FractionalOptions widthFactor(double widthFactor) {
      this.widthFactor = widthFactor;
      return this;
    }

    public FractionalOptions heightFactor(double heightFactor) {
      this.heightFactor = heightFactor;
      return this;
    }

    public FractionalOptions horizontalAlignment(Alignment horizontalAlignment) {
      this.horizontalAlignment = horizontalAlignment;
      return this;
    }

    public FractionalOptions verticalAlignment(Alignment verticalAlignment) {
      this.verticalAlignment = verticalAlignment;
      return this;
    }
  }
}
