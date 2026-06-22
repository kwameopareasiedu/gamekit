package dev.gamekit.rendering;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Quad {
  private final int vao;
  private final int vbo;

  public Quad() {
    float[] vertices = {
      -0.5f, 0.5f,
      -0.5f, -0.5f,
      0.5f, -0.5f,

      -0.5f, 0.5f,
      0.5f, -0.5f,
      0.5f, 0.5f,
    };

    vao = glGenVertexArrays();
    vbo = glGenBuffers();

    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);

    FloatBuffer buf = BufferUtils.createFloatBuffer(vertices.length);
    buf.put(vertices).flip();

    glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
    glEnableVertexAttribArray(0);
    glBindVertexArray(0);
  }

  public void render() {
    glBindVertexArray(vao);
    glDrawArrays(GL_TRIANGLES, 0, 6);
    glBindVertexArray(0);
  }
}
