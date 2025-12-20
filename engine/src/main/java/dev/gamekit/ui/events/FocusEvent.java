package dev.gamekit.ui.events;

import dev.gamekit.ui.widgets.Widget;

/** Event class representing focus and blur events */
public class FocusEvent extends InputEvent {
  public final Type type;

  public FocusEvent(Type type) {
    this.type = type;
  }

  /** Constants for {@link FocusEvent} types */
  public enum Type {
    /** Indicates that a {@link Widget} has received focus */
    FOCUS,
    /** Indicates that a {@link Widget} has lost focus */
    BLUR,
  }

  /** Handler interface for {@link FocusEvent focus events} */
  public interface Handler extends InputEvent.Handler {
    /** Called to handle a {@link FocusEvent} */
    void handleEvent(FocusEvent ev);
  }
}
