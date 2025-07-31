package dev.gamekit.core;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Constants store for engine */
public final class Constants {
  public static final long FRAME_TIME_MS = 1000 / 240;
  public static final long RENDER_TIME_MS = 1000 / 90;
  public static final int PIXELS_PER_METER = 128;

  public static final Font DEFAULT_FONT = IO.getResourceFont("font-default.ttf");
  public static final Color TRANSPARENT_COLOR = new Color(0x0000000, true);
  public static final BufferedImage BUTTON_DEFAULT_BG =
    IO.getResourceImage("default-sprites.png", 64, 64, 350, 120);
  public static final BufferedImage BUTTON_HOVER_BG =
    IO.getResourceImage("default-sprites.png", 64, 232, 350, 120);
  public static final BufferedImage BUTTON_PRESS_BG =
    IO.getResourceImage("default-sprites.png", 64, 400, 350, 120);
  public static final BufferedImage PANEL_DEFAULT_BG =
    IO.getResourceImage("default-sprites.png", 470, 64, 120, 120);
  public static final BufferedImage FIELD_DEFAULT_BG =
    IO.getResourceImage("default-sprites.png", 646, 64, 96, 32);
  public static final BufferedImage FIELD_FOCUS_BG =
    IO.getResourceImage("default-sprites.png", 646, 135, 96, 32);
  public static final BufferedImage DEFAULT_CHECKBOX_ICON =
    IO.getResourceImage("default-sprites.png", 646, 206, 32, 32);
  public static final BufferedImage TOGGLED_CHECKBOX_ICON =
    IO.getResourceImage("default-sprites.png", 646, 277, 32, 32);
  public static final BasicStroke DEBUG_STROKE =
    new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  public static final double C1 = 1.70158;
  public static final double C2 = C1 * 1.525;
  public static final double C3 = C1 + 1;
  public static final double C4 = (2 * java.lang.Math.PI) / 3;
  public static final double C5 = (2 * Math.PI) / 4.5;
  public static final double N1 = 7.5625;
  public static final double D1 = 2.75;

  private Constants() { }
}
