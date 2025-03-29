package dev.gamekit.ui.events;

/**
 * Abstract base class for mouse related events.
 * Contains an {@code (x, y)} for the screen location
 * the event took place.
 */
public abstract class MouseEvent extends Event {
  public final int x;
  public final int y;

  public MouseEvent(int x, int y) {
    this.x = x;
    this.y = y;
  }
}
