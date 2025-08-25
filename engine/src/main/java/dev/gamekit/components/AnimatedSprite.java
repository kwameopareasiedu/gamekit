package dev.gamekit.components;

import dev.gamekit.animation.Animation;
import dev.gamekit.settings.ImageInterpolation;
import dev.gamekit.utils.ValueCallback;
import dev.gamekit.utils.VoidCallback;

import java.awt.image.BufferedImage;

import static dev.gamekit.utils.Math.cycle;

/** {@link AnimatedSprite} extends {@link Sprite} to render an animated sprite sheet */
public class AnimatedSprite extends Sprite implements ValueCallback<Animation.State> {
  protected VoidCallback completedCallback;
  protected ValueCallback<Animation.State> animationStateCallback;
  protected BufferedImage[] sprites;
  protected int imageIndex = 0;
  protected boolean looping;

  private Animation animation;

  /** Creates a new {@link AnimatedSprite} with the given sprites */
  public AnimatedSprite(BufferedImage[] sprites, double durationMs, boolean looping) {
    super(sprites[0], ImageInterpolation.DEFAULT);
    setSpriteSheet(sprites, durationMs, looping);
  }

  /**
   * Creates a new {@link AnimatedSprite}.
   * <p>
   * The {@code coordinates} should be of the format {@code [x1, y1, x2, y2, ..., xn,yn]}, where
   * each  {@code (x, y)} pair represents the top-left point of the individual sprites in the
   * sprite sheet
   */
  public AnimatedSprite(
    BufferedImage spriteSheet,
    int spriteWidth,
    int spriteHeight,
    int[] coordinates,
    double durationMs,
    boolean looping
  ) {
    super(spriteSheet, ImageInterpolation.DEFAULT);
    setSpriteSheet(spriteSheet, spriteWidth, spriteHeight, coordinates, durationMs, looping);
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
    int[] coordinates,
    double durationMs,
    boolean looping
  ) {
    if (coordinates.length % 2 != 0)
      throw new IllegalArgumentException("Sprite coordinates must be an even array");

    if (coordinates.length < 4)
      throw new IllegalArgumentException("At least two (2) pairs of sprite coordinates required");

    BufferedImage[] sprites = new BufferedImage[coordinates.length / 2];

    for (int i = 0; i < coordinates.length; i += 2) {
      int spriteX = coordinates[i];
      int spriteY = coordinates[i + 1];
      sprites[i / 2] = spriteSheet.getSubimage(spriteX, spriteY, spriteWidth, spriteHeight);
    }

    setSpriteSheet(sprites, durationMs, looping);
  }

  /** Updates the generated sprites directly */
  public void setSpriteSheet(BufferedImage[] sprites, double durationMs, boolean looping) {
    if (sprites == null)
      throw new IllegalArgumentException("Sprites cannot be null");

    if (sprites.length == 0)
      throw new IllegalArgumentException("At least one sprite is required");

    this.sprites = sprites;
    this.looping = looping;
    this.image = sprites[0];
    this.imageIndex = 0;

    Animation.State prevAnimationState = null;

    if (animation != null) {
      prevAnimationState = animation.getState();
      animation.end();
    }

    animation = new Animation(
      durationMs / sprites.length,
      Animation.RepeatMode.RESTART
    );

    animation.setStateListener(this);

    if (prevAnimationState == Animation.State.RUNNING)
      animation.start();
  }

  /** Sets a callback listener which is notified when the internal animation state ends */
  public void setAnimationStateCallback(ValueCallback<Animation.State> animationStateCallback) {
    this.animationStateCallback = animationStateCallback;
  }

  /** Sets a callback listener which is notified when the <b>non-looping</b> animation ends */
  public void setCompletedCallback(VoidCallback completedCallback) {
    this.completedCallback = completedCallback;
  }

  @Override
  public void run(Animation.State state) {
    if (state == Animation.State.RESTARTED) {
      if (imageIndex == this.sprites.length - 1 && !this.looping) {
        if (completedCallback != null)
          completedCallback.run();

        animation.stop();
        return;
      }

      imageIndex = cycle(imageIndex + 1, 0, this.sprites.length - 1);
      image = this.sprites[imageIndex];
    }

    if (animationStateCallback != null)
      animationStateCallback.run(state);
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
