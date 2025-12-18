package dev.gamekit.ui.events;

/** Event class for keyboard character events */
public class KeyCharEvent extends InputEvent {
  public final char charPressed;

  public KeyCharEvent(char charPressed) {
    this.charPressed = charPressed;
  }

  /** Handler interface for {@link KeyCharEvent key char events} */
  public interface Handler extends InputEvent.Handler {
    /** Called to handle a {@link KeyCharEvent} */
    void handleEvent(KeyCharEvent ev);
  }
}
