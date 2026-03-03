# Padding Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Padding` is a widget which applies a spacing around its child.

## Properties

| Property  | Description                              |
|-----------|------------------------------------------|
| `padding` | The padding object to apply to the child |

## Usage

```java
Padding.create(
  props -> {
    // Top = 10px, Right = 20px, Bottom = 30px, Left = 40px
    props.padding = new Spacing(10, 20, 30, 40);
  },
  Text.create("Padded child"),
);

Padding.create(
  // Top = 10px, Right = 20px, Bottom = 30px, Left = 40px
  10, 20, 30, 40, Text.create("Padded child"),
);

Padding.create(
  // Top & Bottom = 10px, Left & Right = 20px
  10, 20, Text.create("Padded child"),
);

Padding.create(
  // All sides = 10px
  10, Text.create("Padded child"),
);
```
