package calibration;

import dev.gamekit.core.Application;
import dev.gamekit.core.Camera;
import dev.gamekit.core.IO;
import dev.gamekit.core.Renderer;
import dev.gamekit.scene.Scene;

import java.awt.*;

import static dev.gamekit.utils.MathUtils.clamp;

public class CalibrationTest extends Scene {
  private static final int WORLD_WIDTH = 2400;
  private static final int WORLD_HEIGHT = 1400;
  private static final Font LEXEND_FONT = IO.loadFont("lexend-regular.ttf").deriveFont(12f);

  private double time;

  public CalibrationTest() {
    super("Calibration Test");
  }

  public static void main(String[] args) {
    Application game = new Application("Calibration Test", 800, 600) { };
    game.loadScene(new CalibrationTest());
    game.run();
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

    time += 0.025;
    double x = 50 * Math.sin(time);
    double y = 50 * Math.cos(time);
    Camera.getInstance().lookAt(x, y);
    Camera.getInstance().setZoom(clamp(1 + Math.sin(time), 1, 2));
    //    Camera.getInstance().lookAt(-200, -100);
  }

  @Override
  public void onRender() {
    super.onRender();
    Renderer.setColor(Color.BLACK);
    Renderer.clear();

    Renderer.beginGroup();
    Renderer.setColor(Color.BLUE);
    Renderer.lineH(-WORLD_WIDTH / 2, 0, WORLD_WIDTH / 2);
    Renderer.lineV(0, -WORLD_HEIGHT / 2, WORLD_HEIGHT / 2);
    Renderer.endGroup();

    // Top right quadrant
    Renderer.beginGroup();
    Renderer.setColor(Color.RED);
    Renderer.rect(100, 100, 20, 20, true);
    Renderer.rect(100, 100, 30, 30);
    Renderer.endGroup();

    // Top left quadrant
    Renderer.beginGroup();
    Renderer.setColor(Color.YELLOW);
    Renderer.oval(-100, 100, 20, 30, true);
    Renderer.oval(-100, 100, 30, 40);
    Renderer.endGroup();

    // Bottom left quadrant
    Renderer.beginGroup();
    Renderer.setColor(Color.GREEN);
    Renderer.roundRect(-100, -100, 50, 50, 10, 10, true);
    Renderer.roundRect(-100, -100, 60, 60, 10, 10);
    Renderer.endGroup();

    // Bottom right quadrant
    Renderer.beginGroup();
    Renderer.setColor(Color.BLUE);
    Renderer.circle(100, -100, 20, true);
    Renderer.circle(100, -100, 25);
    Renderer.endGroup();

    for (int i = 0; i <= WORLD_WIDTH / 2; i += 50) {
      Renderer.beginGroup();
      Renderer.setColor(Color.CYAN);
      Renderer.lineH(0, i, 10);
      Renderer.lineV(i, 0, -10);
      Renderer.endGroup();

      //      Renderer.beginGroup();
      //      Renderer.useTextLayer();
      //      Renderer.setFont(LEXEND_FONT);
      //      Renderer.text(i, i, 0);
      //      Renderer.text(i, 0, i);
      //      Renderer.endGroup();
    }
  }
}
