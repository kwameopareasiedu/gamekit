# Entity Components

_[Back To Features](../overview.md)_

Components are mechanisms to add behaviours to entities. Depending on what an entity is supposed to be, you can combine
multiple components to achieve its function.

For example, every entity has a `Transform` component which cannot be removed. This component dictates the entity's
position and rotation in 2D space.

> You can call the `findComponent` and `findComponents` methods on an entity instance to get specific components.

GameKit comes with a number of built-in components which are listed below:

- [Transform](./transform.md)
- [Sprite](./sprite.md)
- [Animated Sprite](./animated-sprite.md)
- [RigidBody](./rigidbody.md)
- [Collider](collider.md)

<configure hideToc contentWidth="center"></configure>
