package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.utils.Constraints;

/** A {@link MultiChildParent} which stacks its children on top of each other */
@WidgetBuilder
public class Stack extends MultiChildParent {
  public Stack(StackConfig config, Widget... children) {
    super(config, children);
  }

  public static Stack create(StackConfig.Updater updater, Widget... children) {
    return new Stack(Widgets.configureStack(updater), children);
  }

  public static Stack create(Widget... children) {
    return Stack.create(props -> { }, children);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    double maxWidth = 0, maxHeight = 0;

    for (Widget child : children) {
      child.layout(
        new Constraints(
          0, constraints.maxWidth(),
          0, constraints.maxHeight()
        )
      );

      maxWidth = Math.max(maxWidth, child.computedBounds.width);
      maxHeight = Math.max(maxHeight, child.computedBounds.height);
    }

    intrinsicSize.set(maxWidth, maxHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    for (Widget child : children) {
      child.computedBounds.setPosition(
        computedBounds.width / 2 - child.computedBounds.width / 2,
        computedBounds.height / 2 - child.computedBounds.height / 2
      );
    }
  }
}
