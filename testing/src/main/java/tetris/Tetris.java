package tetris;

import dev.gamekit.core.Application;
import dev.gamekit.core.Window;
import dev.gamekit.core.Input;
import dev.gamekit.core.IO;
import dev.gamekit.core.Scene;

import java.awt.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;

import static dev.gamekit.utils.Math.toInt;
import static tetris.GameState.*;
import static tetris.Utils.getIndex;

public class Tetris extends Scene {
  static final int STEP_INTERVAL = 250;
  static final int PADDING_X = 50;
  static final int PADDING_Y = 85;
  static final int COLS = 10;
  static final int ROWS = 20;
  static final int CELL_SIZE = 40;
  static final int BOARD_W = CELL_SIZE * COLS;
  static final int BOARD_H = CELL_SIZE * ROWS;
  static final int QUEUE_CELL_SIZE = 30;
  static final int QUEUE_W = 6 * QUEUE_CELL_SIZE;
  static final int QUEUE_H = 6 * QUEUE_CELL_SIZE;
  static final Color CLEAR_COLOR = new Color(0xff333333, true);
  static final Color TRANSPARENT = new Color(0x00000000, true);
  static final Color GRID_COLOR = new Color(0x773f3f3f, true);
  static final Stroke STROKE_DEFAULT = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  static final Stroke STROKE_OUTLINE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  static final Font BASE_FONT = IO.getResourceFont("game over.ttf");
  static final Font NEXT_FONT = Objects.requireNonNull(BASE_FONT).deriveFont(40f);
  static final Font SCORE_LABEL_FONT = NEXT_FONT;
  static final Font CONTINUE_FONT = NEXT_FONT.deriveFont(28f);
  static final Font SCORE_VALUE_FONT = Objects.requireNonNull(BASE_FONT).deriveFont(80f);
  static final Font PAUSED_FONT = Objects.requireNonNull(BASE_FONT).deriveFont(128f);
  static final int[] SCORING = new int[]{ 40, 100, 300, 1200 };
  static final int PAUSE_PANEL_W = 480;
  static final int PAUSE_PANEL_H = 128;
  static final int GAME_OVER_PANEL_W = 640;
  static final int GAME_OVER_PANEL_H = 128;

  final Queue<Tetromino> queue = new LinkedList<>();
  final CellState[] grid = new CellState[ROWS * COLS];
  final Color[] gridColors = new Color[ROWS * COLS];
  final Random rand = new Random();
  GameState gameState = PLAYING;
  Tetromino tetromino;
  long stepTime = 0;
  int score = 0;

  public Tetris() {
    super("Tetris");

    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLS; col++) {
        int gridIdx = getIndex(row, col, COLS);
        grid[gridIdx] = CellState.FREE;
        gridColors[gridIdx] = TRANSPARENT;
      }
    }

    queue.add(Tetromino.PIECES[rand.nextInt(0, Tetromino.PIECES.length)]);
    queue.add(Tetromino.PIECES[rand.nextInt(0, Tetromino.PIECES.length)]);
    queue.add(Tetromino.PIECES[rand.nextInt(0, Tetromino.PIECES.length)]);
  }

  public static void main(String[] args) throws InterruptedException {
    Application game = new Application("GameKit Tetris") { };
    game.loadScene(new Tetris());
    game.run();
  }

  @Override
  public void update() {
    super.update();

    if (gameState == PLAYING) {
      stepTime += Application.FRAME_TIME;
      if (Input.isKeyDown(Input.KEY_ESCAPE)) gameState = PAUSED;
    } else if (gameState == PAUSED && Input.isKeyDown(Input.KEY_ESCAPE)) {
      gameState = PLAYING;
    } else if (gameState == GAME_OVER && Input.isKeyDown(Input.KEY_R)) {
      Application.getInstance().loadScene(new Tetris());
    }

    if (tetromino != null) {
      if (Input.isKeyDown(Input.KEY_LEFT)) {
        tetromino.move(grid, COLS, Direction.LEFT);
      } else if (Input.isKeyDown(Input.KEY_RIGHT)) {
        tetromino.move(grid, COLS, Direction.RIGHT);
      } else if (Input.isKeyDown(Input.KEY_UP)) {
        tetromino.rotateCW();
      } else if (Input.isKeyDown(Input.KEY_DOWN)) {
        tetromino.rotateCCW();
      }
    }

    if (gameState == PLAYING && stepTime >= STEP_INTERVAL) {
      stepTime = 0;

      if (tetromino == null || !tetromino.move(grid, COLS, Direction.DOWN)) {
        if (tetromino != null) {
          tetromino.placeOnGrid(grid, gridColors, COLS);
          tetromino = null;
        }

        if (eliminateFullRows()) compactGrid();

        spawnTetromino();

        if (!tetromino.move(grid, COLS, Direction.DOWN)) {
          gameState = GAME_OVER;
        }
      }
    }
  }

  private void spawnTetromino() {
    tetromino = new Tetromino(queue.remove());
    queue.add(Tetromino.PIECES[rand.nextInt(0, Tetromino.PIECES.length)]);
  }

  private boolean eliminateFullRows() {
    boolean isDirty = false;
    int linesRemoved = 0;

    for (var row = ROWS - 1; row >= 0; row--) {
      if (isRowFull(row)) {
        for (var col = 0; col < COLS; col++) {
          int gridIdx = getIndex(row, col, COLS);
          grid[gridIdx] = CellState.FREE;
          gridColors[gridIdx] = CLEAR_COLOR;
        }

        linesRemoved++;
        isDirty = true;
      }
    }

    if (linesRemoved > 0) {
      score += SCORING[linesRemoved - 1];
    }

    return isDirty;
  }

  private void compactGrid() {
    int emptyRowTop;

    for (var row = ROWS - 1; row > 0; row--) {
      emptyRowTop = row - 1;

      if (isRowEmpty(row)) {
        while (isRowEmpty(emptyRowTop) && emptyRowTop - 1 >= 0) {
          emptyRowTop--;
        }

        for (var col = 0; col < COLS; col++) {
          int sourceGridIdx = getIndex(emptyRowTop, col, COLS);
          int targetGridIdx = getIndex(row, col, COLS);

          grid[targetGridIdx] = grid[sourceGridIdx];
          gridColors[targetGridIdx] = gridColors[sourceGridIdx];

          grid[sourceGridIdx] = CellState.FREE;
          gridColors[sourceGridIdx] = CLEAR_COLOR;
        }
      }
    }
  }

  private boolean isRowEmpty(int row) {
    for (var col = 0; col < COLS; col++) {
      int gridIdx = getIndex(row, col, COLS);

      if (grid[gridIdx] == CellState.OCCUPIED) {
        return false;
      }
    }

    return true;
  }

  private boolean isRowFull(int row) {
    for (var col = 0; col < COLS; col++) {
      int gridIdx = getIndex(row, col, COLS);

      if (grid[gridIdx] == CellState.FREE) {
        return false;
      }
    }

    return true;
  }

  @Override
  public void render() {
    super.render();

    Graphics2D g = null;
    g.setColor(CLEAR_COLOR);
    g.fillRect(0, 0, Window.getInstance().getDisplayWidth(), Window.getInstance().getDisplayHeight());
    renderQueue(g);
    renderBoard(g);
    renderScore(g);

    if (tetromino != null) {
      renderTetromino(g);
    }

    if (gameState == PAUSED) {
      renderPausePanel(g);
    } else if (gameState == GAME_OVER) {
      renderGameOverPanel(g);
    }
  }

  private void renderTetromino(Graphics2D g) {
    g.translate(PADDING_X, PADDING_Y);

    for (int row = 0; row < tetromino.size; row++) {
      for (int col = 0; col < tetromino.size; col++) {
        int idx = row * tetromino.size + col;
        int state = tetromino.state[idx];

        if (state == 1) {
          g.setColor(tetromino.color);

          g.fillRect(
            (tetromino.getCol() + col) * CELL_SIZE,
            (tetromino.getRow() + row) * CELL_SIZE,
            CELL_SIZE, CELL_SIZE
          );
        }
      }
    }

    g.translate(-PADDING_X, -PADDING_Y);
  }

  private void renderBoard(Graphics2D g) {
    g.translate(PADDING_X, PADDING_Y);

    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLS; col++) {
        int gridIdx = getIndex(row, col, COLS);
        Color cellColor = gridColors[gridIdx];
        CellState cell = grid[gridIdx];

        g.setColor(cell == CellState.OCCUPIED ? cellColor : CLEAR_COLOR);
        g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
      }
    }

    g.setColor(GRID_COLOR);
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

  private void renderQueue(Graphics2D g) {
    int QUEUE_X = Window.getInstance().getDisplayWidth() - QUEUE_W - PADDING_X;

    Tetromino tetromino = Objects.requireNonNull(queue.peek());
    int[] offset = tetromino.getOffset();
    int tetrominoWidthCount = offset[3] - offset[1] + 1;
    int queueCellCount = tetrominoWidthCount + 2;
    int queueCellSize = toInt((double) QUEUE_W / queueCellCount);

    g.translate(QUEUE_X, PADDING_Y);
    g.setStroke(STROKE_DEFAULT);
    g.setColor(GRID_COLOR);

    for (int col = 0; col <= queueCellCount; col++) {
      int x = col * queueCellSize;
      g.drawLine(x, 0, x, QUEUE_H);
    }

    for (int row = 0; row <= queueCellCount + 1; row++) {
      int y = row * queueCellSize;
      g.drawLine(0, y, QUEUE_W, y);
    }

    for (int row = 0; row < tetromino.size; row++) {
      for (int col = 0; col < tetromino.size; col++) {
        int idx = row * tetromino.size + col;
        int state = tetromino.state[idx];

        if (state == 1) {
          g.setColor(tetromino.color);

          g.fillRect(
            (col + 1) * queueCellSize,
            (row + 2) * queueCellSize,
            queueCellSize, queueCellSize
          );
        }
      }
    }

    g.translate(-QUEUE_X, -PADDING_Y);

    g.setFont(NEXT_FONT);
    g.setColor(Color.WHITE);
    g.drawString("NEXT", QUEUE_X + toInt(0.5 * QUEUE_W - 0.5 * g.getFontMetrics().stringWidth("NEXT")), PADDING_Y + 28);

    g.setColor(Color.WHITE);
    g.setStroke(STROKE_OUTLINE);
    g.drawRect(QUEUE_X, PADDING_Y, QUEUE_W, QUEUE_H);
  }

  private void renderScore(Graphics2D g) {
    int SCORE_X = Window.getInstance().getDisplayWidth() - QUEUE_W - PADDING_X;
    int SCORE_Y = PADDING_Y + 256;

    g.translate(SCORE_X, SCORE_Y);

    g.setFont(SCORE_LABEL_FONT);
    g.setColor(Color.WHITE);
    g.drawString("Score", 0, 0);

    g.setFont(SCORE_VALUE_FONT);
    g.drawString(String.valueOf(score), 0, 56);

    g.translate(-SCORE_X, -SCORE_Y);
  }

  private void renderPausePanel(Graphics2D g) {
    int screenW = Window.getInstance().getDisplayWidth();
    int screenH = Window.getInstance().getDisplayHeight();
    int panelX = (screenW - PAUSE_PANEL_W) / 2;
    int panelY = (screenH - PAUSE_PANEL_H) / 2;

    g.setColor(Color.BLACK);
    g.setStroke(STROKE_OUTLINE);
    g.drawRoundRect(panelX, panelY, PAUSE_PANEL_W, PAUSE_PANEL_H, 4, 4);

    g.setColor(CLEAR_COLOR);
    g.setStroke(STROKE_DEFAULT);
    g.fillRoundRect(panelX, panelY, PAUSE_PANEL_W, PAUSE_PANEL_H, 4, 4);

    g.setFont(PAUSED_FONT);
    g.setColor(Color.WHITE);
    g.drawString(
      "PAUSED",
      toInt(panelX + 0.5 * PAUSE_PANEL_W - 0.5 * g.getFontMetrics().stringWidth("PAUSED")),
      toInt(panelY + 0.5 * PAUSE_PANEL_H + 6)
    );

    g.setFont(CONTINUE_FONT);
    g.drawString(
      "Press Escape to continue",
      toInt(panelX + 0.5 * PAUSE_PANEL_W - 0.5 * g.getFontMetrics().stringWidth("Press Escape to continue")),
      toInt(panelY + 0.5 * PAUSE_PANEL_H + 38)
    );
  }

  private void renderGameOverPanel(Graphics2D g) {
    int screenW = Window.getInstance().getDisplayWidth();
    int screenH = Window.getInstance().getDisplayHeight();
    int panelX = (screenW - GAME_OVER_PANEL_W) / 2;
    int panelY = (screenH - GAME_OVER_PANEL_H) / 2;

    g.setColor(Color.BLACK);
    g.setStroke(STROKE_OUTLINE);
    g.drawRoundRect(panelX, panelY, GAME_OVER_PANEL_W, GAME_OVER_PANEL_H, 4, 4);

    g.setColor(CLEAR_COLOR);
    g.setStroke(STROKE_DEFAULT);
    g.fillRoundRect(panelX, panelY, GAME_OVER_PANEL_W, GAME_OVER_PANEL_H, 4, 4);

    g.setFont(PAUSED_FONT);
    g.setColor(Color.RED);
    g.drawString(
      "Game Over",
      toInt(panelX + 0.5 * GAME_OVER_PANEL_W - 0.5 * g.getFontMetrics().stringWidth("Game Over")),
      toInt(panelY + 0.5 * GAME_OVER_PANEL_H + 6)
    );

    g.setFont(CONTINUE_FONT);
    g.setColor(Color.WHITE);
    g.drawString(
      "Score: " + score,
      toInt(panelX + 0.5 * GAME_OVER_PANEL_W - 0.5 * g.getFontMetrics().stringWidth("Score: " + score)),
      toInt(panelY + 0.5 * GAME_OVER_PANEL_H + 38)
    );
  }
}
