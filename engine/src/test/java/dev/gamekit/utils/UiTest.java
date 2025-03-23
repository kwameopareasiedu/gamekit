package dev.gamekit.utils;

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
  public void sizeTest() {
    Size size = new Size(10);
    assertEquals(10, size.width);
    assertEquals(10, size.height);

    Size size2 = new Size(15, 20);
    assertEquals(15, size2.width);
    assertEquals(20, size2.height);

    Size size3 = new Size(size);
    assertEquals(10, size3.width);
    assertEquals(10, size3.height);

    size.set(29, 43);
    assertEquals(29, size.width);
    assertEquals(43, size.height);

    size.set(50);
    assertEquals(50, size.width);
    assertEquals(50, size.height);

    size.set(size2);
    assertEquals(15, size.width);
    assertEquals(20, size.height);
  }

  @Test
  public void positionTest() {
    Position position = new Position(15, 20);
    assertEquals(15, position.x);
    assertEquals(20, position.y);

    Position position2 = new Position(position);
    assertEquals(15, position2.x);
    assertEquals(20, position2.y);

    position.set(29, 43);
    assertEquals(29, position.x);
    assertEquals(43, position.y);

    position.set(position2);
    assertEquals(15, position.x);
    assertEquals(20, position.y);
  }
}
