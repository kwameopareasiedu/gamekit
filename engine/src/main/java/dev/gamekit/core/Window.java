package dev.gamekit.core;

import dev.gamekit.utils.VoidCallback;

/** Interface for the windowing system which renders the application */
public interface Window {
  /** Makes the window visible on the primary monitor */
  void show();

  /** Notifies the window of a close intent */
  void notifyClose();

  /** Returns {@code true} if a close event has been received and {@code false} otherwise */
  boolean closeEventReceived();

  /** Sets a callback for when a close event is received */
  void setCloseListener(VoidCallback callback);

  /** Sets a callback for inputs which occur on the window */
  void setInputListener(InputListener listener);

  /** Runs post-frame dispose operations */
  void disposeFrame();

  /** Runs cleanup operations prior to disposal */
  void dispose();

  /** Handler for inputs that occur on a {@link Window} */
  interface InputListener {
    /** Called with the integer key code of a pressed key */
    void onKeyPressed(int keyCode);

    /** Called with the integer key code of a released key */
    void onKeyReleased(int keyCode);
  }
}
