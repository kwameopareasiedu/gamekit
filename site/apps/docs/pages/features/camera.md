# Camera

_[Back To Features](./overview.md)_

The static `Camera` class gives you the ability to pan and zoom around in your game world. This is useful for features
like player/object tracking.

## Static Methods

The `Camera` class is incredibly simple to use. The table below shows all the available methods of `Camera`:

| Method                  | Description                                                                             |
|-------------------------|-----------------------------------------------------------------------------------------|
| `lookAt`                | Positions the camera such that the given coordinates appear in the center of the window |
| `setZoom`               | Sets the zoom level of the camera                                                       |
| `screenToWorldPosition` | Converts a point in screen coordinates to a point in world coordinates                  |
| `worldToScreenPosition` | Converts a point in world coordinates to a point in screen coordinates                  |
| `getX`                  | Returns the `x` translation of the camera                                               |
| `getY`                  | Returns the `y` translation of the camera                                               |
