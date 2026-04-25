package dev.gamekit.audio;

import dev.gamekit.audio.filters.AudioFilter;
import dev.gamekit.utils.GMath;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link AudioBus} is a channel which can process playback of one or more {@link AudioClip}.
 * <p>
 * {@link AudioBus} are also an interface for filters to be applied on an audio data stream. These include gain
 * (volume), pan, pitch, reverb, etc.
 */
public class AudioBus {
  public static final String DEFAULT_ID = "default";

  public final Object id;

  private final List<AudioClip> clips;
  private final List<AudioFilter> filters;
  private final int[] clipByteBuffer;
  private final double alpha = 0.81;
  private double volume;
  private double pan;
  private boolean muted;

  public AudioBus(Object id, double volume, double pan, boolean muted) {
    this.id = id;
    this.volume = volume;
    this.pan = pan;
    this.muted = muted;

    clips = new ArrayList<>();
    filters = new ArrayList<>();
    clipByteBuffer = new int[2];
  }

  public AudioBus(Object id) {
    this(id, 1, 0, false);
  }

  /** Called internally to add an {@link AudioClip} to this bus */
  public AudioBus addClip(AudioClip clip) {
    if (clips.contains(clip))
      return this;

    synchronized (clips) {
      clips.add(clip);
    }

    return this;
  }

  /** Adds an {@link AudioFilter} to this bus */
  public AudioBus addFilter(AudioFilter filter) {
    if (filters.contains(filter))
      return this;

    synchronized (filters) {
      filters.add(filter);
    }

    return this;
  }

  /** Returns the volume value of this bus */
  public double getVolume() {
    return volume;
  }

  /** Sets the volume value of this bus */
  public AudioBus setVolume(double volume) {
    this.volume = volume;
    return this;
  }

  /** Returns the pan value of this bus */
  public double getPan() {
    return pan;
  }

  /** Sets the pan value of this bus */
  public AudioBus setPan(double pan) {
    this.pan = pan;
    return this;
  }

  /** Returns the muted status of this bus */
  public boolean isMuted() {
    return muted;
  }

  /** Sets the muted status of this bus */
  public AudioBus setMuted(boolean muted) {
    this.muted = muted;
    return this;
  }

  /**
   * Reads a specified number of bytes from a given offset of the audio data of all {@link AudioClip} objects,
   * returning the number of bytes actually read.
   * <p>
   * Audio filters are applied to the signal sum of all audio clips.
   */
  public int read(byte[] out, int offset, int length) {
    int bytesRead = 0;
    boolean didReadBytes = true;

    for (int i = offset; i < (offset + length) && didReadBytes; i += 4) {
      double busOutL = 0, busOutR = 0;

      didReadBytes = false;

      synchronized (clips) {
        for (AudioClip clip : clips) {
          if (!clip.isPlaying() || clip.getRemainingBytes() <= 0)
            continue;

          clip.readNextTwoBytes(clipByteBuffer);
          didReadBytes = true;

          double clipOutL = (!muted ? volume : 0) * clipByteBuffer[0];
          double clipOutR = (!muted ? volume : 0) * clipByteBuffer[1];

          if (!GMath.isPracticallyZero(pan)) {
            double ll = (pan <= 0) ? 1.0 : (1.0 - pan);
            double lr = (pan <= 0) ? Math.abs(pan) : 0.0;
            double rl = (pan >= 0) ? pan : 0.0;
            double rr = (pan >= 0) ? 1.0 : (1.0 - Math.abs(pan));
            double tmpL = (ll * clipOutL) + (lr * clipOutR);
            double tmpR = (rl * clipOutL) + (rr * clipOutR);

            clipOutL = tmpL;
            clipOutR = tmpR;
          }

          busOutL += clipOutL;
          busOutR += clipOutR;
        }
      }

      synchronized (filters) {
        for (AudioFilter filter : filters) {
          double[] filterOut = filter.process(busOutL, busOutR);
          busOutL = filterOut[0];
          busOutR = filterOut[1];
        }
      }

      if (didReadBytes) {
        int finalOutL = GMath.clamp((int) busOutL, -Short.MAX_VALUE, Short.MAX_VALUE);
        int finalOutR = GMath.clamp((int) busOutR, -Short.MAX_VALUE, Short.MAX_VALUE);

        // Left channel bytes (little endian byte ordering)
        out[i] = (byte) (finalOutL & 0xFF);
        out[i + 1] = (byte) ((finalOutL >> 8) & 0xFF);

        // Right channel bytes (little endian byte ordering)
        out[i + 2] = (byte) (finalOutR & 0xFF);
        out[i + 3] = (byte) ((finalOutR >> 8) & 0xFF);

        bytesRead += 4;
      }
    }

    return bytesRead;
  }

  /** Disposes the bus, releasing any consumed resources */
  public void dispose() {
    for (AudioClip clip : clips)
      clip.dispose();

    clips.clear();
  }
}
