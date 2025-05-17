package dev.gamekit.audio;

import dev.gamekit.audio.shapes.AudioShape;
import dev.gamekit.utils.Vector;

import javax.sound.sampled.FloatControl;

import static dev.gamekit.utils.Math.clamp;

/**
 * {@link AudioClip3D} is a {@link AudioClip} which uses spatialization to update its parameters
 * in relation to the {@link AudioListener}.
 * <p>
 * {@link AudioClip3D} is best for positional sounds within a game
 */
public class AudioClip3D extends AudioClip {
  protected static final Vector UP = new Vector(0, 1);

  protected final Vector position;
  protected final FloatControl panControl;
  protected final AudioAttenuation attenuation;
  protected final AudioShape shape;
  protected final Vector listenerVector;

  // Cached effective volume since log10() is expensive to compute every frame
  private double effectiveVolume = -1;
  private double effectivePan = 0;

  public AudioClip3D(
    String resPath,
    AudioGroup group,
    double maxVolume,
    AudioAttenuation attenuation,
    AudioShape shape
  ) {
    super(resPath, group, maxVolume);
    this.attenuation = attenuation;
    this.shape = shape;
    this.position = new Vector();
    this.panControl = getControl(FloatControl.Type.PAN);
    this.listenerVector = new Vector();
  }

  public Vector getPosition() { return position; }

  public void setPosition(double x, double y) {
    position.set(x, y);
  }

  @Override
  public void performUpdate() {
    Vector listenerPos = AudioListener.getPosition();

    listenerVector.set(
      listenerPos.x - position.x,
      listenerPos.y - position.y
    );

    double distanceToListener = shape.getDistance(position, listenerPos);
    double attenuation = this.attenuation.attenuate(
      distanceToListener, shape.minDistance, shape.maxDistance
    );

    double effectiveVolume = !group.isMuted() ? group.getMaxVolume() * maxVolume * attenuation : 0;
    double effectivePan = Math.abs(Vector.dot(listenerVector, UP)) - 1;

    if (this.effectiveVolume != effectiveVolume) {
      if (gainControl != null) {
        double gain = 20 * Math.log10(effectiveVolume);
        gain = clamp(gain, gainControl.getMinimum(), gainControl.getMaximum());
        gainControl.setValue((float) gain);
      }

      this.effectiveVolume = effectiveVolume;
    }

    if (listenerPos.x < position.x) effectivePan *= -1;

    if (this.effectivePan != effectivePan) {
      if (panControl != null)
        panControl.setValue((float) effectivePan);

      this.effectivePan = effectivePan;
    }
  }
}
