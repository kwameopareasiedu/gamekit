package dev.gamekit.audio;

import dev.gamekit.audio.filters.AudioFilter;
import dev.gamekit.utils.GMath;

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
  private double gain;
  private boolean muted;

  public AudioMixer(Object id, boolean muted) {
    this.id = id;
    this.volume = 1;
    this.gain = 1;
    this.muted = muted;

    filters = new ArrayList<>();
  }

  public AudioMixer(Object id) {
    this(id, false);
  }

  /** Adds a list of {@link AudioFilter} to this mixer */
  public AudioMixer addFilters(AudioFilter... filters) {
    synchronized (this.filters) {
      for (AudioFilter filter : filters) {
        if (!this.filters.contains(filter))
          this.filters.add(filter);
      }
    }

    return this;
  }

  /** Returns the volume value of this mixer */
  public double getVolume() {
    return volume;
  }

  /** Sets the volume value of this mixer */
  public AudioMixer setVolume(double volume) {
    this.volume = GMath.clamp(volume, 0, 1.5);

    if (GMath.isPracticallyZero(this.volume)) {
      gain = 0;
    } else {
      double db = GMath.lerp(-60, 6, volume / 1.5);
      gain = Math.pow(10, db / 20);
    }

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
   * Processes an audio sample in {@code buffer}, applying the mixer volume, mute and filters to it, and storing the
   * result back in the {@code buffer}
   */
  public void process(final double[] buffer) {
    buffer[0] *= (!muted ? gain : 0);
    buffer[1] *= (!muted ? gain : 0);

    if (!muted) {
      synchronized (filters) {
        for (AudioFilter filter : filters) {
          double[] filterSample = filter.process(buffer[0], buffer[1]);
          buffer[0] = filterSample[0];
          buffer[1] = filterSample[1];
        }
      }
    }
  }
}
