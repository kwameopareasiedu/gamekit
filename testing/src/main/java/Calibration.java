import dev.gamekit.core.*;
import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import dev.gamekit.settings.WindowMode;
import dev.gamekit.utils.Position;

import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.gamekit.utils.Math.clamp;

public class Calibration extends Scene {
  private static final int WORLD_WIDTH = 2400;
  private static final int WORLD_HEIGHT = 1400;
  private static final BufferedImage SPRITE = IO.getResourceImage("zainar.png");
  private static final double INTERVAL = Application.FRAME_TIME_MS / 1000.0;
  private static final double FREQ = 0.5;

  private double time;
  private int x = 0, y = 0;

  public Calibration() {
    super("Calibration Test");
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Settings("Calibration Test", Resolution.HD, WindowMode.WINDOWED)
    ) { };
    game.loadScene(new Calibration());
    game.run();
  }

  @Override
  public void update() {
    time += INTERVAL;

    if (Input.isButtonPressed(Input.BUTTON_LMB)) {
      Position mousePos = Input.getMousePosition();
      Position pos = Camera.screenToWorldPosition(mousePos.x, mousePos.y);
      x = pos.x;
      y = pos.y;
    }

    double v = 2 * Math.PI * FREQ * time;
    double vsin = Math.sin(v);

    double x = 50 * vsin;
    double y = 50 * Math.cos(v);
    Camera.lookAt(x, y);
    Camera.setZoom(clamp(1 + vsin, 1, 2));
    //    Camera.lookAt(-200, -100);
    //    Camera.setZoom(1);
  }

  @Override
  public void render() {
    Renderer.setColor(Color.DARK_GRAY);
    Renderer.clear();

    Renderer.setColor(Color.CYAN);
    Renderer.drawRect(x, y, Resolution.SVGA.width, Resolution.SVGA.height);

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
