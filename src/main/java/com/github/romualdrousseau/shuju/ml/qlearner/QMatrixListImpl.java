package com.github.romualdrousseau.shuju.ml.qlearner;

import java.util.HashMap;
import java.util.Map.Entry;

public class QMatrixListImpl extends QMatrix
{
  public QMatrixListImpl(QEnvironment env) {
    this.numStates = env.numStates;
    this.numActions = env.numActions;
  }

  public void reset() {
    this.map.clear();
  }

  public void train(int s, int a, double v, double learnRate) {
    Integer[] node = new Integer[] {s, a};
    
    MemoryCell cell = this.map.get(node);
    if(cell == null) {
      cell = new MemoryCell(s, a, learnRate * v);
      this.map.put(node, cell); 
    }
    else {
      cell.reward += learnRate * (v - cell.reward);
    }
  }

  public int predictAction(int s) {
    MemoryCell e = getBestMemory(s);
    return (e == null) ? 0 : e.action;
  }

  public double predictReward(int s) {
    MemoryCell e = getBestMemory(s);
    return (e == null) ? 0 : e.reward;
  }

  private MemoryCell getBestMemory(int s) {
    MemoryCell result = null;
    for(MemoryCell e: map.values()) if(e.state == s) {
      if(result == null || e.reward > result.reward) {
        result = e;
      }
    }
    return result;
  }

  private int numStates;
  private int numActions;
  private HashMap<Integer[], MemoryCell> map = new HashMap<Integer[], MemoryCell>();
}
