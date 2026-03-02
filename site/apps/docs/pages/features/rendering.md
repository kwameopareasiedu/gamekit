# Renderer

_[Back To Features](./overview.md)_

A game wouldn't really be much of a game, if you couldn't see anything on the screen.

In GameKit, the ability to draw anything to the screen is handled by the static `Renderer` class. `Renderer` works by
dispatching **draw calls** to the render queue. In the draw thread, these calls are evaluated and applied to the game
window.

## Static Methods

| Method               | Description                                                           |
|----------------------|-----------------------------------------------------------------------|
| `clear`              | Clears the visible window area with a color                           |
| `drawLine`           | Draws a line between two pairs of coordinates                         |
| `drawVerticalLine`   | Draws a vertical line between two y-coordinates and an x-coordinate   |
| `drawHorizontalLine` | Draws a horizontal line between two x-coordinates and an y-coordinate |
| `fillRect`           | Draws a **center-origin** rectangle filled with a color               |
| `drawRect`           | Draws a **center-origin** rectangle outline                           |
| `fillRoundRect`      | Draws a **center-origin** rounded rectangle filled with a color       |
| `drawRoundRect`      | Draws a **center-origin** rounded rectangle outline                   |
| `fillOval`           | Draws a **center-origin** oval filled with a color                    |
| `drawOval`           | Draws a **center-origin** oval outline                                |
| `fillCircle`         | Draws a **center-origin** circle filled with a color                  |
| `drawCircle`         | Draws a **center-origin** circle outline                              |
| `drawImage`          | Draws a **center-origin** image                                       |
| `fillPolygon`        | Draws a polygon from a point array filled with a color                |
| `drawPolygon`        | Draws a polygon outline from a point array                            |

> In screen-space, drawing originates from the top-left of the shape by default. In GameKit, all drawing is offset to
> the center of the shape, hence the term **center-origin**.

## Render Attributes

Each of the static methods in `Renderer` can further be enhanced with attribute methods which customize their
operation. These attributes are defined in the table below:

| Method Call         | Description                                                                             |
|---------------------|-----------------------------------------------------------------------------------------|
| `withColor`         | Sets the foreground color of the draw call                                              |
| `withStroke`        | Sets the border stroke (width and type) of the draw call                                |
| `withPaint`         | Sets the paint of the draw call. Paint allows drawing of gradients                      |
| `withClip`          | Sets the clip region of the draw call which prevents drawing outside the specified area |
| `withRotation`      | Sets the clockwise rotation in radian of the draw call                                  |
| `withOpacity`       | Sets the opacity of the draw call                                                       |
| `withInterpolation` | Sets the image interpolation of the draw image call                                     |

## Rendering Sample

The sample below is a scene which uses the renderer methods and attributes shown above:

```java
import dev.gamekit.core.Application;
import dev.gamekit.core.IO;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.ImageInterpolation;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RendererScene extends Scene {
  private static final BufferedImage IMAGE = IO.getResourceImage("test.jpg");
  private static final int[] POLYGON_POINTS = new int[]{ 90, -190, 110, -110, 50, -99 };
  private static final Stroke DEFAULT_STROKE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  public RendererScene() {
    super("Rendering Showcase");
  }

  public static void main(String[] args) {
    Application game = new Application("Renderer Showcase") { };
    game.loadScene(new RendererScene());
    game.run();
  }

  @Override
  protected void render() {
    Renderer.clear(Color.DARK_GRAY);

    Renderer.drawLine(0, 0, 10, 10)
      .withColor(Color.GREEN); // Draw green line from (0, 0) to (10, 10)

    Renderer.drawVerticalLine(0, -20, -4)
      .withStroke(DEFAULT_STROKE); // Draw vertical line from (0, -20) to (0, -4) using the DEFAULT_STROKE object

    Renderer.drawHorizontalLine(0, 20, 5)
      .withColor(Color.GREEN)
      .withStroke(DEFAULT_STROKE); // Draw a green horizontal line from (0, 5) to (20, 5) using the DEFAULT_STROKE object

    Renderer.drawRect(0, 0, 50, 20); // A 50x20 rect centered at (0, 0)
    Renderer.fillRect(0, 0, 48, 16);

    Renderer.drawRoundRect(50, 50, 75, 20, 4, 4); // A 75x20 rect centered at (50, 50) with horizontal arc radius 4 and vertical arc radius 4
    Renderer.fillRoundRect(50, 50, 75, 20, 4, 4);

    Renderer.drawOval(10, 20, 50, 30); // A 50x30 oval centered at (10, 20)
    Renderer.fillOval(10, 20, 48, 16);

    Renderer.drawCircle(30, 30, 50); // A 50px radius circle centered at (30, 30)
    Renderer.fillCircle(30, 30, 48);

    Renderer.drawImage(IMAGE, 100, 100, 300, 200)     // A 300x200 image centered at (100, 100)...
      .withRotation(100, 100, 0.5 * Math.PI)          // rotated by 90 degrees about (100, 100)...
      .withInterpolation(ImageInterpolation.NEAREST); // with nearest neighbour image interpolation

    Renderer.drawPolygon(POLYGON_POINTS)
      .withClip(0, 0, 100, 100); // Polygon points cliped to the area defined by top-left (0, 0) and width and height of 100x100
    Renderer.fillPolygon(POLYGON_POINTS);
  }
}
```
