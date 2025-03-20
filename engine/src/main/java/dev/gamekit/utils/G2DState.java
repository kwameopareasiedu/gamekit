package dev.gamekit.utils;

import java.awt.*;

/**
 * G2DState maintains the state of a {@link Graphics2D} object.
 * <p>
 * This state includes the following:
 * <ul>
 *   <li>Foreground {@link Color color}</li>
 *   <li>Background {@link Color color}</li>
 *   <li>{@link Stroke Stroke}</li>
 *   <li>{@link Paint Paint}</li>
 * </ul>
 */
public class G2DState {
  public static final Stroke DEFAULT_STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  public Color bgColor;
  public Stroke stroke;
  public Paint paint;
  public Color color;
  public Font font;
  private boolean preserve;

  /**
   * Copies the state of a {@link Graphics2D} object
   * @param g The Graphics2D object
   */
  public void save(Graphics2D g) {
    bgColor = g.getBackground();
    stroke = g.getStroke();
    paint = g.getPaint();
    color = g.getColor();
    font = g.getFont();
  }

  /**
   * Applies the internal state to a {@link Graphics2D} object
   * @param g The Graphics2D object
   */
  public void apply(Graphics2D g) {
    g.setBackground(bgColor);
    g.setStroke(stroke != null ? stroke : DEFAULT_STROKE);
    g.setPaint(paint);
    g.setColor(color);
    g.setFont(font);
  }

  /**
   * Activates the preserve mode of this state.
   * <p>
   * In preserve mode, calls to {@link #reset()} are ignored
   * @see #discard()
   */
  public void preserve() {
    reset();
    preserve = true;
  }

  /**
   * Deactivates the preserve mode of this state.
   * @see #preserve()
   */
  public void discard() {
    preserve = false;
    reset();
  }

  /**
   * Resets the internal state to null.
   * <p>
   * If preserve mode is activated, this does nothing
   */
  public void reset() {
    if (preserve) return;

    bgColor = null;
    stroke = null;
    paint = null;
    color = null;
    font = null;
  }
}
