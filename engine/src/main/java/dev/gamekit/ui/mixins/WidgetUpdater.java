package dev.gamekit.ui.mixins;

import dev.gamekit.ui.widgets.MultiChildParent;
import dev.gamekit.ui.widgets.Parent;
import dev.gamekit.ui.widgets.SingleChildParent;
import dev.gamekit.ui.widgets.Widget;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.ValueCallback;
import dev.gamekit.utils.VoidCallback;

import java.util.ArrayList;
import java.util.List;

/** Mixin which provides functionality for comparing and updating two {@link Widget} trees */
public interface WidgetUpdater {
  List<Widget> CURRENT_QUEUE = new ArrayList<>();
  List<Widget> NEW_QUEUE = new ArrayList<>();

  /**
   * Updates the widget tree using a "diffing" algorithm.
   * <p>
   * This "diffing" algorithm involves generating a new widget tree with the new state, comparing it to the current
   * widget tree and updating or replacing widgets whose states have changed.
   */
  default void updateTree(
    Widget.Host widgetHost,
    Constraints constraints,
    Widget currentTree,
    Widget newTree,
    ValueCallback<Widget> treeSetter,
    VoidCallback renderTrigger
  ) {
    CURRENT_QUEUE.clear();
    NEW_QUEUE.clear();
    boolean treeUpdated = false;

    // Initialize the new tree to set up internal state before comparison
    newTree.init(widgetHost);
    CURRENT_QUEUE.add(currentTree);
    NEW_QUEUE.add(newTree);

    while (!CURRENT_QUEUE.isEmpty() && !NEW_QUEUE.isEmpty()) {
      Widget currentWidget = CURRENT_QUEUE.remove(0);
      Widget newWidget = NEW_QUEUE.remove(0);

      boolean typeMatch = currentWidget.getClass().equals(newWidget.getClass());
      boolean configMatch = currentWidget.configEquals(newWidget);

      if (!typeMatch) {
        Parent currentWidgetParent = (Parent) currentWidget.getParent();

        currentWidget.unmount();

        if (currentWidgetParent == null) {
          currentTree = newWidget;
        } else if (currentWidgetParent instanceof SingleChildParent currentWidgetSingleChildParent) {
          currentWidgetSingleChildParent.updateChild(newWidget);
        } else if (currentWidgetParent instanceof MultiChildParent currentWidgetMultiChildParent) {
          int index = List.of(currentWidgetMultiChildParent.getChildren()).indexOf(currentWidget);
          currentWidgetMultiChildParent.updateChild(index, newWidget);
        }

        treeUpdated = true;
      } else if (!configMatch) {
        currentWidget.update(newWidget);
        treeUpdated = true;
      }

      if (currentWidget instanceof SingleChildParent currentParent && newWidget instanceof SingleChildParent newParent) {
        // Add child of SingleChildParent to queue for processing
        CURRENT_QUEUE.add(currentParent.getChild());
        NEW_QUEUE.add(newParent.getChild());
      } else if (currentWidget instanceof MultiChildParent currentParent && newWidget instanceof MultiChildParent newParent) {
        // Add children of MultiChildParent to queue for processing
        List<Widget> currentParentChildrenWidgets = List.of(currentParent.getChildren());
        List<Widget> newParentChildrenWidgets = List.of(newParent.getChildren());
        CURRENT_QUEUE.addAll(currentParentChildrenWidgets);
        NEW_QUEUE.addAll(newParentChildrenWidgets);
      }
    }

    if (treeUpdated) {
      treeSetter.update(currentTree);
      currentTree.layout(constraints);
      currentTree.postLayout();
      renderTrigger.run();
    }
  }
}
