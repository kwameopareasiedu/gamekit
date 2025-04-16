package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;
import dev.gamekit.ui.enums.ImageFit;

import java.awt.image.BufferedImage;

public class ImageParam extends SingleChildParentParam {
  public static Param<ImageParam> image(BufferedImage value) {
    return new Param<>("image", value);
  }

  public static Param<ImageParam> imageFit(ImageFit value) {
    return new Param<>("imageFit", value);
  }
}
