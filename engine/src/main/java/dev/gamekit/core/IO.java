package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
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
  private static final Map<String, Object> CACHE = new HashMap<>();

  private IO() { }

  /**
   * Loads and caches an image at the specified path
   * @param path Resources path of image to load
   * @return {@link BufferedImage} The loaded image or {@code null} if an error occurred during loading
   */
  public static BufferedImage loadImageResource(String path) {
    if (CACHE.containsKey(path)) {
      LOGGER.debug("Loaded cached image resource at {}", path);
      return (BufferedImage) CACHE.get(path);
    }

    try {
      LOGGER.debug("Loading image resource at {}", path);
      URL assetUrl = IO.class.getClassLoader().getResource(path);
      URI assetUri = Objects.requireNonNull(assetUrl).toURI();
      File assetFile = new File(assetUri);
      BufferedImage image = ImageIO.read(assetFile);
      CACHE.put(path, image);
      return image;
    } catch (URISyntaxException | IOException e) {
      LOGGER.error("Unable to load image resource at {}", path);
      LOGGER.catching(e);
      return null;
    }
  }

  /**
   * Loads and caches a font at the specified path
   * @param path Resources path of font file to load
   * @return {@link BufferedImage} The loaded image or {@code null} if an error occurred during loading
   */
  public static Font loadFontResource(String path) {
    if (CACHE.containsKey(path)) {
      LOGGER.debug("Loaded cached font resource at {}", path);
      return (Font) CACHE.get(path);
    }

    try {
      LOGGER.debug("Loading font resource at {}", path);
      URL assetUrl = IO.class.getClassLoader().getResource(path);
      URI assetUri = Objects.requireNonNull(assetUrl).toURI();
      File assetFile = new File(assetUri);
      return Font.createFont(Font.TRUETYPE_FONT, assetFile);
    } catch (URISyntaxException | FontFormatException | IOException e) {
      LOGGER.error("Unable to load font resource at {}", path);
      LOGGER.catching(e);
      return null;
    }
  }

  /**
   * Opens and returns a {@link BufferedReader} to a resource at the specified path.
   * <p>
   * <strong>Important: Remember to close the reader when done</strong>
   * @param path Resources path of resource file to load
   * @return {@link BufferedImage} The {@link BufferedReader} object to the resource
   */
  public static BufferedReader loadBufferedResource(String path) {
    try {
      URL fileUrl = IO.class.getClassLoader().getResource(path);
      URI fileUri = Objects.requireNonNull(fileUrl).toURI();
      File file = new File(fileUri);
      return Files.newBufferedReader(file.toPath());
    } catch (URISyntaxException | IOException e) {
      LOGGER.error("Unable to load buffered resource at {}", path);
      LOGGER.catching(e);
      return null;
    }
  }
}
