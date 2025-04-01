package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.events.InputEvent;
import dev.gamekit.ui.events.EventListener;
import dev.gamekit.ui.events.MouseClickEvent;

/** A {@link Widget} which can be clicked to trigger an event */
public class Button extends SingleChildParent {
  protected EventListener<MouseClickEvent> clickListener;

  protected Button(Widget child) {
    super(child);
  }

  public static Button create(Widget child) {
    return new Button(child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
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
  public void handleEvent(InputEvent event) {
    super.handleEvent(event);

    if (event instanceof MouseClickEvent clickEvent) {
      if (clickListener != null)
        clickListener.onEvent(clickEvent);
    }
  }

  public Button withClickListener(EventListener<MouseClickEvent> listener) {
    this.clickListener = listener;
    return this;
  }
}
