# Entities

_[Back To Features](./overview.md)_

An `Entity` represents an independent object that exists within a scene/game world. This can be player or enemy
characters, environment objects or non-visual elements like a game manager.

## Entity Lifecycle

For an entity to do anything useful, we need to hook into its lifecycle to write custom logic. This can be done by
extending the `Entity` class and overriding lifecycle methods as shown below:

`MyObject.java`

```java
import dev.gamekit.core.Entity;

public class MyObject extends Entity {
  public MyObject() {
    super("Entity Name");
  }
  
  @Override
  protected void start() { }

  @Override
  protected void update() {  }
  
  @Override
  protected void render() {  }
  
  @Override
  protected void stop() {  }
  
  @Override
  protected void restart() {  }
  
  @Override
  protected void dispose() {  }
}
```

| Lifecycle | Description                                                                | State Before     | State After      |
|-----------|----------------------------------------------------------------------------|------------------|------------------|
| `start`   | Called once to initialize itself                                           | `State.NEW`      | `State.ACTIVE`   |
| `update`  | Called continuously to update itself                                       | `State.ACTIVE`   | `State.ACTIVE`   |
| `render`  | Called continuously to render itself                                       | `State.ACTIVE`   | `State.ACTIVE`   |
| `stop`    | When parent calls `removeChild(Entity)`, this is called before removal     | `State.ACTIVE`   | `State.INACTIVE` |
| `restart` | When parent calls `addChild(Entity)` on an inactive entity, this is called | `State.INACTIVE` | `State.ACTIVE`   |
| `dispose` | Called when scheduled for destruction using `destroy()`                    | `State.ACTIVE`   | `State.DOOMED`   |

## Entity Members

These are other publicly available entity methods

| Method           | Description                                                                                                                                               |
|------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `logger`         | The [Log4j](https://logging.apache.org/log4j/2.x/index.html) logger object of the entity instance                                                         |
| `getParent`      | Returns the entity's parent entity                                                                                                                        |
| `getState`       | Returns the current state of the entity                                                                                                                   |
| `addChild`       | Adds a child entity at the end of the current frame                                                                                                       |
| `removeChild`    | Removes a child entity at the end of the current frame                                                                                                    |
| `findComponent`  | `+1` Returns the first component matching the given class <br/> `+2` Returns the first component matching the given class and passing the provided filter |
| `findComponents` | Finds all component matching the given class and inserts them into the provided `out` list                                                                |
| `destroy`        | Marks the entity as `DOOMED` and schedules it for destruction at the end of the current frame                                                             |

## Scenes

A `Scene` is a collection of entities that makes up a logical part of your game. This can be a main menu, settings page
or a level within your game.

Scenes are actually special entities that can create and manage their own user interface. As such, scenes have the
same [lifecycle](#entity-lifecycle) as entities and inherit their [public and protected members](#entity-members).

The sample below shows a custom scene class:

```java
import dev.gamekit.core.Scene;

import java.awt.Color;

public class CustomScene extends Scene {
  public CustomScene() {
    // The scene name will be used in logs
    super("Hello Game");
  }
}
```

### User Interface

As mentioned previously, a scene can create and manage its own user interface. This is done by overriding the `createUI`
and returning the interface definition.

The sample below shows the UI for a bottom-center aligned text with 48px padding with text "Hello World":

```java
import dev.gamekit.core.Scene;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.widgets.Align;
import dev.gamekit.ui.widgets.Padding;
import dev.gamekit.ui.widgets.Text;
import dev.gamekit.ui.widgets.Widget;

import java.awt.Color;

public class UIShowcase extends Scene {
  public UIShowcase() {
    super("UI Showcase");
  }
  
  @Override
  protected Widget createUI() {
    return Align.create(
      props -> {
        props.horizontalAlignment = Alignment.CENTER;
        props.verticalAlignment = Alignment.END;
      },
      Padding.create(
        48, Text.create(
          props -> {
            props.text = "Hello World";
            props.fontSize = 32;
            props.alignment = Alignment.CENTER;
            props.fontStyle = Text.BOLD;
          }
        )
      )
    );
  }
}
```

This is just a simple use case showcasing how easy it is to create declarative user-interfaces with GameKit. However,
the topic of user creation is discussed in much more detail in a later section.

## Next Steps

This is an introductory guide to entities and scenes. Check out our [examples](../tutorials/examples.md) sections for
more samples involving entities.

While entities themselves are independent objects in your game world, they can be enhanced with predefined behaviours
known as [components](./components.md), which you can take a look at.
