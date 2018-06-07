package org.shuju.cv;

public class Template
{
  public Template(int[][] data) {
    this.data = data;
  }
  
  public int getWidth() {
    return this.data[0].length;
  }
  
  public int getHeight() {
    return this.data.length;
  }
  
  public int get(int x, int y) {
    return this.data[y][x];
  }
  
  private int[][] data;
}