package dev.gamekit.ui.events;

/**
 * A {@link MouseEvent} which is dispatched when the mouse is moved over a
 * listener widget
 */
public class MouseMotionEvent extends MouseEvent {
  public MouseMotionEvent(int x, int y) {
    super(x, y);
  }

  /** Callback interface for a {@link MouseMotionEvent} handler */
  public interface Listener {
    /** Called to handle the {@link MouseMotionEvent} event */
    void onMouseMove(MouseMotionEvent event);
  }
}
