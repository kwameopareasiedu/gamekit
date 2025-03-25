package dev.gamekit.core;

import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.widgets.MultiChildParent;
import dev.gamekit.ui.widgets.Parent;
import dev.gamekit.ui.widgets.SingleChildParent;
import dev.gamekit.ui.widgets.Widget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * A scene is a collection of {@link Prop props} interacting with each other
 * to form a logic part of your application. This can be a main menu, or a
 * level within your game.
 * <p>
 * A scene can also display a {@link Widget} tree which forms its user interface
 */
public abstract class Scene {
  static Scene current;

  protected final Logger logger = LogManager.getLogger(getClass());
  protected final String name;
  protected final Map<Integer, Prop> props;

  private final Queue<Widget> currentWidgetsCompareQueue;
  private final Queue<Widget> newWidgetsCompareQueue;
  private boolean widgetTreeNeedsUpdate = false;
  private boolean widgetTreeNeedsRender = true;
  private Widget widgetTree;

  /**
   * Creates a scene with the given name
   * @param name The name of the scene for logging purposes
   */
  public Scene(String name) {
    this.name = name;
    props = new HashMap<>();
    currentWidgetsCompareQueue = new ArrayDeque<>();
    newWidgetsCompareQueue = new ArrayDeque<>();
  }

  /** Returns the currently loaded scene instance */
  public static Scene getCurrent() { return current; }

  public String getName() { return name; }

  protected void addProp(Prop prop) {
    logger.debug("Adding child: [{} - {}]", prop.internalId, prop.name);

    if (!props.containsKey(prop.internalId)) {
      Application.getInstance().scheduleTask(() -> {
        logger.debug("Added child: {} ({})", prop.name, prop.internalId);
        props.put(prop.internalId, prop);
        if (!prop.ready) prop.onStart();
      });
    }
  }

  protected void removeProp(Prop prop) {
    logger.debug("Removing child: [{} - {}]", prop.internalId, prop.name);

    if (props.containsKey(prop.internalId)) {
      Application.getInstance().scheduleTask(() -> {
        logger.debug("Removed child: {} ({})", prop.name, prop.internalId);
        props.remove(prop.internalId, prop);
        if (prop.ready) prop.onDispose();
      });
    }
  }

  /** Called by {@link #start()} to set up the scene */
  protected void onStart() { }

  /** Called by {@link #update()} to update the scene */
  protected void onUpdate() { }

  /** Called by {@link #render()} to render the scene */
  protected void onRender() { }

  /** Called by {@link #dispose()} to dispose the scene */
  protected void onDispose() { }

  /**
   * Called by {@link #start()} to create the widget tree for the UI
   * Can be overridden by subclasses if they wish to create a UI
   */
  protected Widget onCreateWidgetTree() {
    return null;
  }

  /** Indicates that the widget tree should be updated based on a state change */
  protected final void updateWidgetTree(WidgetTreeUpdater updater) {
    updater.onUpdate();
    widgetTreeNeedsUpdate = true;
  }

  /**
   * Called by {@link Application} to initialize the scene.
   * This calls {@link #onStart()} before calling
   * {@link Prop#onStart() onStart()} on each child prop
   */
  final void start() {
    logger.debug("Starting scene");

    widgetTree = onCreateWidgetTree();

    if (widgetTree != null) {
      Window window = Window.getInstance();

      widgetTree.computeLayout(
        new Constraints(
          window.getRenderWidth(),
          window.getRenderWidth(),
          window.getRenderHeight(),
          window.getRenderHeight()
        )
      );
    }

    onStart();
    props.forEach((k, v) -> v.onStart());
  }

  /**
   * Called by {@link Application} to update the scene.
   * This calls {@link #onUpdate()} before calling
   * {@link Prop#onUpdate() onUpdate()} on each child prop
   */
  final void update() {
    onUpdate();
    props.forEach((k, v) -> v.onUpdate());

    if (widgetTree != null && widgetTreeNeedsUpdate) {
      updateWidgetTreeImpl();
      widgetTreeNeedsUpdate = false;
    }
  }

  /**
   * Called by {@link Application} to render the scene.
   * This calls {@link #onRender()} first, then calls
   * {@link Prop#onRender() onRender()} on each child prop
   * and finally renders the widget tree, if set
   */
  final void render() {
    onRender();
    props.forEach((k, v) -> v.onRender());

    if (widgetTree != null && widgetTreeNeedsRender) {
      logger.debug("Rendering widget tree");
      Renderer.drawWidget(widgetTree);
      widgetTreeNeedsRender = false;
    }
  }

  /**
   * Called by {@link Application} to render the scene.
   * This calls {@link Prop#onDispose() onDispose()}
   * on each child prop before calling {@link #onDispose()}
   */
  final void dispose() {
    logger.debug("Disposing scene");
    props.forEach((k, v) -> v.onDispose());
    onDispose();
  }

  /**
   * Updates the UI by creating a new tree, comparing to the current
   * tree and re-rendering the subtrees that have changed. This is
   * the reconciliation step
   */
  private void updateWidgetTreeImpl() {
    // TODO Monitor performance and consider moving to a new thread if necessary

    Widget newTree = onCreateWidgetTree();
    newWidgetsCompareQueue.add(newTree);

    currentWidgetsCompareQueue.add(widgetTree);
    boolean widgetTreeUpdated = false;

    while (true) {
      Widget treeWidget = currentWidgetsCompareQueue.poll();
      Widget newWidget = newWidgetsCompareQueue.poll();

      if (treeWidget == null && newWidget == null)
        break;

      if (!Objects.equals(treeWidget, newWidget)) {
        // Widget tree differs at this point, reconcile subtrees at this depth
        Parent treeWidgetParent = (Parent) treeWidget.getParent();

        if (treeWidgetParent == null) {
          widgetTree = newWidget;
          widgetTreeUpdated = true;
        } else if (treeWidgetParent instanceof SingleChildParent currentParent) {
          currentParent.updateChild(newWidget);
          widgetTreeUpdated = true;
        } else if (treeWidgetParent instanceof MultiChildParent currentParent) {
          int index = currentParent.getChildren().indexOf(treeWidget);
          currentParent.updateChild(newWidget, index);
          widgetTreeUpdated = true;
        }
      } else if (treeWidget instanceof SingleChildParent currentParent
        && newWidget instanceof SingleChildParent newParent) {
        // Add child of SingleChildParent to queue for processing
        currentWidgetsCompareQueue.add(currentParent.getChild());
        newWidgetsCompareQueue.add(newParent.getChild());
      } else if (treeWidget instanceof MultiChildParent currentParent
        && newWidget instanceof MultiChildParent newParent) {
        // Add children of MultiChildParent to queue for processing
        currentWidgetsCompareQueue.addAll(currentParent.getChildren());
        newWidgetsCompareQueue.addAll(newParent.getChildren());
      }
    }

    if (widgetTreeUpdated) {
      Window window = Window.getInstance();

      widgetTree.computeLayout(
        new Constraints(
          window.getRenderWidth(),
          window.getRenderWidth(),
          window.getRenderHeight(),
          window.getRenderHeight()
        )
      );
    }

    widgetTreeNeedsRender = widgetTreeUpdated;
    currentWidgetsCompareQueue.clear();
    newWidgetsCompareQueue.clear();
  }

  public interface WidgetTreeUpdater {
    void onUpdate();
  }
}
