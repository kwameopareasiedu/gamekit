package dev.gamekit.core;

import java.awt.*;

/** Constants store for engine */
public final class Constants {
  public static final long FRAME_INTERVAL_MS = 1000 / 240;
  public static final long DRAW_INTERVAL_MS = 1000 / 60;
  public static final double PIXELS_PER_METER = 128.0;

  public static final Color TRANSPARENT_COLOR = new Color(0x0000000, true);
  public static final Color DEBUG_COLOR = Color.GREEN;
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
