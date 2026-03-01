# Transform

_[Back To Entity Components](overview.md)_

This component dictates the entity's position and rotation in 2D space. If the entity is a child of another entity, the
local position of the transform is relative to that of its parent.

> Every entity has a `Transform` component attached. Attempting to add another `Transform` to an entity will lead to a
> runtime exception.

## Public Methods

| Method              | Description                          |
|---------------------|--------------------------------------|
| `getLocalPosition`  | Returns the local position vector    |
| `setLocalPosition`  | Sets the local position vector       |
| `getGlobalPosition` | Returns the global position vector   |
| `setGlobalPosition` | Sets the global position vector      |
| `getLocalRotation`  | Returns the local rotation (radian)  |
| `setLocalRotation`  | Set the local rotation (radian)      |
| `getGlobalRotation` | Returns the global rotation (radian) |
| `setGlobalRotation` | Set the global rotation (radian)     |
