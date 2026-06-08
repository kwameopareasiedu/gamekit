# Features

## 0.8.0

### Viewport Scaling

GameKit now allows you to set a base resolution for your game. When the window is resized, viewport scaling kicks in
allowing your game to maintain the aspect ratio of the initial resolution.

With viewport scaling, you can use pixel measurements for UI placement and be assured that everything will scale
exactly with different screen sizes.

### Render Layers

Initially, entities were rendered according to their order of addition to a scene, hence subsequently added entities
will render on top of previous ones.

At certain times, there will be a need to render entities in a different order, or even render different sprites of the
same entity in a different order.

Render layers solves this problem. GameKit now provides 64 render layers to work with (0 - 63). Using the `Renderer.
onLayer(index, renderAction)` method, you can specify which layer rendering should occur on. Higher indices will
appear on top of lower ones.

### Widget Keys

During UI reconciliation, widgets can be reused when they have the same type as the other widget at the same
location in the new widget tree. While this is favorable, there are times, you want to explicitly replace the widget
regardless of the type match.

This is where widget keys come in. By assigning keys to widgets, you can force a replacement, rather than a reuse of a
widget during UI reconciliation.

### Scene Stacking

Scene stacking is a unique feature of GameKit where you can load a new scene without disposing the current scene.

When a new scene is stacked, the current scene is put in a suspended state which can be resumed later with optional data
returned from the new scene.

This allows you to maintain clean separation of logic and entities between related scenes.

### Audio Effects

The audio subsystem has been overhauled, replacing the initial abstract Java `Clip` version with a new byte-level 
manipulation. This has opened the door for implementing an audio effect interface with concrete implementations such 
as:

- Low pass filter
- Delay filter
- Reverb filter

### Renderer Image Masking

Rendering in GameKit uses draw calls. These are instructions sent to the draw thread to perform actual drawing on 
the screen.

The draw call for rendering images now implements masking, which is where the alpha layer of an image determines 
which portions of another image is rendered.

Using the `Render.drawImage(...).withMask` modifier method, you can specify a mask to use for the drawn image. 

### Custom Render Targets

By default, the draw call for images performs its drawing only to the monitor screen. There are times however, when you 
want to draw to another location.

With custom render targets, you can specify another buffered image object as a target on which the image draw call 
should be applied.

## 0.7.0

### Maven Plugin

GameKit now ships with its own maven plugin responsible for building your game/application when you run the `mvn
package` command. More on that [here](https://docs.gamekit.opare.dev/tools/export/).

### New UI Widgets

| Widget     | Description                                                                                       |
|------------|---------------------------------------------------------------------------------------------------|
| `Stateful` | An abstract widget which allows you to build custom widgets that manage an internal state         |
| `Measure`  | A widget which displays a visual ruler for its children, allowing you to measure pixel dimensions |
| `Spinner`  | A widget which displays a loading spinner indicator                                               |

## 0.6.0

## Physics Ray Casting

Static ray cast methods have been added to the Physics utility class. This allows you to project a ray into the game
world and check which colliders it hit.

## Animation Slices

An animation slice observes a range within an animation, mapping it to a new 0 - 1 range. This is very useful if you
want to coordinate several actions using a single animation.

As an example, let's consider a 10-second animation. You can create a slice that observes the 0s to 2.5s period, another
slice which observes the 2.5s to 7s period and a third slice to observe the 7s to 10s period.

Using these slices, you can easily perform a multistep animation on an object.

## 0.5.0

### New UI Widgets

| Widget     | Description                                                            |
|------------|------------------------------------------------------------------------|
| `Checkbox` | An input widget which toggles between two states                       |
| `Field`    | A `Text` widget extension which accepts text input                     |
| `Progress` | A widget which displays a progress bar based on a value and a range    |
| `Slider`   | A `Progress` widget extension which adjusts a value by moving a slider |

