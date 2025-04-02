package dev.gamekit.ui.events;

/**
 * A {@link MouseEvent} which is dispatched when the mouse just enters a
 * listener widget
 */
public class MouseEnterEvent extends MouseEvent {
  public MouseEnterEvent(int x, int y) {
    super(x, y);
  }

  /** Callback interface for a {@link MouseEnterEvent} handler */
  public interface Listener {
    /** Called to handle the {@link MouseEnterEvent} event */
    void onMouseEnter(MouseEnterEvent event);
  }
}
