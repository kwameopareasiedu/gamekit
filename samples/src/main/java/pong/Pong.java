package pong;

import dev.gamekit.Config;
import dev.gamekit.Application;

public class Pong extends Application {
  public Pong() {
    super(
      new Config.Builder()
        .setTitle("New Age Pong")
        .setWindowWidth(1280)
        .setWindowHeight(720)
        .build()
    );
  }

  public static void main(String[] args) throws InterruptedException {
    Pong game = new Pong();
    game.loadScene(new PlayScene());
    game.run();
  }
}
