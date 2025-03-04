package dev.gamekit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class Resources {
  private static final Logger LOGGER = LogManager.getLogger();

  private Resources() { }

  public static BufferedImage loadImage(String path) {
    try {
      LOGGER.debug("Loading image at {}", path);
      URL assetUrl = Resources.class.getClassLoader().getResource(path);
      URI assetUri = Objects.requireNonNull(assetUrl).toURI();
      File assetFile = new File(assetUri);
      return ImageIO.read(assetFile);
    } catch (URISyntaxException | IOException e) {
      LOGGER.error("Unable to load image at {}", path);
      LOGGER.catching(e);
      return null;
    }
  }
}
