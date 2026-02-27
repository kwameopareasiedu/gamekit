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

| Lifecycle Method | Description                                                                | State Before     | State After      |
|------------------|----------------------------------------------------------------------------|------------------|------------------|
| `start`          | Called once to initialize itself                                           | `State.NEW`      | `State.ACTIVE`   |
| `update`         | Called continuously to update itself                                       | `State.ACTIVE`   | `State.ACTIVE`   |
| `render`         | Called continuously to dispatch render itself                              | `State.ACTIVE`   | `State.ACTIVE`   |
| `stop`           | When parent calls `removeChild(Entity)`, this is called before removal     | `State.ACTIVE`   | `State.INACTIVE` |
| `restart`        | When parent calls `addChild(Entity)` on an inactive entity, this is called | `State.INACTIVE` | `State.ACTIVE`   |
| `dispose`        | Called when scheduled for destruction using `destroy()`                    | `State.ACTIVE`   | `State.DOOMED`   |

## Entity Methods

These are other publicly available entity methods

| Method           | Description                                                                                                                                               |
|------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `getParent`      | Returns the entity's parent entity                                                                                                                        |
| `getState`       | Returns the current state of the entity                                                                                                                   |
| `addChild`       | Adds a child entity at the end of the current frame                                                                                                       |
| `removeChild`    | Removes a child entity at the end of the current frame                                                                                                    |
| `findComponent`  | `+1` Returns the first component matching the given class <br/> `+2` Returns the first component matching the given class and passing the provided filter |
| `findComponents` | Finds all component matching the given class and inserts them into the provided `out` list                                                                |
| `destroy`        | Mark the entity as `DOOMED` and schedule it for destruction at the end of the current frame                                                               |
