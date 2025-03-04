package dev.gamekit;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.stream.IntStream;

/**
 * GameKit's static input class. Input can be used to detect press and release
 * events of keys with ASCII codes from 0 to 255.
 */
public final class Input extends KeyAdapter {
  static final Input instance = new Input();

  private final boolean[] downStates = new boolean[256];
  private final boolean[] upStates = new boolean[256];
  private boolean anyKeyPressed = false;
  private boolean anyKeyReleased = false;

  private Input() { }

  public static boolean isKeyPressed(int keyCode) {
    return instance.downStates[keyCode];
  }

  public static boolean isKeyReleased(int keyCode) {
    return instance.upStates[keyCode];
  }

  public static boolean isAnyKeyPressed() {
    return instance.anyKeyPressed;
  }

  public static boolean isAnyKeyReleased() {
    return instance.anyKeyReleased;
  }

  public static void reset() {
    IntStream.range(0, 256).forEach(i -> instance.upStates[i] = false);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();

    if (keyCode >= 0 && keyCode < 256) {
      downStates[keyCode] = true;
      upStates[keyCode] = false;
      anyKeyPressed = true;
      anyKeyReleased = false;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    int keyCode = e.getKeyCode();

    if (keyCode >= 0 && keyCode < 256) {
      downStates[keyCode] = false;
      upStates[keyCode] = true;
      anyKeyPressed = false;
      anyKeyReleased = true;
    }
  }
}
