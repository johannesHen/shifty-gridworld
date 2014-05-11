package gridworld;


import java.awt.*;
import java.util.Random;

class Agent
{
    private Random sRandom = new Random();
    private QTable iQTable;
    private int iCurrentState;
    private World iWorld;
    private Image iImage;
    private int iNextAction; // anv�nds f�r manuell kontroll.
    private int iNActions;
    private int iPreviousAction;  // minns f�rra
    private int iPreviousState;   // minns f�rra
    private boolean iIsFirstTime;
    private int iUpdateMethodID;
    private Update iUpdate;
    private Object iSyncObject1 = new Object();
    private Object iSyncObject2 = new Object();

    public Agent(World w, QTable Q, Update u, int nActions)
    {
        iWorld = w;
        iQTable = Q;
        iUpdate = u;
        setNextAction(-1);
        iNActions = nActions;
    }

    public synchronized void newTrial(int state)
    {
        setCurrentState(state);
        setNextAction(-1);
        iIsFirstTime = true;
    }

    /**
     * Perform action selection, state transition and update QTable
     * Don't update the first iteration.
     * @return true if ends up in the goal state, else false.
     */
    public synchronized boolean act(double eta, double gamma, double epsilon, double stepReward)
    {
        // SELECT ACTION
        int action = getNextAction();
        if(action > -1)  // next action has been decided externally
            setNextAction(-1);
        else
            action = doSelectAction(iCurrentState, epsilon);

        // STATE TRANSITION
        int newState = iWorld.doTransition(iCurrentState, action);
        boolean isGoal = iWorld.isGoal(iCurrentState); // can't happen first time

        // UPDATE QTABLE
        if(!iIsFirstTime)
        {
            double reward = iWorld.getReward(iCurrentState, stepReward);
            iUpdate.update(getUpdateMethod(), iQTable, iPreviousState, iPreviousAction, reward, iCurrentState, action, eta, gamma);
        }
        else { iIsFirstTime = false; }

        // remember
        iPreviousState = iCurrentState;
        iPreviousAction = action;
        setCurrentState(newState);

        return isGoal;
    }

    // Epsilon-greedy action selection
    private int doSelectAction(int state, double epsilon)
    {
        if (sRandom.nextDouble() < epsilon)
            return sRandom.nextInt(iNActions);

        return iQTable.getGreedyAction(state);
    }

    public void show(Graphics g, int squareWidth, int squareHeight)
    {
        Graphics2D g2d = (Graphics2D)(g);

        int currentState = getCurrentState();

        int width = iWorld.getWidth();
        int x = (currentState % width) * squareWidth + 2;
        int y = (currentState / width) * squareHeight + 2;
        int w = squareWidth - 4;
        int h = squareHeight - 4;

        if(w > h)
            g2d.drawImage(iImage, x + (w-h)/2, y, h, h, null);
        else if(w < h)
            g2d.drawImage(iImage, x, y + (h-w)/2, w, w, null);
        else
            g2d.drawImage(iImage, x, y, w, h, null);
    }


    public int getCurrentState() { synchronized(iSyncObject1) { return iCurrentState; } }
    private void setCurrentState(int i) { synchronized(iSyncObject1) { iCurrentState = i; } }

    private int getUpdateMethod() { return iUpdateMethodID; }
    public void setUpdateMethod(int i) {  iUpdateMethodID = i; }

    public void setNextAction(int action) { synchronized(iSyncObject2) { iNextAction = action; } }
    private int getNextAction() { synchronized(iSyncObject2) { return iNextAction; } }

    public void setImage(Image i) { iImage = i; }
}
