package dev.gamekit.ui.nodes;

import dev.gamekit.ui.Node;
import dev.gamekit.utils.Alignment;

/** A container which centers its single child */
public class Center extends Align {
  public Center(Node child) {
    super(child, Alignment.CENTER);
  }
}
