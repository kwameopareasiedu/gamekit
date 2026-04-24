package dev.gamekit.audio.filters;

/** {@link AudioFilter} represents objects that can perform some manipulation to a chunk of audio stream data */
public interface AudioFilter {
  /** Called to process the left and right channel values of an audio stream */
  double[] process(double leftValue, double rightValue);
}
