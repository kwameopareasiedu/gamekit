package dev.gamekit.settings;

import java.awt.*;

/** Represents the width and height the window at startup */
public enum Resolution implements Setting {
  VGA(640, 480),
  SVGA(800, 600),
  XGA(1024, 768),
  HD(1280, 720),
  WXGA(1366, 768),
  FULL_HD(1920, 1080),
  NATIVE(
    Toolkit.getDefaultToolkit().getScreenSize().width,
    Toolkit.getDefaultToolkit().getScreenSize().height
  );

  public final int width;
  public final int height;

  Resolution(int width, int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public String toString() {
    return String.format(getClass().getName() + "[width=%d,height=%d]", width, height);
  }
}
