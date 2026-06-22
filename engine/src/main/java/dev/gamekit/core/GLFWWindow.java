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
  private final GLFWErrorCallback glfwErrorCallback;
  private final GLFWWindowCloseCallback glfwWindowCloseCallback;
  private final GLFWKeyCallback glfwKeyCallback;
  private VoidCallback closeCallback;
  private InputCallback inputCallback;

  GLFWWindow() {
    glfwErrorCallback = GLFWErrorCallback.createPrint(System.err).set();

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

    glfwWindowCloseCallback = glfwSetWindowCloseCallback(windowHandle, (win) -> {
      if (closeCallback != null)
        closeCallback.invoke();
    });

    glfwKeyCallback = glfwSetKeyCallback(windowHandle, (window, key, scanCode, action, mods) -> {
      LOGGER.debug("Key ev: {}, {}, {}", key, scanCode, action);

      if (inputCallback != null) {
        if (action == GLFW_PRESS)
          inputCallback.onKeyPressed(key);
        else if (action == GLFW_RELEASE)
          inputCallback.onKeyReleased(key);
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
  public void setCloseCallback(VoidCallback callback) {
    this.closeCallback = callback;
  }

  @Override
  public void setInputCallback(InputCallback listener) {
    this.inputCallback = listener;
  }

  @Override
  public void disposeFrame() {
    glfwSwapBuffers(windowHandle);
    glfwPollEvents();
  }

  @Override
  public void dispose() {
    glfwWindowCloseCallback.free();
    glfwKeyCallback.free();
    glfwErrorCallback.free();

    glfwDestroyWindow(windowHandle);
    glfwTerminate();
  }
}
