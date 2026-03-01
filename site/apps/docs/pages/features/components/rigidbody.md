# RigidBody

_[Back To Entity Components](overview.md)_

This component provides a physics-based way to control the movement and position of an entity. An entity with a
`RigidBody` component is controlled by GameKit's physics engine and responds to applied forces, gravity and collisions.

When a `RigidBody` is attached to an entity, it overrides the `Transform` global position and rotation directly with the
computed physics position and rotation.

> An entity with `RigidBody` attached cannot be a descendant of another entity which also has `RigidBody` attached.
> Attempting to do this will result in a runtime exception.

## Public Methods

| Method              | Description                                                                                                                                                  |
|---------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `setMetaData`       | Sets a custom object with user-defined attributes as the metadata. In collisions, you can retrieve this metadata to identify specific `RigidBody` components |
| `setGravityScale`   | Sets the world gravity multiplier                                                                                                                            |
| `setPosition`       | Sets the global position                                                                                                                                     |
| `setRotation`       | `+1` Sets the global rotation (in radian) about its center<br/><br/>`+2` Sets the global rotation (in radian) about a specified point                        |
| `setLinearVelocity` | Sets the linear velocity                                                                                                                                     |
| `applyForce`        | Applies a linear force vector                                                                                                                                |
| `applyImpulse`      | Applies a linear impulse vector                                                                                                                              |
| `applyTorque`       | Applies a torque (rotational force) about its center                                                                                                         |
| `containsPoint`     | Determines if a point is contained in at least one of the colliders associated with this `RigidBody`                                                         |
