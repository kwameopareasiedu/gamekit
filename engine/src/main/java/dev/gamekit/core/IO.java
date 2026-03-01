package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * {@link IO} handles resource access
 * <p>
 * {@link IO} caches resources loaded, prevent multiple disk reads for the same and improving performance.
 * <p>
 * It also keeps track of opened {@link InputStream} objects and closes them when the current application exits
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

  /**
   * Opens and returns a stream to a file which may or may not be a resource file.
   * <p>
   * The path can either be an absolute path or relative to the working directory the java command is invoked in
   * <p>
   * Throws a {@link FileNotFoundException} if the path is not a valid file.
   */
  public static InputStream getFileStream(String path) throws FileNotFoundException {
    FileInputStream is = new FileInputStream(path);
    INPUT_STREAMS.add(is);
    return is;
  }

  /** Reads and cache an image <b>resource file</b> */
  public static BufferedImage getResourceImage(String resPath) {
    try {
      if (CACHE.containsKey(resPath)) return (BufferedImage) CACHE.get(resPath);

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

  /** Reads and cache an image <b>resource file</b>, returning a slice of it */
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

  /** Reads and cache a font <b>resource file</b> */
  public static Font getResourceFont(String resPath) {
    try {
      if (CACHE.containsKey(resPath)) return (Font) CACHE.get(resPath);

      LOGGER.debug("Loading resource font at {}", resPath);
      Font font = Font.createFont(Font.TRUETYPE_FONT, getResourceStream(resPath));
      CACHE.put(resPath, font);
      return font;
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
