package dev.gamekit.settings;

import java.awt.*;

/** Represents the size and height the window at startup */
public record Resolution(int width, int height) implements Setting {

  /** A 640x480 resolution constant */
  public static final Resolution VGA = new Resolution(640, 480);

  /** A 800x600 resolution constant */
  public static final Resolution SVGA = new Resolution(800, 600);

  /** A 1024x768 resolution constant */
  public static final Resolution XGA = new Resolution(1024, 768);

  /** A 1280x720 resolution constant */
  public static final Resolution HD = new Resolution(1280, 720);

  /** A 1366x768 resolution constant */
  public static final Resolution WXGA = new Resolution(1366, 768);

  /** A 1920x1080 resolution constant */
  public static final Resolution FULL_HD = new Resolution(1920, 1080);

  /** Represents the native resolution of the primary monitor */
  public static final Resolution NATIVE = new Resolution(
    Toolkit.getDefaultToolkit().getScreenSize().width,
    Toolkit.getDefaultToolkit().getScreenSize().height
  );

  /** Creates a new resolution from a given size and height */
  public static Resolution create(int width, int height) {
    return new Resolution(width, height);
  }

  @Override
  public String toString() {
    return String.format("%s[size=%d,height=%d]", getClass().getName(), width, height);
  }
}
