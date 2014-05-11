package gridworld;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GridWorld extends JComponent
{
    public static final String sLMOUSE_GETVALUE = "Left MB: Get Value";
    public static final String sLMOUSE_SETGOAL = "Left MB: Set Goal";
    public static final String sLMOUSE_SETSTART = "Left MB: Set Start";

    private static final int sNActions = 4;

    private World iWorld;
    private Agent iAgent;
    private QTable iQTable;
    private int iWidth = 714;
    private int iHeight = 714;

    private int iNSquaresX;
    private int iNSquaresY;
    private double iEta;
    private double iGamma;
    private double iEpsilon;
    private RLApplet iApplet;
    private int iNTrials;
    private boolean iShowAgent = true;
    
    private double iReward;
    private double iRandom1;
    private double iRandom2;

    private Image iImageAgent;
    private Image iImageGoal;
    private Image iImageWall;
    private String iLMouseAction;
    private Update iUpdate;

    public GridWorld(RLApplet a, Image agent, Image goal, Image wall)
    {
        setOpaque(true);
        iImageAgent = agent;
        iImageGoal = goal;
        iImageWall = wall;
        iApplet = a;
        iUpdate = new Update();

        setFocusable(true); // enable focus to generate keyevents.
        addMouseListener(new MouseAdapter(){ public void mouseClicked(MouseEvent e){ onMouseClick(e); } });

        setPreferredSize(new Dimension(iWidth, iHeight));
    }

    public void init(double eta, double gamma, double epsilon, int mapID, int squaresX, int squaresY)
    {
        iNTrials = 0;

        iNSquaresX = squaresX;
        iNSquaresY = squaresY;

        iWorld = new World(iNSquaresX, iNSquaresY, mapID);
        iQTable = new QTable(iNSquaresX * iNSquaresY, sNActions);
        iAgent = new Agent(iWorld, iQTable, iUpdate, sNActions);

        iAgent.setImage(iImageAgent);
        iWorld.setImageGoal(iImageGoal);
        iWorld.setImageWall(iImageWall);

        iWorld.setRandomStartState();
        iAgent.newTrial(iWorld.getStartState());

        repaint();
    }


    /**
     * Handle mouse click events
     */
    protected void onMouseClick(MouseEvent e)
    {
        requestFocusInWindow();

        int x = e.getX();
        int y = e.getY();
        int state = iWorld.getState(x, y);

        if (e.getButton() == MouseEvent.BUTTON1)  // left
        {
            if(iLMouseAction.equals(sLMOUSE_GETVALUE))
            {
                String s = "State " + state + " Q:" +
                        " [N: " + iQTable.getStateActionValue(state, 0) +
                        "] [E: " + iQTable.getStateActionValue(state, 1) +
                        "] [S: " + iQTable.getStateActionValue(state, 2) +
                        "] [W: " + iQTable.getStateActionValue(state, 3) + "]";
                iApplet.setStatus(s);
            }
            else
            if(iLMouseAction.equals(sLMOUSE_SETGOAL))
            {
                if(iAgent.getCurrentState() != state && !iWorld.isWall(state))
                {
                    iWorld.setGoalState(state);
                    iQTable.resetState(state);
                    repaint();
                }
            }
            else
            if(iLMouseAction.equals(sLMOUSE_SETSTART))
            {
                if(!iWorld.isGoal(state) && !iWorld.isWall(state) && state != iAgent.getCurrentState() )
                {
                    iWorld.setStartState(state);
                    iAgent.newTrial(state);
                    repaint();
                }
            }
        }

//        if (e.getButton() == MouseEvent.BUTTON2) // middle{}

        if (e.getButton() == MouseEvent.BUTTON3 && state != iAgent.getCurrentState()) // right
        {
            iWorld.toggleWall(state);
            if(iWorld.isWall(state))
                iQTable.resetState(state);
            repaint();
        }
            
    }

    public boolean tic()
    {
        boolean isGoal = iAgent.act(iEta, iGamma, iEpsilon, iReward);
        if (isGoal)
        {
            iNTrials++;
            iWorld.toggleRandomWall(iRandom1, iRandom2);
            iAgent.newTrial(iWorld.setRandomStartState());
        }
        iWorld.toggleWallAtStep(iRandom1, iRandom2, iAgent.getCurrentState());
        repaint();
        return isGoal;
    }

    protected void paintComponent(Graphics g)
    {
        Rectangle r = g.getClipBounds();
        Dimension s = getSize();

        if(r.getX() > 0 || r.getY() > 0 || r.getHeight() < s.getHeight() || r.getWidth() < s.getWidth())
        {
            repaint();  // do not handle repaints of parts of the component
        }
        else
        {
            int height = (int) s.getHeight();
            int width = (int) s.getWidth();
            int squareWidth = width / iNSquaresX;
            int squareHeight = height / iNSquaresY;

            iWorld.show(g, squareWidth, squareHeight);
            iQTable.show(g, squareWidth, squareHeight, iNSquaresX, iNSquaresY, iWorld);

            if (iShowAgent)
                iAgent.show(g, squareWidth, squareHeight);
        }

    }

    public void setParameters(double eta, double gamma, double epsilon, int updateMethod, double reward, double random1, double random2)
    {
        iEta = eta;
        iGamma = gamma;
        iEpsilon = epsilon;
        setUpdateMethod(updateMethod);
        iReward =reward;
        iRandom1 = random1;
        iRandom2 = random2;
    }

    public void setShowAgent(boolean b)
    {
        iShowAgent = b;
    }

    public void setMap(int mapID)
    {
        iWorld.setMap(mapID);
        repaint();
    }

    public synchronized void setLMouseAction(String action)
    {
        iLMouseAction = action;
    }

    public void setNextAction(int action)
    {
        iAgent.setNextAction(action);
    }

    public void setUpdateMethod(int i)
    {
        iAgent.setUpdateMethod(i);
    }

    public int getTrialCount()
    {
        return iNTrials;
    }
}
