package dev.gamekit.audio;

import dev.gamekit.utils.Vector;

/**
 * {@link AudioListener} is a reference point for spatial {@link AudioClip} to compute their perceived volume and pan
 * <p>
 * A practical use of {@link AudioListener} would be to set its position to that of say, the player character.
 * This way, spatial {@link AudioClip} would sound differently relative to their position and direction from the player
 */
public class AudioListener {
  public static final Vector POSITION = new Vector();

  private AudioListener() { }
}
