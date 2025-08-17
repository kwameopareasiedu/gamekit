package dev.gamekit.graphics;

import java.awt.*;

import static dev.gamekit.utils.Math.degToRad;

/** {@link DrawCall} is an instruction to the renderer to render something */
@SuppressWarnings("unchecked")
public abstract class DrawCall<T extends DrawCall<T>> {
  private int rotationPointX;
  private int rotationPointY;
  private double rotationAngle;

  /**
   * Called internally by the engine to modify the provided {@link Graphics2D} object with the
   * call's draw logic
   */
  public final void apply(Graphics2D g) {
    if (rotationAngle != 0) {
      g.translate(rotationPointX, -rotationPointY);
      g.rotate(rotationAngle);
      g.translate(-rotationPointX, rotationPointY);
    }

    setup(g);
    draw(g);
    cleanup(g);

    if (rotationAngle != 0) {
      g.translate(rotationPointX, -rotationPointY);
      g.rotate(-rotationAngle);
      g.translate(-rotationPointX, rotationPointY);
    }
  }

  /**
   * A modifier which applies a rotation {@code deg} about the point {@code (x, y)}.
   * <p>
   * This method returns the object on which it was called for further chaining
   */
  public T withRotation(int x, int y, double deg) {
    rotationPointX = x;
    rotationPointY = y;
    rotationAngle = degToRad(deg);
    return (T) this;
  }

  /** Overridable method called before {@link #draw(Graphics2D)} to perform pre-draw setup */
  protected void setup(Graphics2D g) { /* No-op */ }

  /** Abstract method which should perform the draw call's rendering logic */
  protected abstract void draw(Graphics2D g);

  /** Overridable method called after {@link #draw(Graphics2D)} to perform post-draw cleanup */
  protected void cleanup(Graphics2D g) { /* No-op */ }
}
