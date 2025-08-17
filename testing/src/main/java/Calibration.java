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
  private static final double INTERVAL = Constants.FRAME_INTERVAL_MS / 1000.0;
  private static final double FREQ = 0.5;

  private double time;
  private int x = 0, y = 0;
  private final int[] polygonPoints;
  private final int[] filledPolygonPoints;

  public Calibration() {
    super("Calibration Test");

    polygonPoints = new int[]{ 90, -190, 110, -110, 50, -99 };
    filledPolygonPoints = new int[]{ 9, -19, 11, -11, 5, -10 };
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
    Renderer.clear(Color.DARK_GRAY);
    Renderer.drawRect(x, y, Resolution.SVGA.width(), Resolution.SVGA.height()).withColor(Color.CYAN);

    Renderer.drawHorizontalLine(-WORLD_WIDTH / 2, WORLD_WIDTH / 2, 0).withColor(Color.BLUE);
    Renderer.drawVerticalLine(0, -WORLD_HEIGHT / 2, WORLD_HEIGHT / 2).withColor(Color.BLUE);

    // Top right quadrant
    Renderer.fillRect(100, 100, 20, 20).withColor(Color.RED);
    Renderer.drawRect(100, 100, 30, 30).withColor(Color.RED);

    // Top left quadrant
    Renderer.fillOval(-100, 100, 20, 30).withColor(Color.YELLOW);
    Renderer.drawOval(-100, 100, 30, 40).withColor(Color.YELLOW);

    // Bottom left quadrant
    Renderer.fillRoundRect(-100, -100, 50, 50, 10, 10).withColor(Color.GREEN);
    Renderer.drawRoundRect(-100, -100, 60, 60, 10, 10).withColor(Color.GREEN);

    // Bottom right quadrant
    Renderer.fillCircle(100, -100, 20).withColor(Color.BLUE);
    Renderer.drawCircle(100, -100, 25).withColor(Color.BLUE);

    Renderer.drawPolygon(polygonPoints).withColor(Color.ORANGE);
    Renderer.fillPolygon(filledPolygonPoints).withColor(Color.PINK);

    for (int i = 0; i <= WORLD_WIDTH / 2; i += 50) {
      Renderer.drawHorizontalLine(0, 10, i).withColor(Color.CYAN);
      Renderer.drawVerticalLine(i, 0, -10).withColor(Color.CYAN);
    }

    Renderer.drawRect(0, 0, 10, 10).withColor(Color.MAGENTA);

    Renderer.drawImage(SPRITE, x, y, 10, 10);
  }
}
