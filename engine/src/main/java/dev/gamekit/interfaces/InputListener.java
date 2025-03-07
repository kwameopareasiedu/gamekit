package dev.gamekit.interfaces;

import java.awt.event.KeyEvent;

public interface InputListener {
  void onKeyDown(KeyEvent event);

  void onKeyUp(KeyEvent event);
}
