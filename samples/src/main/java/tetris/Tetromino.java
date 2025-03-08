package tetris;

import dev.gamekit.Utils;

import java.awt.*;

public class Tetromino {
  public static final Tetromino O_PIECE = new Tetromino(
    2, Color.YELLOW,
    new int[][]{
      new int[]{
        1, 1,
        1, 1 },
      new int[]{
        1, 1,
        1, 1 },
      new int[]{
        1, 1,
        1, 1 },
      new int[]{
        1, 1,
        1, 1 },
    },
    new int[]{
      0, 0, 0, 0
    }
  );

  public static final Tetromino I_PIECE = new Tetromino(
    4, Color.CYAN,
    new int[][]{
      new int[]{
        0, 0, 0, 0,
        1, 1, 1, 1,
        0, 0, 0, 0,
        0, 0, 0, 0 },
      new int[]{
        0, 0, 1, 0,
        0, 0, 1, 0,
        0, 0, 1, 0,
        0, 0, 1, 0 },
      new int[]{
        0, 0, 0, 0,
        0, 0, 0, 0,
        1, 1, 1, 1,
        0, 0, 0, 0 },
      new int[]{
        0, 1, 0, 0,
        0, 1, 0, 0,
        0, 1, 0, 0,
        0, 1, 0, 0 },
    },
    new int[]{
      2, 0, 1, 0
    }
  );

  public static final Tetromino L_PIECE = new Tetromino(
    3, Color.ORANGE,
    new int[][]{
      new int[]{
        0, 0, 1,
        1, 1, 1,
        0, 0, 0 },
      new int[]{
        0, 1, 0,
        0, 1, 0,
        0, 1, 1 },
      new int[]{
        0, 0, 0,
        1, 1, 1,
        1, 0, 0 },
      new int[]{
        1, 1, 0,
        0, 1, 0,
        0, 1, 0 },
    },
    new int[]{
      1, 0, 0, 0
    }
  );

  public static final Tetromino J_PIECE = new Tetromino(
    3, Color.BLUE,
    new int[][]{
      new int[]{
        1, 0, 0,
        1, 1, 1,
        0, 0, 0 },
      new int[]{
        0, 1, 1,
        0, 1, 0,
        0, 1, 0 },
      new int[]{
        0, 0, 0,
        1, 1, 1,
        0, 0, 1 },
      new int[]{
        0, 1, 0,
        0, 1, 0,
        1, 1, 0 },
    },
    new int[]{
      1, 0, 0, 0
    }
  );

  public static final Tetromino S_PIECE = new Tetromino(
    3, Color.GREEN,
    new int[][]{
      new int[]{
        0, 1, 1,
        1, 1, 0,
        0, 0, 0 },
      new int[]{
        0, 1, 0,
        0, 1, 1,
        0, 0, 1 },
      new int[]{
        0, 0, 0,
        0, 1, 1,
        1, 1, 0 },
      new int[]{
        1, 0, 0,
        1, 1, 0,
        0, 1, 0 },
    },
    new int[]{
      1, 0, 0, 0
    }
  );

  public static final Tetromino Z_PIECE = new Tetromino(
    3, Color.RED,
    new int[][]{
      new int[]{
        1, 1, 0,
        0, 1, 1,
        0, 0, 0 },
      new int[]{
        0, 0, 1,
        0, 1, 1,
        0, 1, 0 },
      new int[]{
        0, 0, 0,
        1, 1, 0,
        0, 1, 1 },
      new int[]{
        0, 1, 0,
        1, 1, 0,
        1, 0, 0 },
    },
    new int[]{
      1, 0, 0, 0
    }
  );

  public static final Tetromino T_PIECE = new Tetromino(
    3, Color.MAGENTA,
    new int[][]{
      new int[]{
        0, 1, 0,
        1, 1, 1,
        0, 0, 0 },
      new int[]{
        0, 1, 0,
        0, 1, 1,
        0, 1, 0 },
      new int[]{
        0, 0, 0,
        1, 1, 1,
        0, 1, 0 },
      new int[]{
        0, 1, 0,
        1, 1, 0,
        0, 1, 0 },
    },
    new int[]{
      1, 0, 0, 0
    }
  );

  public static final Tetromino[] PIECES = new Tetromino[]{
    O_PIECE, I_PIECE, L_PIECE, J_PIECE, S_PIECE, Z_PIECE, T_PIECE,
  };

  public int row;
  public int col;
  public final int size;
  public final Color color;

  private Orientation orientation;
  private final int[][] states;
  public int[] state;
  public int[] stateRowOffset;

  private Tetromino(int size, Color color, int[][] states, int[] stateRowOffset) {
    this.size = size;
    this.color = color;
    this.states = states;
    this.stateRowOffset = stateRowOffset;
    state = states[0];
    orientation = Orientation.UP;
  }

  public int getRowOffset() {
    int ord = orientation.ordinal();
    return stateRowOffset[ord];
  }

  public void rotateCW() { rotate(1); }

  public void rotateCCW() { rotate(-1); }

  private void rotate(int offset) {
    int ord = orientation.ordinal();
    int newOrd = Utils.cycle(ord + offset, 0, 3);
    orientation = Orientation.valueOf(newOrd);
    state = states[newOrd];
  }

  enum Orientation {
    UP, RIGHT, DOWN, LEFT;

    static Orientation valueOf(int val) {
      int cycledVal = val % 4;

      return switch (cycledVal) {
        case 0 -> UP;
        case 1 -> RIGHT;
        case 2 -> DOWN;
        default -> LEFT;
      };
    }
  }
}
