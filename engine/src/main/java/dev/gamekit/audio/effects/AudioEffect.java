package dev.gamekit.audio.effects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/** {@link AudioEffect} represents objects that can perform some manipulation to a chunk of audio stream data */
public abstract class AudioEffect {
  public final String id;

  protected final Logger logger = LogManager.getLogger(getClass());
  protected final int bufferLength;
  protected final double[] bufferL;
  protected final double[] bufferR;
  protected final double[] out;
  protected int head = 0;

  protected AudioEffect(String id, int bufferLength) {
    this.id = id;
    this.bufferLength = bufferLength;
    this.bufferL = new double[bufferLength];
    this.bufferR = new double[bufferLength];
    this.out = new double[2];

    Arrays.fill(bufferL, 0);
    Arrays.fill(bufferR, 0);
    Arrays.fill(out, 0);
  }

  /** Processes the instantaneous value of an audio signal */
  public final double[] process(double sampleL, double sampleR) {
    performProcess(out, sampleL, sampleR);

    head += 1;

    if (head >= bufferLength)
      head = 0;

    return out;
  }

  /** Delegate method which implements the effect logic, writing its result to the out array */
  protected abstract void performProcess(double[] out, double sampleL, double sampleR);

  /** Returns the index wrapped around the buffer size */
  protected int getWrappedIndex(int index) {
    int newIndex = index;

    if (newIndex >= bufferLength) {
      while (newIndex >= bufferLength)
        newIndex -= bufferLength;
    } else if (newIndex < 0) {
      while (newIndex < 0)
        newIndex += bufferLength;
    }

    return newIndex;
  }
}
