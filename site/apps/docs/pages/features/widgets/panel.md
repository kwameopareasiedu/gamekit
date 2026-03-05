# Panel Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Panel` is a widget which renders an image or a solid color as a background for its child.

## Properties

| Property       | Description                                                                |
|----------------|----------------------------------------------------------------------------|
| `background`   | The background image to render                                             |
| `color`        | The background color to render. `color` takes precedence over `background` |
| `cornerRadius` | The corner radius of the panel                                             |
| `clip`         | Whether to clip children or not                                            |

## Usage

```java
Panel.create(
  props -> {
    props.background = IO.getImage("background.png");
    props.cornerRadius = 32;
    props.clip = true;
  },
  Text.create("Panel child"),
);
```
