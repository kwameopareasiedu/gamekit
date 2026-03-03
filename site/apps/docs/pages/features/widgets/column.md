# Column Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Column` is a widget which arranges its children vertically.

## Properties

| Property             | Description                          |
|----------------------|--------------------------------------|
| `gapSize`            | The gap space between children       |
| `mainAxisAlignment`  | The vertical alignment of children   |
| `crossAxisAlignment` | The horizontal alignment of children |

## Usage

```java
Column.create(
  props -> {
    props.gapSize = 12.0;
    props.mainAxisAlignment = MainAxisAlignment.START;
    props.crossAxisAlignment = CrossAxisAlignment.STRETCH;
  },
  Text.create("First child"),
  Text.create("Second child"),
  Text.create("Third child"),
);
```
