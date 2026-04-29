import dev.gamekit.audio.effects.AudioEffect;
import dev.gamekit.audio.effects.DelayFilter;
import dev.gamekit.core.Audio;

public class Playground {
  public static void main(String[] args) throws InterruptedException {
    double[] noise = new double[(int) Audio.SAMPLE_RATE];

    for (int i = 0; i < noise.length; i++) {
      noise[i] = Short.MAX_VALUE * (Math.random() * 2 - 1);
    }

    AudioEffect filter = new DelayFilter(0.1);

    for (int i = 0; i < noise.length; i += 2) {
      double[] out = filter.process(noise[i], noise[i + 1]);
      Thread.sleep(100);
    }
  }
}
