package pong;

import dev.gamekit.core.Application;
import dev.gamekit.settings.*;

public class Pong extends Application {
  public Pong() {
    super(
      new Settings(
        "GameKit - Pong",
        Resolution.HD,
        Antialiasing.ON,
        TextAntialiasing.ON,
        ImageInterpolation.BILINEAR,
        RenderingStrategy.SPEED,
        Dithering.OFF
      )
    );
  }

  public static void main(String[] args) {
    Pong pong = new Pong();
    pong.loadScene(new PlayScene());
    pong.run();
  }
}
