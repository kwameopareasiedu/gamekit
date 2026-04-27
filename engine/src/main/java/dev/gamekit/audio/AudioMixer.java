package dev.gamekit.audio;

import dev.gamekit.audio.filters.AudioFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link AudioMixer} is a channel which can process playback of one or more {@link AudioClip}.
 * <p>
 * {@link AudioMixer} also allow {@link AudioFilter filters} to be applied on all connected {@link AudioClip clips}.
 */
public class AudioMixer {
  public static final String DEFAULT_ID = "default";

  public final Object id;

  private final List<AudioFilter> filters;
  private double volume;
  private boolean muted;

  public AudioMixer(Object id, double volume, boolean muted) {
    this.id = id;
    this.volume = volume;
    this.muted = muted;

    filters = new ArrayList<>();
  }

  public AudioMixer(Object id) {
    this(id, 1, false);
  }

  /** Adds an {@link AudioFilter} to this mixer */
  public AudioMixer addFilter(AudioFilter filter) {
    if (filters.contains(filter))
      return this;

    synchronized (filters) {
      filters.add(filter);
    }

    return this;
  }

  /** Returns the volume value of this mixer */
  public double getVolume() {
    return volume;
  }

  /** Sets the volume value of this mixer */
  public AudioMixer setVolume(double volume) {
    this.volume = volume;
    return this;
  }

  /** Returns the muted status of this mixer */
  public boolean isMuted() {
    return muted;
  }

  /** Sets the muted status of this mixer */
  public AudioMixer setMuted(boolean muted) {
    this.muted = muted;
    return this;
  }

  /**
   * Processes an audio sample in {@code buffer},  applying the mixer volume, mute and filters to it, and storing the
   * result back in the {@code buffer}
   */
  public void process(final double[] buffer) {
    buffer[0] *= (!muted ? volume : 0);
    buffer[1] *= (!muted ? volume : 0);

    synchronized (filters) {
      for (AudioFilter filter : filters) {
        double[] filterSample = filter.process(buffer[0], buffer[1]);
        buffer[0] = filterSample[0];
        buffer[1] = filterSample[1];
      }
    }
  }
}
