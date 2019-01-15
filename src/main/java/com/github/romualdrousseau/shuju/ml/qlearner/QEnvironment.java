package com.github.romualdrousseau.shuju.ml.qlearner;

public abstract class QEnvironment {
    public QEnvironment(int numStates, int numActions) {
        this(numStates, numActions, false);
    }

    public QEnvironment(int numStates, int numActions, boolean interactiveMode) {
        this.interactiveMode = interactiveMode;
        this.numStates = numStates;
        this.numActions = numActions;
    }

    public boolean isCompleted() {
        return isWon() || isLost();
    }

    public abstract boolean isWon();

    public abstract boolean isLost();

    public abstract int reset();

    public abstract int getState();

    public abstract double getReward();

    public abstract boolean doAction(int a);

    public abstract void doInteractive();

    public abstract void draw();

    protected boolean interactiveMode;
    protected int numStates;
    protected int numActions;
}
