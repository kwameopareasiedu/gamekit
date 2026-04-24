package dev.gamekit.audio;

import dev.gamekit.core.Audio;
import dev.gamekit.utils.GMath;

/**
 * {@link AudioClip} is abstract class which stores audio data.
 * <p>
 * {@link AudioClip} is not instantiated directly, but by using methods in the {@link Audio} utility
 */
public class AudioClip {
  private final byte[] channelLBytes;
  private final byte[] channelRBytes;
  private boolean playing;
  private boolean looping;
  private int position;

  public AudioClip(byte[] channelLBytes, byte[] channelRBytes, boolean looping) {
    this.channelLBytes = channelLBytes;
    this.channelRBytes = channelRBytes;
    this.playing = false;
    this.looping = looping;
    this.position = 0;
  }

  /** Returns {@code true} if this clip is currently playing */
  public boolean isPlaying() {
    return playing;
  }

  /** Sets the playing status of this clip */
  public void setPlaying(boolean playing) {
    this.playing = playing;
  }

  /** Returns {@code true} if this clip is set to loop */
  public boolean isLooping() {
    return looping;
  }

  /** Sets the looping status of this clip */
  public void setLooping(boolean looping) {
    this.looping = looping;
  }

  /** Returns the byte index of this clip */
  public int getPosition() {
    return position;
  }

  /** Sets the position of this clip */
  public void setPosition(int position) {
    this.position = GMath.clamp(position, 0, channelLBytes.length - 1);
  }

  public int getRemainingBytes() {
    return channelLBytes.length - position;
  }

  public void readNextTwoBytes(int[] out) {
    // Little endian byte ordering
    out[0] = (channelLBytes[position + 1] << 8) | (channelLBytes[position] & 0xFF);
    out[1] = (channelRBytes[position + 1] << 8) | (channelRBytes[position] & 0xFF);

    position += 2;

    if (position > channelLBytes.length) {
      if (looping) position = 0;
      else playing = false;
    }
  }
}
