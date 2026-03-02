# Audio Shape

_[Back To Audio](overview.md)_

Audio shapes model the area of a [spatial audio](audio-clip.md#spatial-audio)'s range. For example in an open field
sound follows a circular shape outward from the point of origin. In a closed room, sound would take on a more
rectangular shape.

Audio shapes specify the minimum and maximum attenuation distances with respect to a shape. This is illustrated in the
following images:

<div class="caption-image-container" style="width: 45%; margin-right: 5%; margin-bottom: 5%">
  <img src="/assets/box-audio-shape.png" style="border:none;" alt="Box Audio Shape"/>
  <small class="caption">Box Audio Shape</small>
</div>

<div class="caption-image-container" style="width: 45%">
  <img src="/assets/circle-audio-shape.png" style="border:none;" alt="Circle Audio Shape"/>
  <small class="caption">Circle Audio Shape</small>
</div>

<div class="caption-image-container" style="display: flex; width: 70%; margin: 0 auto;">
  <img src="/assets/capsule-audio-shape.png" style="border:none;" alt="Capsule Audio Shape"/>
  <small class="caption">Capsule Audio Shape</small>
</div>

<br>

In GameKit, audio shapes are represented by the abstract `AudioShape` class. You can extend this and implement the
`getDistance` method to define custom audio shapes.

The `getDistance` method is called with the following arguments and should return the relative distance between the
source and listener based on the shape:

1. Position of the audio source
2. Position of the audio listener

GameKit ships with one (1) audio shape implementation which is `CircuelAudioShape` class.

## Public Methods

| Method        | Description                                                                      |
|---------------|----------------------------------------------------------------------------------|
| `getDistance` | Returns the relative distance between the source and listener based on the shape |
