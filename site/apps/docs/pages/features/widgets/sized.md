# Sized Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Sized` is a widget which applies size constraints to its child in the following priority:

```text
Intrinsic size -> Fractional size -> Fixed size
```

## Properties

| Property             | Description                                                     |
|----------------------|-----------------------------------------------------------------|
| `useIntrinsicWidth`  | Indicates that the intrinsic width of the child should be used  |
| `useIntrinsicHeight` | Indicates that the intrinsic height of the child should be used |
| `fractionalWidth`    | The desired fractional width of the child (0 to 1)              |
| `fractionalHeight`   | The desired fractional height of the child (0 to 1)             |
| `fixedWidth`         | The desired fixed width of the child in pixels                  |
| `fixedHeight`        | The desired fixed height of the child in pixels                 |

## Usage

```java
// Child has an intrinsic width and 40% of height of Sized
Sized.create(
  props -> {
    props.useIntrinsicWidth = true;
    props.fractionalHeight = 0.4;
  },
  Text.create("Aligned child"),
);

// Child has a fixed width and height
Sized.create(
  props -> {
    props.fixedWidth = 128.0;
    props.fixedHeight = 92.0;
  },
  Text.create("Aligned child"),
);
```
