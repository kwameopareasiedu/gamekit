package tetris;

import java.awt.*;

public class Utils {
  public static final Color I = Color.GREEN;
  public static final Color O = new Color(0xff333333);

  public static final Color[] INITIAL_GRID_COLORS = new Color[]{
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, O, O, O, O, O, O, O, O,
    O, O, I, O, O, O, O, O, O, O,
    I, O, I, O, O, O, O, O, I, O,
    I, I, I, I, I, O, O, I, I, I,
    I, I, I, I, I, O, O, I, I, I,
  };

  public static int getIndex(int row, int col, int cols) {
    if (col >= cols) {
      throw new ArrayIndexOutOfBoundsException(
        String.format("Invalid column index: %d", col)
      );
    }

    return row * cols + col;
  }
}
