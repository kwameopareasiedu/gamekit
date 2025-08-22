package dev.gamekit.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathTest {
  @Test
  public void cycleTest() {
    assertEquals(0, Math.cycle(4, 0, 3));
    assertEquals(3, Math.cycle(3, 0, 3));
    assertEquals(2, Math.cycle(2, 0, 3));
    assertEquals(1, Math.cycle(1, 0, 3));
    assertEquals(0, Math.cycle(0, 0, 3));
    assertEquals(3, Math.cycle(-1, 0, 3));
  }

  @Test
  public void clampTest() {
    assertEquals(5, Math.clamp(5, 0, 10));
    assertEquals(10, Math.clamp(15.0, 0, 10));
  }

  @Test
  public void degToRadTest() {
    assertEquals(0, Math.degToRad(0));
    assertEquals(0.5 * java.lang.Math.PI, Math.degToRad(90));
    assertEquals(java.lang.Math.PI, Math.degToRad(180));
    assertEquals(1.5 * java.lang.Math.PI, Math.degToRad(270));
    assertEquals(2 * java.lang.Math.PI, Math.degToRad(360));
    assertEquals(2.5 * java.lang.Math.PI, Math.degToRad(450));
  }

  @Test
  public void radToDegTest() {
    assertEquals(0, Math.radToDeg(0));
    assertEquals(90, Math.radToDeg(0.5 * java.lang.Math.PI));
    assertEquals(180, Math.radToDeg(java.lang.Math.PI));
    assertEquals(270, Math.radToDeg(1.5 * java.lang.Math.PI));
    assertEquals(360, Math.radToDeg(2 * java.lang.Math.PI));
    assertEquals(450, Math.radToDeg(2.5 * java.lang.Math.PI));
  }

  @Test
  public void lerpTest() {
    assertEquals(0, Math.lerp(0, 10, 0));
    assertEquals(10, Math.lerp(0, 10, 1));
    assertEquals(5, Math.lerp(0, 10, 0.5));
    assertEquals(10, Math.lerp(10, 20, 0));
    assertEquals(20, Math.lerp(10, 20, 1));
    assertEquals(15, Math.lerp(10, 20, 0.5));
  }

  @Test
  public void angleTest() {
    assertEquals(Math.degToRad(0), Vector.angle(new Vector(), new Vector(0, 1)));
    assertEquals(Math.degToRad(45), Vector.angle(new Vector(), new Vector(1, 1)));
    assertEquals(Math.degToRad(90), Vector.angle(new Vector(), new Vector(1, 0)));
    assertEquals(Math.degToRad(135), Vector.angle(new Vector(), new Vector(1, -1)));
    assertEquals(Math.degToRad(180), Vector.angle(new Vector(), new Vector(0, -1)));
    assertEquals(Math.degToRad(225), Vector.angle(new Vector(), new Vector(-1, -1)));
    assertEquals(Math.degToRad(270), Vector.angle(new Vector(), new Vector(-1, 0)));
    assertEquals(Math.degToRad(315), Vector.angle(new Vector(), new Vector(-1, 1)));
  }
}
