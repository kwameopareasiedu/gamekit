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
  public void toIntTest() {
    assertEquals(4, Math.toInt(4.0));
    assertEquals(10, Math.toInt(10f));
  }

  @Test
  public void clampTest() {
    assertEquals(5, Math.clamp(5, 0, 10));
    assertEquals(10, Math.clamp(15.0, 0, 10));
  }
}
