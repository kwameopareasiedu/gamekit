# Panel Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Panel` is a widget which renders a [9-patch](https://en.wikipedia.org/wiki/9-slice_scaling){:target="_blank"}
background for its child.

## Properties

| Property     | Description                                                  |
|--------------|--------------------------------------------------------------|
| `background` | The background image to render                               |
| `padding`    | The 9-patch padding to be applied to the rendered background |

## Usage

```java
Panel.create(
  props -> {
    props.background = IO.getResourceImage("background.png");
    props.padding = new Spacing(24, 32);
  },
  Text.create("Panel child"),
);
```
