package gridworld;


import java.awt.*;


// Detta �r lite fult... map kanske borde ing� i world...


class World
{
    public static final int sUP = 0;
    public static final int sRIGHT = 1;
    public static final int sDOWN = 2;
    public static final int sLEFT = 3;

//    private boolean iWorld[];
    private int iSize, iWidth, iHeight;
//    private int iGoal;

    private int iSquareHeight;
    private int iSquareWidth;
    private Color iBackGroundColor = new Color(250, 250, 250);
    private Color iGridColor = new Color(200,200,200);
    private Color iStartStateGridColor = new Color(160,160,170);
    private Image iImageGoal;
    private Image iImageWall;
    private int iStartState;

    private final int[] iStep;
    private final double iReward = 1.0;    // �r nu enbart f�r goal state.
    private Map iMap;

    public World(int rows, int columns, int mapID)
    {
        iSize = rows * columns;
        iWidth = rows;
        iHeight = columns;
//        iWorld = new boolean[iSize];
        iStep = new int[4];
        iStep[0] = -rows;
        iStep[1] = 1;
        iStep[2] = rows;
        iStep[3] = -1;

        setMap(mapID);
    }

    // paint the world... (not including the agent and the QTable)
    public void show(Graphics g, int squareWidth, int squareHeight)
    {
        iSquareHeight = squareHeight;
        iSquareWidth = squareWidth;
        Graphics2D g2d= (Graphics2D)g;
        g2d.setBackground(iBackGroundColor);
        g2d.clearRect(0, 0, (int)g2d.getClipBounds().getWidth(), (int)g2d.getClipBounds().getHeight());

        g2d.setColor(iGridColor);

        // draw walls and grid
        for (int x = 0; x < iWidth; x++)
        {
            for (int y = 0; y < iHeight; y++)
            {
                int state = y * iWidth + x;
                int posX = x * squareWidth;
                int posY = y * squareHeight;
                // draw grid
                g2d.drawRect(posX, posY, squareWidth, squareHeight);

                // draw wall
                if (isWall(state))
                    g2d.drawImage(iImageWall, posX, posY, squareWidth, squareHeight, null);

                // draw start state
                if(state == iStartState)
                {
                    g2d.setColor(iStartStateGridColor);
                    g2d.drawRect(posX+1, posY+1, squareWidth-2, squareHeight-2);
                    g2d.setColor(iGridColor);
                }

            }
        }

        // Draw the goal
        int x = (iMap.getGoal() % iWidth) * squareWidth + 2;
        int y = (iMap.getGoal() / iWidth) * squareHeight + 2;
        int w = squareWidth - 4;
        int h = squareHeight - 4;

        if(w > h)
            g2d.drawImage(iImageGoal, x + (w-h)/2, y, h, h, null);
        else if(w < h)
            g2d.drawImage(iImageGoal, x, y + (h-w)/2, w, w, null);
        else
            g2d.drawImage(iImageGoal, x, y, w, h, null);


    }

    public int doTransition(int state, int action)
    {
        final int next = state + iStep[action];

        if (isLegalTransition(state, next, action))
            return next;
        else
            return state;
    }

    public boolean isLegalState(int state){ return ( (state >= 0) && (state < iSize) && isEmpty(state) ); }
    public boolean isWall(int state){ return iMap.getMap()[state]; }
    public boolean isEmpty(int state){ return !isWall(state); }
    public boolean isGoal(int state){ return iMap.isGoal(state); }

    // kolla att om man g�r fr�n ett state till ett annat s� �r de angr�nsande col och row, dvs man kan inte warpa
    public boolean isLegalTransition(int state, int nextState, int action)
    {
        if( ! isLegalState(nextState))
            return false;

        if(action == sUP || action == sDOWN)
            return true;

        final int c = getColumn(state);
        final int c2 = getColumn(nextState);


        boolean ok=false;
        switch (action)
        {
            case World.sLEFT:
                ok = (c2 == c-1);
                break;
            case World.sRIGHT:
                ok = (c2 == c+1);
                break;
        }

        return ok;
    }

    public double getReward(int state, double stepReward)
    {
        return isGoal(state)? iReward : -stepReward;
//        if(isGoal(state)) return iReward;
//        else
//        if(isWall(state)) return -10*iReward;
//        else
//            return 0;
    }
    public int getRow(int state){ return state / iHeight; }
    public int getColumn(int state){ return state % iWidth; }
    public int getWidth(){ return iWidth; }
    public int getHeight(){ return iHeight; }
    public int getStartState(){ return iStartState; }

    // from screen coordinates to state
    public int getState(int x, int y)
    {
        int column = x / iSquareWidth;
        int row = y / iSquareHeight;
        int state = row * iWidth + column;
        return state;
    }

    public int setRandomStartState()
    {
        int state = iMap.getRandomStartState();
        setStartState(state);
        return state;
    }

    public void toggleWall(int state)
    {
        boolean[] m = iMap.getMap();
        m[state] = !m[state];

    }
    public void setGoalState(int state){ iMap.setGoal(state); }
    public void setImageGoal(Image image){ iImageGoal = image; }
    public void setImageWall(Image image){ iImageWall = image; }
    public void setStartState(int state){ iStartState = state; }

    public void setMap(int mapID)
    {
        iMap = Map.createMap(mapID, iWidth, iHeight);
//        iWorld = m.getMap();
//        iGoal = m.getGoal();
    }

    public void toggleRandomWall(double Random1, double Random2){
        iMap.toggleRandomWall(Random1, Random2);
    }
    
    public void toggleWallAtStep(double Random1, double Random2, int agentStep){
        iMap.toggleWallAtStep(Random1, Random2, agentStep);
    }

}
