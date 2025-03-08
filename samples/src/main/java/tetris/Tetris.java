package tetris;

import dev.gamekit.Window;
import dev.gamekit.*;
import dev.gamekit.interfaces.InputListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tetris extends Scene implements InputListener {
  static final int PADDING_X = 50;
  static final int PADDING_Y = 85;
  static final int COLS = 10;
  static final int ROWS = 20;
  static final int CELL_SIZE = 40;
  static final int BOARD_W = CELL_SIZE * COLS;
  static final int BOARD_H = CELL_SIZE * ROWS;
  static final Color CLEAR_COLOR = new Color(0xff333333);
  static final Color TRANSPARENT = new Color(0x00000000);
  static final Stroke STROKE_DEFAULT = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  static final Stroke STROKE_OUTLINE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  final CellState[][] cellStates = new CellState[ROWS][COLS];
  final Color[][] cellColors = new Color[ROWS][COLS];
  final Random rand = new Random();
  Tetromino tetromino;
  long stepTime = 0;

  public Tetris() {
    super("Tetris");

    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLS; col++) {
        cellStates[row][col] = CellState.FREE;
        cellColors[row][col] = TRANSPARENT;
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Application game = new Application(
      new Config.Builder()
        .setTitle("GameKit Tetris")
        .setWindowWidth(720)
        .setWindowHeight(960)
        .build()
    ) { };
    game.loadScene(new Tetris());
    game.run();
  }

  @Override
  protected void onStart() {
    super.onStart();
    Input.registerListener(this);
  }

  @Override
  public void onKeyDown(KeyEvent event) { }

  @Override
  public void onKeyUp(KeyEvent event) {
    if (tetromino != null) {
      if (event.getKeyCode() == KeyEvent.VK_LEFT) {
        tetromino.col -= 1;
      } else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
        tetromino.col += 1;
      } else if (event.getKeyCode() == KeyEvent.VK_UP) {
        tetromino.rotateCW();
      } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
        tetromino.rotateCCW();
      }
    }
  }

  @Override
  protected void onUpdate() {
    super.onUpdate();

    stepTime += Time.FRAME_TIME;

    if (stepTime >= 500) {
      stepTime = 0;

      if (tetromino == null) {
        tetromino = Tetromino.PIECES[rand.nextInt(0, Tetromino.PIECES.length)];
      }

      tetromino.row++;
    }
  }

  @Override
  protected void onRender(Graphics2D g) {
    super.onRender(g);

    g.setColor(CLEAR_COLOR);
    g.fillRect(0, 0, Window.getInstance().getWidth(), Window.getInstance().getHeight());
    renderBoard(g);

    if (tetromino != null) {
      renderTetromino(g);
    }
  }

  @Override
  protected void onDispose() {
    super.onDispose();
    Input.unregisterListener(this);
  }

  private void renderTetromino(Graphics2D g) {
    g.translate(PADDING_X, PADDING_Y);

    for (int row = 0; row < tetromino.size; row++) {
      for (int col = 0; col < tetromino.size; col++) {
        int idx = row * tetromino.size + col;
        int state = tetromino.state[idx];

        if (state == 1) {
          g.setColor(tetromino.color);
          g.fillRect((tetromino.col + col) * CELL_SIZE, (tetromino.row + row) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
      }
    }

    g.translate(-PADDING_X, -PADDING_Y);
  }

  private void renderBoard(Graphics2D g) {
    g.translate(PADDING_X, PADDING_Y);

    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLS; col++) {
        CellState state = cellStates[row][col];

        g.setColor(state == CellState.FIXED ? cellColors[row][col] : TRANSPARENT);
        g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
      }
    }

    g.setColor(Color.GRAY);
    g.setStroke(STROKE_DEFAULT);

    for (int col = 0; col <= COLS; col++) {
      int x = col * CELL_SIZE;
      g.drawLine(x, 0, x, BOARD_H);
    }

    for (int row = 0; row <= ROWS; row++) {
      int y = row * CELL_SIZE;
      g.drawLine(0, y, BOARD_W, y);
    }

    g.setColor(Color.WHITE);
    g.setStroke(STROKE_OUTLINE);
    g.drawRect(0, 0, BOARD_W, BOARD_H);

    g.translate(-PADDING_X, -PADDING_Y);
  }

  enum CellState {
    FREE, // Indicates an unoccupied cell
    FIXED, // Indicates a cell occupied by a block
  }
}
