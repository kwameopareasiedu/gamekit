public class Playground {
  public static void main(String[] args) throws InterruptedException {
    int[] buffer = new int[10];

    for (int i = -10; i < 0; i++) {
      System.out.printf("%d => %d%n", i, getWrappedIndex(buffer, i));
    }
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
