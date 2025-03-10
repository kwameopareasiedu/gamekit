package dev.gamekit.scene;

import dev.gamekit.interfaces.InputListener;

import java.awt.*;
import java.awt.event.KeyEvent;

public abstract class GameObject implements InputListener {
  private static int idCounter = 0;

  final int internalId;
  final String name;
  boolean ready;

  public GameObject(String name) {
    internalId = GameObject.idCounter++;
    this.name = name;
    this.ready = false;
  }

  protected void onStart() { }

  protected void onUpdate() { }

  protected void onRender(Graphics2D g) { }

  protected void onDispose() { }

  @Override
  public void onKeyDown(KeyEvent event) { }

  @Override
  public void onKeyUp(KeyEvent event) { }
}
