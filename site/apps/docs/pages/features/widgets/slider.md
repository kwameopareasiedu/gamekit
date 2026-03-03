# Slider Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Slider` widget is a [Progress](progress.md) extension which allows the user to set its value by sliding a thumb across
it.

## Properties

In addition to all [Progress properties](progress.md#properties), slider also has these properties:

| Property          | Description                                                      |
|-------------------|------------------------------------------------------------------|
| `thumbBackground` | The image to use for the thumb                                   |
| `thumbEdgeInsets` | The 9-patch padding to be applied to the thumb background        |
| `thumbWidth`      | The width of the slider thumb                                    |
| `thumbHeight`     | The height of the slider thumb                                   |
| `changeListener`  | The change event handler to invoke when the slider value changes |

## Usage

```java
double value = 50.0;

Slider.create(
  props -> {
    props.thumbBackground = IO.getResourceFont("default-progress-bg.ttf");
    props.thumbEdgeInsets = new Spacing(2, 2, 2, 2);
    props.thumbWidth = 24;
    props.thumbHeight = 24;
    props.changeListener = (event) -> {
      System.out.println("Value changed: " + event.value);
    };
  
    props.trackBackground = IO.getResourceFont("default-progress-bg.ttf");
    props.fillBackground = IO.getResourceFont("default-fill-bg.ttf");
    props.trackEdgeInsets = new Spacing(2, 2, 2, 2);
    props.fillEdgeInsets = new Spacing(2, 2, 2, 2);
    props.fillMargin = new Spacing(4);
    props.fillMode = FillMode.CLIP;
    props.minValue = 0.0;
    props.maxValue = 100.0;
    props.value = value;
  }
);
```
