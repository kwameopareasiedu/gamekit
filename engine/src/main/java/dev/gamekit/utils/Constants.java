package dev.gamekit.utils;

import dev.gamekit.core.IO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.Math;

/** Holds constants accessible across the entire engine */
public final class Constants {
  public static final Font DEFAULT_FONT = IO.getResourceFont("dogicapixel.ttf");
  public static final Color TRANSPARENT_COLOR = new Color(0x0000000, true);
  public static final BufferedImage DEFAULT_BUTTON_BG =
    IO.getResourceImage("btn-default.png");
  public static final BufferedImage HOVER_BUTTON_BG =
    IO.getResourceImage("btn-hover.png");
  public static final BufferedImage PRESSED_BUTTON_BG =
    IO.getResourceImage("btn-press.png");
  public static final BufferedImage DEFAULT_PANEL_BG =
    IO.getResourceImage("panel-default.png");

  // Animation curve related constants - Start
  public static final double C1 = 1.70158;
  public static final double C2 = C1 * 1.525;
  public static final double C3 = C1 + 1;
  public static final double C4 = (2 * java.lang.Math.PI) / 3;
  public static final double C5 = (2 * Math.PI) / 4.5;
  public static final double N1 = 7.5625;
  public static final double D1 = 2.75;
  // Animation curve related constants - End

  private Constants() { }
}
