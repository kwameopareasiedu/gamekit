public class Playground {
  public static void main(String[] args) throws InterruptedException {
    int[] buffer = new int[10];

    for (int i = -10; i < 0; i++) {
      System.out.printf("%d => %d%n", i, getWrappedIndex(buffer, i));
    }
//    double[] noise = new double[(int) Audio.SAMPLE_RATE];
//
//    for (int i = 0; i < noise.length; i++) {
//      noise[i] = Short.MAX_VALUE * (Math.random() * 2 - 1);
//    }
//
//    AudioEffect filter = new DelayFilter(0.1);
//
//    for (int i = 0; i < noise.length; i += 2) {
//      double[] out = filter.process(noise[i], noise[i + 1]);
//      Thread.sleep(100);
//    }
  }

  protected static int getWrappedIndex(int[] buffer, int index) {
    int newIndex = index;

    if (newIndex >= buffer.length) {
      while (newIndex >= buffer.length)
        newIndex -= buffer.length;
    } else if (newIndex < 0) {
      while (newIndex < 0)
        newIndex += buffer.length;
    }

    return newIndex;
  }
}
