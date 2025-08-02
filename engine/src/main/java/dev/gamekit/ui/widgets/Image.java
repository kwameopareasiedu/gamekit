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

  public Image(ImageConfig config, BufferedImage image) {
    super(config.image(image));
  }

  public static Image create(ImageConfig config, BufferedImage image) {
    return new Image(config, image);
  }

  public static Image create(BufferedImage image) {
    return new Image(new ImageConfig(), image);
  }

  public static ImageConfig config() {
    return new ImageConfig();
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Image imageWidget &&
      Objects.equals(image, imageWidget.image) &&
      Objects.equals(fit, imageWidget.fit) &&
      Objects.equals(interpolation, imageWidget.interpolation);
  }

  @Override
  protected void performInit() {
    ImageConfig config = (ImageConfig) super.config;

    if (config.image == null)
      throw new IllegalArgumentException("Image image cannot be null");

    this.image = config.image;
    this.fit = coalesce(config.fit, ImageFit.FIT);
    this.interpolation = coalesce(config.interpolation, ImageInterpolation.DEFAULT);

    super.performInit();
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
    boolean clipChanged = false;
    Shape originalClip = null;

    switch (fit) {
      case FIT, CROP -> {
        double widthRatio = absoluteBounds.width / intrinsicBounds.width;
        double heightRatio = absoluteBounds.height / intrinsicBounds.height;

        double scaleRatio = fit == ImageFit.FIT ?
          (intrinsicBounds.width > intrinsicBounds.height ? heightRatio : widthRatio) :
          (intrinsicBounds.width > intrinsicBounds.height ? widthRatio : heightRatio);

        int scaledWidth = (int) (intrinsicBounds.width * scaleRatio);
        int scaledHeight = (int) (intrinsicBounds.height * scaleRatio);
        dx1 = absoluteBounds.x + (absoluteBounds.width - scaledWidth) / 2;
        dy1 = absoluteBounds.y + (absoluteBounds.height - scaledHeight) / 2;
        dx2 = dx1 + scaledWidth;
        dy2 = dy1 + scaledHeight;

        if (fit == ImageFit.CROP) {
          originalClip = g.getClip();

          g.setClip(
            (int) absoluteBounds.x,
            (int) absoluteBounds.y,
            (int) absoluteBounds.width,
            (int) absoluteBounds.height
          );

          clipChanged = true;
        }
      }
      case STRETCH -> {
        dx1 = absoluteBounds.x;
        dy1 = absoluteBounds.y;
        dx2 = dx1 + absoluteBounds.width;
        dy2 = dy1 + absoluteBounds.height;
      }
    }

    ImageInterpolation originalInterpolation = ImageInterpolation.from(g);

    interpolation.apply(g);

    g.drawImage(
      image, (int) dx1, (int) dy1, (int) dx2, (int) dy2,
      0, 0, (int) intrinsicBounds.width, (int) intrinsicBounds.height, null
    );

    originalInterpolation.apply(g);

    if (clipChanged)
      g.setClip(originalClip);
  }

  public static class ImageConfig extends LeafConfig {
    protected BufferedImage image;
    protected ImageFit fit;
    protected ImageInterpolation interpolation;

    private ImageConfig image(BufferedImage image) {
      this.image = image;
      return this;
    }

    public ImageConfig fit(ImageFit fit) {
      this.fit = fit;
      return this;
    }

    public ImageConfig interpolation(ImageInterpolation interpolation) {
      this.interpolation = interpolation;
      return this;
    }
  }
}
