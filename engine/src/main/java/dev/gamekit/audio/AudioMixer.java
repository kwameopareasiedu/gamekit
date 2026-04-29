package dev.gamekit.audio;

import dev.gamekit.audio.effects.AudioEffect;
import dev.gamekit.utils.GMath;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link AudioMixer} is a channel which can process playback of one or more {@link AudioClip}.
 * <p>
 * {@link AudioMixer} also allow {@link AudioEffect effects} to be applied on all connected {@link AudioClip clips}.
 */
public class AudioMixer {
  public static final String DEFAULT_ID = "default";

  public final Object id;

  private final List<AudioEffect> effects;
  private double volume;
  private double gain;
  private boolean muted;

  public AudioMixer(Object id, boolean muted) {
    this.id = id;
    this.volume = 1;
    this.gain = 1;
    this.muted = muted;

    effects = new ArrayList<>();
  }

  public AudioMixer(Object id) {
    this(id, false);
  }

  /** Adds a list of {@link AudioEffect} to this mixer */
  public AudioMixer addEffects(AudioEffect... effects) {
    synchronized (this.effects) {
      for (AudioEffect effect : effects) {
        if (effect != null && !this.effects.contains(effect))
          this.effects.add(effect);
      }
    }

    return this;
  }

  /** Sets the list of {@link AudioEffect} of this mixer */
  public AudioMixer setEffects(AudioEffect... effects) {
    synchronized (this.effects) {
      this.effects.clear();

      for (AudioEffect effect : effects) {
        if (effect != null && !this.effects.contains(effect))
          this.effects.add(effect);
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
   * Processes an audio sample in {@code buffer}, applying the mixer volume, mute and audio effects to it, and storing
   * the result back in the {@code buffer}
   */
  public void process(final double[] buffer) {
    buffer[0] *= (!muted ? gain : 0);
    buffer[1] *= (!muted ? gain : 0);

    if (!muted) {
      synchronized (effects) {
        for (AudioEffect effect : effects) {
          double[] effectSample = effect.process(buffer[0], buffer[1]);
          buffer[0] = effectSample[0];
          buffer[1] = effectSample[1];
        }
      }
    }
  }
}
