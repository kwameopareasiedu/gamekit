package dev.gamekit.core;

import dev.gamekit.settings.Settings;
import dev.gamekit.utils.VoidCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class GLFWWindow implements Window {
  private static final Logger LOGGER = LogManager.getLogger(GLFWWindow.class);

  private final long windowHandle;
  private final GLFWErrorCallback errorCallback;
  private final GLFWWindowCloseCallback windowCloseCallback;
  private final GLFWKeyCallback keyCallback;
  private VoidCallback onClose;
  private InputListener inputListener;

  GLFWWindow() {
    errorCallback = GLFWErrorCallback.createPrint(System.err).set();

    if (!glfwInit()) {
      glfwTerminate();
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    Settings settings = Application.getInstance().getSettings();

    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

    windowHandle = glfwCreateWindow(
      settings.resolution.width,
      settings.resolution.height,
      settings.title,
      NULL, NULL
    );

    if (windowHandle == NULL) {
      glfwTerminate();
      throw new IllegalStateException("Unable to create a GLFW window");
    }

    windowCloseCallback = glfwSetWindowCloseCallback(windowHandle, (win) -> {
      LOGGER.debug("Closing!!!");
      if (onClose != null)
        onClose.invoke();
    });

    keyCallback = glfwSetKeyCallback(windowHandle, (window, key, scanCode, action, mods) -> {
      LOGGER.debug("Key ev: {}, {}, {}", key, scanCode, action);

      if (inputListener != null) {
        if (action == GLFW_PRESS)
          inputListener.onKeyPressed(key);
        else if (action == GLFW_RELEASE)
          inputListener.onKeyReleased(key);
      }
    });

    try (MemoryStack memStack = MemoryStack.stackPush()) {
      IntBuffer widthBuf = memStack.mallocInt(1);
      IntBuffer heightBuf = memStack.mallocInt(1);

      glfwGetWindowSize(windowHandle, widthBuf, heightBuf);
      GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

      if (vidMode != null) {
        // Center window
        glfwSetWindowPos(windowHandle,
          (vidMode.width() - widthBuf.get(0)) / 2,
          (vidMode.height() - heightBuf.get(0)) / 2
        );
      }
    }

    glfwMakeContextCurrent(windowHandle);
    glfwSwapInterval(1);
  }

  @Override
  public void show() {
    glfwShowWindow(windowHandle);
    GL.createCapabilities();
  }

  @Override
  public boolean closeEventReceived() {
    return glfwWindowShouldClose(windowHandle);
  }

  @Override
  public void notifyClose() {
    glfwSetWindowShouldClose(windowHandle, true);
  }

  @Override
  public void setCloseListener(VoidCallback callback) {
    this.onClose = callback;
  }

  @Override
  public void setInputListener(InputListener listener) {
    this.inputListener = listener;
  }

  @Override
  public void disposeFrame() {
    glfwSwapBuffers(windowHandle);
    glfwPollEvents();
  }

  @Override
  public void dispose() {
    windowCloseCallback.free();
    keyCallback.free();
    errorCallback.free();

    glfwDestroyWindow(windowHandle);
    glfwTerminate();
  }
}
