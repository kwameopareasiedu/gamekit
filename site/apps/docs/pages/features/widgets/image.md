# Image Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Image` is a widget which renders an image to the screen.

## Properties

| Property        | Description                                  |
|-----------------|----------------------------------------------|
| `image`         | The image to render                          |
| `fit`           | The fit to apply to the image                |
| `interpolation` | The pixel interpolation to use for the image |

## Usage

```java
Image.create(IO.getResourceImage("img.png"));

Image.create(
  props -> {
    props.image = IO.getResourceImage("img.png");
    props.fit = ImageFit.CROP;
    props.interpolation = ImageInterpolation.BICUBIC;
  }
);
```
