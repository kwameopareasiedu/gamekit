# Opacity Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Panel` is a widget which changes the opacity of its child.

## Properties

| Property  | Description                |
|-----------|----------------------------|
| `opacity` | The opacity level (0 to 1) |

## Usage

```java
Opacity.create(
  props -> {
    props.opacity = 0.75;
  },
  Text.create("Panel child"),
);

Opacity.create(
  0.75,
  Text.create("Panel child"),
);
```
