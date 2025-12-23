package dev.gamekit.utils;

import org.junit.jupiter.api.Test;

import static dev.gamekit.utils.Math.degToRad;
import static dev.gamekit.utils.Math.radToDeg;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VectorTest {
  private static final double EPSILON = 1e-9;

  @Test
  public void angleTest() {
    assertEquals(degToRad(0), Vector.angle(new Vector(), new Vector(0, 1)));
    assertEquals(degToRad(45), Vector.angle(new Vector(), new Vector(1, 1)));
    assertEquals(degToRad(90), Vector.angle(new Vector(), new Vector(1, 0)));
    assertEquals(degToRad(135), Vector.angle(new Vector(), new Vector(1, -1)));
    assertEquals(degToRad(180), Vector.angle(new Vector(), new Vector(0, -1)));
    assertEquals(degToRad(225), Vector.angle(new Vector(), new Vector(-1, -1)));
    assertEquals(degToRad(270), Vector.angle(new Vector(), new Vector(-1, 0)));
    assertEquals(degToRad(315), Vector.angle(new Vector(), new Vector(-1, 1)));

    assertEquals(degToRad(0), Vector.angle(new Vector(1, 1), new Vector(1, 2)));
    assertEquals(degToRad(45), Vector.angle(new Vector(1, 1), new Vector(2, 2)));
    assertEquals(degToRad(90), Vector.angle(new Vector(1, 1), new Vector(2, 1)));
    assertEquals(degToRad(135), Vector.angle(new Vector(1, 1), new Vector(2, 0)));
    assertEquals(degToRad(180), Vector.angle(new Vector(1, 1), new Vector(1, 0)));
    assertEquals(degToRad(225), Vector.angle(new Vector(1, 1), new Vector(0, 0)));
    assertEquals(degToRad(270), Vector.angle(new Vector(1, 1), new Vector(-1, 1)));
    assertEquals(degToRad(315), Vector.angle(new Vector(1, 1), new Vector(0, 2)));
  }

  @Test
  public void fromMagnitudeRotationTest() {
    double hs2 = 0.5 * java.lang.Math.sqrt(2);

    Vector v1, v2;

    v1 = new Vector(0, 1);
    v2 = Vector.from(1, degToRad(0));
    assertTrue(abs(v1.x - v2.x) <= EPSILON);
    assertTrue(abs(v1.y - v2.y) <= EPSILON);

    v1 = new Vector(hs2, hs2);
    v2 = Vector.from(1, degToRad(45));
    assertTrue(abs(v1.x - v2.x) <= EPSILON);
    assertTrue(abs(v1.y - v2.y) <= EPSILON);

    v1 = new Vector(1, 0);
    v2 = Vector.from(1, degToRad(90));
    assertTrue(abs(v1.x - v2.x) <= EPSILON);
    assertTrue(abs(v1.y - v2.y) <= EPSILON);

    v1 = new Vector(hs2, -hs2);
    v2 = Vector.from(1, degToRad(135));
    assertTrue(abs(v1.x - v2.x) <= EPSILON);
    assertTrue(abs(v1.y - v2.y) <= EPSILON);

    v1 = new Vector(0, -1);
    v2 = Vector.from(1, degToRad(180));
    assertTrue(abs(v1.x - v2.x) <= EPSILON);
    assertTrue(abs(v1.y - v2.y) <= EPSILON);

    v1 = new Vector(-hs2, -hs2);
    v2 = Vector.from(1, degToRad(225));
    assertTrue(abs(v1.x - v2.x) <= EPSILON);
    assertTrue(abs(v1.y - v2.y) <= EPSILON);

    v1 = new Vector(-1, 0);
    v2 = Vector.from(1, degToRad(270));
    assertTrue(abs(v1.x - v2.x) <= EPSILON);
    assertTrue(abs(v1.y - v2.y) <= EPSILON);

    v1 = new Vector(-hs2, hs2);
    v2 = Vector.from(1, degToRad(315));
    assertTrue(abs(v1.x - v2.x) <= EPSILON);
    assertTrue(abs(v1.y - v2.y) <= EPSILON);
  }

  @Test
  public void lerpAngleTest() {
    Vector v = new Vector(0, 10);
    assertEquals(0, v.getAngle());
    assertEquals(10, v.getMagnitude());

    v.lerpAngle(new Vector(1, 0), 0.5);
    assertTrue(abs(degToRad(45) - v.getAngle()) < Math.EPSILON);

    v.lerpAngle(new Vector(1, 0), 0.5);
    assertTrue(abs(degToRad(67.5) - v.getAngle()) < Math.EPSILON);

    v.lerpAngle(new Vector(1, 0), 1);
    assertTrue(abs(PI * 0.5 - v.getAngle()) < Math.EPSILON);
    assertTrue(abs(10 - v.getMagnitude()) < Math.EPSILON);
  }
}
