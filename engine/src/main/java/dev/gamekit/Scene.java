package dev.gamekit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

/**
 * An arrangement of entities that represent a logical part of your game.
 * This can be a main menu, testing area or a level within your game.
 * <p>
 * When a scene is added to a game, its {@code start()} is called to initialize it.
 * Then its {@code update()} method is called in the game loop and then its
 * {@code render()} to render it to the game window
 */
public abstract class Scene {
  private static final Logger LOGGER = LogManager.getLogger();

  final String name;

  public Scene(String name) {
    this.name = name;
  }

  protected void start() {
    LOGGER.debug("Started scene");
  }

  protected void update() { }

  protected void render(Graphics2D g) { }

  protected void dispose() {
    LOGGER.debug("Disposing scene");
  }
}
