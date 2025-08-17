package dev.gamekit.components;

import dev.gamekit.animation.Animation;
import dev.gamekit.settings.ImageInterpolation;

import java.awt.image.BufferedImage;

import static dev.gamekit.utils.Math.cycle;

/**
 * {@link AnimatedSprite} extends {@link Sprite} to render an animated sprite sheet
 * <p>
 * When creating an {@link AnimatedSprite}, the {@code spriteCoordinates} parameter should be of
 * the format {@code [x1, y1, x2, y2, ..., xn, yn]}, where each {@code (x, y)} pair represents
 * the top-left point of the individual sprites in the sprite sheet
 */
public class AnimatedSprite extends Sprite {
  protected final BufferedImage[] sprites;
  protected final Animation animation;

  protected int imageIndex = 0;

  public AnimatedSprite(
    BufferedImage spriteSheet,
    ImageInterpolation interpolation,
    double durationMs,
    int spriteWidth,
    int spriteHeight,
    int... spriteCoordinates
  ) {
    super(spriteSheet, interpolation);

    if (spriteCoordinates.length % 2 != 0)
      throw new IllegalArgumentException("Sprite coordinates must be an even array");

    if (spriteCoordinates.length < 4)
      throw new IllegalArgumentException("At least two (2) pairs of sprite coordinates required");

    sprites = new BufferedImage[spriteCoordinates.length / 2];

    for (int i = 0; i < spriteCoordinates.length; i += 2) {
      int spriteX = spriteCoordinates[i];
      int spriteY = spriteCoordinates[i + 1];
      sprites[i / 2] = spriteSheet.getSubimage(spriteX, spriteY, spriteWidth, spriteHeight);
    }

    animation = new Animation(durationMs / sprites.length, Animation.RepeatMode.RESTART);

    animation.setValueListener(value -> {
      if (value == 1) {
        imageIndex = cycle(imageIndex + 1, 0, sprites.length - 1);
        image = sprites[imageIndex];
      }
    });

    image = sprites[0];
  }

  public AnimatedSprite(
    BufferedImage spriteSheet,
    double durationMs,
    int spriteWidth,
    int spriteHeight,
    int... spriteCoordinates
  ) {
    this(
      spriteSheet,
      ImageInterpolation.DEFAULT,
      durationMs,
      spriteWidth,
      spriteHeight,
      spriteCoordinates
    );
  }

  /** No-op for {@link AnimatedSprite} */
  @Override
  public void setImage(BufferedImage image) { /* No-op */}

  @Override
  protected void start() {
    animation.start();
  }

  @Override
  protected void dispose() {
    animation.end();
  }
}
