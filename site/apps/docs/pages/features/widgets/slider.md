# Slider Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Slider` widget is a [Progress](progress.md) extension which allows the user to set its value by sliding a thumb across
it.

## Properties

In addition to all [Progress properties](progress.md#properties), slider also has these properties:

| Property          | Description                                                      |
|-------------------|------------------------------------------------------------------|
| `thumbBackground` | The image to use for the thumb                                   |
| `thumbWidth`      | The width of the slider thumb                                    |
| `thumbHeight`     | The height of the slider thumb                                   |
| `changeListener`  | The change event handler to invoke when the slider value changes |

## Usage

```java
double value = 50.0;

Slider.create(
  props -> {
    props.thumbBackground = IO.getImage("default-progress-bg.ttf");
    props.thumbWidth = 24;
    props.thumbHeight = 24;
    props.changeListener = (event) -> {
      System.out.println("Value changed: " + event.value);
    };
  
    props.trackBackground = IO.getImage("default-progress-bg.ttf");
    props.fillBackground = IO.getImage("default-fill-bg.ttf");
    props.fillMargin = new Spacing(4);
    props.fillMode = FillMode.CLIP;
    props.minValue = 0.0;
    props.maxValue = 100.0;
    props.value = value;
  }
);
```
