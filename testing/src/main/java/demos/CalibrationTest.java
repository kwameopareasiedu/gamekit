package demos;

import dev.gamekit.core.Application;
import dev.gamekit.core.Camera;
import dev.gamekit.core.IO;
import dev.gamekit.core.Renderer;
import dev.gamekit.scene.Scene;

import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.gamekit.utils.MathUtils.clamp;

public class CalibrationTest extends Scene {
  private static final int WORLD_WIDTH = 2400;
  private static final int WORLD_HEIGHT = 1400;
  private static final Font LEXEND_FONT = IO.loadFontResource("lexend-regular.ttf").deriveFont(12f);
  private static final BufferedImage SPRITE = IO.loadImageResource("zainar.png");

  private double time;

  public CalibrationTest() {
    super("Calibration Test");
  }

  public static void main(String[] args) {
    Application game = new Application("Calibration Test") { };
    game.loadScene(new CalibrationTest());
    game.run();
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

//    time += 0.025;
//    double x = 50 * Math.sin(time);
//    double y = 50 * Math.cos(time);
//    Camera.getInstance().lookAt(x, y);
//    Camera.getInstance().setZoom(clamp(1 + Math.sin(time), 1, 2));
    //    Camera.getInstance().lookAt(-200, -100);
        Camera.getInstance().setZoom(2.5);
  }

  @Override
  public void onRender() {
    super.onRender();
    Renderer.setColor(Color.BLACK);
    Renderer.clear();

    Renderer.beginGroup();
    Renderer.setColor(Color.BLUE);
    Renderer.drawLineH(-WORLD_WIDTH / 2, 0, WORLD_WIDTH / 2);
    Renderer.drawLineV(0, -WORLD_HEIGHT / 2, WORLD_HEIGHT / 2);
    Renderer.endGroup();

    // Top right quadrant
    Renderer.beginGroup();
    Renderer.setColor(Color.RED);
    Renderer.fillRect(100, 100, 20, 20);
    Renderer.drawRect(100, 100, 30, 30);
    Renderer.endGroup();

    // Top left quadrant
    Renderer.beginGroup();
    Renderer.setColor(Color.YELLOW);
    Renderer.fillOval(-100, 100, 20, 30);
    Renderer.drawOval(-100, 100, 30, 40);
    Renderer.endGroup();

    // Bottom left quadrant
    Renderer.beginGroup();
    Renderer.setColor(Color.GREEN);
    Renderer.fillRoundRect(-100, -100, 50, 50, 10, 10);
    Renderer.drawRoundRect(-100, -100, 60, 60, 10, 10);
    Renderer.endGroup();

    // Bottom right quadrant
    Renderer.beginGroup();
    Renderer.setColor(Color.BLUE);
    Renderer.fillCircle(100, -100, 20);
    Renderer.drawCircle(100, -100, 25);
    Renderer.endGroup();

    for (int i = 0; i <= WORLD_WIDTH / 2; i += 50) {
      Renderer.beginGroup();
      Renderer.setColor(Color.CYAN);
      Renderer.drawLineH(0, i, 10);
      Renderer.drawLineV(i, 0, -10);
      Renderer.endGroup();

      //      Renderer.beginGroup();
      //      Renderer.useTextLayer();
      //      Renderer.setFont(LEXEND_FONT);
      //      Renderer.text(i, i, 0);
      //      Renderer.text(i, 0, i);
      //      Renderer.endGroup();
    }

    Renderer.setColor(Color.MAGENTA);
    Renderer.drawRect(0, 0, 10, 10);
    Renderer.drawImage(SPRITE, -100, -100, 10, 10);
  }
}
