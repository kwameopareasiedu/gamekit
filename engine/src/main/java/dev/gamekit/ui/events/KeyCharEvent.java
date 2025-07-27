package dev.gamekit.ui.events;

/** Abstract base class for keyboard character events */
public class KeyCharEvent extends InputEvent {
  public final char charPressed;

  public KeyCharEvent(char charPressed) {
    this.charPressed = charPressed;
  }

  /** Handler interface for {@link KeyCharEvent mouse events} */
  public interface Handler extends InputEvent.Handler {
    void handleEvent(KeyCharEvent ev);
  }
}
