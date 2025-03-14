package dev.gamekit.core;

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

/**
 * Static class responsible for resource loading and file output.
 * <p>
 * IO caches resources loaded, prevent multiple disk reads for the same and improving performance.
 */
public class IO {
  private static final Logger LOGGER = LogManager.getLogger();
  private static final Map<String, Object> cache = new HashMap<>();

  private IO() { }

  /**
   * Loads and caches an image at the specified path
   * @param path Resources path of image to load
   * @return {@link BufferedImage} The loaded image or {@code null} if an error occurred during loading
   */
  public static BufferedImage loadImage(String path) {
    if (cache.containsKey(path)) {
      LOGGER.debug("Loaded cached image at {}", path);
      return (BufferedImage) cache.get(path);
    }

    try {
      LOGGER.debug("Loading image at {}", path);
      URL assetUrl = IO.class.getClassLoader().getResource(path);
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

  /**
   * Loads and caches a font at the specified path
   * @param path Resources path of font file to load
   * @return {@link BufferedImage} The loaded image or {@code null} if an error occurred during loading
   */
  public static Font loadFont(String path) {
    if (cache.containsKey(path)) {
      LOGGER.debug("Loaded cached font at {}", path);
      return (Font) cache.get(path);
    }

    try {
      URL assetUrl = IO.class.getClassLoader().getResource(path);
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
