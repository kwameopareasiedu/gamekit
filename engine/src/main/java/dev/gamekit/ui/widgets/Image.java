package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.settings.ImageInterpolation;
import dev.gamekit.ui.enums.ImageFit;
import dev.gamekit.utils.Constraints;

import java.awt.*;
import java.awt.image.BufferedImage;

/** A {@link Leaf} which renders a {@link BufferedImage} to the screen */
@WidgetBuilder
public class Image extends Leaf {
  @WidgetBuilderField(themable = false)
  protected BufferedImage image;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.enums.ImageFit.FIT")
  protected ImageFit fit;
  @WidgetBuilderField(fallback = "dev.gamekit.settings.ImageInterpolation.DEFAULT")
  protected ImageInterpolation interpolation;

  public Image(ImageConfig config) {
    super(config);
  }

  public static Image create(ImageConfig.Updater updater) {
    return new Image(Widgets.configureImage(updater));
  }

  public static Image create(BufferedImage image) {
    return Image.create(props -> props.image = image);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicSize.set(image.getWidth(), image.getHeight());

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }

  @Override
  protected void performRender(Graphics2D g) {
    double dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
    boolean clipChanged = false;
    Shape originalClip = null;

    switch (fit) {
      case FIT -> {
        double widthRatio = absoluteBounds.width / computedBounds.width;
        double heightRatio = absoluteBounds.height / computedBounds.height;
        double scale = computedBounds.width > computedBounds.height ? heightRatio : widthRatio;
        int scaledWidth = (int) (computedBounds.width * scale);
        int scaledHeight = (int) (computedBounds.height * scale);

        dx1 = absoluteBounds.x + (absoluteBounds.width - scaledWidth) / 2;
        dy1 = absoluteBounds.y + (absoluteBounds.height - scaledHeight) / 2;
        dx2 = dx1 + scaledWidth;
        dy2 = dy1 + scaledHeight;
      }
      case CROP -> {
        double widthRatio = absoluteBounds.width / intrinsicSize.width;
        double heightRatio = absoluteBounds.height / intrinsicSize.height;
        double scaleRatio = intrinsicSize.width > intrinsicSize.height
          ? widthRatio : heightRatio;
        int scaledWidth = (int) (intrinsicSize.width * scaleRatio);
        int scaledHeight = (int) (intrinsicSize.height * scaleRatio);

        dx1 = absoluteBounds.x + (absoluteBounds.width - scaledWidth) / 2;
        dy1 = absoluteBounds.y + (absoluteBounds.height - scaledHeight) / 2;
        dx2 = dx1 + scaledWidth;
        dy2 = dy1 + scaledHeight;

        originalClip = g.getClip();

        g.setClip(
          (int) absoluteBounds.x,
          (int) absoluteBounds.y,
          (int) absoluteBounds.width,
          (int) absoluteBounds.height
        );

        clipChanged = true;
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
      0, 0, (int) intrinsicSize.width, (int) intrinsicSize.height, null
    );

    originalInterpolation.apply(g);

    if (clipChanged)
      g.setClip(originalClip);
  }
}
