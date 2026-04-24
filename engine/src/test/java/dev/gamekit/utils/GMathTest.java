package dev.gamekit.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GMathTest {
  @Test
  public void cycleTest() {
    assertEquals(0, GMath.cycle(4, 0, 3));
    assertEquals(3, GMath.cycle(3, 0, 3));
    assertEquals(2, GMath.cycle(2, 0, 3));
    assertEquals(1, GMath.cycle(1, 0, 3));
    assertEquals(0, GMath.cycle(0, 0, 3));
    assertEquals(3, GMath.cycle(-1, 0, 3));
  }

  @Test
  public void clampTest() {
    assertEquals(5, GMath.clamp(5, 0, 10));
    assertEquals(10, GMath.clamp(15.0, 0, 10));
  }

  @Test
  public void degToRadTest() {
    assertEquals(0, GMath.degToRad(0));
    assertEquals(0.5 * Math.PI, GMath.degToRad(90));
    assertEquals(Math.PI, GMath.degToRad(180));
    assertEquals(1.5 * Math.PI, GMath.degToRad(270));
    assertEquals(2 * Math.PI, GMath.degToRad(360));
    assertEquals(2.5 * Math.PI, GMath.degToRad(450));
  }

  @Test
  public void radToDegTest() {
    assertEquals(0, GMath.radToDeg(0));
    assertEquals(90, GMath.radToDeg(0.5 * Math.PI));
    assertEquals(180, GMath.radToDeg(Math.PI));
    assertEquals(270, GMath.radToDeg(1.5 * Math.PI));
    assertEquals(360, GMath.radToDeg(2 * Math.PI));
    assertEquals(450, GMath.radToDeg(2.5 * Math.PI));
  }

  @Test
  public void lerpTest() {
    assertEquals(0, GMath.lerp(0, 10, 0));
    assertEquals(10, GMath.lerp(0, 10, 1));
    assertEquals(5, GMath.lerp(0, 10, 0.5));
    assertEquals(10, GMath.lerp(10, 20, 0));
    assertEquals(20, GMath.lerp(10, 20, 1));
    assertEquals(15, GMath.lerp(10, 20, 0.5));
  }
}
