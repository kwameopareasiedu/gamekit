package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.Alignment;

import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link SingleChildParent} which aligns its single child within itself */
public class Align extends SingleChildParent {
  protected Alignment horizontalAlignment;
  protected Alignment verticalAlignment;

  private final Config config;

  public Align(Config config, Widget child) {
    super(child);
    this.config = config;
  }

  public static Align create(Config config, Widget child) {
    return new Align(config, child);
  }

  public static Config config() {
    return new Config();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Align alignWidget) {
      return Objects.equals(horizontalAlignment, alignWidget.horizontalAlignment)
        && Objects.equals(verticalAlignment, alignWidget.verticalAlignment);
    }

    return false;
  }

  @Override
  protected void performUpdateState(Widget widget) {
    this.horizontalAlignment = ((Align) widget).horizontalAlignment;
    this.verticalAlignment = ((Align) widget).verticalAlignment;
  }

  @Override
  protected void performMounted() {
    this.horizontalAlignment = coalesce(config.horizontalAlignment, Alignment.START);
    this.verticalAlignment = coalesce(config.verticalAlignment, Alignment.START);
    super.performMounted();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(
      child.computedBounds.width,
      child.computedBounds.height
    );

    computedBounds.setSize(
      constraints.maxWidth(),
      constraints.maxHeight()
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

  public static class Config {
    Alignment horizontalAlignment;
    Alignment verticalAlignment;

    Config() { }

    public Config horizontalAlignment(Alignment horizontalAlignment) {
      this.horizontalAlignment = horizontalAlignment;
      return this;
    }

    public Config verticalAlignment(Alignment verticalAlignment) {
      this.verticalAlignment = verticalAlignment;
      return this;
    }
  }
}
