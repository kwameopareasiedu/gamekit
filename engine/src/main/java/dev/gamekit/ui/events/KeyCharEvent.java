package dev.gamekit.ui.events;

/** Abstract base class for keyboard character events */
public class KeyCharEvent extends InputEvent {
  public final char charPressed;

  public KeyCharEvent(char charPressed) {
    this.charPressed = charPressed;
  }

  /** Callback interface for a {@link KeyCharEvent} handler */
  public interface Listener {
    /** Called to handle the {@link KeyCharEvent} event */
    void handleEvent(KeyCharEvent event);
  }
}
