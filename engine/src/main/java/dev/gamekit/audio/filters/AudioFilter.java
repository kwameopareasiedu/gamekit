package dev.gamekit.audio.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/** {@link AudioFilter} represents objects that can perform some manipulation to a chunk of audio stream data */
public abstract class AudioFilter {
  protected final Logger logger = LogManager.getLogger(getClass());
  protected final double[] out, inputSamples, outputSamples;

  protected AudioFilter(double[] inputSamples, double[] outputSamples) {
    this.inputSamples = inputSamples;
    this.outputSamples = outputSamples;
    out = new double[2];

    Arrays.fill(inputSamples, 0);
    Arrays.fill(outputSamples, 0);
    Arrays.fill(out, 0);
  }

  /** Processes the instantaneous left and right channel values of an audio stream, and returns the results */
  public final double[] process(double sampleL, double sampleR) {
    performProcess(sampleL, sampleR);
    shiftSamplesAndInsertNew(inputSamples, sampleL, sampleR);
    shiftSamplesAndInsertNew(outputSamples, out[0], out[1]);
    return out;
  }

  /** Delegate method which implements the filter processing logic and writes the outputs to the {@link #out} array */
  protected abstract void performProcess(double sampleL, double sampleR);

  /** Shifts given samples to the right by 2, and inserts 2 new samples at the beginning */
  protected void shiftSamplesAndInsertNew(double[] samples, double newL, double newR) {
    System.arraycopy(samples, 0, samples, 2, samples.length - 2);
    samples[0] = newL;
    samples[1] = newR;
  }

  /** Retrieves a delayed output sample value */
  protected double[] getDelayedOutputSamples(double delayMs) {
    int sampleIndex = (int) Math.round(0.001 * delayMs * 2 * (outputSamples.length - 2));
    return new double[]{ outputSamples[sampleIndex], outputSamples[sampleIndex + 1] };
  }
}
