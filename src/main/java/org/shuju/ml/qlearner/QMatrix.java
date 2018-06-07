package org.shuju.ml.qlearner;

public abstract class QMatrix
{
  public abstract void reset();
  
  public abstract void train(int s, int a, double v, double learnRate);
  
  public abstract int predictAction(int s);
  
  public abstract double predictReward(int s);
}
