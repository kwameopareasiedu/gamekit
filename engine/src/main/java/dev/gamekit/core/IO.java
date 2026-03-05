package dev.gamekit.core;

import dev.gamekit.utils.EngineImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * {@link IO} handles resource access
 * <p>
 * It also keeps track of opened {@link InputStream} objects and closes them when the current application exits
 */
public final class IO {
  private static final Logger LOGGER = LogManager.getLogger(IO.class);
  private static final List<InputStream> INPUT_STREAMS = new ArrayList<>();

  private IO() { }

  /** Returns a stream to a <b>resource file</b> */
  public static InputStream getStream(String resPath) {
    InputStream is = IO.class.getClassLoader().getResourceAsStream(resPath);
    INPUT_STREAMS.add(is);
    return is;
  }

  /** Returns an image <b>resource file</b> */
  public static EngineImage getImage(String resPath) {
    try {
      LOGGER.debug("Loading resource image at {}", resPath);
      return new EngineImage(ImageIO.read(getStream(resPath)));
    } catch (IOException e) {
      LOGGER.error("Unable to load resource image at {}", resPath);
      LOGGER.catching(e);
      return null;
    }
  }

  /** Returns a slice of an image <b>resource file</b> */
  public static EngineImage getImageSlice(String resPath, int sliceX, int sliceY, int sliceWidth, int sliceHeight) {
    try {
      LOGGER.debug("Loading resource image at {}", resPath);
      BufferedImage img = ImageIO.read(getStream(resPath));

      return new EngineImage(img, sliceX, sliceY, sliceWidth, sliceHeight, 0, 0, 0, 0);
    } catch (IOException e) {
      LOGGER.error("Unable to load resource image at {}", resPath);
      LOGGER.catching(e);
      return null;
    } catch (RasterFormatException e) {
      LOGGER.error("Invalid slice bounds for {}", resPath);
      LOGGER.catching(e);
      return null;
    }
  }

  /**
   * Returns an image <b>resource file</b> with
   * <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-patch</a> insets
   */
  public static EngineImage getImageWithInsets(
    String resPath, int topInset, int rightInset, int bottomInset, int leftInset
  ) {
    try {
      LOGGER.debug("Loading resource image at {}", resPath);
      BufferedImage img = ImageIO.read(getStream(resPath));

      return new EngineImage(img, topInset, rightInset, bottomInset, leftInset);
    } catch (IOException e) {
      LOGGER.error("Unable to load resource image at {}", resPath);
      LOGGER.catching(e);
      return null;
    }
  }

  /**
   * Returns a slice of an image <b>resource file</b>, with
   * <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-patch</a> insets
   */
  public static EngineImage getImageSliceWithInsets(
    String resPath,
    int sliceX, int sliceY, int sliceWidth, int sliceHeight,
    int topInset, int rightInset, int bottomInset, int leftInset
  ) {
    try {
      LOGGER.debug("Loading resource image at {}", resPath);
      BufferedImage img = ImageIO.read(getStream(resPath));

      return new EngineImage(
        img, sliceX, sliceY, sliceWidth, sliceHeight, topInset, rightInset, bottomInset, leftInset
      );
    } catch (IOException e) {
      LOGGER.error("Unable to load resource image at {}", resPath);
      LOGGER.catching(e);
      return null;
    } catch (RasterFormatException e) {
      LOGGER.error("Invalid slice bounds for {}", resPath);
      LOGGER.catching(e);
      return null;
    }
  }

  /** Returns a font <b>resource file</b> */
  public static Font getFont(String resPath) {
    try {
      LOGGER.debug("Loading resource font at {}", resPath);
      return Font.createFont(Font.TRUETYPE_FONT, getStream(resPath));
    } catch (FontFormatException | IOException e) {
      LOGGER.error("Unable to load resource font at {}", resPath);
      LOGGER.catching(e);
      return null;
    }
  }

  /**
   * Writes data to the file at the specified path.
   * <p>
   * The path can either be an absolute path or relative to the working directory the java command is invoked in
   */
  public static boolean writeFile(String path, String content, boolean overwrite) {
    File file = new File(path);
    File parent = file.getParentFile();

    try {
      if (!file.exists()) {
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
          LOGGER.error("Unable to create directory hierarchy: {}", path);
          return false;
        }

        if (!file.createNewFile()) {
          LOGGER.error("Unable to create file: {}", path);
          return false;
        }
      } else if (!overwrite) {
        LOGGER.error("Failed to overwrite file. Disable overwrite protection first");
        return false;
      }

      try (FileWriter writer = new FileWriter(file)) {
        writer.write(content);
        return true;
      }
    } catch (IOException e) {
      LOGGER.error("Failed to write to file", e);
      return false;
    }
  }

  /** Saves a string value in the application's {@link Preferences} node */
  public static void saveString(String key, String value) {
    getApplicationPreferenceNode().put(key, value);
  }

  /** Saves an integer value in the application's {@link Preferences} node */
  public static void saveInteger(String key, int value) {
    getApplicationPreferenceNode().putInt(key, value);
  }

  /** Saves a boolean value in the application's {@link Preferences} node */
  public static void saveBoolean(String key, boolean value) {
    getApplicationPreferenceNode().putBoolean(key, value);
  }

  /** Saves a double value in the application's {@link Preferences} node */
  public static void saveDouble(String key, double value) {
    getApplicationPreferenceNode().putDouble(key, value);
  }

  /** Saves a float value in the application's {@link Preferences} node */
  public static void saveFloat(String key, float value) {
    getApplicationPreferenceNode().putFloat(key, value);
  }

  /** Saves a long value in the application's {@link Preferences} node */
  public static void saveLong(String key, long value) {
    getApplicationPreferenceNode().putLong(key, value);
  }

  /** Saves a byte array value in the application's {@link Preferences} node */
  public static void saveBytes(String key, byte[] value) {
    getApplicationPreferenceNode().putByteArray(key, value);
  }

  /**
   * Returns a string value from the application's {@link Preferences} node associated with the specified key.
   * <p>
   * If no value is found, the given default value is returned instead.
   */
  public static String getSavedString(String key, String defaultValue) {
    return getApplicationPreferenceNode().get(key, defaultValue);
  }

  /**
   * Returns an integer value from the application's {@link Preferences} node associated with the specified key.
   * <p>
   * If no value is found, the given default value is returned instead.
   */
  public static int getSavedInteger(String key, int defaultValue) {
    return getApplicationPreferenceNode().getInt(key, defaultValue);
  }

  /**
   * Returns a boolean value from the application's {@link Preferences} node associated with thespecified key.
   * <p>
   * If no value is found, the given default value is returned instead.
   */
  public static boolean getSavedBoolean(String key, boolean defaultValue) {
    return getApplicationPreferenceNode().getBoolean(key, defaultValue);
  }

  /**
   * Returns a double value from the application's {@link Preferences} node associated with thespecified key.
   * <p>
   * If no value is found, the given default value is returned instead.
   */
  public static double getSavedDouble(String key, double defaultValue) {
    return getApplicationPreferenceNode().getDouble(key, defaultValue);
  }

  /**
   * Returns a float value from the application's {@link Preferences} node associated with the specified key.
   * <p>
   * If no value is found, the given default value is returned instead.
   */
  public static float getSavedFloat(String key, float defaultValue) {
    return getApplicationPreferenceNode().getFloat(key, defaultValue);
  }

  /**
   * Returns a long value from the application's {@link Preferences} node associated with the specified key.
   * <p>
   * If no value is found, the given default value is returned instead.
   */
  public static long getSavedLong(String key, long defaultValue) {
    return getApplicationPreferenceNode().getLong(key, defaultValue);
  }

  /**
   * Returns a byte array value from the application's {@link Preferences} node associated with the specified key.
   * <p>
   * If no value is found, the given default value is returned instead.
   */
  public static byte[] getSavedBytes(String key, byte[] defaultValue) {
    return getApplicationPreferenceNode().getByteArray(key, defaultValue);
  }

  /** Clears the saved value of a key from the application's {@link Preferences} node */
  public static void clearPreference(String key) {
    getApplicationPreferenceNode().remove(key);
  }

  /** Clears all saved values in the application's {@link Preferences} node */
  public static boolean clearPreferences() {
    try {
      getApplicationPreferenceNode().clear();
      return true;
    } catch (BackingStoreException e) {
      LOGGER.error("Unable to clear preferences", e);
      return false;
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

  /**
   * Returns the {@link Preferences} node associated with the current {@link Application} instance.
   * <p>
   * The preference path is computed from the application's title and hence is always the same for a title string.
   */
  private static Preferences getApplicationPreferenceNode() {
    if (Application.getInstance() == null) throw new IllegalStateException("No instance of Application running");

    String applicationTitle = Application.getInstance().getSettings().title;
    String preferencePath = "_" + applicationTitle.trim().replaceAll("\\W", "_");
    return Preferences.userRoot().node(preferencePath);
  }
}
