# Attenuation

_[Back To Audio](overview.md)_

[Spatial audio](audio-clip.md#spatial-audio) is positional sound in-game. Therefore, as the listener moves away from it,
we expect the sound to falloff and vice versa. This falloff (I.e. the way the sounds fades) is what is known as
**attenuation**.

Formally, attenuation is a mathematical function which maps the distance between the source and listener to a volume
value.

Below, we see three different attenuation functions: linear, logarithmic and inverse. Linear gives a proportional
falloff as the distance increases while logarithmic and inverse model a more realistic falloff.

<div class="caption-image-container" style="width: 32%">
  <img src="/assets/linear-attenuation.png" alt="Linear attenuation"/>
  <small class="caption">Linear Attenuation</small>
</div>

<div class="caption-image-container" style="width: 32%">
  <img src="/assets/logarithmic-attenuation.png" alt="Logarithmic attenuation"/>
  <small class="caption">Logarithmic Attenuation</small>
</div>

<div class="caption-image-container" style="width: 32%">
  <img src="/assets/inverse-attenuation.png" alt="Inverse attenuation"/>
  <small class="caption">Inverse Attenuation</small>
</div>

> The x-axis represents distance between the source and listener while the y-axis represents the perceived volume.

In GameKit, these curves can be modelled by implementing the `AudioAttenuation` interface and its `attenuate` method.

The `attenuate` method is called with the following arguments and should return a 0 - 1 ratio of the max volume:

1. Distance between the audio source and the listener
2. Minimum possible distance defined by the [audio shape](audio-shape.md)
3. Maximum possible distance defined by the [audio shape](audio-shape.md)

GameKit ships with one attenuation implementation which is `LinearAudioAttenuation`.

## Public Methods

| Method      | Description                                                                    |
|-------------|--------------------------------------------------------------------------------|
| `attenuate` | Returns a value between 0 and 1 representing the falloff based on the distance |
