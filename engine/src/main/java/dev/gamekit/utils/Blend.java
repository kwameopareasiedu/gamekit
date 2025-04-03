package dev.gamekit.utils;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.Math;

/**
 * An abstract composite which blends source and destination colors in a way
 * to produce the final pixel color.
 * <p>
 * Subclasses must implement the {@link #blend(int, int)} method to determine
 * how colors are blended together.
 */
public abstract class Blend implements Composite, CompositeContext {
  /**
   * A {@link Blend} which multiplies the source and destination
   * colors together. If the destination is transparent, it is unaffected
   */
  public static final Blend MULTIPLY = new Blend() {
    @Override
    protected int blend(int x, int y) {
      int ya = (y >> 24) & 0xFF;
      int yr = (y >> 16) & 0xFF;
      int yg = (y >> 8) & 0xFF;
      int yb = (y) & 0xFF;

      if (ya == 0) return y;

      int xa = (x >> 24) & 0xFF;
      int xr = (x >> 16) & 0xFF;
      int xg = (x >> 8) & 0xFF;
      int xb = (x) & 0xFF;

      int r = (int) (yr + (xr - yr) * (xa / 255.0));
      int g = (int) (yg + (xg - yg) * (xa / 255.0));
      int b = (int) (yb + (xb - yb) * (xa / 255.0));

      return ya << 24 | r << 16 | g << 8 | b;
    }
  };

  /**
   * Determines how two colors x and y are blended together. It returns the
   * final color for the pixel
   */
  protected abstract int blend(int x, int y);

  @Override
  public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
    int width = java.lang.Math.min(src.getWidth(), dstIn.getWidth());
    int height = Math.min(src.getHeight(), dstIn.getHeight());
    int x, y;
    int[] srcPixels = new int[width];
    int[] dstPixels = new int[width];

    for (y = 0; y < height; y++) {
      src.getDataElements(0, y, width, 1, srcPixels);
      dstIn.getDataElements(0, y, width, 1, dstPixels);

      for (x = 0; x < width; x++) {
        dstPixels[x] = blend(srcPixels[x], dstPixels[x]);
      }

      dstOut.setDataElements(0, y, width, 1, dstPixels);
    }
  }

  @Override
  public CompositeContext createContext(
    ColorModel srcColorModel,
    ColorModel dstColorModel,
    RenderingHints hints) {
    return this;
  }

  @Override
  public void dispose() { }
}