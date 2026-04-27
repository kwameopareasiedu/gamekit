package dev.gamekit.audio;

import dev.gamekit.core.Audio;
import dev.gamekit.utils.GMath;
import dev.gamekit.utils.ValueCallback;

/**
 * {@link AudioClip} stores 16-bit, dual channel audio data retrieved from a resource file.
 * <p>
 * To load an {@link AudioClip} use {@link Audio#loadClip}
 */
public class AudioClip {
  private final byte[] dataL;
  private final byte[] dataR;
  private final double[] buffer;
  private boolean playing;
  private boolean looping;
  private double volume;
  private double pan;
  private boolean muted;
  private AudioMixer mixer;
  private ValueCallback<Event> eventListener;
  private int head;

  public AudioClip(
    byte[] dataL,
    byte[] dataR,
    boolean playing,
    boolean looping,
    double volume,
    double pan,
    boolean muted,
    AudioMixer mixer,
    ValueCallback<Event> eventListener
  ) {
    this.dataL = dataL;
    this.dataR = dataR;
    this.playing = playing;
    this.looping = looping;
    this.volume = GMath.clamp(volume, 0, 1.5);
    this.pan = GMath.clamp(pan, -1, 1);
    this.muted = muted;
    this.mixer = mixer;
    this.eventListener = eventListener;
    this.head = 0;
    this.buffer = new double[2];
  }

  public AudioClip(byte[] dataL, byte[] dataR) {
    this(dataL, dataR, false, false, 1, 0, false, null, null);
  }

  public AudioClip(byte[] dataL, byte[] dataR, AudioMixer mixer) {
    this(dataL, dataR, false, false, 1, 0, false, mixer, null);
  }

  public AudioClip(byte[] dataL, byte[] dataR, boolean looping) {
    this(dataL, dataR, false, looping, 1, 0, false, null, null);
  }

  public AudioClip(byte[] dataL, byte[] dataR, boolean looping, AudioMixer mixer) {
    this(dataL, dataR, false, looping, 1, 0, false, mixer, null);
  }

  /** Returns {@code true} if this clip is currently playing */
  public boolean isPlaying() {
    return playing;
  }

  /**
   * Marks this clip as playable.
   * <p>
   * If this clip is at its end and {@link #looping} is false, this method does nothing, else the clip's position is
   * reset, and it is marked as playable
   */
  public AudioClip play() {
    if (playing)
      return this;

    if (getRemainingBytes() > 0) {
      playing = true;

      if (eventListener != null)
        eventListener.invoke(Event.PLAY);
    } else if (looping) {
      playing = true;
      head = 0;

      if (eventListener != null)
        eventListener.invoke(Event.RESTART);
    }

    return this;
  }

  /** Pauses playback of this clip */
  public AudioClip pause() {
    if (!playing)
      return this;

    playing = false;

    if (eventListener != null)
      eventListener.invoke(Event.PAUSE);

    return this;
  }

  /** Stops playback of this clip and resets its head */
  public AudioClip stop() {
    playing = false;
    head = 0;

    if (eventListener != null)
      eventListener.invoke(Event.STOP);

    return this;
  }

  /** Returns {@code true} if this clip is set to loop */
  public boolean isLooping() {
    return looping;
  }

  /** Sets the clip should loop when playback is finished */
  public AudioClip setLooping(boolean looping) {
    this.looping = looping;
    return this;
  }

  /** Returns the volume of this clip */
  public double getVolume() {
    return volume;
  }

  /** Sets the volume of this clip */
  public AudioClip setVolume(double volume) {
    this.volume = GMath.clamp(volume, 0, 1.5);
    return this;
  }

  /** Returns the pan of this clip */
  public double getPan() {
    return pan;
  }

  /** Sets the pan of this clip */
  public AudioClip setPan(double pan) {
    this.pan = pan;
    return this;
  }

  /** Returns the muted status of this clip */
  public boolean isMuted() {
    return muted;
  }

  /** Sets the muted status of this clip */
  public AudioClip setMuted(boolean muted) {
    this.muted = muted;
    return this;
  }

  /** Returns the attached {@link AudioMixer} */
  public AudioMixer getMixer() {
    return mixer;
  }

  /** Sets the attached {@link AudioMixer} */
  public AudioClip setMixer(AudioMixer mixer) {
    this.mixer = mixer;
    return this;
  }

  /** Returns the number of bytes remaining till the end of the clip */
  public int getRemainingBytes() {
    return dataL.length - head;
  }

  /** Sets the listener to be notified when this clip emits an event */
  public AudioClip setEventListener(ValueCallback<Event> eventListener) {
    this.eventListener = eventListener;
    return this;
  }

  /**
   * Writes the next two bytes of this clip's data to the provided {@code out} array, advancing the head by 2 bytes.
   * <p>
   * If the head exceeds the buffer length, it is reset and a {@link Event#STOP} or {@link Event#RESTART} event
   * emitted depending on whether the clip is looping.
   */
  public void readNextTwoBytes(int[] out) {
    // Little endian byte ordering
    buffer[0] = (dataL[head + 1] << 8) | (dataL[head] & 0xFF);
    buffer[1] = (dataR[head + 1] << 8) | (dataR[head] & 0xFF);

    buffer[0] *= (!muted ? volume : 0);
    buffer[1] *= (!muted ? volume : 0);

    if (!GMath.isPracticallyZero(pan)) {
      double ll = (pan <= 0) ? 1.0 : (1.0 - pan);
      double lr = (pan <= 0) ? Math.abs(pan) : 0.0;
      double rl = (pan >= 0) ? pan : 0.0;
      double rr = (pan >= 0) ? 1.0 : (1.0 - Math.abs(pan));
      double tmpL = (ll * buffer[0]) + (lr * buffer[1]);
      double tmpR = (rl * buffer[0]) + (rr * buffer[1]);

      buffer[0] = tmpL;
      buffer[1] = tmpR;
    }

    if (mixer != null)
      mixer.process(buffer);

    out[0] = (int) buffer[0];
    out[1] = (int) buffer[1];

    head += 2;

    if (head > dataL.length) {
      head = 0;

      if (looping) {
        if (eventListener != null)
          eventListener.invoke(Event.RESTART);
      } else {
        playing = false;

        if (eventListener != null)
          eventListener.invoke(Event.STOP);
      }
    }
  }

  /** Clears all audio data and resets internal state */
  public void dispose() {
    playing = false;
    head = 0;

    if (eventListener != null)
      eventListener.invoke(Event.DISPOSE);
  }

  /** Constants for the events emitted by an {@link AudioClip} */
  public enum Event {
    /** Indicates a playing clip */
    PLAY,
    /** Indicates a paused clip which can be resumed */
    PAUSE,
    /** Pseudo-state indicating a clip has restarted */
    RESTART,
    /** Indicates a stopped clip which can be restarted */
    STOP,
    /** Indicates a clip is being disposed */
    DISPOSE
  }
}
