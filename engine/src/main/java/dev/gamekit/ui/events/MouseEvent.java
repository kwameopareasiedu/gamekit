package dev.gamekit.ui.events;

import dev.gamekit.ui.widgets.Widget;

/** Event class for mouse related events. Contains an {@code (x, y)} for the screen location the event took place */
public class MouseEvent extends InputEvent {
  public final Type type;
  public final int x;
  public final int y;
  public final int button;

  public MouseEvent(Type type, int x, int y, int button) {
    this.type = type;
    this.x = x;
    this.y = y;
    this.button = button;
  }

  /** Constants for {@link MouseEvent} types */
  public enum Type {
    /** Indicates that the mouse entered a {@link Widget} */
    ENTER,
    /** Indicates that the mouse has moved within a {@link Widget} */
    MOTION,
    /** Indicates that the primary mouse button has just been pressed inside a {@link Widget} */
    DOWN,
    /** Indicates that the primary mouse button is being been pressed inside a {@link Widget} */
    PRESS,
    /** Indicates that the primary mouse button has been released inside a {@link Widget} */
    RELEASE,
    /** Indicates that a {@link Widget} has been clicked on */
    CLICK,
    /** Indicates that the mouse exited a {@link Widget} */
    EXIT
  }

  /** Handler interface for {@link MouseEvent mouse events} */
  public interface Handler extends InputEvent.Handler {
    /** Called to handle a {@link MouseEvent} */
    void handleEvent(MouseEvent ev);
  }
}
