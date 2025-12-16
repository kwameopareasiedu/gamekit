package dev.gamekit.audio;

import dev.gamekit.utils.Vector;

/**
 * {@link AudioListener} is a reference point for {@link AudioClip3D} sources to compute their
 * volume and pan
 * <p>
 * A practical use of {@link AudioListener} would be to set its position to that of say, the
 * player character. This way, {@link AudioClip3D} sources would sound differently relative to
 * their position and direction from the player
 */
public class AudioListener {
  private static final Vector POSITION = new Vector();

  private AudioListener() { }

  /** Returns the position of the {@link AudioListener} */
  public static Vector getPosition() {
    return POSITION;
  }

  /**
   * Sets the position of the {@link AudioListener} which effectively "moves" the listener
   * within the game world
   */
  public static void setPosition(double x, double y) {
    POSITION.set(x, y);
  }

  /**
   * Sets the position of the {@link AudioListener} which effectively "moves" the listener
   * within the game world
   */
  public static void setPosition(Vector pos) {
    POSITION.set(pos);
  }
}
