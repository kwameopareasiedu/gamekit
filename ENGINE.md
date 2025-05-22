# Engine Docs

This sections contains the full in-depth documentation of the classes and
interactions of classes that make GameKit function.

## Application

`dev.gamekit.core.Application`

`Application` is the heart of a GameKit program. Your game must extend this
class to do anything with the engine.

`Application` runs a fixed-step game loop which processes input, updates and
renders the current scene and runs end-of-frame tasks. This fixed time step
approach makes sure during lag spikes don't overshoot logic and physics updates.

### Startup

When your game is run, it sets up the engine internals which include:

- Creating the window object
- Attaching keyboard and mouse listeners to the window
- Making the window visible

### Game Loop

After setup, the game loop starts which runs the following until the application
exits:

- Capture keyboard and mouse input
- Update running animations
- Update active timeout tasks
- Update the active scene
- Update audio system
- Render the current scene to the window
- Perform end of frame tasks

The game loops continues running until the close event is received on the
window. When this happens, the loop is halted and the application runs its
dispose logic before exiting. You can override the `onDispose()` to custom
cleanup before the application exits.

To access the application from anywhere in the application, use
`Application.getInstance()`.

## Window

`dev.gamekit.core.Window`

`Window` manages the view frame in which the game is displayed. Since this is a
Java Swing project, the frame is a
[JFrame](https://docs.oracle.com/javase/8/docs/api/javax/swing/JFrame.html)
object.

### Screen Space vs World Space

In screen space, the origin, `(0,0)` is at the top-left of the screen, `+x` is
to the right of the screen and `+y` is down the screen.

This doesn't translate to world/scene space which uses a cartesian coordinate
system where the origin is at the center of the screen, `+x` is to the right
and `+y` is upwards.

![](docs2/assets/screen-space-vs-world-space.png)

### Multiple Render Targets

Due to this, `Window` provides two
[BufferedImage](https://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferedImage.html)
targets for rendering. One configured for screen space rendering for your UI
and the other configured for world rendering (I.e. your scene).

### Multi Resolution & Fullscreen Support

`Window` supports multiple resolution, ranging from 640x480 to native
resolution. Higher resolutions appear best but at a performance cost and vice
versa.

You can also configure your game to launch in a windowed mode or full screen.
This is done before the application starts and cannot be changed at runtime.

## Renderer

`dev.gamekit.core.Renderer`

So `Application` manages your game and `Window` manages the view frame, but how
do we get stuff drawn unto the screen?

That's where `Renderer` comes in. It's a static utility class containing all the
supported draw functions of the engine. This includes drawing lines, arcs,
curves, shapes (rects, ovals) and images.

Before calling a draw function, you can set attributes like, color, stroke,
paint to be applied. These are reset after the draw function is done.

## Input

`dev.gamekit.core.Input`

`Input` provides mechanisms for detecting keyboard key and mouse button events.
You can detect `pressed`, `released` and `held` for any key or button.

When the game is launched, `Application` attaches `Input` as an event listener
for `Window`. During each frame, `Input` preserves a snapshot of the current
key and button states, which the scene can then read from in the update phase.

`Input` contains public static constants which map to Java's
[KeyEvent](https://docs.oracle.com/javase/8/docs/api/java/awt/event/KeyEvent.html)
constants so they can be used interchangeably. An example is shown below:

```java
import java.awt.event.KeyEvent;

import dev.gamekit.core.Scene;
import dev.gamekit.core.Input;

/* Omitted code */

public class MyAwesomeScene extends Scene {
  public MyAwesomeScene() {
    super("MyAwesomeScene");
  }

  @Override
  public void update() {
    // Instead of using KeyEvent.VK_SPACE
    Input.isKeyJustPressed(KeyEvent.VK_SPACE);

    // You can use Input.KEY_SPACE
    Input.isKeyJustPressed(Input.KEY_SPACE);
  }
}
```

## IO

`dev.gamekit.core.IO`

`IO` is GameKit's asset loader. It allows you to access files placed in the
**resource directories** of your application. This includes images and font.

Assets loaded by `IO` are cached using the file path as a key. When the same
file path is requested, the cache responds, improving your game's performance.

## Scene

`dev.gamekit.core.Scene`

`Scene` represents a logical part of your game. This can be a main menu or a
level in your game.

Simple scenes can have all the logic contained within them, but more complex
scenes can contain `Prop` objects which can be scripted to interact with each
other.

Each `Scene` can also render a user interface (UI) which is a collection of
`Widget` (More on this [later](#ui)).

`Scene` has five (5) lifecycle methods which can be overridden.

- `start()` is called **once** when `Application` loads the scene
- `update()` is called every frame to update the state of the scene
- `render()` is called every frame after `onUpdate()` to draw the state of the
  scene to `Window`
- `dispose()` is called **once** when the scene is about to be unloaded during
  a scene switch
- `createUI()` is called when the scene (re)creates the user interface

## Camera

`dev.gamekit.core.Camera`

`Camera` is a utility which provides functions to control which part of a
`Scene` is being rendered to `Window`. These include:

- Panning around in the scene
- Zooming in/out

It does this by updating the transform matrix of `Window`
scene [BufferedImage](https://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferedImage.html).

## UI

`dev.gamekit.core.UI`

`UI` manages the user interface of `Scene`. It works with `Scene` to create the
user interface and update it when dependent variables in the scene change.

This allows for a declarative approach to user interfaces in GameKit.

`UI` is internally managed by `Scene` so the developer doesn't need to worry
about its function.

The scene's user interface is created using building blocks called
**widgets**. Each widget performs a single function such as, rendering text,
rendering an image or arranging other widgets.

Below is an example of creating a column containing an image and some text,
all centered on the screen.

```java
import java.awt.event.KeyEvent;

import dev.gamekit.core.Scene;
import dev.gamekit.core.Input;

/* Omitted code */

public class MyAwesomeScene extends Scene {
  private final BufferedImage img = IO.getResourceImage("test.png");

  public MyAwesomeGame() {
    super("MyAwesomeScene");
  }

  @Override
  public Widget createUI() {
    return Center.create(
      Column.create(
        Column.options()
          .mainAxisAlignment(MainAxisAlignment.CENTER)
          .crossAxisAlignment(CrossAxisAlignment.CENTER)
          .gapSize(24),
        Image.create(img),
        Text.create(
          Text.options()
            .alignment(Alignment.CENTER)
            .text("Hello World")
        )
      )
    );
  }
}
```

GameKit comes with many pre-built widgets for creating interfaces. These are
described in the table below:

| Widget  | Description                                |
|---------|--------------------------------------------|
| Row     | Arranges its children horizontally         |
| Column  | Arranges its children vertically           |
| Stack   | Arranges its children on top of each other |
| Text    | Renders text unto the window               |
| Image   | Renders an image unto the window           |
| Empty   | Placeholder widget which does nothing      |
| Scaled  | Applies scaling to its child               |
| Padding | Applies padding to its child               |
| Align   | Aligns its child relative to itself        |
| Sized   | Enforces a fixed size on its child         |
| Opacity | Applies transparency to its child          |
| Button  | Clickable widget which triggers an action  |
| Center  | Centers its child within itself            |
| Compose | Serves as a base to build custom widgets   |
