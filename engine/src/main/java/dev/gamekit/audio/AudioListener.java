package dev.gamekit.audio;

import dev.gamekit.utils.Vector;

/**
 * {@link AudioListener} is a reference point for {@link AudioClip3D} sources to
 * compute its volume and pan
 * <p>
 * A practical use of {@link AudioListener} would be to set its position to
 * that of say, the player character. This way,{@link AudioClip3D} sources
 * would sound differently relative to their position and direction from the
 * player
 */
public class AudioListener {
  private static final Vector POSITION = new Vector();

  private AudioListener() { }

  public static Vector getPosition() { return POSITION; }

  public static void setPosition(double x, double y) {
    POSITION.set(x, y);
  }

  public static void setPosition(Vector pos) {
    POSITION.set(pos);
  }
}
