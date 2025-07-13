package dev.gamekit.traits;

import dev.gamekit.core.Application;
import dev.gamekit.core.Physics;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Trait;
import dev.gamekit.utils.Constants;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Rectangle;

import java.awt.*;

/** The {@link Physical} {@link Trait trait} adds physics-based motion to an entity */
public class Physical extends Trait {
  public static boolean DEBUG_DRAW = false;

  private final Body body;

  public Physical() {
    body = new Body();
    body.setMassType(MassType.INFINITE);
  }

  public Physical(MassType massType, Vector2 massCenter, double mass, double inertia) {
    body = new Body();
    body.setMassType(massType);
    body.setMass(new Mass(massCenter, mass, inertia));
  }

  public void setGravityScale(double scale) {
    body.setGravityScale(scale);
  }

  public BodyFixture addCircleFixture(double radius) {
    Circle circle = new Circle(radius);
    BodyFixture fx = new BodyFixture(circle);
    body.addFixture(fx);
    body.updateMass();
    return fx;
  }

  public BodyFixture addRectFixture(double width, double height) {
    Rectangle rect = new Rectangle(width, height);
    BodyFixture fx = new BodyFixture(rect);
    body.addFixture(fx);
    body.updateMass();
    return fx;
  }

  @Override
  protected void start() {
    super.start();
    Physics.addBody(body);
  }

  @Override
  protected void render() {
    super.render();

    if (DEBUG_DRAW) {
      Vector2 center = body.getWorldCenter();
      int scaledCenterX = (int) (center.x * Constants.PIXELS_PER_METER);
      int scaledCenterY = (int) (center.y * Constants.PIXELS_PER_METER);

      for (BodyFixture fx : body.getFixtures()) {
        Convex shape = fx.getShape();

        if (shape instanceof Circle circle) {
          Renderer.fillCircle(
            scaledCenterX, scaledCenterY,
            (int) (circle.getRadius() * Constants.PIXELS_PER_METER)
          ).withColor(Color.CYAN);
        } else if (shape instanceof Rectangle rect) {
          Renderer.drawRect(
            scaledCenterX, scaledCenterY,
            (int) (rect.getWidth() * Constants.PIXELS_PER_METER),
            (int) (rect.getHeight() * Constants.PIXELS_PER_METER)
          ).withColor(Color.CYAN);
        }
      }
    }
  }

  @Override
  protected void dispose() {
    super.dispose();

    Application.getInstance().scheduleTask(() -> {
      Physics.removeBody(body);
    });
  }
}
