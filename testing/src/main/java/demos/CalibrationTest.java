package demos;

import dev.gamekit.core.*;
import dev.gamekit.utils.Config;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Resolution;

import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.gamekit.utils.Math.clamp;

public class CalibrationTest extends Scene {
  private static final int WORLD_WIDTH = 2400;
  private static final int WORLD_HEIGHT = 1400;
  private static final Font LEXEND_FONT = IO.getResourceFont("lexend-regular.ttf").deriveFont(12f);
  private static final BufferedImage SPRITE = IO.getResourceImage("zainar.png");

  private double time;
  private int x, y;

  public CalibrationTest() {
    super("Calibration Test");
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Config("Calibration Test", Resolution.SVGA, false)
    ) { };
    game.loadScene(new CalibrationTest());
    game.run();
  }

  @Override
  protected void start() {
    super.start();

    Position pos = Camera.pointToWorldPosition(0, 0);
    x = pos.x;
    y = pos.y;
  }

  @Override
  public void update() {
    super.update();

    time += 0.025;

    if (Input.isButtonPressed(Input.BUTTON_LMB)) {
      Position mousePos = Input.getMousePosition();
      Position pos = Camera.screenToWorldPosition(
        mousePos.x,
        mousePos.y
      );
      x = pos.x;
      y = pos.y;
    }

    double x = 50 * Math.sin(time);
    double y = 50 * Math.cos(time);
    Camera.lookAt(x, y);
    Camera.setZoom(clamp(1 + Math.sin(time), 1, 2));
    //    Camera.lookAt(-200, -100);
    //    Camera.setZoom(1);
  }

  @Override
  public void render() {
    super.render();
    Renderer.setColor(Color.DARK_GRAY);
    Renderer.clear();

    Renderer.setColor(Color.BLUE);
    Renderer.drawLineH(-WORLD_WIDTH / 2, WORLD_WIDTH / 2, 0);
    Renderer.setColor(Color.BLUE);
    Renderer.drawLineV(0, -WORLD_HEIGHT / 2, WORLD_HEIGHT / 2);

    // Top right quadrant
    Renderer.setColor(Color.RED);
    Renderer.fillRect(100, 100, 20, 20);
    Renderer.setColor(Color.RED);
    Renderer.drawRect(100, 100, 30, 30);

    // Top left quadrant
    Renderer.setColor(Color.YELLOW);
    Renderer.fillOval(-100, 100, 20, 30);
    Renderer.setColor(Color.YELLOW);
    Renderer.drawOval(-100, 100, 30, 40);

    // Bottom left quadrant
    Renderer.setColor(Color.GREEN);
    Renderer.fillRoundRect(-100, -100, 50, 50, 10, 10);
    Renderer.setColor(Color.GREEN);
    Renderer.drawRoundRect(-100, -100, 60, 60, 10, 10);

    // Bottom right quadrant
    Renderer.setColor(Color.BLUE);
    Renderer.fillCircle(100, -100, 20);
    Renderer.setColor(Color.BLUE);
    Renderer.drawCircle(100, -100, 25);

    for (int i = 0; i <= WORLD_WIDTH / 2; i += 50) {
      Renderer.setColor(Color.CYAN);
      Renderer.drawLineH(0, 10, i);
      Renderer.setColor(Color.CYAN);
      Renderer.drawLineV(i, 0, -10);
    }

    Renderer.setColor(Color.MAGENTA);
    Renderer.drawRect(0, 0, 10, 10);

    Renderer.drawImage(SPRITE, x, y, 10, 10);
  }
}
