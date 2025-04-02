package dev.gamekit.ui.enums;

import dev.gamekit.ui.widgets.Flex;

/**
 * Determines how children of a {@link Flex Flex} parent are aligned along the
 * main axis
 */
public enum MainAxisAlignment {
  /** Aligns all children to the start of the main axis */
  START,
  /** Centers all children along the main axis */
  CENTER,
  /** Aligns all children to the end of the main axis */
  END,
  /**
   * Aligns all children along the main axis, distributing remaining space
   * between the children
   */
  SPACE_BETWEEN
}
