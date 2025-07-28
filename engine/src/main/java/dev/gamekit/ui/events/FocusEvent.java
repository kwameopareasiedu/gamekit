package dev.gamekit.ui.events;

/** Event class representing focus and blur events */
public class FocusEvent extends InputEvent {
  public final Type type;

  public FocusEvent(Type type) {
    this.type = type;
  }

  /** Represents the type of {@link FocusEvent} */
  public enum Type {
    FOCUS,
    BLUR,
  }

  /** Handler interface for {@link FocusEvent focus events} */
  public interface Handler extends InputEvent.Handler {
    void handleEvent(FocusEvent ev);
  }
}
