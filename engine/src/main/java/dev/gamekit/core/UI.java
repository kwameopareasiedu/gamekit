package dev.gamekit.core;

import dev.gamekit.ui.Bounds;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.widgets.MultiChildParent;
import dev.gamekit.ui.widgets.Parent;
import dev.gamekit.ui.widgets.SingleChildParent;
import dev.gamekit.ui.widgets.Widget;
import dev.gamekit.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

/**
 * UI manages the user interface within for {@link Scene}.
 * A {@link Scene} will contain a single instance of UI
 */
final class UI {
  private static final Logger LOGGER = LogManager.getLogger();

  private final Queue<Widget> currentWidgetQueue;
  private final Queue<Widget> newWidgetQueue;
  private final WidgetTreeCreator treeCreator;
  private final Constraints windowConstraints;
  private Widget tree;
  private boolean needsUpdate = false;
  private boolean needsRepaint = true;

  public UI(WidgetTreeCreator treeCreator) {
    Window window = Window.getInstance();

    this.windowConstraints = new Constraints(
      window.getRenderWidth(),
      window.getRenderWidth(),
      window.getRenderHeight(),
      window.getRenderHeight()
    );

    this.treeCreator = treeCreator;
    this.currentWidgetQueue = new ArrayDeque<>();
    this.newWidgetQueue = new ArrayDeque<>();
  }

  /** Sets the widget tree and triggers the layout computation */
  public void setWidgetTree(Widget tree) {
    this.tree = tree;

    if (this.tree != null) {
      this.tree.computeLayout(windowConstraints);
    }
  }

  public void triggerUpdate() {
    needsUpdate = true;
  }

  /**
   * If {@link #needsUpdate} is true, this performs a reconciliation process on the widget tree.
   * <p>
   * During reconciliation, a new widget tree is generated and compared to the current one.
   * Widgets in the current tree whose states differ from those in the new tree are substituted
   * with those of the new tree.
   * <p>
   * At the end of this, the updated widget tree's layout is recomputed and {@link #needsRepaint}
   * is set to trigger a re-render.
   */
  public void update() {
    if (tree == null || !needsUpdate)
      return;

    Widget newTree = treeCreator.onCreateUI();
    newWidgetQueue.add(newTree);

    currentWidgetQueue.add(tree);
    boolean treeUpdated = false;

    while (true) {
      Widget treeWidget = currentWidgetQueue.poll();
      Widget newWidget = newWidgetQueue.poll();

      if (treeWidget == null && newWidget == null)
        break;

      if (!Objects.equals(treeWidget, newWidget)) {
        // Widget tree differs at this point, reconcile subtrees at this depth
        Parent treeWidgetParent = (Parent) treeWidget.getParent();

        if (treeWidgetParent == null) {
          tree = newWidget;
          treeUpdated = true;
        } else if (treeWidgetParent instanceof SingleChildParent currentParent) {
          currentParent.updateChild(newWidget);
          treeUpdated = true;
        } else if (treeWidgetParent instanceof MultiChildParent currentParent) {
          int index = currentParent.getChildren().indexOf(treeWidget);
          currentParent.updateChild(newWidget, index);
          treeUpdated = true;
        }
      } else if (treeWidget instanceof SingleChildParent currentParent
        && newWidget instanceof SingleChildParent newParent) {
        // Add child of SingleChildParent to queue for processing
        currentWidgetQueue.add(currentParent.getChild());
        newWidgetQueue.add(newParent.getChild());
      } else if (treeWidget instanceof MultiChildParent currentParent
        && newWidget instanceof MultiChildParent newParent) {
        // Add children of MultiChildParent to queue for processing
        currentWidgetQueue.addAll(currentParent.getChildren());
        newWidgetQueue.addAll(newParent.getChildren());
      }
    }

    if (treeUpdated) {
      tree.computeLayout(windowConstraints);
    }

    needsRepaint = treeUpdated;
    currentWidgetQueue.clear();
    newWidgetQueue.clear();
    needsUpdate = false;
  }

  /** Draws the {@link Widget} tree to the {@link Window} UI target */
  public void render() {
    if (tree == null || !needsRepaint)
      return;

    LOGGER.debug("Rendering UI");

    Window win = Window.getInstance();
    Graphics2D g = win.getUiGraphics();
    Widget.Appearance appearance = tree.getAppearance();
    Bounds widgetBounds = tree.getComputedBounds();

    g.setBackground(Constants.TRANSPARENT_COLOR);
    g.clearRect(0, 0, win.getRenderWidth(), win.getRenderHeight());
    g.drawImage(
      appearance.image,
      widgetBounds.x,
      widgetBounds.y,
      widgetBounds.width,
      widgetBounds.height,
      null
    );

    needsRepaint = false;
  }

  public interface WidgetTreeCreator {
    /** Called to create a widget tree */
    Widget onCreateUI();
  }

  public interface WidgetTreeUpdater {
    void onUpdate();
  }
}
