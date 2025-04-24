package dev.gamekit.utils;

import dev.gamekit.ui.Bounds;
import dev.gamekit.ui.Spacing;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UiTest {
  @Test
  public void spacingTest() {
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

    spacing.set(100, 50);
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

    Spacing s1 = new Spacing(spacing);
    assertEquals(101, s1.top);
    assertEquals(151, s1.right);
    assertEquals(194, s1.bottom);
    assertEquals(193, s1.left);
    assertEquals(295, s1.getVertical());
    assertEquals(344, s1.getHorizontal());
  }

  @Test
  public void boundsTest() {
    Bounds bounds = new Bounds(15, 20, 75, 100);
    assertEquals(15, bounds.x);
    assertEquals(20, bounds.y);
    assertEquals(75, bounds.width);
    assertEquals(100, bounds.height);

    Bounds bounds2 = new Bounds(bounds);
    assertEquals(15, bounds2.x);
    assertEquals(20, bounds2.y);
    assertEquals(75, bounds2.width);
    assertEquals(100, bounds2.height);

    bounds.setPosition(29, 43);
    assertEquals(29, bounds.x);
    assertEquals(43, bounds.y);

    bounds.set(bounds2);
    assertEquals(15, bounds.x);
    assertEquals(20, bounds.y);
    assertEquals(75, bounds.width);
    assertEquals(100, bounds.height);

    bounds.setPosition(21, 23);
    assertEquals(21, bounds.x);
    assertEquals(23, bounds.y);

    bounds.setSize(93, 104);
    assertEquals(93, bounds.width);
    assertEquals(104, bounds.height);

    bounds.set(0, 0, 50, 50);
    bounds2.set(30, 0, 100, 100);
    bounds.extend(bounds2);

    assertEquals(0, bounds.x);
    assertEquals(0, bounds.y);
    assertEquals(130, bounds.width);
    assertEquals(100, bounds.height);

    bounds.set(0, 0, 50, 50);
    bounds2.set(25, -10, 200, 150);
    bounds.extend(bounds2);

    assertEquals(0, bounds.x);
    assertEquals(-10, bounds.y);
    assertEquals(225, bounds.width);
    assertEquals(150, bounds.height);

    bounds.set(25, 25, 50, 50);
    bounds2.set(10, 30, 25, 10);
    bounds.extend(bounds2);

    assertEquals(10, bounds.x);
    assertEquals(25, bounds.y);
    assertEquals(65, bounds.width);
    assertEquals(50, bounds.height);

    bounds.set(0, 0, 1920, 1080);
    bounds2.set(50, 50, 100, 100);
    Bounds intersection = bounds.intersection(bounds2);

    assertEquals(50, intersection.x);
    assertEquals(50, intersection.y);
    assertEquals(100, intersection.width);
    assertEquals(100, intersection.height);
  }
}
