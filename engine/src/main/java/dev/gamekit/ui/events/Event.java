package dev.gamekit.ui.events;

import dev.gamekit.ui.widgets.Widget;

/**
 * Abstract class representing UI events in the engine.
 * <p>
 * The {@link dev.gamekit.core.UI UI} class generates
 * and dispatches events to target widget based on the
 * nature of the events.
 */
public abstract class Event {
  public final Widget target;
  private boolean handled;

  public Event(Widget target) {
    this.target = target;
    this.handled = false;
  }

  public boolean isHandled() { return handled; }

  public void setHandled() { this.handled = true; }
}
