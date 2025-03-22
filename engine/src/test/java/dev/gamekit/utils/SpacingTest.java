package dev.gamekit.utils;

import dev.gamekit.ui.Spacing;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpacingTest {
  @Test
  public void valueTest() {
    Spacing spacing = new Spacing(10);
    assertEquals(10, spacing.top);
    assertEquals(10, spacing.right);
    assertEquals(10, spacing.bottom);
    assertEquals(10, spacing.left);
    assertEquals(20, spacing.getVertical());
    assertEquals(20, spacing.getHorizontal());

    spacing.set(50);
    assertEquals(50, spacing.top);
    assertEquals(50, spacing.right);
    assertEquals(50, spacing.bottom);
    assertEquals(50, spacing.left);
    assertEquals(100, spacing.getVertical());
    assertEquals(100, spacing.getHorizontal());

    spacing.set(50, 100);
    assertEquals(50, spacing.top);
    assertEquals(100, spacing.right);
    assertEquals(50, spacing.bottom);
    assertEquals(100, spacing.left);
    assertEquals(100, spacing.getVertical());
    assertEquals(200, spacing.getHorizontal());

    spacing.set(10, 20, 30, 40);
    assertEquals(10, spacing.top);
    assertEquals(20, spacing.right);
    assertEquals(30, spacing.bottom);
    assertEquals(40, spacing.left);
    assertEquals(40, spacing.getVertical());
    assertEquals(60, spacing.getHorizontal());

    spacing.set(new Spacing(101, 151, 194, 193));
    assertEquals(101, spacing.top);
    assertEquals(151, spacing.right);
    assertEquals(194, spacing.bottom);
    assertEquals(193, spacing.left);
    assertEquals(295, spacing.getVertical());
    assertEquals(344, spacing.getHorizontal());
  }
}
