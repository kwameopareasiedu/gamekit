package dev.gamekit.components;

import dev.gamekit.animation.Animation;
import dev.gamekit.settings.ImageInterpolation;
import dev.gamekit.utils.VoidCallback;

import java.awt.image.BufferedImage;

import static dev.gamekit.utils.Math.cycle;

/** {@link AnimatedSprite} extends {@link Sprite} to render an animated sprite sheet */
public class AnimatedSprite extends Sprite {
  protected final Animation animation;

  protected VoidCallback completedCallback;
  protected BufferedImage[] sprites;
  protected int imageIndex = 0;

  /**
   * Creates a new {@link AnimatedSprite}.
   * <p>
   * The {@code coordinates} should be of the format {@code [x1, y1, x2, y2, ..., xn,yn]}, where
   * each  {@code (x, y)} pair represents the top-left point of the individual sprites in the
   * sprite sheet
   */
  public AnimatedSprite(
    BufferedImage spriteSheet,
    double durationMs,
    int spriteWidth,
    int spriteHeight,
    int[] coordinates,
    boolean loop
  ) {
    super(spriteSheet, ImageInterpolation.DEFAULT);

    setSpriteSheet(spriteSheet, spriteWidth, spriteHeight, coordinates);

    animation = new Animation(durationMs / sprites.length, Animation.RepeatMode.RESTART);

    animation.setStateListener(state -> {
      if (state == Animation.State.RESTARTED) {
        if (imageIndex == sprites.length - 1 && !loop) {
          if (completedCallback != null)
            completedCallback.run();

          animation.stop();
          return;
        }

        imageIndex = cycle(imageIndex + 1, 0, sprites.length - 1);
        image = sprites[imageIndex];
      }
    });

    image = sprites[0];
  }

  /**
   * Updates the sprite sheet and computes the sprites based on the given {@code spriteWidth},
   * {@code spriteHeight} and {@code coordinates}. Each coordinates pair represents the top-left
   * corner of a sprite in the sprite sheet
   */
  public void setSpriteSheet(
    BufferedImage spriteSheet,
    int spriteWidth,
    int spriteHeight,
    int... coordinates
  ) {
    if (coordinates.length % 2 != 0)
      throw new IllegalArgumentException("Sprite coordinates must be an even array");

    if (coordinates.length < 4)
      throw new IllegalArgumentException("At least two (2) pairs of sprite coordinates required");

    sprites = new BufferedImage[coordinates.length / 2];

    for (int i = 0; i < coordinates.length; i += 2) {
      int spriteX = coordinates[i];
      int spriteY = coordinates[i + 1];
      sprites[i / 2] = spriteSheet.getSubimage(spriteX, spriteY, spriteWidth, spriteHeight);
    }
  }

  /** Sets a callback listener which is notified when the <b>non-looping</b> animation ends */
  public void setCompletedCallback(VoidCallback completedCallback) {
    this.completedCallback = completedCallback;
  }

  @Override
  protected void start() {
    animation.start();
  }

  @Override
  protected void dispose() {
    animation.end();
  }
}
