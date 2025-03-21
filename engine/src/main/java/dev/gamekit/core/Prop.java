package dev.gamekit.core;

/**
 * Represents game objects within Scenes. Because these are part of the "game world",
 * they have lifecycle methods which are called by the engine to set up, update, render
 * and dispose.
 * <p>
 * <p>
 * Props im
 */
public abstract class Prop {
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

  protected void onRender() { }

  protected void onDispose() { }
}
