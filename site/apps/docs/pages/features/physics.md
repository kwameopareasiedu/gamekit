# Physics

_[Back To Features](./overview.md)_

GameKit integrates the [dyn4j](https://dyn4j.org/){:target="_blank"} physics engine to handle collision detection and
resolution between
entities with the [RigidBody](components/rigidbody.md) component.

The physics engine is not something you would have to interact with directly. Entities with the
[RigidBody](components/rigidbody.md) are automatically added to the physics world and updated
accordingly. The physics engine also notifies collision listeners on [collider](components/collider.md) components when
they are involved in collisions.

## Static Methods

| Method                    | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
|---------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `addBody`                 | Adds a [RigidBody](components/rigidbody.md) to the physics world for simulation<br/>_<small>Automatically called by [RigidBody](components/rigidbody.md) components</small>_                                                                                                                                                                                                                                                                                                        |
| `removeBody`              | Removes a [RigidBody](components/rigidbody.md) from the physics world<br/>_<small>Automatically called by [RigidBody](components/rigidbody.md) components</small>_                                                                                                                                                                                                                                                                                                                  |
| `addCollisionListener`    | Registers a collision listener with the specified id<br/>_<small>Automatically called by [Collider](components/collider.md) components</small>_                                                                                                                                                                                                                                                                                                                                     |
| `removeCollisionListener` | Removes a collision listener<br/>_<small>Automatically called by [Collider](components/collider.md) components</small>_                                                                                                                                                                                                                                                                                                                                                             |
| `raycast`                 | `+1` Projects a ray from one point to another point and returns a list of hit results which contain information on the hit point, hit normal and the [Collider](components/collider.md) components that were hit<br/><br/>`+2` Projects a fixed-length ray from starting position with the specified angle (in radian) and returns a list of hit results which contain information on the hit point, hit normal and the [Collider](components/collider.md) components that were hit |


