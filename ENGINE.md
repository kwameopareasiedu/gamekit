# Engine Docs

This sections contains the full in-depth documentation of the classes and interactions of classes that make GameKit
function.

## Application

`dev.gamekit.core.Application`

`Application` is the heart of a GameKit program. Your game must extend this class to do anything with the engine.

`Application` runs a fixed-step game loop which processes input, updates and renders the current scene and runs
end-of-frame tasks. This fixed time step approach makes sure during lag spikes don't overshoot logic and physics
updates.

### Startup

When your game is run, it sets up the engine internals which include:

- Creating the window object
- Attaching keyboard and mouse listeners to the window
- Making the window visible

### Game Loop

After setup, the game loop starts which runs the following until the application exits:

- Capture keyboard and mouse input
- Update running animations
- Update active timeout tasks
- Update the active scene
- Render the current scene to the window
- Perform scheduled end of frame tasks

The game loops continues running until the close event is received on the window. When this happens, the loop is halted
and the application runs its dispose logic before exiting. You can override the `onDispose()` to custom cleanup before
the application exits.

To access the application from anywhere in the application, use `Application.getInstance()`.

## Window

`dev.gamekit.core.Window`

`Window` manages the view frame in which the game is displayed. Since this is a Java Swing project, the frame is
a [JFrame](https://docs.oracle.com/javase/8/docs/api/javax/swing/JFrame.html) object.

### Screen Space vs World Space

In screen space, the origin, `(0,0)` is at the top-left of the screen, `+x` is to the right of the screen and `+y` is
down the screen.

This doesn't translate to world/scene space which uses a cartesian coordinate system where the origin is at the center
of the screen, `+x` is to the right and `+y` is upwards.

![](./docs/assets/screen-space-vs-world-space.png)

### Multiple Render Targets

Due to this, `Window` provides
two [BufferedImage](https://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferedImage.html) targets for
rendering. One configured for screen space rendering for your UI and the other configured for world rendering (I.e. your
scene).

### Multi Resolution & Fullscreen Support

`Window` supports multiple resolution, ranging from 640x480 to native resolution. Higher resolutions appear best but at
a performance cost and vice versa.

You can also configure your game to launch in a windowed mode or full screen.This is done before the application starts
and cannot be changed at runtime.

## Renderer

`dev.gamekit.core.Renderer`

So `Application` manages your game and `Window` manages the view frame, but how do we get stuff drawn unto the screen?

That's where `Renderer` comes in. It's a static utility class containing all the supported draw functions of the engine.
This includes drawing lines, arcs, curves and shapes (rects, ovals).

Before calling a draw function, you can set attributes like, color, stroke, paint to be applied. By default, these are
reset after the draw function is done, but `Renderer` allows you to configure this behaviour.

## Input

`dev.gamekit.core.Input`

`Input` provides mechanisms for detecting keyboard key and mouse button events. You can detect `pressed`, `released` and
`held` for any key or button.

When the game is launched, `Application` attaches `Input` as an event listener for `Window`. During each frame, `Input`
preserves a snapshot of the current key and button states, which the scene can then read from in the update phase.

`Input` contains public static constants which map to
Java's [KeyEvent](https://docs.oracle.com/javase/8/docs/api/java/awt/event/KeyEvent.html) constants so they can be used
interchangeably. An example is shown below:

```java
import java.awt.event.KeyEvent;

import dev.gamekit.core.Scene;
import dev.gamekit.core.Input;

/* Omitted code */

public class MyAwesomeGame extends Scene {
  @Override
  public void onUpdate() {
    // Instead of using KeyEvent.VK_SPACE
    Input.isKeyJustPressed(KeyEvent.VK_SPACE);

    // You can use Input.KEY_SPACE
    Input.isKeyJustPressed(Input.KEY_SPACE);
  }
}
```

## IO

`dev.gamekit.core.IO`

`IO` is GameKit's asset loader. It allows you to access files placed in the resource directories of your application.
This includes images and font.

For other files, `IO` can open a [
BufferedReader](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html) for efficient reads (I.e. Not
reading all the file in memory).

Assets loaded by `IO` are cached using the file path as a key. When the same file path is requested, the cache responds,
improving your game's performance.

## Camera

Singleton class which controls which part of the game world is rendered in the `Window`.

`Camera` allows you to pan around the game world as well as zooming. Internally, `Camera` controls the 3x3
transformation matrix of the Window graphics object.

| Method                                      | Description                                                          |
|---------------------------------------------|----------------------------------------------------------------------|
| `public static Camera getInstance()`        | Get the current instance of the camera                               |
| `public void lookAt(double x, double y)`    | Pan the camera such that point (x, y) is at the center of the screen |
| `public void setZoom(double zoom)`          | Sets the zoom of the camera                                          |
| `public Point transformPoint(int x, int y)` | Projects the point (x, y) into the camera's local space              |

| Method                                                 | Description                                            |
|--------------------------------------------------------|--------------------------------------------------------|
| `public static boolean isKeyPressed(int keyCode)`      | Check if a key is being held down in the current frame |
| `public static boolean isKeyJustPressed(int keyCode)`  | Check if a key just pressed in the current frame       |
| `public static boolean isKeyJustReleased(int keyCode)` | Check if a key just released in the current frame      |

