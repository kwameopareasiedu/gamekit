package dev.gamekit.audio;

import dev.gamekit.core.Audio;
import dev.gamekit.utils.ValueCallback;

/**
 * {@link AudioClip} stores 16-bit, dual channel audio data retrieved from a resource file.
 * <p>
 * To load an {@link AudioClip} use {@link Audio#loadClip}
 */
public class AudioClip {
  private final byte[] channelLBytes;
  private final byte[] channelRBytes;
  private ValueCallback<Event> eventListener;
  private boolean playing;
  private boolean looping;
  private int head;

  public AudioClip(
    byte[] channelLBytes,
    byte[] channelRBytes,
    boolean playing,
    boolean looping,
    ValueCallback<Event> eventListener
  ) {
    this.channelLBytes = channelLBytes;
    this.channelRBytes = channelRBytes;
    this.eventListener = eventListener;
    this.playing = playing;
    this.looping = looping;
    this.head = 0;
  }

  public AudioClip(byte[] channelLBytes, byte[] channelRBytes) {
    this(channelLBytes, channelRBytes, false, false, null);
  }

  public AudioClip(byte[] channelLBytes, byte[] channelRBytes, boolean looping) {
    this(channelLBytes, channelRBytes, false, looping, null);
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

  /** Returns the number of bytes remaining till the end of the clip */
  public int getRemainingBytes() {
    return channelLBytes.length - head;
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
    out[0] = (channelLBytes[head + 1] << 8) | (channelLBytes[head] & 0xFF);
    out[1] = (channelRBytes[head + 1] << 8) | (channelRBytes[head] & 0xFF);

    head += 2;

    if (head > channelLBytes.length) {
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
