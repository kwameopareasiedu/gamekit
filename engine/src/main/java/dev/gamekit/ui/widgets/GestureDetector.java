package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.events.Event;
import dev.gamekit.ui.events.EventListener;
import dev.gamekit.ui.events.MouseClickEvent;
import dev.gamekit.ui.events.MouseMotionEvent;

public class GestureDetector extends SingleChildParent {
  protected EventListener<MouseMotionEvent> motionListener;
  protected EventListener<MouseClickEvent> clickListener;

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

    if (event instanceof MouseMotionEvent motionEvent) {
      if (motionListener != null)
        motionListener.onEvent(motionEvent);
    } else if (event instanceof MouseClickEvent clickEvent) {
      if (clickListener != null)
        clickListener.onEvent(clickEvent);
    }
  }

  public GestureDetector withMotionListener(EventListener<MouseMotionEvent> listener) {
    this.motionListener = listener;
    return this;
  }

  public GestureDetector withClickListener(EventListener<MouseClickEvent> listener) {
    this.clickListener = listener;
    return this;
  }
}
