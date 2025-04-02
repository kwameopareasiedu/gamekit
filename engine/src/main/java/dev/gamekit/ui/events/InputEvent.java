package dev.gamekit.ui.events;

/**
 * Abstract class representing UI events in the engine.
 * <p>
 * The {@link dev.gamekit.core.UI UI} class generates and dispatches events to
 * target widget based on the nature of the events.
 */
public abstract class InputEvent {
  private boolean handled;

  public InputEvent() {
    this.handled = false;
  }

  public boolean isHandled() { return handled; }

  /**
   * Sets whether the event has been handled.
   * Handled events are no longer propagated to ancestor widgets
   */
  public void setHandled() { this.handled = true; }
}
