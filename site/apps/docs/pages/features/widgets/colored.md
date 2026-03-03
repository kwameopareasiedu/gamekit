# Colored Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Colored` is a widget which renders a solid color.

## Properties

| Property       | Description                     |
|----------------|---------------------------------|
| `color`        | The solid color to render       |
| `borderRadius` | The corder radius of the widget |

## Usage

```java
Colored.create(Color.RED);

Colored.create(
  props -> {
    props.color = Color.RED;
    props.borderRadius = 32;
  }
);
```
