# Audio Listener

_[Back To Audio](overview.md)_

[Spatial audio](audio-clip.md#spatial-audio) requires a **listener reference** position to compute the relative panning.
(I.e. How much of it is heard through the left and right speakers). This **listener reference** is represented by the
`AudioListener` instance. It is essentially the position of your "ears" in the scene.

As an example, if your player character is the entity responsible for "hearing", you should make sure to update the
`AudioListener` instance position to match the player's in-game position. This way, if the player gets close to, say a
fireplace which emits sound, you'd hear the sound more on the right speaker if the fireplace is to the right of it.

## Public Methods

| Method        | Description                                      |
|---------------|--------------------------------------------------|
| `getPosition` | Returns the position of the listener             |
| `setPosition` | Sets the position of the listener in world-space |

## AudioListener Sample

The sample below shows how you would update the `AudioListener` instance in a player entity class.

```java
import dev.gamekit.audio.AudioListener;
import dev.gamekit.core.Entity;
import dev.gamekit.core.Input;

class PlayerCharacter extends Entity {
  public PlayerCharacter() {
    super("Player Character");
  }
  
  /** Omitted code */
  
  @Override
  protected void update() {
    if (Input.isKeyPressed(Input.KEY_A)) {
      /** Code to move player character left */
    } else if (Input.isKeyPressed(Input.KEY_D)) {
      /** Code to move player character right */
    }
    
    double playerPositionX = /** Retrive x-coordinate */
    double playerPositionY = /** Retrive y-coordinate */
    
    // Update the audio listener position
    AudioListener.setPosition(playerPositionX, playerPositionY);
  }
}
```
