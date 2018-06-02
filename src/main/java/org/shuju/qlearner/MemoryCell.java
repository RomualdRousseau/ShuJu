package org.shuju.qlearner;

class MemoryCell
{
  public int state;
  public int action;
  public double reward;
  public int timestamp;

  public MemoryCell(int s, int a, double v) {
    this(s, a, v, 0);
  }

  public MemoryCell(int s, int a, double v, int t) {
    state = s;
    action = a;
    reward = v;
    timestamp = t;
  }

  public static double[] IntToDoubleArray(int a, int n) {
    double[] result = new double[n];
    for(int i = 0; i < n; i++) {
      if((a & (1 << i)) > 0) {
        result[i] = 1.0;  
      }
      else {
        result[i] = 0.0;    
      }
    }
    return result;
  }

  public static int DoubleArrayToInt(double[] a, int n) {
    int result = 0;
    for(int i = 0; i < n; i++) {
      if(a[i] >= 0.5) {
        result |= (1 << i);
      }
    }
    return result;
  }
}
