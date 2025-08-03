package dev.gamekit.ui.events;

/**
 * Event class for keyboard character code events. These are more suited for action key events
 * rather than character events (E.g. Arrow keys)
 */
public class KeyCodeEvent extends InputEvent {
  public final int keyCode;

  public KeyCodeEvent(int keyCode) {
    this.keyCode = keyCode;
  }

  /** Handler interface for {@link KeyCodeEvent key char events} */
  public interface Handler extends InputEvent.Handler {
    void handleEvent(KeyCodeEvent ev);
  }
}
