package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.events.Event;
import dev.gamekit.ui.events.MouseEvent;

public class GestureDetector extends SingleChildParent {
  protected GestureDetector(Widget child) {
    super(child);
  }

  public static GestureDetector create(Widget child) {
    return new GestureDetector(child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.computeLayout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(child.computedBounds.width, child.computedBounds.height);

    int computedWidth = constraints.constrainWidth(intrinsicBounds.width);
    int computedHeight = constraints.constrainHeight(intrinsicBounds.height);
    computedBounds.setSize(computedWidth, computedHeight);

    child.computedBounds.setPosition(0, 0);
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    return true;
  }

  @Override
  public void handleEvent(Event event) {
    super.handleEvent(event);

    if (event instanceof MouseEvent mouseEvent) {
      System.out.println(mouseEvent.x + " " + mouseEvent.y);
    }
  }
}
