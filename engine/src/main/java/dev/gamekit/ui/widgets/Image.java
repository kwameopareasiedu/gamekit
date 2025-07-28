package dev.gamekit.ui.widgets;

import dev.gamekit.settings.ImageInterpolation;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.enums.ImageFit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link Leaf} which renders a {@link BufferedImage} to the screen */
public class Image extends Leaf {
  protected BufferedImage image;
  protected ImageFit fit;
  protected ImageInterpolation interpolation;

  private Config config;

  public Image(Config config, BufferedImage image) {
    if (image == null)
      throw new IllegalArgumentException("Image cannot be null");

    this.config = config;
    this.image = image;
  }

  public static Image create(Config config, BufferedImage image) {
    return new Image(config, image);
  }

  public static Image create(BufferedImage image) {
    return new Image(new Config(), image);
  }

  public static Config config() {
    return new Config();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    if (widget instanceof Image imageWidget) {
      return Objects.equals(image, imageWidget.image)
        && Objects.equals(fit, imageWidget.fit);
    }

    return false;
  }

  @Override
  protected void performUpdateState(Widget widget) {
    this.config = ((Image) widget).config;
    this.fit = ((Image) widget).fit;
    this.interpolation = ((Image) widget).interpolation;
  }

  @Override
  protected void performMounted() {
    super.performMounted();

    this.fit = coalesce(config.fit, ImageFit.FIT);
    this.interpolation = coalesce(config.interpolation, ImageInterpolation.DEFAULT);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicBounds.setSize(image.getWidth(), image.getHeight());

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicBounds.width),
      constraints.constrainHeight(intrinsicBounds.height)
    );
  }

  @Override
  protected void performRender(Graphics2D g) {
    double dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

    switch (fit) {
      case FIT, CROP -> {
        double widthRatio = absoluteBounds.width / intrinsicBounds.width;
        double heightRatio = absoluteBounds.height / intrinsicBounds.height;

        double scaleRatio = fit == ImageFit.FIT ?
          intrinsicBounds.width > intrinsicBounds.height ? widthRatio : heightRatio :
          intrinsicBounds.width <= intrinsicBounds.height ? widthRatio : heightRatio;

        int scaledWidth = (int) (intrinsicBounds.width * scaleRatio);
        int scaledHeight = (int) (intrinsicBounds.height * scaleRatio);
        dx1 = absoluteBounds.x + (absoluteBounds.width - scaledWidth) / 2;
        dy1 = absoluteBounds.y + (absoluteBounds.height - scaledHeight) / 2;
        dx2 = dx1 + scaledWidth;
        dy2 = dy1 + scaledHeight;
      }
      case STRETCH -> {
        dx2 = absoluteBounds.width;
        dy2 = absoluteBounds.height;
      }
    }

    ImageInterpolation originalInterpolation = ImageInterpolation.from(g);

    if (interpolation != ImageInterpolation.DEFAULT)
      interpolation.apply(g);

    g.drawImage(
      image, (int) dx1, (int) dy1, (int) dx2, (int) dy2,
      0, 0, (int) intrinsicBounds.width, (int) intrinsicBounds.height, null
    );

    originalInterpolation.apply(g);
  }

  public static class Config {
    ImageFit fit;
    ImageInterpolation interpolation;

    Config() { }

    public Config fit(ImageFit fit) {
      this.fit = fit;
      return this;
    }

    public Config interpolation(ImageInterpolation interpolation) {
      this.interpolation = interpolation;
      return this;
    }
  }
}
