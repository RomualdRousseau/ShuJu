package com.github.romualdrousseau.shuju.graph;

import java.util.function.BiFunction;

public abstract class Graph
{
  public float zoom = 50;
  public float mouseX = 0;
  public float mouseY = 0;

  protected GraphFunction inline;

  public Graph apply(GraphFunction inline_) {
    inline = inline_;
    return this;
  }

  public abstract void draw();

  public abstract void pixels(BiFunction<Float, Float, Float> inline);

  public abstract void point(float[] p, String label, boolean moveable);

  public abstract void segment(float[] p1, float[] p2, String label);

  public abstract void line(float[] l, String label);
}
