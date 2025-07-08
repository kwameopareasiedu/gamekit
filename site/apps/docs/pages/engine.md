# Engine

In this section, we'll delve more into the GameKit engine structure.

## Application

`dev.gamekit.core.Application`

`Application` is the heart of a GameKit program. Your game must extend this class to do anything with the engine.

`Application` runs a fixed-step game loop which processes input, updates and renders the current scene and runs
end-of-frame tasks. This fixed time step approach makes sure during lag spikes don't overshoot logic and physics
updates.

When your game is run, it sets up the engine internals which include:

- Creating the window object
- Attaching keyboard and mouse listeners to the window
- Making the window visible

After setup, the main thread starts which runs the following until the application exits:

- Capture keyboard and mouse input
- Update the current scene
- Render the current scene to the window
- Perform end of frame tasks

In a worker thread, the following updates also occur in another game loop:

- Update running animations
- Update active timeouts
- Update audio system

The game loops continues running until the close event is received on the window. When this happens, the loop is halted
and the application runs its dispose logic before exiting.

You can override the `dispose()` to custom cleanup before the application exits

```java
@Override
protected void dispose() {
  super.dispose();
  /** Custom dispose logic */
}
```

To access the application from anywhere in the application, use `Application.getInstance()`.

## Scene

`dev.gamekit.core.Scene`

A `Scene` represents a logical part of your game. This can be a main menu or a level in your game.

Simple scenes can have all the logic contained within them, but more complex scenes can contain `Prop` objects which can
be scripted to interact with each other.

Each scene can also render a user interface (UI) which is a collection of `Widget`.

`Scene` has five (5) lifecycle methods which can be overridden:

- `start()` is called **once** when `Application` loads the scene
- `update()` is called every frame to update the state of the scene
- `render(Renderer)` is called every frame after `onUpdate()` to draw the state of the scene to `Window`
- `dispose()` is called **once** when the scene is about to be unloaded during a scene switch
- `createUI()` is called when the scene creates/recreates the user interface

## Input

`dev.gamekit.core.Input`

`Input` provides mechanisms for detecting keyboard key and mouse button events. You can detect `down`, `click` and
`released` for any key or button.

When the game is launched, `Application` attaches `Input` as an event listener for `Window`. During each frame, `Input`
preserves a snapshot of the current key and button states, which the scene can then read from in the update phase.

`Input` contains public static constants which map to
Java's [KeyEvent](https://docs.oracle.com/javase/8/docs/api/java/awt/event/KeyEvent.html) constants so they can be used
interchangeably. An example is shown below:

```java
import java.awt.event.KeyEvent;

import dev.gamekit.core.Scene;
import dev.gamekit.core.Input;

import java.awt.KeyEvent;

public class MyAwesomeScene extends Scene {
  public MyAwesomeScene() {
    super("MyAwesomeScene");
  }

  @Override
  public void update() {
    // Instead of using KeyEvent.VK_SPACE
    Input.isKeyDown(KeyEvent.VK_SPACE);

    // You can use Input.KEY_SPACE
    Input.isKeyDown(Input.KEY_SPACE);
  }
}
```

## IO

`dev.gamekit.core.IO`

`IO` is GameKit's asset loader. It allows you to load files placed in the **resource directory** of your application.
This includes images, font and audio (`.wav`).

Assets loaded by `IO` are cached using the file path as a key. When the same file path is requested, the cache responds,
improving your game's performance.

## Renderer

`dev.gamekit.core.Renderer`

So `Application` manages your game and `Window` manages the view frame, but how do we get stuff drawn unto the screen?

That's where `Renderer` comes in. Each entity/scene is passed a renderer instance containing all the supported draw 
functions of the engine. This includes drawing lines, arcs, curves, shapes (rects, ovals) and images.

The renderer also allows you can set attributes like color, stroke and paint of a draw function. E.g.

```java
public void (Renderer renderer) {
  renderer.fillRect(0, 0, 10, 10).withColor(Color.RED);
}
```

## Camera

`dev.gamekit.core.Camera`

`Camera` is a utility which provides functions to control which part of a `Scene` is being rendered to `Window`. These
functions include:

- Panning around in the scene
- Zooming in/out

It does this by updating the transform matrix of `Window`
scene [BufferedImage](https://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferedImage.html).
