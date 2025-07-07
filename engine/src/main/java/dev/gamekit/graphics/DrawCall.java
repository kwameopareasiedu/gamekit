package dev.gamekit.graphics;

import java.awt.*;

import static dev.gamekit.utils.Math.degToRad;

/** {@link DrawCall} instructs the engine to render something on the window */
public abstract class DrawCall {
  private int rotationX;
  private int rotationY;
  private double rotationRadian;

  public final void apply(Graphics2D g) {
    setup(g);
    draw(g);
    cleanup(g);
  }

  public <T extends DrawCall> T withRotation(int x, int y, double deg) {
    rotationX = x;
    rotationY = y;
    rotationRadian = degToRad(deg);
    //noinspection unchecked
    return (T) this;
  }

  protected void setup(Graphics2D g) {
    g.translate(rotationX, -rotationY);
    g.rotate(rotationRadian);
    g.translate(-rotationX, rotationY);
  }

  protected abstract void draw(Graphics2D g);

  protected void cleanup(Graphics2D g) {
    g.translate(rotationX, -rotationY);
    g.rotate(-rotationRadian);
    g.translate(-rotationX, rotationY);
  }
}
