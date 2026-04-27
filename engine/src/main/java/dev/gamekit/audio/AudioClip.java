package dev.gamekit.audio;

import dev.gamekit.audio.attenuation.AudioAttenuation;
import dev.gamekit.audio.attenuation.LinearAttenuation;
import dev.gamekit.core.Audio;
import dev.gamekit.utils.GMath;
import dev.gamekit.utils.ValueCallback;
import dev.gamekit.utils.Vector;

/**
 * {@link AudioClip} stores 16-bit, dual channel audio data retrieved from a resource file.
 * <p>
 * To load an {@link AudioClip} use {@link Audio#loadClip}
 */
public class AudioClip {
  private static final Vector UP = new Vector(0, 1);
  private static final AudioAttenuation DEFAULT_ATTENUATION = new LinearAttenuation(0, 100);

  private final byte[] dataL;
  private final byte[] dataR;
  private final Vector position;
  private final Vector listenerVector;
  private boolean playing;
  private boolean looping;
  private double volume;
  private double gain;
  private boolean muted;
  private boolean spatial;
  private AudioMixer mixer;
  private ValueCallback<Event> eventListener;
  private AudioAttenuation attenuation;
  private int head;

  public AudioClip(byte[] dataL, byte[] dataR, boolean playing, boolean looping, boolean muted, AudioMixer mixer) {
    this.dataL = dataL;
    this.dataR = dataR;
    this.playing = playing;
    this.looping = looping;
    this.volume = 1;
    this.gain = 1;
    this.spatial = false;
    this.muted = muted;
    this.mixer = mixer;
    this.eventListener = null;
    this.position = new Vector();
    this.listenerVector = new Vector();
    this.attenuation = DEFAULT_ATTENUATION;
    this.head = 0;
  }

  public AudioClip(byte[] dataL, byte[] dataR) {
    this(dataL, dataR, false, false, false, null);
  }

  public AudioClip(byte[] dataL, byte[] dataR, AudioMixer mixer) {
    this(dataL, dataR, false, false, false, mixer);
  }

  public AudioClip(byte[] dataL, byte[] dataR, boolean looping) {
    this(dataL, dataR, false, looping, false, null);
  }

  public AudioClip(byte[] dataL, byte[] dataR, boolean looping, AudioMixer mixer) {
    this(dataL, dataR, false, looping, false, mixer);
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
    if (GMath.isPracticallyZero(Math.abs(this.volume - volume)))
      return this;

    System.out.printf("Vol: %.2f\n", volume);
    this.volume = GMath.clamp(volume, 0, 1.5);

    if (GMath.isPracticallyZero(this.volume)) {
      gain = 0;
    } else {
      double db = GMath.lerp(-60, 6, volume);
      gain = Math.pow(10, db / 20);
    }

    return this;
  }

  /** Returns whether this clip is 2D (non-spatial) or 3D (spatial) */
  public boolean isSpatial() {
    return spatial;
  }

  /** Sets whether this clip is 2D (non-spatial) or 3D (spatial) */
  public AudioClip setSpatial(boolean spatial) {
    this.spatial = spatial;
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

  /** Sets the listener to be notified when this clip emits an event */
  public AudioClip setEventListener(ValueCallback<Event> eventListener) {
    this.eventListener = eventListener;
    return this;
  }

  /** Sets the attenuation (fall-off) of this clip */
  public AudioClip setAttenuation(AudioAttenuation attenuation) {
    this.attenuation = attenuation != null ? attenuation : DEFAULT_ATTENUATION;
    return this;
  }

  /** Returns the number of bytes remaining till the end of the clip */
  public int getRemainingBytes() {
    return dataL.length - head;
  }

  /**
   * Writes the next two bytes of this clip's data to the provided {@code out} array, advancing the head by 2 bytes.
   * <p>
   * If the head exceeds the buffer length, it is reset and a {@link Event#STOP} or {@link Event#RESTART} event
   * emitted depending on whether the clip is looping.
   */
  public void readNextTwoBytes(double[] out) {
    // Little endian byte ordering
    out[0] = (dataL[head + 1] << 8) | (dataL[head] & 0xFF);
    out[1] = (dataR[head + 1] << 8) | (dataR[head] & 0xFF);

    out[0] *= (!muted ? gain : 0);
    out[1] *= (!muted ? gain : 0);

    if (spatial) {
      listenerVector.set(
        AudioListener.POSITION.x - position.x,
        AudioListener.POSITION.y - position.y
      );

      setVolume(attenuation.attenuate(listenerVector.getMagnitude()));

      Vector normalizedListenerVector = listenerVector.getNormalized();
      double pan = Math.signum(normalizedListenerVector.x) * (Math.abs(Vector.dot(normalizedListenerVector, UP)) - 1);

      if (!GMath.isPracticallyZero(pan)) {
        double ll = (pan <= 0) ? 1.0 : (1.0 - pan);
        double lr = (pan <= 0) ? Math.abs(pan) : 0.0;
        double rl = (pan >= 0) ? pan : 0.0;
        double rr = (pan >= 0) ? 1.0 : (1.0 - Math.abs(pan));
        double tmpL = (ll * out[0]) + (lr * out[1]);
        double tmpR = (rl * out[0]) + (rr * out[1]);

        out[0] = tmpL;
        out[1] = tmpR;
      }
    }

    if (mixer != null)
      mixer.process(out);

    head += 2;

    if (head >= dataL.length) {
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
