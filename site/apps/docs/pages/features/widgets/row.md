# Row Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Row` is a widget which arranges its children horizontally.

## Properties

| Property             | Description                          |
|----------------------|--------------------------------------|
| `gapSize`            | The gap space between children       |
| `mainAxisAlignment`  | The horizontal alignment of children |
| `crossAxisAlignment` | The vertical alignment of children   |

## Usage

```java
Row.create(
  props -> {
    props.gapSize = 12.0;
    props.mainAxisAlignment = MainAxisAlignment.CENTER;
    props.crossAxisAlignment = CrossAxisAlignment.STRETCH;
  },
  Text.create("First child"),
  Text.create("Second child"),
  Text.create("Third child"),
);
```
