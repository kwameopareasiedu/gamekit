package dev.gamekit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

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

  protected final String name;
  protected final List<Entity> entities;

  private Color graphicsBgColor;
  private AffineTransform graphicsTransform;
  private Stroke graphicsStroke;
  private Paint graphicsPaint;
  private Color graphicsColor;
  private Font graphicsFont;

  public Scene(String name) {
    this.name = name;
    entities = new ArrayList<>();
  }

  protected void start() {
    LOGGER.debug("Starting scene");
    entities.forEach(Entity::start);
  }

  protected void update() {
    entities.forEach(Entity::update);
  }

  protected void render(Graphics2D g) {
    entities.forEach(entity -> {
      saveGraphicsState(g);
      entity.render(g);
      resetGraphicsState(g);
    });
  }

  protected void dispose() {
    LOGGER.debug("Disposing scene");
    entities.forEach(Entity::dispose);
  }

  private void saveGraphicsState(Graphics2D g) {
    graphicsBgColor = g.getBackground();
    graphicsTransform = g.getTransform();
    graphicsStroke = g.getStroke();
    graphicsPaint = g.getPaint();
    graphicsColor = g.getColor();
    graphicsFont = g.getFont();
  }

  private void resetGraphicsState(Graphics2D g) {
    g.setBackground(graphicsBgColor);
    g.setTransform(graphicsTransform);
    g.setStroke(graphicsStroke);
    g.setPaint(graphicsPaint);
    g.setColor(graphicsColor);
    g.setFont(graphicsFont);
  }
}
