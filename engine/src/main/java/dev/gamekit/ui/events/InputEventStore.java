package dev.gamekit.ui.events;

/** Convenience class which stores structures of {@link InputEvent} */
public class InputEventStore {
  public MouseEvent mouseMotionEvent;
  public MouseEvent mouseEnterEvent;
  public MouseEvent mouseDownEvent;
  public MouseEvent mousePressEvent;
  public MouseEvent mouseReleaseEvent;
  public MouseEvent mouseClickEvent;
  public MouseEvent mouseExitEvent;
  public KeyCharEvent keyCharEvent;

  public void clear() {
    mouseMotionEvent = null;
    mouseEnterEvent = null;
    mouseDownEvent = null;
    mousePressEvent = null;
    mouseReleaseEvent = null;
    mouseClickEvent = null;
    mouseExitEvent = null;
    keyCharEvent = null;
  }
}
