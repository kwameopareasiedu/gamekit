# Animated Sprite

_[Back To Components](overview.md)_

This component is an extension of the [Sprite](sprite.md) component which animates a sprite sheet at an entity's
position.

Since `AnimatedSprite` controls an animation, it requires you to specify an animation duration when creating a new
instance and whether the animation should loop.

## Public methods

`AnimatedSprite` inherits all the public methods from [Sprite](sprite.md#public-methods).

| Method             | Description                                                                                                                                                                                                                                                     |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `setSpriteSheet`   | `+1` Updates the sprite sheet and computes the sprites based on the given `spriteWidth`, `spriteHeight` and `coordinates`. Each coordinates pair represents the top-left corner of a sprite in the sprite sheet<br/>`+2` Updates the generated sprites directly |
| `setStateListener` | Sets a listener which is notified when the internal animation state changes                                                                                                                                                                                     |

