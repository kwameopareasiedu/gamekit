package dev.gamekit.ui.events;

/**
 * Abstract base class for mouse related events. Contains an {@code (x, y)} for
 * the screen location the event took place.
 */
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

  /** Represents the type of {@link MouseEvent} */
  public enum Type {
    ENTER,
    MOTION,
    DOWN,
    PRESS,
    RELEASE,
    CLICK,
    EXIT
  }

  /** Callback interface for a {@link MouseEvent} handler */
  public interface Listener {
    /** Called to handle the {@link MouseEvent} event */
    void handleEvent(MouseEvent event);
  }
}
