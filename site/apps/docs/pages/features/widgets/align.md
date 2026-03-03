# Align Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Align` is a widget which aligns child to an anchor position within itself.

## Properties

| Property              | Description                           |
|-----------------------|---------------------------------------|
| `verticalAlignment`   | The vertical alignment of the child   |
| `horizontalAlignment` | The horizontal alignment of the child |

## Usage

```java
Align.create(
  props -> {
    props.horizontalAlignment = Alignment.START;
    props.verticalAlignment = Alignment.CENTER;
  },
  Text.create("Aligned child"),
);
```
