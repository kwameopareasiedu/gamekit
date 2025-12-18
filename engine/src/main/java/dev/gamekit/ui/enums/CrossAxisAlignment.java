package dev.gamekit.ui.enums;

import dev.gamekit.ui.widgets.Flex;

/** Constants determining how children of a {@link Flex Flex} parent are aligned along the cross axis */
public enum CrossAxisAlignment {
  /** Aligns children to the start of the cross axis */
  START,
  /** Centers children along the cross axis */
  CENTER,
  /** Aligns children to the end of the cross axis */
  END,
  /** Stretches children to same size on the cross axis */
  STRETCH
}
