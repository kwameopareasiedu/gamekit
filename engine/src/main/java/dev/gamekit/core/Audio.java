package dev.gamekit.core;

import dev.gamekit.audio.AudioBus;
import dev.gamekit.audio.AudioClip;
import dev.gamekit.utils.GMath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.*;

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
  private static final Map<Object, AudioBus> BUSSES = new HashMap<>();
  private static final SourceDataLine OUT;
  private static final byte[] BYTE_BUFFER;

  static {
    try {
      BUSSES.put(AudioBus.DEFAULT_ID, new AudioBus(AudioBus.DEFAULT_ID));
      OUT = AudioSystem.getSourceDataLine(PREFERRED_FORMAT);
      OUT.open(PREFERRED_FORMAT);
      OUT.start();

      BYTE_BUFFER = new byte[(int) PREFERRED_FORMAT.getSampleRate() * PREFERRED_FORMAT.getFrameSize()];
    } catch (LineUnavailableException e) {
      LOGGER.fatal("Unable to create audio output line", e);
      throw new RuntimeException(e);
    }
  }

  private Audio() { }

  /**
   * Loads audio data at the resource path into an {@link AudioClip} object and places it in an {@link AudioBus}
   * matching the {@code busId}
   */
  public static AudioClip loadClip(String resPath, Object busId) throws UnsupportedAudioFileException, IOException {
    AudioInputStream audioStream = get16BitAudioInputStream(resPath);
    byte[][] data = getChannelStreamData(audioStream);
    AudioClip clip = new AudioClip(data[0], data[1], false);

    AudioBus defaultBus = createBus(busId);
    defaultBus.addClip(clip);

    return clip;
  }

  /**
   * Loads the audio data at the audio resource path into an {@link AudioClip} object and places it in the default
   * {@link AudioBus}
   */
  public static AudioClip loadClip(String resPath) throws UnsupportedAudioFileException, IOException {
    return loadClip(resPath, AudioBus.DEFAULT_ID);
  }

  /** Creates and returns a new {@link AudioBus} with the given id and default parameters */
  public static AudioBus createBus(Object id) {
    AudioBus existingBus = getBus(id);

    if (existingBus != null)
      return existingBus;

    AudioBus bus = new AudioBus(id);
    BUSSES.put(id, bus);
    return bus;
  }

  /** Returns the {@link AudioBus} with the given id else {@code null} */
  public static AudioBus getBus(Object id) {
    Collection<AudioBus> busList = BUSSES.values();

    for (AudioBus bus : busList)
      if (Objects.equals(id, bus.id))
        return bus;

    return null;
  }

  /** Called internally to perform update logic */
  static void update() {
    Collection<AudioBus> busList = BUSSES.values();
    int framesToRead = (int) (PREFERRED_FORMAT.getFrameRate() / Application.FRAME_INTERVAL_MS);
    int bytesToRead = framesToRead * PREFERRED_FORMAT.getFrameSize();
    int totalBytesRead = 0;

    for (AudioBus bus : busList) {
      int bytesRead = bus.read(BYTE_BUFFER, totalBytesRead, bytesToRead);
      int paddingRequired = bytesToRead - bytesRead;

      if (paddingRequired > 0) {
        for (int i = bytesRead; i < bytesToRead; i++)
          BYTE_BUFFER[i] = 0;

        bytesRead += paddingRequired;
      }

      totalBytesRead += bytesRead;
    }

    if (totalBytesRead > 0)
      OUT.write(BYTE_BUFFER, 0, totalBytesRead);
  }

  /** Disposes the Audio object and releases resources */
  static synchronized void dispose() {
    BUSSES.forEach((key, bus) -> bus.dispose());
    BUSSES.clear();

    OUT.close();
    OUT.flush();
  }

  /**
   * Opens and returns an audio stream to the resource path specified.
   * <p>
   * If the stream is not a 16-bit stream, an attempt is made to convert it to one else an error is thrown
   */
  private static AudioInputStream get16BitAudioInputStream(String resPath)
    throws IOException, UnsupportedAudioFileException {
    AudioInputStream stream = AudioSystem.getAudioInputStream(IO.getStream(resPath));

    if (stream == null)
      throw new IllegalArgumentException("Unable to find resource: " + resPath);

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
      stream = convert8BitStereoStreamTo16BitStereo(stream);
    } else if (streamFormat.matches(MONO_8) || AudioSystem.isConversionSupported(MONO_8, streamFormat)) {
      if (!streamFormat.matches(MONO_8))
        stream = AudioSystem.getAudioInputStream(MONO_8, stream);
      stream = convert8BitMonoStreamTo16BitMono(stream);
    } else {
      stream.close();
      throw new IllegalArgumentException("Unsupported stream format: " + streamFormat);
    }

    long frameLength = stream.getFrameLength();

    if (frameLength > Integer.MAX_VALUE)
      throw new IllegalArgumentException("Audio resource too long");

    return stream;
  }

  /** Converts an 8-bit stereo audio stream to a 16-bit stereo audio stream */
  private static AudioInputStream convert8BitStereoStreamTo16BitStereo(AudioInputStream stream) throws IOException {
    byte[] streamData = getRawStreamData(stream);
    byte[] newStreamData;

    int newStreamSize = streamData.length * 2 * 2;

    if (newStreamSize < 0)
      throw new BufferOverflowException();

    newStreamData = new byte[newStreamSize];

    for (int i = 0, j = 0; i < streamData.length; i += 2, j += 4) {
      double leftFloatVal = streamData[i];
      double rightFloatVal = streamData[i + 1];

      leftFloatVal /= (leftFloatVal < 0) ? 128 : 127;
      rightFloatVal /= (rightFloatVal < 0) ? 128 : 127;

      leftFloatVal = GMath.clamp(leftFloatVal, -1, 1);
      rightFloatVal = GMath.clamp(rightFloatVal, -1, 1);

      int leftVal = (int) (leftFloatVal * Short.MAX_VALUE);
      int rightVal = (int) (rightFloatVal * Short.MAX_VALUE);

      // Left channel bytes
      newStreamData[j + 1] = (byte) ((leftVal >> 8) & 0xFF); //MSB
      newStreamData[j] = (byte) (leftVal & 0xFF); //LSB

      // Right channel bytes
      newStreamData[j + 3] = (byte) ((rightVal >> 8) & 0xFF); //MSB
      newStreamData[j + 2] = (byte) (rightVal & 0xFF); //LSB
    }

    AudioFormat stereo16 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
    return new AudioInputStream(new ByteArrayInputStream(newStreamData), stereo16, newStreamData.length / 4);
  }

  /** Converts an 8-bit mono audio stream to a 16-bit mono audio stream */
  private static AudioInputStream convert8BitMonoStreamTo16BitMono(AudioInputStream stream) throws IOException {
    byte[] streamData = getRawStreamData(stream);
    byte[] newStreamData;

    int newStreamSize = streamData.length * 2;

    if (newStreamSize < 0)
      throw new BufferOverflowException();

    newStreamData = new byte[newStreamSize];

    for (int i = 0, j = 0; i < streamData.length; i++, j += 2) {
      double floatVal = streamData[i];

      floatVal /= (floatVal < 0) ? 128 : 127;
      floatVal = GMath.clamp(floatVal, -1, 1);

      int val = (int) (floatVal * Short.MAX_VALUE);

      // Left channel bytes
      newStreamData[j + 1] = (byte) ((val >> 8) & 0xFF); //MSB
      newStreamData[j] = (byte) (val & 0xFF); //LSB
    }

    AudioFormat stereo16 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
    return new AudioInputStream(new ByteArrayInputStream(newStreamData), stereo16, newStreamData.length / 2);
  }

  /** Reads and returns all the channel bytes in the given audio stream */
  private static byte[][] getChannelStreamData(AudioInputStream stream) throws IOException {
    int channels = stream.getFormat().getChannels();
    byte[] data = getRawStreamData(stream);

    if (channels == 1) {
      return new byte[][]{ data, data };
    } else if (channels == 2) {
      byte[] dataL = new byte[data.length / 2];
      byte[] dataR = new byte[data.length / 2];

      for (int i = 0, j = 0; i < data.length; i += 4, j += 2) {
        dataL[j] = data[i];
        dataL[j + 1] = data[i + 1];

        dataR[j] = data[i + 2];
        dataR[j + 1] = data[i + 3];
      }

      return new byte[][]{ dataL, dataR };
    } else {
      throw new IllegalArgumentException("Invalid stream channel length: " + channels);
    }
  }

  /** Reads and returns all the bytes in the given audio stream */
  private static byte[] getRawStreamData(AudioInputStream stream) throws IOException {
    int bufSize = (int) PREFERRED_FORMAT.getSampleRate()
      * PREFERRED_FORMAT.getChannels()
      * PREFERRED_FORMAT.getFrameSize();

    byte[] buffer = new byte[bufSize];
    List<Byte> list = new ArrayList<>(bufSize);
    int bytesRead;

    while ((bytesRead = stream.read(buffer)) > -1) {
      for (int i = 0; i < bytesRead; i++)
        list.add(buffer[i]);
    }

    byte[] streamBytes = new byte[list.size()];

    for (int i = 0; i < list.size(); i++)
      streamBytes[i] = list.get(i);

    return streamBytes;
  }
}
