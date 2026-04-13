package dev.gamekit.ui.widgets;

import dev.gamekit.animation.Animation;
import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.core.IO;
import dev.gamekit.settings.ImageInterpolation;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Picture;

import java.awt.*;
import java.awt.image.BufferedImage;

/** A {@link Leaf} which renders a loading indicator */
@WidgetBuilder
public class Spinner extends Leaf {
  private static final Picture LOADING_BG = IO.getImage("default-loader.png");
  private static final int BG_SLICE_WIDTH = 200;
  private static final int BG_SLICE_HEIGHT = 200;
  private static final ImageInterpolation INTERPOLATION = ImageInterpolation.BICUBIC;

  private BufferedImage[] bgSlices;
  private Animation timerAnimation;
  private int index = 0;

  public Spinner() {
    super(new SpinnerConfig());
  }

  public static Spinner create() {
    return new Spinner();
  }

  @Override
  protected void performInit() {
    bgSlices = new BufferedImage[LOADING_BG.getWidth() / BG_SLICE_WIDTH];

    for (int i = 0; i < bgSlices.length; i++)
      bgSlices[i] = LOADING_BG.getSubimage(i * BG_SLICE_WIDTH, 0, BG_SLICE_WIDTH, BG_SLICE_HEIGHT);

    if (timerAnimation != null)
      timerAnimation.end();

    timerAnimation = new Animation(25, Animation.RepeatMode.RESTART);
    timerAnimation.setStateListener(state -> {
      if (state == Animation.State.RESTARTED) {
        index = (index + 1) % bgSlices.length;
        host.triggerRender();
      }
    });

    super.performInit();
  }

  @Override
  protected void performMount() {
    super.performMount();

    timerAnimation.start();
  }

  @Override
  protected void performLayout(Constraints constraints) {
    intrinsicSize.set(BG_SLICE_WIDTH, BG_SLICE_HEIGHT);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );
  }

  @Override
  protected void performRender(Graphics2D g) {
    ImageInterpolation originalInterpolation = ImageInterpolation.from(g);

    INTERPOLATION.apply(g);

    g.drawImage(
      bgSlices[index],
      (int) absoluteBounds.x,
      (int) absoluteBounds.y,
      (int) absoluteBounds.width,
      (int) absoluteBounds.height,
      null
    );

    originalInterpolation.apply(g);
  }

  @Override
  protected void performUnmount() {
    super.performUnmount();

    if (timerAnimation != null)
      timerAnimation.end();
  }
}
