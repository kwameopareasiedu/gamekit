package dev.gamekit.utils;

import java.awt.*;

/** Represents the width and height the window at startup */
public record Resolution(int width, int height) {
  public static final Resolution VGA = new Resolution(640, 480);
  public static final Resolution SVGA = new Resolution(800, 600);
  public static final Resolution XGA = new Resolution(1024, 768);
  public static final Resolution HD = new Resolution(1280, 720);
  public static final Resolution WXGA = new Resolution(1366, 768);
  public static final Resolution FULL_HD = new Resolution(1920, 1080);
  public static final Resolution NATIVE = new Resolution(
    Toolkit.getDefaultToolkit().getScreenSize().width,
    Toolkit.getDefaultToolkit().getScreenSize().height
  );

  @Override
  public String toString() {
    return String.format(getClass().getName() + "[width=%d,height=%d]", width, height);
  }
}
