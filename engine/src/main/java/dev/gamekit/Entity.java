package dev.gamekit;

import java.awt.*;

/** Entities are used to represent objects in the game world */
public abstract class Entity {
  private static int idCounter = 0;

  final int internalId;
  final String name;
  boolean ready;

  public Entity(String name) {
    internalId = Entity.idCounter++;
    this.name = name;
    this.ready = false;
  }

  protected void onStart() { }

  protected void onUpdate() { }

  protected void onRender(Graphics2D g) { }

  protected void onDispose() { }
}
