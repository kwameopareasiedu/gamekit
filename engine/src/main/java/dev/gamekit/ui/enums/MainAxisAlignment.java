package dev.gamekit.ui.enums;

import dev.gamekit.ui.widgets.Flex;

/** Constants determining how children of a {@link Flex Flex} parent are aligned along the main axis */
public enum MainAxisAlignment {
  /** Aligns children to the start of the main axis */
  START,
  /** Centers children along the main axis */
  CENTER,
  /** Aligns children to the end of the main axis */
  END,
  /** Aligns children along the main axis, distributing remaining space between the children */
  SPACE_BETWEEN
}
