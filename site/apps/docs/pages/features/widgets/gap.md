# Gap Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Gap` is a widget which occupies a fixed color.

## Properties

| Property | Description              |
|----------|--------------------------|
| `width`  | The width of the widget  |
| `height` | The height of the widget |

## Usage

```java
Gap.create(24, 48);

Colored.create(
  props -> {
    props.width = 24;
    props.height = 48;
  }
);
```
