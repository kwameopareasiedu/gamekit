package dev.gamekit.ui.events;

import dev.gamekit.ui.widgets.Widget;

/**
 * Abstract class representing UI events in the engine.
 * <p>
 * The {@link dev.gamekit.core.UI UI} class generates and dispatches events to
 * target widget based on the nature of the events.
 */
public abstract class Event {
  private Widget target;
  private boolean handled;

  public Event() {
    this.handled = false;
  }

  public boolean isHandled() { return handled; }

  /**
   * Sets whether the event has been handled.
   * Handled events are no longer propagated to ancestor widgets
   */
  public void setHandled() { this.handled = true; }

  public Widget getTarget() { return target; }

  /**
   * Internally called to set the target of the event.
   * Attempts to call this externally will raise an {@link IllegalStateException}
   */
  public void setTarget(Widget target) {
    if (this.target != null) throw new IllegalStateException("Attempting to set target of finalized Event");
    this.target = target;
  }
}
