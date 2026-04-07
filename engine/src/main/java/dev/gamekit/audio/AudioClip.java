package dev.gamekit.audio;

import dev.gamekit.core.IO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static dev.gamekit.utils.Math.clamp;

/**
 * {@link AudioClip} is abstract class which stores and handles playback for audio in GameKit.
 * <p>
 * All {@link AudioClip} must belong to an {@link AudioGroup}. This makes it possible to control the properties of
 * all audio clips in specific groups by adjusting the group's properties.
 */
public abstract class AudioClip {
  protected final Logger logger = LogManager.getLogger(getClass());
  protected final Clip clip;
  protected final AudioGroup group;
  protected final double maxVolume;
  protected final FloatControl gainControl;
  protected final Map<Event.Handler, LineListener> listenerMap;

  public AudioClip(String resPath, AudioGroup group, double maxVolume) {
    this.clip = loadClip(resPath);
    this.group = group;
    this.maxVolume = clamp(maxVolume, 0, 1);

    gainControl = getControl(FloatControl.Type.MASTER_GAIN);
    listenerMap = new HashMap<>();
  }

  /** Begins playback of this {@link AudioClip} from the beginning without looping */
  public void play() {
    play(false);
  }

  /**
   * Begins playback of this {@link AudioClip} from the beginning
   * <p>
   * If {@code loop} is {@code true}, playback restarts automatically after playback is complete
   */
  public void play(boolean loop) {
    stop();

    if (loop) {
      clip.loop(Clip.LOOP_CONTINUOUSLY);
    } else {
      clip.start();
    }
  }

  /** Resumes playback of this {@link AudioClip} */
  public void resume() {
    clip.start();
  }

  /** Pauses playback of this {@link AudioClip} */
  public void pause() {
    clip.stop();
  }

  /** Stops playback of this {@link AudioClip}, resetting the clip position to the beginning */
  public void stop() {
    clip.stop();
    clip.flush();
    clip.setMicrosecondPosition(0);
  }

  /** Registers an {@link Event.Handler} object to be notified of changes to playback state */
  public void addListener(Event.Handler handler) {
    if (listenerMap.containsKey(handler))
      return;

    LineListener listener = (ev) -> {
      String evName = ev.getType().toString();

      if (evName.equals("Start")) {
        handler.handleEvent(new Event(Event.Type.START));
      } else if (evName.equals("Stop")) {
        handler.handleEvent(new Event(Event.Type.STOP));
      }
    };

    listenerMap.put(handler, listener);

    clip.addLineListener(listener);
  }

  /** Unregisters an {@link Event.Handler} object */
  public void removeListener(Event.Handler handler) {
    if (listenerMap.containsKey(handler)) {
      LineListener listener = listenerMap.get(handler);

      if (listener != null)
        clip.removeLineListener(listener);

      listenerMap.remove(handler);
    }
  }

  /** Called internally to update the clip's parameters */
  public final void update() {
    if (clip.isRunning()) performUpdate();
  }

  /** Delegate method which performs the actual update and must be overridden in subclasses */
  public abstract void performUpdate();

  /** Called internally to dispose resources held by this clip */
  public final void dispose() {
    stop();
    clip.close();
    performDispose();
  }

  /** Delegate method for subclasses to perform additional dispose logic */
  public void performDispose() { /* No-op */ }

  @SuppressWarnings("unchecked")
  protected <T extends Control> T getControl(T.Type controlType) {
    if (clip != null && clip.isControlSupported(controlType)) return (T) clip.getControl(controlType);
    return null;
  }

  private Clip loadClip(String resPath) {
    try {
      logger.debug("Loading audio clip at {}", resPath);

      Clip clip = AudioSystem.getClip();
      clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(IO.getStream(resPath))));
      return clip;
    } catch (LineUnavailableException e) {
      logger.error("Could not get audio clip resource from system mixer", e);
      throw new RuntimeException(e);
    } catch (UnsupportedAudioFileException e) {
      logger.error("Unsupported audio file format", e);
      throw new RuntimeException(e);
    } catch (IOException e) {
      logger.error("Unable to load resource audio clip at {}", resPath, e);
      throw new RuntimeException(e);
    }
  }

  /** Event class for {@link AudioClip} playback events */
  public record Event(Type type) {
    /** Constants form {@link Event} types */
    public enum Type {
      /** Indicates that the {@link AudioClip} has started or restarted playback */
      START,
      /** Indicates that the {@link AudioClip} has paused or stopped playback */
      STOP
    }

    /** Handler interface for {@link Event} */
    public interface Handler {
      /** Called to handle an {@link Event} */
      void handleEvent(Event ev);
    }
  }
}
