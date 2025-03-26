package dev.gamekit.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Appearance contains a {@link BufferedImage} and a
 * {@link Graphics2D} object which draws to the image
 */
public class Appearance {
  public final BufferedImage image;
  public final Graphics2D graphics;

  public Appearance(BufferedImage image) {
    this.image = image;
    this.graphics = image.createGraphics();
  }
}
