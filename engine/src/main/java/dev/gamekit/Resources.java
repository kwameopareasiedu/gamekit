package dev.gamekit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Resources {
  private static final Logger LOGGER = LogManager.getLogger();
  private static final Map<String, Object> cache = new HashMap<>();

  private Resources() { }

  public static BufferedImage loadImage(String path) {
    if (cache.containsKey(path)) {
      LOGGER.debug("Loaded cached image at {}", path);
      return (BufferedImage) cache.get(path);
    }

    try {
      LOGGER.debug("Loading image at {}", path);
      URL assetUrl = Resources.class.getClassLoader().getResource(path);
      URI assetUri = Objects.requireNonNull(assetUrl).toURI();
      File assetFile = new File(assetUri);
      BufferedImage image = ImageIO.read(assetFile);
      cache.put(path, image);
      return image;
    } catch (URISyntaxException | IOException e) {
      LOGGER.error("Unable to load image at {}", path);
      LOGGER.catching(e);
      return null;
    }
  }

  public static Font loadFont(String path) {
    if (cache.containsKey(path)) {
      LOGGER.debug("Loaded cached font at {}", path);
      return (Font) cache.get(path);
    }

    try{
      URL assetUrl = Resources.class.getClassLoader().getResource(path);
      URI assetUri = Objects.requireNonNull(assetUrl).toURI();
      File assetFile = new File(assetUri);
      return Font.createFont(Font.TRUETYPE_FONT, assetFile);
    } catch (URISyntaxException | FontFormatException | IOException e) {
      LOGGER.error("Unable to load image at {}", path);
      LOGGER.catching(e);
      return null;
    }
  }
}
