package dev.gamekit.ui.events;

/**
 * A {@link MouseEvent} which is dispatched when the mouse just leaves a
 * listener widget
 */
public class MouseExitEvent extends MouseEvent {
  public MouseExitEvent(int x, int y) {
    super(x, y);
  }

  /** Callback interface for a {@link MouseExitEvent} handler */
  public interface Listener {
    /** Called to handle the {@link MouseExitEvent} event */
    void onMouseExit(MouseExitEvent event);
  }
}
