# Progress Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Progress` is a widget which displays a progress bar.

## Properties

| Property          | Description                                                  |
|-------------------|--------------------------------------------------------------|
| `trackBackground` | The image to use for the background track                    |
| `fillBackground`  | The image to use for the progress background                 |
| `trackEdgeInsets` | The 9-patch padding to be applied to the track background    |
| `fillEdgeInsets`  | The 9-patch padding to be applied to the progress background |
| `fillMargin`      | The margin between the widget bounds and the fill            |
| `fillMode`        | The rendering mode to use for the `fillBackground`           |
| `minValue`        | The minimum value of the widget                              |
| `maxValue`        | The maximum value of the widget                              |
| `value`           | The value of the widget bounded by `minValue` and `maxValue` |

## Usage

```java
Progress.create(
  props -> {
    props.trackBackground = IO.getResourceFont("default-progress-bg.ttf");
    props.fillBackground = IO.getResourceFont("default-fill-bg.ttf");
    props.trackEdgeInsets = new Spacing(2, 2, 2, 2);
    props.fillEdgeInsets = new Spacing(2, 2, 2, 2);
    props.fillMargin = new Spacing(4);
    props.fillMode = FillMode.CLIP;
    props.minValue = 0.0;
    props.maxValue = 100.0;
    props.value = 50.0;
  }
);
```
