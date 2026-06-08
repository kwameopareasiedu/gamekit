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
| `onLayer`            | Creates a separate context to draw on a different layer               |

> In screen-space, drawing originates from the top-left of the shape by default. In GameKit, all drawing is offset to
> the center of the shape, hence the term **center-origin**.

## Render Modifiers

Each of the static methods in `Renderer` can further be enhanced with modifier methods which customize their
operation. These modifiers are defined in the table below:

| Method Call         | Description                                                                             |
|---------------------|-----------------------------------------------------------------------------------------|
| `withColor`         | Sets the foreground color of the draw call                                              |
| `withStroke`        | Sets the border stroke (width and type) of the draw call                                |
| `withPaint`         | Sets the paint of the draw call. Paint allows drawing of gradients                      |
| `withClip`          | Sets the clip region of the draw call which prevents drawing outside the specified area |
| `withRotation`      | Sets the clockwise rotation in radian of the draw call                                  |
| `withOpacity`       | Sets the opacity of the draw call                                                       |
| `withInterpolation` | Sets the image interpolation of the draw image call                                     |
| `withMask`          | Sets the alpha mask of the draw image call                                              |
| `withTarget`        | Sets the secondary render target of the draw image call                                 |

## Render Layers

The `Renderer` provides 64 layers (0 to 63) on which drawing can take place. This allows you to control the order in
which draw calls are processed. Layers with higher indices are drawn in front of layers with lower indices and by
default rendering is done on layer 0.

To draw on a particular layer, use `Renderer.onLayer()` to create a new layer context as shown below:

```java
Renderer.onLayer(2, () -> {
  // Any Renderer command here will be drawn on the 3rd layer (index 2).
  // Items drawn to layer 2 will appear in front of layer 0 and layer 1.
  Renderer.fillRect(0, 0, 48, 16);
  ...
});
```

## Image Masking

The `Renderer` allows you to mask one image with another. Masking is where the alpha layer of an image determines
which portions of another image is visible.

When performing masking, at the portions of the mask where the alpha = 1.0, the pixels in the corresponding area of the
underlying image are cleared and if the alpha is 0.0, the pixels in the overlapping area are unchanged.

In other words, the transparent parts of the mask are where the underlying image is drawn. Likewise, the opaque parts of
the mask are where underlying image is hidden.

To set a mask on a **draw image call**, use the `withMask()` modifier method as shown below:

```java
BufferedImage mask = ...;

Renderer.drawImage(...).withMask(target);
```

## Custom Render Targets

As stated initially, **draw calls** are objects dispatched by the `Renderer` which perform actual drawing onto the
screen, which is the primary render target.

The custom render targets feature allows you to set a `BufferedImage` as a secondary render target to a **draw image
call**. This means, after drawing the image to the screen, the draw call will also draw to the provided target.

This is useful if you need the rendered image for computation within your game.

To set a render target on a **draw image call**, use the `withTarget()` modifier method as shown below:

```java
BufferedImage target = ...;

Renderer.drawImage(...).withTarget(target);
```

## Rendering Sample

The sample below is a scene which uses the renderer methods and modifiers shown above:

```java
import dev.gamekit.core.Application;
import dev.gamekit.core.IO;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.ImageInterpolation;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RendererScene extends Scene {
  private static final BufferedImage IMAGE = IO.getImage("test.jpg");
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
      .withColor(Color.GREEN); // Draw green line from (0, 0) to (10, 10) on layer 0

    Renderer.drawVerticalLine(0, -20, -4)
      .withStroke(DEFAULT_STROKE); // Draw vertical line from (0, -20) to (0, -4) using the DEFAULT_STROKE object on layer 0

    Renderer.drawHorizontalLine(0, 20, 5)
      .withColor(Color.GREEN)
      .withStroke(DEFAULT_STROKE); // Draw a green horizontal line from (0, 5) to (20, 5) using the DEFAULT_STROKE object on layer 0

    Renderer.onLayer(1, () -> {
      Renderer.drawRect(0, 0, 50, 20); // A 50x20 rect centered at (0, 0) on layer 1
      Renderer.fillRect(0, 0, 48, 16);
  
      Renderer.drawRoundRect(50, 50, 75, 20, 4, 4); // A 75x20 rect centered at (50, 50) with horizontal arc radius 4 and vertical arc radius 4 on layer 1
      Renderer.fillRoundRect(50, 50, 75, 20, 4, 4);
    });

    Renderer.drawOval(10, 20, 50, 30); // A 50x30 oval centered at (10, 20) on layer 0
    Renderer.fillOval(10, 20, 48, 16);

    Renderer.drawCircle(30, 30, 50); // A 50px radius circle centered at (30, 30) on layer 0
    Renderer.fillCircle(30, 30, 48);

    Renderer.drawImage(IMAGE, 100, 100, 300, 200)     // A 300x200 image centered at (100, 100)...
      .withRotation(100, 100, 0.5 * Math.PI)          // rotated by 90 degrees about (100, 100)...
      .withInterpolation(ImageInterpolation.NEAREST); // with nearest neighbour image interpolation on layer 0

    Renderer.drawPolygon(POLYGON_POINTS)
      .withClip(0, 0, 100, 100); // Polygon points cliped to the area defined by top-left (0, 0) and width and height of 100x100 on layer 0
    Renderer.fillPolygon(POLYGON_POINTS);
  }
}
```
