# Audio Effect

_[Back To Audio](overview.md)_

Audio effect provide an interface to manipulate the output of [audio mixers](audio-mixer.md). It can be used to create
filters to filter the frequency ranges of the sound, apply reverb or perform custom audio signal processing.

## Provided Effects

GameKit ships with a number of prebuilt audio effects described in the table below:

| Effect          | Description                                                                                                                                     |
|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| `LowPassFilter` | Filters out audio frequencies above a specified cuttoff frequency                                                                               |
| `DelayFilter`   | Delays an audio signal by a specified time                                                                                                      |
| `ReverbFilter`  | Applies [reverberation](https://www.sfu.ca/sonic-studio-webdav/cmns/Handbook5/handbook/Reverberation.html){:target="_blank"} to an audio signal |
