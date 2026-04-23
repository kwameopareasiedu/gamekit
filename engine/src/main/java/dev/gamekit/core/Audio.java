package dev.gamekit.core;

import dev.gamekit.audio.AudioBus;
import dev.gamekit.audio.AudioClip;
import dev.gamekit.utils.Math;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** {@link Audio} handles loading and playback of sounds in GameKit */
public final class Audio {
  private static final Logger LOGGER = LogManager.getLogger(Audio.class);
  private static final AudioFormat PREFERRED_FORMAT = new AudioFormat(
    AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false
  );
  private static final AudioFormat STEREO_8 = new AudioFormat(
    AudioFormat.Encoding.PCM_SIGNED, 44100, 8, 2, 2, 44100, false
  );
  private static final AudioFormat MONO_16 = new AudioFormat(
    AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false
  );
  private static final AudioFormat MONO_8 = new AudioFormat(
    AudioFormat.Encoding.PCM_SIGNED, 44100, 8, 1, 1, 44100, false
  );
  private final static Map<Object, AudioBus> BUSSES = new HashMap<>();
  private static final SourceDataLine OUT;

  static {
    try {
      BUSSES.put(AudioBus.DEFAULT_ID, new AudioBus(AudioBus.DEFAULT_ID));
      OUT = AudioSystem.getSourceDataLine(PREFERRED_FORMAT);
      OUT.open(PREFERRED_FORMAT);
      OUT.start();
    } catch (LineUnavailableException e) {
      LOGGER.fatal("Unable to create audio output line", e);
      throw new RuntimeException(e);
    }
  }

  private Audio() { }

  public static AudioClip loadClip(String resPath) {
    AudioInputStream audioStream = getCompatibleAudioInputStream(resPath);

    if (audioStream == null)
      return null;

    AudioBus defaultBus = BUSSES.get(AudioBus.DEFAULT_ID);
    byte[][] data = getStereoStreamData(audioStream);

    if (data == null)
      return null;

    return new AudioClip(defaultBus, data[0], data[1], false);
  }

  /** Called internally to perform update logic */
  static void update() {

  }

  static void dispose() {
    BUSSES.forEach((key, clip) -> clip.dispose());
    BUSSES.clear();

    OUT.close();
    OUT.flush();
  }

  private static AudioInputStream getCompatibleAudioInputStream(String resPath) {
    try {
      AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(IO.getStream(resPath)));
      AudioFormat streamFormat = stream.getFormat();

      if (streamFormat.matches(PREFERRED_FORMAT) || streamFormat.matches(MONO_16)) {
        return stream;
      } else if (AudioSystem.isConversionSupported(PREFERRED_FORMAT, streamFormat)) {
        stream = AudioSystem.getAudioInputStream(PREFERRED_FORMAT, stream);
      } else if (AudioSystem.isConversionSupported(MONO_16, streamFormat)) {
        stream = AudioSystem.getAudioInputStream(MONO_16, stream);
      } else if (streamFormat.matches(STEREO_8) || AudioSystem.isConversionSupported(STEREO_8, streamFormat)) {
        if (!streamFormat.matches(STEREO_8))
          stream = AudioSystem.getAudioInputStream(STEREO_8, stream);
        stream = convertStereo8StreamToStereo16(stream);
      } else {
        LOGGER.error("Unable to convert {} into a compatible audio stream", resPath);
        stream.close();
        return null;
      }

      if (stream == null)
        return null;

      long frameLength = stream.getFrameLength();

      if (frameLength > Integer.MAX_VALUE) {
        LOGGER.error("Audio resource too long!");
        return null;
      }

      return stream;
    } catch (UnsupportedAudioFileException e) {
      LOGGER.error("Unsupported audio file {}", resPath);
      LOGGER.error(e);
      return null;
    } catch (IOException e) {
      LOGGER.error("Unable to get audio stream for {}", resPath);
      LOGGER.error(e);
      return null;
    }
  }

  private static AudioInputStream convertStereo8StreamToStereo16(AudioInputStream stream) {
    byte[] streamData = getStreamData(stream);
    byte[] newStreamData;

    if (streamData == null)
      return null;

    int newStreamSize = streamData.length * 2 * 2;

    if (newStreamSize < 0)
      throw new BufferOverflowException();

    newStreamData = new byte[newStreamSize];

    for (int i = 0, j = 0; i < streamData.length; i += 2, j += 4) {
      double leftFloatVal = streamData[i];
      double rightFloatVal = streamData[i + 1];

      leftFloatVal /= (leftFloatVal < 0) ? 128 : 127;
      rightFloatVal /= (rightFloatVal < 0) ? 128 : 127;

      leftFloatVal = Math.clamp(leftFloatVal, -1, 1);
      rightFloatVal = Math.clamp(rightFloatVal, -1, 1);

      int leftVal = (int) (leftFloatVal * Short.MAX_VALUE);
      int rightVal = (int) (rightFloatVal * Short.MAX_VALUE);

      // Left channel bytes
      newStreamData[j + 1] = (byte) ((leftVal >> 8) & 0xFF); //MSB
      newStreamData[j] = (byte) (leftVal & 0xFF); //LSB

      // Right channel bytes
      newStreamData[j + 3] = (byte) ((rightVal >> 8) & 0xFF); //MSB
      newStreamData[j + 2] = (byte) (rightVal & 0xFF); //LSB
    }

    try { stream.close(); } //
    catch (IOException ignored) { }

    AudioFormat stereo16 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
    return new AudioInputStream(new ByteArrayInputStream(newStreamData), stereo16, newStreamData.length / 4);
  }

  private static byte[] getStreamData(AudioInputStream stream) {
    try {
      int bufSize = (int) PREFERRED_FORMAT.getSampleRate()
        * PREFERRED_FORMAT.getChannels()
        * PREFERRED_FORMAT.getFrameSize();

      byte[] buffer = new byte[bufSize];
      List<Byte> list = new ArrayList<>(bufSize);
      int bytesRead;

      while ((bytesRead = stream.read(buffer)) > -1) {
        for (int i = 0; i < bytesRead; i++) {
          list.add(buffer[i]);
        }
      }

      byte[] streamBytes = new byte[list.size()];

      for (int i = 0; i < list.size(); i++)
        streamBytes[i] = list.get(i);

      return streamBytes;
    } catch (IOException e) {
      LOGGER.error("Error reading all bytes from stream!");
      LOGGER.error(e);
      return null;
    }

  }

  private static byte[][] getStereoStreamData(AudioInputStream stream) {
    int channels = stream.getFormat().getChannels();
    byte[] data = getStreamData(stream);
    byte[][] stereoData;

    if (data == null)
      return null;

    if (channels == 1) {
      stereoData = new byte[][]{ data, data };
    } else if (channels == 2) {
      byte[] dataL = new byte[data.length / 2];
      byte[] dataR = new byte[data.length / 2];

      for (int i = 0, j = 0; i < data.length; i += 4, j += 2) {
        dataL[j] = data[i];
        dataL[j + 1] = data[i + 1];

        dataR[j] = data[i + 2];
        dataR[j + 1] = data[i + 3];
      }

      stereoData = new byte[][]{ dataL, dataR };
    } else {
      LOGGER.error("Invalid channel length: {}", channels);
      return null;
    }

    try { stream.close(); }//
    catch (IOException ignored) { }

    return stereoData;
  }
}
