package dev.gamekit.core;

/**
 * {@link Prop} represent game objects in a {@link Scene}.
 * <p>
 * Like {@link Scene}, {@link Prop} has lifecycle methods which are called by the engine to
 * set up, update, render and dispose.
 */
public abstract class Prop {
  private static int idCounter = 0;

  final int internalId;
  final String name;
  boolean ready;

  private Scene scene;

  public Prop(String name) {
    internalId = Prop.idCounter++;
    this.name = name;
    this.ready = false;
  }

  public void addChild(Prop prop) {
    scene.add(prop);
  }

  public void removeChild(Prop prop) {
    scene.remove(prop);
  }

  protected void start() { }

  protected void update() { }

  protected void render() { }

  protected void dispose() { }

  void _start(Scene scene) {
    this.scene = scene;
    ready = true;
    start();
  }

  void _dispose() {
    dispose();
    scene = null;
  }
}
