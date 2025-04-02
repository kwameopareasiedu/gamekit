package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * IO handles resource loading and file access. It also caches resources loaded,
 * prevent multiple disk reads for the same and improving performance.
 */
public class IO {
  private static final Logger LOGGER = LogManager.getLogger();
  private static final Map<String, Object> CACHE = new HashMap<>();

  private IO() { }

  /**
   * Loads and caches an image at the specified path
   * @param path Resources path of image to load
   * @return {@link BufferedImage} The loaded image or {@code null} if an error
   * occurred during loading
   */
  public static BufferedImage loadImageResource(String path) {
    if (CACHE.containsKey(path)) {
      return (BufferedImage) CACHE.get(path);
    }

    try {
      LOGGER.debug("Loading image resource at {}", path);
      InputStream assetStream = IO.class.getClassLoader().getResourceAsStream(path);
      BufferedImage image = ImageIO.read(assetStream);
      CACHE.put(path, image);
      assetStream.close();
      return image;
    } catch (IOException e) {
      LOGGER.error("Unable to load image resource at {}", path);
      LOGGER.catching(e);
      return null;
    }
  }

  /**
   * Loads and caches a font at the specified path
   * @param path Resources path of font file to load
   * @return {@link BufferedImage} The loaded image or {@code null} if an error
   * occurred during loading
   */
  public static Font loadFontResource(String path) {
    if (CACHE.containsKey(path)) {
      return (Font) CACHE.get(path);
    }

    try {
      LOGGER.debug("Loading font resource at {}", path);
      InputStream assetStream = IO.class.getClassLoader().getResourceAsStream(path);
      Font font = Font.createFont(Font.TRUETYPE_FONT, assetStream);
      CACHE.put(path, font);
      assetStream.close();
      return font;
    } catch (FontFormatException | IOException e) {
      LOGGER.error("Unable to load font resource at {}", path);
      LOGGER.catching(e);
      return null;
    }
  }

  /**
   * Opens and returns a {@link BufferedReader} to a resource at the specified
   * path.
   * <p>
   * <strong>Important: Remember to close the reader when done</strong>
   * @param path Resources path of resource file to load
   * @return {@link BufferedImage} The {@link BufferedReader} object to the
   * resource
   */
  public static BufferedReader loadBufferedResource(String path) {
    InputStream fileInputStream = IO.class.getClassLoader().getResourceAsStream(path);
    InputStreamReader inputReader = new InputStreamReader(fileInputStream);
    return new BufferedReader(inputReader);
  }
}
