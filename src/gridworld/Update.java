package gridworld;


/**
 * Implements all Update functions
 */
public class Update
{
    public static final int sUPDATEID_QLEARNING = 0;
    public static final int sUPDATEID_SARSA = 1;
    public static final int sUPDATEID_NONE = 2;
    private static final int sUPDATEID_COUNT = 3; // m�ste vara samma som antalet ID

    private static String[] sUpdateMethods;

    public static String[] getsUpdateMethods()
    {
        if(sUpdateMethods == null)
        {
            sUpdateMethods = new String[sUPDATEID_COUNT];
            sUpdateMethods[sUPDATEID_QLEARNING] = "Q-Learning";
            sUpdateMethods[sUPDATEID_SARSA] = "SARSA";
            sUpdateMethods[sUPDATEID_NONE] = "None";
        }
    
        return sUpdateMethods;
    }

    /**
     * @param updateMethodID one of the sUPDATEID_xxxx
     */
    public void update(int updateMethodID, QTable table,
                       int state, int action, double reward, int newState, int newAction,
                       double eta, double gamma)
    {
        if(updateMethodID == sUPDATEID_QLEARNING)
            updateQLearning(table, state, action, reward, newState, eta, gamma);
        else
        if(updateMethodID == sUPDATEID_SARSA)
            updateSARSA(table, state, action, reward, newState, newAction, eta, gamma);
        //else
        //if(updateMethodID == sUPDATEID_NONE)
            // do nothing
    }


    /**
     * Q-Learning update
     */
    public void updateQLearning(QTable table,
                                int state, int action, double reward, int newState,
                                double eta, double gamma)
    {
        final double maxV = table.getMaximumStateActionValue(newState);
        final double thisV = table.getStateActionValue(state, action);
        double update = eta * (reward + gamma * maxV - thisV);
        table.update(state, action, update);
    }



    /**
     * SARSA update
     */
    public void updateSARSA(QTable table,
                            int state, int action, double reward, int newState, int newAction,
                            double eta, double gamma)
    {
        final double newV = table.getStateActionValue(newState, newAction);
        final double thisV = table.getStateActionValue(state, action);
        final double update = eta * (reward + gamma * newV - thisV);
        table.update(state,action, update);
    }

}