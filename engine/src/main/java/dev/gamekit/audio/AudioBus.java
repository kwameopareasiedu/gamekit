package dev.gamekit.audio;

import dev.gamekit.utils.GMath;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link AudioBus} is a channel which can process playback of one or more {@link AudioClip}.
 * <p>
 * {@link AudioBus} are also an interface for filters to be applied on an audio data stream. These include gain
 * (volume), pan, pitch, reverb, etc.
 */
public class AudioBus {
  public static final String DEFAULT_ID = "default";

  final Object id;
  private final List<AudioClip> clips;
  private final int[] byteBuffer;
  private double volume = 1.0;
  private double pan = -0.5;

  public AudioBus(Object id) {
    this.id = id;
    clips = new ArrayList<>();
    byteBuffer = new int[2];
  }

  public void addClip(AudioClip clip) {
    if (clips.contains(clip))
      return;

    clips.add(clip);
  }

  public int read(byte[] out, int offset, int length) {
    int bytesRead = 0;
    boolean didReadBytes = true;

    for (int i = offset; i < (offset + length) && didReadBytes; i += 4) {
      double busLv = 0, busRv = 0;

      didReadBytes = false;

      for (AudioClip clip : clips) {
        if (!clip.isPlaying() || clip.getRemainingBytes() <= 0)
          continue;

        clip.readNextTwoBytes(byteBuffer);
        didReadBytes = true;

        double clipLv = volume * byteBuffer[0];
        double clipRv = volume * byteBuffer[1];

        if (!GMath.isPracticallyZero(pan)) {
          double ll = (pan <= 0) ? 1.0 : (1.0 - pan);
          double lr = (pan <= 0) ? Math.abs(pan) : 0.0;
          double rl = (pan >= 0) ? pan : 0.0;
          double rr = (pan >= 0) ? 1.0 : (1.0 - Math.abs(pan));
          double tmpL = (ll * clipLv) + (lr * clipRv);
          double tmpR = (rl * clipLv) + (rr * clipRv);

          clipLv = tmpL;
          clipRv = tmpR;
        }

        busLv += clipLv;
        busRv += clipRv;
      }

      if (didReadBytes) {
        int finalBusLv = GMath.clamp((int) busLv, -Short.MAX_VALUE, Short.MAX_VALUE);
        int finalBusRv = GMath.clamp((int) busRv, -Short.MAX_VALUE, Short.MAX_VALUE);

        // Left channel bytes
        out[i + 1] = (byte) ((finalBusLv >> 8) & 0xFF); //MSB
        out[i] = (byte) (finalBusLv & 0xFF); //LSB

        // Right channel bytes
        out[i + 3] = (byte) ((finalBusRv >> 8) & 0xFF); //MSB
        out[i + 2] = (byte) (finalBusRv & 0xFF); //LSB

        bytesRead += 4;
      }
    }

    return bytesRead;
  }

  public void dispose() {

  }
}
