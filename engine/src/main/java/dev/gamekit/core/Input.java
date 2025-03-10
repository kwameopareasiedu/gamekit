package dev.gamekit.core;

import dev.gamekit.interfaces.InputListener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * GameKit's static input class. Input can be used to detect press and release
 * events of keys with ASCII codes from 0 to 255.
 */
public final class Input extends KeyAdapter {
  static final Input INSTANCE = new Input();

  private final boolean[] downStates;
  private final boolean[] upStates;
  private boolean anyKeyPressed = false;
  private boolean anyKeyReleased = false;
  private final List<InputListener> listeners;

  private Input() {
    downStates = new boolean[256];
    upStates = new boolean[256];
    listeners = new ArrayList<>();
  }

  public static boolean isKeyPressed(int keyCode) {
    return INSTANCE.downStates[keyCode];
  }

  public static boolean isKeyReleased(int keyCode) {
    return INSTANCE.upStates[keyCode];
  }

  public static boolean isAnyKeyPressed() {
    return INSTANCE.anyKeyPressed;
  }

  public static boolean isAnyKeyReleased() {
    return INSTANCE.anyKeyReleased;
  }

  public static void reset() {
    IntStream.range(0, 256).forEach(i -> INSTANCE.upStates[i] = false);
  }

  public static void registerListener(InputListener listener) {
    if (!INSTANCE.listeners.contains(listener)) {
      INSTANCE.listeners.add(listener);
    }

  }

  public static void unregisterListener(InputListener listener) {
    INSTANCE.listeners.remove(listener);
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

    listeners.forEach(listener -> listener.onKeyDown(e));
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

    listeners.forEach(listener -> listener.onKeyUp(e));
  }
}
