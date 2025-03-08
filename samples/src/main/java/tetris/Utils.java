package tetris;

public class Utils {
  private Utils() { }

  public static int getIndex(int row, int col, int cols) {
    if (col >= cols) {
      throw new ArrayIndexOutOfBoundsException(
        String.format("Invalid column index: %d", col)
      );
    }

    return row * cols + col;
  }
}
