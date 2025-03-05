package tetris;

import dev.gamekit.Scene;
import dev.gamekit.Time;
import dev.gamekit.Window;

import java.awt.*;

public class MainScene extends Scene {
  private static final Color CLEAR_COLOR = new Color(0xff333333);

  private long stepTime = 0;
  private final Board board;

  public MainScene() {
    super("Game");
    board = new Board();
  }

  @Override
  protected void start() {
    super.start();

    entities.add(board);
  }

  @Override
  protected void update() {
    stepTime += Time.FRAME_TIME;

    if (stepTime >= 1000) {
      stepTime = 0;
    }

    super.update();
  }

  @Override
  protected void render(Graphics2D g) {
    Window.getInstance().clearScreen(CLEAR_COLOR);

    super.render(g);
  }
}
