package dev.gamekit.core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.stream.IntStream;

/**
 * GameKit's static input class. Input can be used to detect press and release
 * events of keys with ASCII codes from 0 to 255.
 */
public final class Input extends KeyAdapter {
  static final Input INSTANCE = new Input();
  private static final int COUNT = 256;

  private final KeyState[] states;
  private boolean isFrozen = false;

  private Input() {
    states = new KeyState[COUNT];

    IntStream.range(0, 256).forEach(
      i -> states[i] = new KeyState()
    );
  }

  public synchronized static boolean isKeyPressed(int keyCode) {
    return INSTANCE.states[keyCode].isPressed;
  }

  public synchronized static boolean isKeyJustPressed(int keyCode) {
    return INSTANCE.states[keyCode].isJustPressed;
  }

  public synchronized static boolean isKeyJustReleased(int keyCode) {
    return INSTANCE.states[keyCode].isJustReleased;
  }

  static void freeze() {
    INSTANCE.isFrozen = true;
  }

  static void reset() {
    IntStream.range(0, COUNT).forEach(
      i -> INSTANCE.states[i].reset()
    );

    INSTANCE.isFrozen = false;
  }

  @Override
  public synchronized void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();
    if (!isFrozen && keyCode >= 0 && keyCode < 256) {
      states[keyCode].update(true);
    }
  }

  @Override
  public synchronized void keyReleased(KeyEvent e) {
    int keyCode = e.getKeyCode();
    if (!isFrozen && keyCode >= 0 && keyCode < 256) {
      states[keyCode].update(false);
    }
  }

  private static class KeyState {
    boolean isPressed = false;
    boolean isJustPressed = false;
    boolean isJustReleased = false;

    private void update(boolean isPressed) {
      isJustPressed = !this.isPressed && isPressed;
      isJustReleased = this.isPressed && !isPressed;
      this.isPressed = isPressed;
    }

    private void reset() {
      isJustPressed = false;
      isJustReleased = false;
    }
  }
}
