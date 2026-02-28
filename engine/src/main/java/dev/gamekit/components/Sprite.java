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
  protected final Bounds bounds;

  protected BufferedImage image;
  protected ImageInterpolation interpolation;
  protected boolean flippedX = false;
  protected boolean flippedY = false;
  protected double scaleX = 1;
  protected double scaleY = 1;

  private double aspectRatio;
  private double opacity = 1;

  public Sprite(BufferedImage image, ImageInterpolation interpolation) {
    if (image == null) throw new IllegalArgumentException("Image cannot be null");

    this.image = image;
    this.interpolation = interpolation;
    this.bounds = new Bounds(0, 0, image.getWidth(), image.getHeight());
    this.aspectRatio = (double) image.getWidth() / (double) image.getHeight();
  }

  public Sprite(BufferedImage image) {
    this(image, ImageInterpolation.DEFAULT);
  }

  /** Updates this {@link Sprite sprite's} image */
  public void setImage(BufferedImage image) {
    this.image = image;
    this.aspectRatio = (double) image.getWidth() / (double) image.getHeight();
  }

  /** Sets the image interpolation setting */
  public void setInterpolation(ImageInterpolation interpolation) {
    this.interpolation = interpolation;
  }

  /** Sets the image opacity */
  public void setOpacity(double opacity) {
    this.opacity = opacity;
  }

  /** Sets the width and computes the height based on the aspect ratio */
  public void setWidth(double width) {
    bounds.setSize(width, width / aspectRatio);
  }

  /** Sets the height and computes the width based on the aspect ratio */
  public void setHeight(double height) {
    bounds.setSize(height * aspectRatio, height);
  }

  /** Sets the width and height, not respecting the aspect ratio */
  public void setSize(double width, double height) {
    bounds.setSize(width, height);
  }

  /** Sets the center offset of the {@link #bounds} */
  public void setOffset(double centerX, double centerY) {
    bounds.setPosition(centerX, centerY);
  }

  /** Sets the render scale of this {@link Sprite} */
  public void setScale(double scaleX, double scaleY) {
    this.scaleX = scaleX;
    this.scaleY = scaleY;
  }

  /** Sets whether the sprite is flipped horizontally */
  public void flipX(boolean flipped) {
    flippedX = flipped;
  }

  /** Sets whether the sprite is flipped vertically */
  public void flipY(boolean flipped) {
    flippedY = flipped;
  }

  @Override
  protected void render() {
    Transform transform = entity.findComponent(Transform.class);
    Vector globalPosition = transform.getGlobalPosition();
    double signedWidth = !flippedX ? bounds.width : -bounds.width;
    double signedHeight = !flippedY ? bounds.height : -bounds.height;

    Sprite parentEntitySprite = entity.getParent().findComponent(Sprite.class);
    double parentSpriteOpacity = parentEntitySprite != null ? parentEntitySprite.opacity : 1;
    double parentSpriteScaleX = parentEntitySprite != null ? parentEntitySprite.scaleX : 1;
    double parentSpriteScaleY = parentEntitySprite != null ? parentEntitySprite.scaleY : 1;
    double resolvedOpacity = parentSpriteOpacity * opacity;
    double resolvedScaleX = parentSpriteScaleX * scaleX;
    double resolvedScaleY = parentSpriteScaleY * scaleY;

    Renderer.drawImage(
        image,
        (int) (globalPosition.x + bounds.x),
        (int) (globalPosition.y + bounds.y),
        (int) (signedWidth * resolvedScaleX),
        (int) (signedHeight * resolvedScaleY)
      )
      .withInterpolation(interpolation)
      .withOpacity(resolvedOpacity)
      .withRotation(
        (int) (globalPosition.x),
        (int) (globalPosition.y),
        transform.getGlobalRotation()
      );
  }
}
