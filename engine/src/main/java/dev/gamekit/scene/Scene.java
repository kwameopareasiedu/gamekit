package dev.gamekit.scene;

import dev.gamekit.core.Application;
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
  private static Scene active;

  protected final String name;
  protected final Camera camera;

  private final Map<Integer, Prop> props;
  private Color graphicsBgColor;
  private AffineTransform graphicsTransform;
  private Stroke graphicsStroke;
  private Paint graphicsPaint;
  private Color graphicsColor;
  private Font graphicsFont;

  public Scene(String name) {
    this.name = name;
    camera = new Camera();
    props = new HashMap<>();
  }

  public static Scene getActive() { return active; }

  public static void setActive(Scene scene) { active = scene; }

  public String getName() { return name; }

  public Camera getCamera() { return camera; }

  public void addChild(Prop prop) {
    LOGGER.debug("Adding child: [{} - {}]", prop.internalId, prop.name);

    if (!props.containsKey(prop.internalId)) {
      Application.getInstance().runOnFrameEnd(() -> {
        props.put(prop.internalId, prop);

        if (!prop.ready) {
          prop.onStart();
        }

        LOGGER.debug("Added child: [{} - {}]", prop.internalId, prop.name);
      });
    }
  }

  public void removeChild(Prop prop) {
    LOGGER.debug("Removing child: [{} - {}]", prop.internalId, prop.name);

    if (props.containsKey(prop.internalId)) {
      Application.getInstance().runOnFrameEnd(() -> {
        props.remove(prop.internalId, prop);
        LOGGER.debug("Removed child: [{} - {}]", prop.internalId, prop.name);
      });
    }
  }

  public void onStart() {
    LOGGER.debug("Starting scene");
    props.forEach((k, v) -> v.onStart());
  }

  public void onUpdate() {
    props.forEach((k, v) -> v.onUpdate());
  }

  public void onRender(Graphics2D g) {
    g.setTransform(camera.transform);

    props.forEach((k, v) -> {
      saveGraphicsState(g);
      v.onRender(g);
      resetGraphicsState(g);
    });
  }

  public void onDispose() {
    LOGGER.debug("Disposing scene");
    props.forEach((k, v) -> v.onDispose());
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
