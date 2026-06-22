package dev.gamekit.rendering;

import static org.lwjgl.opengl.GL30.*;

public class Shader {
  private final int programId;

  public Shader(String vertexSrc, String fragmentSrc) {
    int vertexShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexShader, vertexSrc);
    glCompileShader(vertexShader);

    if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE)
      throw new IllegalStateException(glGetShaderInfoLog(vertexShader));

    int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentShader, fragmentSrc);
    glCompileShader(fragmentShader);

    if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE)
      throw new IllegalStateException(glGetShaderInfoLog(fragmentShader));

    programId = glCreateProgram();

    glAttachShader(programId, vertexShader);
    glAttachShader(programId, fragmentShader);
    glLinkProgram(programId);
    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);
  }

  public void bind() {
    glUseProgram(programId);
  }

  public void unbind() {
    glUseProgram(0);
  }
}
