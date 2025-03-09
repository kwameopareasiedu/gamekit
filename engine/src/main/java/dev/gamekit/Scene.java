package dev.gamekit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

/**
 * An arrangement of entities that represent a logical part of your game.
 * This can be a main menu, testing area or a level within your game.
 */
public abstract class Scene {
  private static final Logger LOGGER = LogManager.getLogger();
  static Scene active;

  protected final String name;
  protected final Camera camera;
  final Map<Integer, Entity> children;

  private Color graphicsBgColor;
  private AffineTransform graphicsTransform;
  private Stroke graphicsStroke;
  private Paint graphicsPaint;
  private Color graphicsColor;
  private Font graphicsFont;

  public Scene(String name) {
    this.name = name;
    camera = new Camera();
    children = new HashMap<>();
  }

  public static Scene getActive() {
    return active;
  }

  public Camera getCamera() {
    return camera;
  }

  public void addChild(Entity entity) {
    LOGGER.debug("Adding child: [{} - {}]", entity.internalId, entity.name);

    if (!children.containsKey(entity.internalId)) {
      Application.getInstance().runOnFrameEnd(() -> {
        children.put(entity.internalId, entity);

        if (!entity.ready) {
          entity.onStart();
        }

        LOGGER.debug("Added child: [{} - {}]", entity.internalId, entity.name);
      });
    }
  }

  public void removeChild(Entity entity) {
    LOGGER.debug("Removing child: [{} - {}]", entity.internalId, entity.name);

    if (children.containsKey(entity.internalId)) {
      Application.getInstance().runOnFrameEnd(() -> {
        children.remove(entity.internalId, entity);
        LOGGER.debug("Removed child: [{} - {}]", entity.internalId, entity.name);
      });
    }
  }

  protected void onStart() {
    LOGGER.debug("Starting scene");
    children.forEach((k, v) -> v.onStart());
  }

  protected void onUpdate() {
    children.forEach((k, v) -> v.onUpdate());
  }

  protected void onRender(Graphics2D g) {
    children.forEach((k, v) -> {
      saveGraphicsState(g);
      v.onRender(g);
      resetGraphicsState(g);
    });
  }

  protected void onDispose() {
    LOGGER.debug("Disposing scene");
    children.forEach((k, v) -> v.onDispose());
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

    graphicsBgColor = null;
    graphicsTransform = null;
    graphicsStroke = null;
    graphicsPaint = null;
    graphicsColor = null;
    graphicsFont = null;
  }
}
