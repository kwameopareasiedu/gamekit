package dev.gamekit;

import org.junit.jupiter.api.Test;

import static dev.gamekit.utils.MathUtils.cycle;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathUtilsTest {
  @Test
  public void cycleTest() {
    assertEquals(0, cycle(4, 0, 3));
    assertEquals(3, cycle(3, 0, 3));
    assertEquals(2, cycle(2, 0, 3));
    assertEquals(1, cycle(1, 0, 3));
    assertEquals(0, cycle(0, 0, 3));
    assertEquals(3, cycle(-1, 0, 3));
  }
}
