package dev.gamekit.components;

import dev.gamekit.core.Component;
import dev.gamekit.core.Entity;
import dev.gamekit.core.Renderer;
import dev.gamekit.settings.ImageInterpolation;
import dev.gamekit.utils.Bounds;

import java.awt.image.BufferedImage;

/** {@link Sprite} renders a {@link BufferedImage} appearance  for an {@link Entity} */
public class Sprite extends Component {
  protected BufferedImage image;
  protected ImageInterpolation interpolation;
  protected final Bounds bounds;

  public Sprite(BufferedImage image, ImageInterpolation interpolation) {
    if (image == null)
      throw new IllegalArgumentException("Image cannot be null");

    this.image = image;
    this.interpolation = interpolation;
    this.bounds = new Bounds(0, 0, image.getWidth(), image.getHeight());
  }

  public Sprite(BufferedImage image) {
    this(image, ImageInterpolation.DEFAULT);
  }

  @Override
  protected void render() {
    Transform tx = entity.findComponent(Transform.class);
    double posX = tx.getX() + bounds.x;
    double posY = tx.getY() + bounds.y;

    Renderer.drawImage(image,
      (int) posX, (int) posY,
      (int) bounds.width,
      (int) bounds.height
    );
  }

  /** Updates this {@link Sprite sprite's} image */
  public void setImage(BufferedImage image) {
    this.image = image;
  }

  /** Updates this {@link Sprite sprite's} interpolation setting */
  public void setInterpolation(ImageInterpolation interpolation) {
    this.interpolation = interpolation;
  }

  /** Updates the center offset portion of the {@link #bounds} */
  public void setOffset(double offsetX, double offsetY) {
    bounds.setPosition(offsetX, offsetY);
  }

  /** Updates the dimension portion of the {@link #bounds} */
  public void setSize(double width, double height) {
    bounds.setSize(width, height);
  }
}
