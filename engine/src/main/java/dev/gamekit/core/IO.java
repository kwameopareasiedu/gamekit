package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link IO} handles resource access
 * <p>
 * {@code IO} caches resources loaded, prevent multiple disk reads for the same and improving
 * performance.
 * <p>
 * It also keeps track of opened {@link InputStream} objects and closes them when the current
 * application exits
 */
public final class IO {
  private static final Logger LOGGER = LogManager.getLogger(IO.class);
  private static final Map<String, Object> CACHE = new HashMap<>();
  private static final List<InputStream> INPUT_STREAMS = new ArrayList<>();

  private IO() { }

  /** Open and return a stream to a <b>resource file</b> */
  public static InputStream getResourceStream(String resPath) {
    InputStream is = IO.class.getClassLoader().getResourceAsStream(resPath);
    INPUT_STREAMS.add(is);
    return is;
  }

  /** Read and cache an image <b>resource file</b> */
  public static BufferedImage getResourceImage(String resPath) {
    try {
      if (CACHE.containsKey(resPath))
        return (BufferedImage) CACHE.get(resPath);

      LOGGER.debug("Loading resource image at {}", resPath);
      BufferedImage image = ImageIO.read(getResourceStream(resPath));
      CACHE.put(resPath, image);
      return image;
    } catch (IOException e) {
      LOGGER.error("Unable to load resource image at {}", resPath);
      LOGGER.catching(e);
      return null;
    }
  }

  /** Read and cache an image <b>resource file</b>, returning a slice of it */
  public static BufferedImage getResourceImage(String resPath, int x, int y, int w, int h) {
    try {
      return getResourceImage(resPath).getSubimage(x, y, w, h);
    } catch (NullPointerException e) {
      LOGGER.error("Unable to load resource image at {}", resPath);
      LOGGER.catching(e);
      return null;
    } catch (RasterFormatException e) {
      LOGGER.error("Invalid slice bounds for {}", resPath);
      LOGGER.catching(e);
      return null;
    }
  }

  /** Read and cache a font <b>resource file</b> */
  public static Font getResourceFont(String resPath) {
    try {
      if (CACHE.containsKey(resPath))
        return (Font) CACHE.get(resPath);

      LOGGER.debug("Loading resource font at {}", resPath);
      Font font = Font.createFont(Font.TRUETYPE_FONT,
        getResourceStream(resPath));
      CACHE.put(resPath, font);
      return font;
    } catch (FontFormatException | IOException e) {
      LOGGER.error("Unable to load resource font at {}", resPath);
      LOGGER.catching(e);
      return null;
    }
  }

  /** Close any open IO resources */
  static void dispose() {
    try {
      for (var is : INPUT_STREAMS)
        is.close();
    } catch (IOException e) {
      LOGGER.error("Unable to close input stream", e);
    }
  }
}
