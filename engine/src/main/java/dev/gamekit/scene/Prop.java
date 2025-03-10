package dev.gamekit.scene;

import dev.gamekit.interfaces.InputListener;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Represents game objects within Scenes. Because these are part of the "game world",
 * they have lifecycle methods which are called by the engine to set up, update, render
 * and dispose.
 * <p>
 * <p>
 * Props im
 */
public abstract class Prop implements InputListener {
  private static int idCounter = 0;

  final int internalId;
  final String name;
  boolean ready;

  public Prop(String name) {
    internalId = Prop.idCounter++;
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
