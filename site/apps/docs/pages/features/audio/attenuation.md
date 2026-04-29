# Attenuation

_[Back To Audio](overview.md)_

Attenuation refers to how [spatial audio](audio-clip.md#spatial-audio) falls off/fades as
the [listener](audio-listener.md) moves away from it.

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
2. Minimum possible distance, below which the audio clip should play at max volume
3. Maximum possible distance, above which the audio clip should be effectively silent

[//]: # (@formatter:off)
GameKit ships with one (1) attenuation implementation which is
[LinearAttenuation](https://github.com/kwameopareasiedu/gamekit/blob/master/engine/src/main/java/dev/gamekit/audio/attenuation/LinearAttenuation.java){:target="_blank"}.
[//]: # (@formatter:on)

## Public Methods

| Method      | Description                                                                    |
|-------------|--------------------------------------------------------------------------------|
| `attenuate` | Returns a value between 0 and 1 representing the falloff based on the distance |
