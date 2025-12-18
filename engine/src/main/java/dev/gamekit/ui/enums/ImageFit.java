package dev.gamekit.ui.enums;

/** Constants determining how an image should be resized/fitted in its bounds */
public enum ImageFit {
  /** Resize the image to fit within the bounds */
  FIT,
  /** Cutout the portions of the image which are outside the bounds */
  CROP,
  /** Stretch the image to completely cover the bounds */
  STRETCH
}
