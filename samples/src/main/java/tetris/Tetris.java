package tetris;

import dev.gamekit.Application;
import dev.gamekit.Config;

public class Tetris extends Application {
  public Tetris() {
    super(
      new Config.Builder()
        .setTitle("GameKit Tetris")
        .setWindowWidth(720)
        .setWindowHeight(960)
        .build()
    );
  }

  public static void main(String[] args) throws InterruptedException {
    Tetris game = new Tetris();
    game.loadScene(new MainScene());
    game.run();
  }
}
