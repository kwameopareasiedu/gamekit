package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;

import java.awt.*;

/** A {@link Leaf} which renders a solid color background */
@WidgetBuilder
public class Colored extends Leaf {
  @WidgetBuilderField(fallback = "java.awt.Color.GRAY")
  protected Color color;
  @WidgetBuilderField(fallback = "0")
  protected Integer borderRadius;

  public Colored(ColoredConfig config) {
    super(config);
  }

  public static Colored create(ColoredConfig.Updater updater) {
    return new Colored(Widgets.configureColored(updater));
  }

  public static Colored create(Color color) {
    return Colored.create(props -> props.color = color);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicSize.set(
      constraints.maxWidth(),
      constraints.maxHeight()
    );

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }

  @Override
  public void performRender(Graphics2D g) {
    g.setColor(color);
    g.fillRoundRect(
      (int) absoluteBounds.x, (int) absoluteBounds.y,
      (int) absoluteBounds.width, (int) absoluteBounds.height,
      borderRadius, borderRadius
    );
  }
}
