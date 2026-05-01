package dev.gamekit.core;

import dev.gamekit.audio.AudioClip;
import dev.gamekit.audio.AudioMixer;
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
  public static final float SAMPLE_RATE = 44100;

  private static final Logger LOGGER = LogManager.getLogger(Audio.class);
  private static final AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
  private static final AudioFormat FORMAT = new AudioFormat(ENCODING, SAMPLE_RATE, 16, 2, 4, SAMPLE_RATE, false);
  private static final AudioFormat STEREO_8 = new AudioFormat(ENCODING, SAMPLE_RATE, 8, 2, 2, SAMPLE_RATE, false);
  private static final AudioFormat MONO_16 = new AudioFormat(ENCODING, SAMPLE_RATE, 16, 1, 2, SAMPLE_RATE, false);
  private static final AudioFormat MONO_8 = new AudioFormat(ENCODING, SAMPLE_RATE, 8, 1, 1, SAMPLE_RATE, false);
  private static final Map<Object, AudioMixer> MIXERS = new HashMap<>();
  private static final Map<String, AudioClip> CLIPS = new HashMap<>();
  private static final SourceDataLine OUT;
  private static final double[] CLIP_BUFFER;
  private static final byte[] OUT_BUFFER;

  static {
    try {
      MIXERS.put(AudioMixer.DEFAULT_ID, new AudioMixer(AudioMixer.DEFAULT_ID));

      OUT = AudioSystem.getSourceDataLine(FORMAT);
      OUT.open(FORMAT);
      OUT.start();

      CLIP_BUFFER = new double[2];
      OUT_BUFFER = new byte[2048];
    } catch (LineUnavailableException e) {
      LOGGER.fatal("Unable to create audio output line", e);
      throw new RuntimeException(e);
    }
  }

  private Audio() { }

  /**
   * Loads audio data at the resource path into an {@link AudioClip} object and places it in an {@link AudioMixer}
   * matching the {@code mixerId}
   */
  public static AudioClip loadClip(String resPath, Object mixerId) throws UnsupportedAudioFileException, IOException {
    if (CLIPS.containsKey(resPath))
      return CLIPS.get(resPath);

    AudioInputStream audioStream = get16BitAudioInputStream(resPath);
    byte[][] data = getChannelStreamData(audioStream);

    AudioMixer mixer = createMixer(mixerId);
    AudioClip clip = new AudioClip(data[0], data[1], false, mixer);
    CLIPS.put(resPath, clip);

    return clip;
  }

  /**
   * Loads the audio data at the audio resource path into an {@link AudioClip} object, attaching the default
   * {@link AudioMixer} to it
   */
  public static AudioClip loadClip(String resPath) throws UnsupportedAudioFileException, IOException {
    return loadClip(resPath, AudioMixer.DEFAULT_ID);
  }

  /** Creates and returns a new {@link AudioMixer} with the given id and default parameters */
  public static AudioMixer createMixer(Object id) {
    AudioMixer existingMixer = getMixer(id);

    if (existingMixer != null)
      return existingMixer;

    AudioMixer newMixer = new AudioMixer(id);
    MIXERS.put(id, newMixer);
    return newMixer;
  }

  /** Returns the {@link AudioMixer} with the given id else {@code null} */
  public static AudioMixer getMixer(Object id) {
    return MIXERS.get(id);
  }

  /** Returns the default {@link AudioMixer} */
  public static AudioMixer getDefaultMixer() {
    return MIXERS.get(AudioMixer.DEFAULT_ID);
  }

  /** Called internally to perform update logic */
  static void update() {
    int bytesRead = 0;

    Arrays.fill(OUT_BUFFER, (byte) 0);

    for (int i = 0; i < OUT_BUFFER.length; i += 4) {
      double outL = 0, outR = 0;
      boolean didReadBytes = false;

      synchronized (CLIPS) {
        Collection<AudioClip> clips = CLIPS.values();

        for (AudioClip clip : clips) {
          if (!clip.isPlaying() || clip.getRemainingBytes() <= 0)
            continue;

          clip.readNextTwoBytes(CLIP_BUFFER);
          didReadBytes = true;

          outL += CLIP_BUFFER[0];
          outR += CLIP_BUFFER[1];
        }
      }

      if (didReadBytes) {
        int finalOutL = GMath.clamp((int) outL, -Short.MAX_VALUE, Short.MAX_VALUE);
        int finalOutR = GMath.clamp((int) outR, -Short.MAX_VALUE, Short.MAX_VALUE);

        // Left channel bytes (little endian byte ordering)
        OUT_BUFFER[i] = (byte) (finalOutL & 0xFF);
        OUT_BUFFER[i + 1] = (byte) ((finalOutL >> 8) & 0xFF);

        // Right channel bytes (little endian byte ordering)
        OUT_BUFFER[i + 2] = (byte) (finalOutR & 0xFF);
        OUT_BUFFER[i + 3] = (byte) ((finalOutR >> 8) & 0xFF);

        bytesRead += 4;
      }
    }

    if (bytesRead > 0) {
      OUT.write(OUT_BUFFER, 0, bytesRead);
    } else if (OUT.available() > 0) {
      OUT.drain();
      OUT.flush();
    }
  }

  /** Disposes the Audio object and releases resources */
  static synchronized void dispose() {
    CLIPS.forEach((ignored, clip) -> clip.dispose());
    CLIPS.clear();

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

    if (streamFormat.matches(FORMAT) || streamFormat.matches(MONO_16)) {
      return stream;
    } else if (AudioSystem.isConversionSupported(FORMAT, streamFormat)) {
      stream = AudioSystem.getAudioInputStream(FORMAT, stream);
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

      // Left channel bytes (little endian byte ordering)
      newStreamData[j] = (byte) (leftVal & 0xFF);
      newStreamData[j + 1] = (byte) ((leftVal >> 8) & 0xFF);

      // Right channel bytes (little endian byte ordering)
      newStreamData[j + 2] = (byte) (rightVal & 0xFF);
      newStreamData[j + 3] = (byte) ((rightVal >> 8) & 0xFF);
    }

    AudioFormat stereo16 = new AudioFormat(ENCODING, SAMPLE_RATE, 16, 2, 4, SAMPLE_RATE, false);
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

      // Left channel bytes (little endian byte ordering)
      newStreamData[j] = (byte) (val & 0xFF);
      newStreamData[j + 1] = (byte) ((val >> 8) & 0xFF);
    }

    AudioFormat stereo16 = new AudioFormat(ENCODING, SAMPLE_RATE, 16, 1, 2, SAMPLE_RATE, false);
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
    int bufSize = (int) FORMAT.getSampleRate()
      * FORMAT.getChannels()
      * FORMAT.getFrameSize();

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
