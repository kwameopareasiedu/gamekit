package dev.gamekit.components;

import dev.gamekit.core.Component;
import dev.gamekit.core.Entity;
import dev.gamekit.core.Renderer;
import dev.gamekit.settings.ImageInterpolation;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Vector;

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
    Transform transform = entity.findComponent(Transform.class);
    Vector globalPosition = transform.getGlobalPosition();

    Renderer.drawImage(
      image,
      (int) (globalPosition.x + bounds.x),
      (int) (globalPosition.y + bounds.y),
      (int) bounds.width, (int) bounds.height
    ).withRotation(
      (int) (globalPosition.x),
      (int) (globalPosition.y),
      transform.getGlobalRotation()
    ).withInterpolation(interpolation);
  }

  /** Updates this {@link Sprite sprite's} image */
  public void setImage(BufferedImage image) {
    this.image = image;
  }

  /** Updates this {@link Sprite sprite's} interpolation setting */
  public void setInterpolation(ImageInterpolation interpolation) {
    this.interpolation = interpolation;
  }

  /**
   * Sets the height based on the width and given aspect.
   * <p>
   * NB: <i>This should be called after {@link #setSize}</i>
   */
  public void setAspectRatio(double aspect) {
    setSize(bounds.width, bounds.width / aspect);
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
