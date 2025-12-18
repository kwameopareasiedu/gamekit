package dev.gamekit.ui.events;

import dev.gamekit.core.UI;

/**
 * Abstract class representing UI events in the engine.
 * <p>
 * The {@link UI UI} class generates and dispatches events to target widget based on the nature of the events.
 */
public abstract class InputEvent {
  private boolean handled;

  public InputEvent() {
    this.handled = false;
  }

  public boolean isHandled() {
    return handled;
  }

  /**
   * Sets whether the event has been handled.
   * <p>
   * Handled events are no longer propagated to ancestor widgets
   */
  public void setHandled() {
    this.handled = true;
  }

  /** Base class for handlers of {@link InputEvent input events} */
  public interface Handler { }
}
