package dev.gamekit.ui.widgets;

import dev.gamekit.core.Scene;
import dev.gamekit.core.Window;
import dev.gamekit.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.gamekit.utils.Misc.getFirstMatch;

/**
 * A widget is an abstract representation of a portion of a {@link Scene scene's} user interface.
 * <p>
 * Widgets are used to describe all aspects of a user interface, including physical aspects such as text and buttons
 * to layout effects like padding and alignment.
 * <p>
 * Widgets form a hierarchy based on composition. Each widget nests inside its parent and can receive context from
 * the parent.
 * <p>
 * To create a user interface in a scene, you override its {@code createUI} method and return the desired widget
 * hierarchy.
 * <p>
 * Widget layout is based on the <a href="https://docs.flutter.dev/ui/layout/constraints">box-constraint</a>
 * model which is used in Flutter, where constraints go down the tree, size go up and parents set positions
 */
public abstract class Widget {
  public static final Color DEBUG_COLOR = Color.GREEN;
  public static final BasicStroke DEBUG_STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  public static boolean DEBUG = false;
  public static boolean DEBUG_NAME = false;

  /**
   * Keys are a mechanism to explicitly ensure uniqueness of widgets when comparing.
   * <p>
   * During reconciliation, the UI engine will replace a widget subtree when there is class type mismatch or a key
   * mismatch.
   */
  protected final String key;
  protected final Logger logger = LogManager.getLogger(getClass());
  protected final Bounds absoluteBounds;
  protected final Bounds computedBounds;
  protected final Size intrinsicSize;
  protected Constraints constraints;
  protected Config config;
  protected Widget parent;
  protected Host host;

  private boolean mounted;

  public Widget(String key, Config config) {
    if (config == null)
      throw new IllegalArgumentException("Widget config cannot be null");

    this.key = key;
    this.config = config;
    this.absoluteBounds = new Bounds(0, 0, 0, 0);
    this.computedBounds = new Bounds(0, 0, 0, 0);
    this.intrinsicSize = new Size(0, 0);
    this.parent = null;
  }

  /** Returns the {@link #parent} of this widget */
  public Widget getParent() {
    return parent;
  }

  /** Checks if the {@link Config} object of this widget is equivalent to another {@link Widget} */
  public final boolean configEquals(Widget widget) {
    return config.equals(widget.config);
  }

  /**
   * Called to initialize the widget after it has been inserted into the widget tree and {@link #parent} has been set.
   * <p>
   * If the widget is updated some time after first initialization, this method is called afterward to re-initialize
   * the widget.
   * <p>
   * The {@link #config} will have either been set in the constructor or updated via the {@link #update} method and is
   * used to update this widget instance.
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the {@link #performInit} method instead
   * to perform any post-mount operations
   */
  public final void init(Host host) {
    this.host = host;
    config.updateWidget(this);
    performInit();
  }

  /** Delegate method for subclasses to perform additional (re)initialization operations */
  protected void performInit() { /* No-op */ }

  /**
   * During UI updates, the engine checks which widgets need to be replaced or just updated by comparing the types
   * and configurations.
   * <p>
   * For widgets that only need configurations updates, this method is called with the corresponding widget in the
   * new UI tree, containing the new configuration.
   * <p>
   * It is guaranteed that the type of the incoming widget will be the same as this widget so it is safe to cast the
   * incoming widget to this widget's config class (E.g. the config class for the {@code Image} widget will be the
   * {@code ImageConfig}).
   * <p>
   * {@link #init} method is called afterward to re-initialize the widget.
   */
  public final void update(Widget widget) {
    this.config = widget.config;
    init(host);
  }

  /**
   * Called after the widget is initialized and mounted to the active widget tree.
   * <p>
   * Since {@link #init} can be called multiple times, this is a good place to run one-off initialization tasks
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the {@link #performMount} method instead
   * to perform any post-mount operations
   */
  public final void mount() {
    if (!mounted) {
      performMount();
      mounted = true;
    }
  }

  /** Delegate method for subclasses to perform additional mount-related operations */
  protected void performMount() { /* No-op */ }

  /**
   * Computes the size of the widget and the relative position(s) of its child/children
   * <p>
   * This is called by either the parent widget or window and receives the {@link Constraints} from its parent or the
   * window, and the resulting computed size <b>must</b> always respect  this constraint
   * <p>
   * The goal of this method is to set the {@link #computedBounds} which controls where on the screen the widget is
   * rendered
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the  {@link #performLayout} method
   * instead to perform their layout
   */
  public final void layout(Constraints constraints) {
    this.constraints = constraints;
    performLayout(constraints);
  }

  /**
   * Delegate method for subclasses to perform the actual layout
   * <p>
   * It is passed the {@link Constraints} object which it <b>must</b> always respect
   */
  protected abstract void performLayout(Constraints constraints);

  /**
   * Performs post-layout logic
   * <p>
   * This exists because the engine calls {@link #layout} in a depth-first manner in traversing this widget tree.
   * <p>
   * This means a widget's {@link #layout} method will not complete until its entire widget subtree has completed and
   * hence certain data (e.g. relative position and that of the parent) will not be available until after the scene's
   * UI widget tree has been completely laid out.
   * <p>
   * After complete scene layout, this method is called to perform computations that depend on the data computed
   * during {@link #layout}
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the {@link #performPostLayout} method
   * instead to perform any post layout logic
   */
  public final void postLayout() {
    // Compute absolute position by adding computed position of this widget and its ancestors
    double absoluteX = computedBounds.x;
    double absoluteY = computedBounds.y;
    Widget visitedParent = this.parent;

    while (visitedParent != null) {
      absoluteX += visitedParent.computedBounds.x;
      absoluteY += visitedParent.computedBounds.y;
      visitedParent = visitedParent.parent;
    }

    absoluteBounds.set(
      absoluteX, absoluteY,
      computedBounds.width,
      computedBounds.height
    );

    performPostLayout();
  }

  /** Delegate method for subclasses to perform additional post-layout operations */
  protected void performPostLayout() { /* No-op */ }

  /**
   * Renders the widget with the provided {@link Graphics2D} object
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the {@link #performRender} method
   * instead to perform rendering
   */
  public final void render(Graphics2D canvasGraphics) {
    performRender(canvasGraphics);

    if (DEBUG) {
      Color originalColor = canvasGraphics.getColor();
      Stroke originalStroke = canvasGraphics.getStroke();

      canvasGraphics.setColor(DEBUG_COLOR);
      canvasGraphics.setStroke(DEBUG_STROKE);
      canvasGraphics.drawRect(
        (int) absoluteBounds.x,
        (int) absoluteBounds.y,
        (int) absoluteBounds.width,
        (int) absoluteBounds.height
      );

      if (DEBUG_NAME) {
        canvasGraphics.drawString(
          getClass().getName(),
          (int) absoluteBounds.x,
          (int) absoluteBounds.y
        );
      }

      canvasGraphics.setColor(originalColor);
      canvasGraphics.setStroke(originalStroke);
    }
  }

  /**
   * Delegate method for subclasses to performs the actual rendering.
   * <p>
   * The {@link Graphics2D} object of the widget's {@code canvasImage} to perform drawing
   */
  protected abstract void performRender(Graphics2D g);

  /**
   * Called before the widget is removed from the widget tree during UI reconciliation or during the scene's disposal.
   * <p>
   * This is a good place to clean up any resources and stop animations used by the widget.
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the {@link #performUnmount} method
   * instead to perform unmounting
   */
  public final void unmount() {
    performUnmount();
  }

  /** Delegate method for subclasses to perform additional unmount operations */
  protected void performUnmount() { /* No-op */ }

  /** Determines if the point {@code (x,y)} falls within the absolute bounds of this widget */
  public boolean hitTest(double x, double y) {
    double absoluteRight = absoluteBounds.x + absoluteBounds.width;
    double absoluteBottom = absoluteBounds.y + absoluteBounds.height;
    return absoluteBounds.x <= x && x <= absoluteRight &&
      absoluteBounds.y <= y && y <= absoluteBottom;
  }

  /**
   * Looks up the ancestor of this widget for a {@link Widget} of the specified {@code type} and returns the first
   * instance of said ancestor.
   * <p>
   * If no ancestor of the specified type is found, it returns {@code null}
   */
  public <T extends Widget> T getAncestorOfType(Class<T> type) {
    if (this.parent == null)
      return null;

    Widget parent = this.parent;

    while (parent != null) {
      if (type.isInstance(parent))
        //noinspection unchecked
        return (T) parent;

      parent = parent.parent;
    }

    return null;
  }

  /** Functional interface for all widget constructor configurators */
  @FunctionalInterface
  public interface Config {
    /** Updates matching widget configuration variables with its own variables */
    void updateWidget(Widget widget);
  }

  /** Functional interface a {@link Config} customization method */
  @FunctionalInterface
  public interface ConfigUpdater<T extends Config> {
    /** Called with a newly created {@link Config} for further customization */
    void update(T config);
  }

  /** Interface for the host containing a {@link Widget} tree, providing methods to it */
  public interface Host {
    /** Returns the font metrics for the given font from the {@link Window} object */
    FontMetrics getFontMetrics(Font font);

    /** Triggers an update of the {@link Widget widget} tree */
    void triggerUpdate();

    /** Triggers a re-render of the {@link Widget widget} tree */
    void triggerRender();
  }

  /** Mixin interface which provides functionality for comparing and updating two {@link Widget} trees */
  public interface Updater {
    List<Widget> CURRENT_QUEUE = new ArrayList<>();
    List<Widget> NEW_QUEUE = new ArrayList<>();

    /**
     * Updates the widget tree using a "diffing" algorithm.
     * <p>
     * This "diffing" algorithm involves generating a new widget tree with the new state, comparing it to the current
     * widget tree and updating or replacing widgets whose states have changed.
     */
    default void updateTree(
      Host widgetHost,
      Constraints constraints,
      ValueGetter<Widget> treeGetter,
      ValueGetter<Widget> treeCreator,
      ValueCallback<Widget> treeSetter,
      VoidCallback renderTrigger
    ) {
      CURRENT_QUEUE.clear();
      NEW_QUEUE.clear();
      boolean treeUpdated = false;

      Widget currentTree = treeGetter.get();
      CURRENT_QUEUE.add(currentTree);

      // Initialize the new tree to set up internal state before comparison
      Widget newTree = treeCreator.get();
      newTree.init(widgetHost);
      NEW_QUEUE.add(newTree);

      while (!CURRENT_QUEUE.isEmpty() && !NEW_QUEUE.isEmpty()) {
        Widget currentWidget = CURRENT_QUEUE.remove(0);
        Widget newWidget = NEW_QUEUE.remove(0);

        boolean typeMatch = currentWidget.getClass().equals(newWidget.getClass());
        boolean keyMatch = Objects.equals(currentWidget.key, newWidget.key);
        boolean configMatch = currentWidget.configEquals(newWidget);

        if (!typeMatch || !keyMatch) {
          currentWidget.unmount();

          Parent currentWidgetParent = (Parent) currentWidget.getParent();

          if (currentWidgetParent == null) {
            treeSetter.invoke(newWidget);
          } else if (currentWidgetParent instanceof SingleChildParent currentWidgetSingleChildParent) {
            currentWidgetSingleChildParent.updateChild(newWidget);
          } else if (currentWidgetParent instanceof MultiChildParent currentWidgetMultiChildParent) {
            int index = currentWidgetMultiChildParent.getIndexOf(currentWidget);
            currentWidgetMultiChildParent.updateChild(index, newWidget);
          }

          treeUpdated = true;
        } else if (!configMatch) {
          currentWidget.update(newWidget);
          treeUpdated = true;
        }

        if (currentWidget instanceof SingleChildParent currentParent && newWidget instanceof SingleChildParent newParent) {
          // Add child of SingleChildParent to queue for processing
          CURRENT_QUEUE.add(currentParent.child);
          NEW_QUEUE.add(newParent.child);
        } else if (currentWidget instanceof MultiChildParent currentParent && newWidget instanceof MultiChildParent newParent) {
          // Resize children array of current parent to accommodate for new widgets from new parent
          currentParent.resize(newParent.children.length);

          // Add children of MultiChildParent to queue for processing
          for (int i = 0; i < newParent.children.length; i++) {
            final int ii = i;
            Widget newParentWidget = newParent.children[i];
            Widget currentParentWidget = getFirstMatch(
              currentParent.children,
              widget -> newParentWidget.key != null && Objects.equals(newParentWidget.key, widget.key),
              () -> currentParent.children[ii]
            );

            CURRENT_QUEUE.add(currentParentWidget);
            NEW_QUEUE.add(newParentWidget);
          }
        }
      }

      if (Widget.DEBUG) {
        Traveller t = new Traveller() { };
        t.printTree(treeGetter.get(), 0);
      }

      if (treeUpdated) {
        Widget updatedTree = treeGetter.get();
        updatedTree.mount();
        updatedTree.layout(constraints);
        updatedTree.postLayout();
        renderTrigger.invoke();
      }
    }
  }

  /** Mixin interface which provides functionality to travel up or down a widget tree */
  public interface Traveller {
    default void printTree(Widget tree, int depth) {
      String tabs = "  ".repeat(depth);

      System.out.println(tabs + tree.getClass().getSimpleName());

      if (tree instanceof SingleChildParent parent) {
        printTree(parent.child, depth + 1);
      } else if (tree instanceof MultiChildParent parent) {
        for (Widget child : parent.children)
          printTree(child, depth + 1);
      }
    }

    /** Walks up or down a widget tree, passing each visited widget to the {@code visitor} object */
    default void travelTree(Widget tree, Direction direction, ValueCallback<Widget> visitor) {
      visitor.invoke(tree);

      switch (direction) {
        case OUTWARD -> {
          Widget parent = tree.getParent();
          if (parent == null) return;

          travelTree(parent, direction, visitor);
        }
        case INWARD -> {
          if (tree instanceof SingleChildParent parent) {
            travelTree(parent.child, direction, visitor);
          } else if (tree instanceof MultiChildParent parent) {
            for (Widget child : parent.children)
              travelTree(child, direction, visitor);
          }
        }
      }
    }

    /** Direction for widget tree traversal */
    enum Direction {
      /** Indicates a walk down the descendants of a tree */
      INWARD,
      /** Indicates a walk up the ancestry of a tree */
      OUTWARD
    }
  }
}
