package dev.gamekit.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathUtilsTest {
  @Test
  public void cycleTest() {
    assertEquals(0, MathUtils.cycle(4, 0, 3));
    assertEquals(3, MathUtils.cycle(3, 0, 3));
    assertEquals(2, MathUtils.cycle(2, 0, 3));
    assertEquals(1, MathUtils.cycle(1, 0, 3));
    assertEquals(0, MathUtils.cycle(0, 0, 3));
    assertEquals(3, MathUtils.cycle(-1, 0, 3));
  }

  @Test
  public void toIntTest() {
    assertEquals(4, MathUtils.toInt(4.0));
    assertEquals(10, MathUtils.toInt(10f));
  }

  @Test
  public void clampTest() {
    assertEquals(5, MathUtils.clamp(5, 0, 10));
    assertEquals(10, MathUtils.clamp(15.0, 0, 10));
  }
}
