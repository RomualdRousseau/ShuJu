package org.shuju.qlearner;

public class QLearner
{
  public QLearner(QEnvironment env, double discountFactor, double learnRate, QMatrix matrix)  {
    this(env, discountFactor, learnRate, matrix, -1);
  }
  
  public QLearner(QEnvironment env, double discountFactor, double learnRate, QMatrix matrix, int maxIteration) {
    this.env = env;
    this.discountFactor = discountFactor;
    this.learnRate = learnRate;
    this.maxIteration = maxIteration;

    
    this.q = matrix;
    this.iteration = 0;
    
    this.env.reset();
  }
  
  public void reset() {
    this.iteration = 0;
    this.env.reset();
  }
  
  public boolean explore(double confidence) {
    if(env.interactiveMode) {
      env.doInteractive();
      return false;
    }
    
    // Observe the current state
    int state = this.env.getState();
    
    // Choose and take the action from the state using policy predicted from Q (e.g. e-greedy)
    int action = egreedy((int) random(env.numActions), this.q.predictAction(state), confidence);
    while(!this.env.doAction(action)) {
      action = (int) random(env.numActions); // if action not doable, choose a pure random policy
    }
    
    // Observe the new state, as well as the reward
    int newState = this.env.getState();
    double reward = this.env.getReward();
    
    // Update the Q-value for the state using the observed reward and the maximum reward possible for the next state
    this.q.train(state, action, reward + this.discountFactor * this.q.predictReward(newState), this.learnRate); 
    this.iteration++;
    
    // check if episode is completed and restart a new episode if necessary
    if(env.isCompleted()) {
      this.reset();
      return true;
    }
    else if(this.maxIteration > 0 && this.iteration == this.maxIteration) {
      this.reset();
      return false;
    }
    else {
      return false;
    }
  }
  
  public boolean exploit() {
    if(env.interactiveMode) {
      env.doInteractive();
      return false;
    }
    
    this.env.doAction(q.predictAction(env.getState()));
    return this.env.isCompleted();
  }
  
  public void draw() {
    this.env.draw();
  }
  
  private int egreedy(int a1, int a2, double t) {
    return random(1.0) >= t ? a1 : a2;
  }

  private double random(double l) {
  	return Math.random() * l;
  }
  
  private QEnvironment env;
  private double discountFactor;
  private double learnRate;
  private int maxIteration;
  private QMatrix q;
  private int iteration;
}
