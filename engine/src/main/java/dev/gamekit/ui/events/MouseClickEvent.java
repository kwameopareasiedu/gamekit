package dev.gamekit.ui.events;

/**
 * A {@link MouseEvent} which is dispatched when a mouse button is clicked on
 * a listener widget
 */
public class MouseClickEvent extends MouseEvent {
  public final int buttonIndex;

  public MouseClickEvent(int x, int y, int buttonIndex) {
    super(x, y);
    this.buttonIndex = buttonIndex;
  }
}
