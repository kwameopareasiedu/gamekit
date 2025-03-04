package dev.gamekit;

import java.awt.*;

/** Entities are used to represent objects in the game world */
public abstract class Entity {
  final String name;

  public Entity(String name) {
    this.name = name;
  }

  protected void start() { }

  protected void update() { }

  protected void render(Graphics2D g) { }

  protected void dispose() { }
}
