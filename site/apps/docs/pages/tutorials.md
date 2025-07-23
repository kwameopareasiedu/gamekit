# Tutorials

These tutorials will take you from zero to hero in building games with GameKit.

## Requirements

As mentioned previously, GameKit is a 2D **Java** game engine which is distributed as a **Maven** dependency. To use
this engine, you must have the following installed and configured:

1. Java Development Kit (JDK) and Java Runtime Environment (JRE) (18+)
2. A Java IDE (IntelliJ IDEA, _Eclipse_, _Netbeans_, _VSCode_, etc)
3. Maven build tool and dependency manager (3.9.9+)

Next, you need to create a Maven Java project and [add the GameKit engine to your dependencies](/#installation).

## The GameKit Application

A GameKit application runs a game loop, manages a window and monitors the keyboard and mouse for user input.

To create one, you need to extend the `Application` class. Then in the `main` method, create a new instance of your
subclass and call the `run()` method to launch the game.

```java
import dev.gamekit.core.Application;

public class Game extends Application {
  public App() {
    super("Application Title Here");
  }
  
  public static void main(String[] args) {
    Game game = new App();
    game.run();
  }
}
```

![](assets/basic-application.png){:style="width:480px"}

### Additional Configuration

The `Application` class has additional settings to control display and rendering. These are outlined in the table below:

| Setting              | Description                                                    | Values                                                  |
|----------------------|----------------------------------------------------------------|---------------------------------------------------------|
| `Title`              | The title of the application window                            |                                                         |
| `Resolution`         | The window resolution which controls the width and height also | `VGA`, `SVGA`, `XGA`, `HD`, `WXGA`, `FULL_HD`, `NATIVE` |
| `WindowMode`         | Sets the window mode                                           | `WINDOWED`, `BORDERLESS`, `FULLSCREEN`                  |
| `Antialiasing`       | Enables/Disables rendering antialiasing                        | `ON`, `OFF`, `DEFAULT`                                  |
| `TextAntialiasing`   | Enables/Disables UI text rendering antialiasing                | `ON`, `OFF`, `DEFAULT`                                  |
| `AlphaInterpolation` | Controls the alpha interpolation mode the window               | `QUALITY`, `SPEED`, `DEFAULT`                           |
| `ImageInterpolation` | Controls the image interpolation mode the window               | `BILINEAR`, `BICUBIC`, `NEAREST`, `DEFAULT`             |
| `RenderingStrategy`  | Controls the overall rendering strategy of the window          | `QUALITY`, `SPEED`, `DEFAULT`                           |
| `Dithering`          | Enables/Disables UI dithering                                  | `ON`, `OFF`, `DEFAULT`                                  |

<br>To set one or more of these, pass a `Settings` to the Application constructor **in any order**, as shown below:

```java
import dev.gamekit.core.Application;
import dev.gamekit.settings.*;

public class Game extends Application {
  public App() {
    super(
      new Settings(
        "Application Title Here",
        Resolution.NATIVE,
        WindowMode.FULLSCREEN,
        Antialiasing.ON,
        TextAntialiasing.ON,
        AlphaInterpolation.QUALITY,
        ImageInterpolation.BICUBIC,
        RenderingStrategy.QUALITY,
        Dithering.OFF
      );
    );
  }
  
  public static void main(String[] args) {
    Game game = new App();
    game.run();
  }
}
```

### Access The Current Application Instance

When an application is instantiated, it is stored in a static variable within the `Application` class. Use
**`Application.getInstance()`** to get the current running instance of your application from anywhere within your code.

You can call public methods on the application instance.

### Exit Your Application

When your game needs to exit due to the "Quit" button being pressed in-game, "Alt+F4" being pressed, or some other a
quit action being performed, call the `Application.getInstance().quit()` method to quit.

## Scenes & Entities

A `Scene` represents a logical part of your game. This can be an intro showing the developers logo on start-up, the main
menu or a level/stage in your game. For your game to do anything useful, a scene must be loaded.

To create a scene, simply extend the `Scene` class and provide a name to the scene constructor. After creating a scene,
call the `loadScene(Scene)` method on your application instance to load it.

_The `loadScene(Scene)` method on the Application can be called before the game runs or as the game is running (I.e.
switching scenes)_

```java
import dev.gamekit.core.Application;
import dev.gamekit.core.Scene;

public class SimpleScene extends Scene {
  public SimpleScene() {
    super("Scene Name");
  }

  public static void main(String[] args) {
    Application game = new Application("Application Title") { };
    game.loadScene(new SimpleScene()); // <- Load the scene
    game.run();
  }
}
```

### Lifecycle Methods

A typical game consists of some setup phase, an input, update and render loop and a dispose phase (when exiting). This
is illustrated below:

![](assets/game-loop.png)

The scene class provides methods for each of these phases that can be overridden to implement the functionality you
need. These methods are called by the `Application` at different stages.

```java
import dev.gamekit.core.Application;
import dev.gamekit.core.Scene;

public class SimpleScene extends Scene {
  public SimpleScene() {
    super("Scene Name");
  }

  public static void main(String[] args) {
    Application game = new Application("Application Title") { };
    game.loadScene(new SimpleScene()); // <- Load the scene
    game.run();
  }
  
  /** Called once to set up the scene */
  @Override
  protected void start() { /* Perform scene initialization */ }

  /** Called every frame to update the scene */
  @Override
  protected void update() { /* Grab player input and update the state of the scene */ }

  /** Called every render thread frame to render the scene */
  @Override
  protected void render() { /* Dispatch render calls to draw the state of the scene */ }

  /** Called once to dispose the scene */
  @Override
  protected void dispose() { /* Perform cleanup before the scene is removed */ }
}
```

While these methods are enough to implement the functionality within the scene for simple game ideas, it's generally a
good idea to break apart functionality into distinct parts.

Let's take the classic [pong](https://www.ponggame.org/) game as an example. It consists of a puck, two paddles and the
walls and score areas.

![](assets/pong.png)

You'd agree with me that while the functionality of all these can be written into a single scene class, it wouldn't be
considered good programming practice, since each element has a distinct purpose.

- The puck is the object of focus and bounces around the scene using physics
- The paddles move vertically to deflect the puck and are controlled by players or the computer
- The vertical walls keep the puck in the play area
- The score areas register collisions with the puck and increment the scores

It would make sense to split these into different _entities_ and have the scene manage interactions with each other.
This is where the `Entity` class comes in.

An `Entity` represents an independent object that exists within a scene/game world. Entities have similar lifecycle
methods as scenes, but this time, scenes manage the lifecycle of their children entities.

> Fun fact: _A scene is actually a special kind of entity with the ability to render a user interface (more on this
later) and is managed by the `Application`._

### Entity Parents

Both scenes and entities can contain children entities and manage their lifecycle. When an entity is removed from a
scene or a scene is removed from the application, all child entities are also disposed of.

Use the `addChild(Entity)` and `removeChild(Entity)` methods to add and remove children from scenes/entities
respectively. 

## Rendering Stuff
