# Scaled Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Scaled` is a widget which applies a scaling transformation to its child.

## Properties

| Property | Description                     |
|----------|---------------------------------|
| `scale`  | The scale to apply to the child |

## Usage

```java
Scaled.create(
  props -> {
    props.scale = 1.5;
  },
  Text.create("Scaled child"),
);

Scaled.create(
  1.5,
  Text.create("Scaled child"),
);
```
