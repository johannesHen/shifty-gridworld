package gridworld;


/**
 * Created by IntelliJ IDEA.
 * User: boll
 * Date: 2005-mar-05
 * Time: 17:16:05
 * To change this template use File | Settings | File Templates.
 */
public class Map
{
    protected boolean[] iMap;
    protected int iGoal;
    protected int iSize;
    protected int iWidth;
    protected int iHeight;

    public static final int sMAPID_CLASSIC = 0;
    public static final int sMAPID_TWOROOMS = 1;
    public static final int sMAPID_RANDOM = 2;
    public static final int sMAPID_CUSTOM = 3;
    public static final int sMAPID_TEST = 4;
    public static final int sMAPID_RANDOMWALLS = 5;
    public static final int sMAPID_RANDOMATSTEPS = 6;
    private static final int sMAPID_COUNT = 7; // samma som antalet IDs
    public static String[] sMaps;

    private static Map sCustomMap;      // remember the custom map
    private int iMapID;

    private Map(int width, int height)
    {
        iMap = new boolean[width*height];
        for (int i = 0; i < iSize; i++)
            iMap[i] = false;

        iSize = width * height;
        iWidth = width;
        iHeight = height;
    }

    public static String[] getMaps()
    {
        if(sMaps == null)
        {
            sMaps = new String[sMAPID_COUNT];
            sMaps[sMAPID_CLASSIC] = "Classic Map";
            sMaps[sMAPID_TWOROOMS] = "Two Rooms Map";
            sMaps[sMAPID_RANDOM] = "Random Map";
            sMaps[sMAPID_CUSTOM] = "Custom Map";
            sMaps[sMAPID_TEST] = "Test Map";
            sMaps[sMAPID_RANDOMWALLS] = "Random Walls Map";
            sMaps[sMAPID_RANDOMATSTEPS] = "Random at steps Map";
        }

        return sMaps;
    }

    public boolean[] getMap() { return iMap; }
//    public int getWidth() { return iWidth; }
//    public int getHeight() { return iHeight; }
//    public int getSize() { return iSize; }
    private int getState(int x, int y) { return iWidth*y + x; } // from row, col to state
    public int getGoal() { return iGoal; }

    public boolean isWall(int state) { return iMap[state]; }
    public boolean isGoal(int state) { return state == iGoal; }

    public void setWall(int x, int y)
    {
        int state = getState(x,y);
        iMap[state] = true;
    }

    public void setGoal(int x, int y) { iGoal = getState(x,y); }
    public void setGoal(int state) { iGoal = state; }
    
    public void toggleWallAtStep(double random1, double random2, int agentState){
        if(iMapID == sMAPID_RANDOMATSTEPS)
        {
            if (Math.random()*random1<1){
                int state = (int) (iSize * Math.random());
                if (!(state==iGoal || state==agentState || state==iGoal-1 || state==iGoal+1 || state==iGoal-iWidth || state==iGoal+iWidth))
                {
                    iMap[state] = false;
                }
            }
            if (Math.random()*random2<1){
                int state = (int) (iSize * Math.random());
                if (!(state==iGoal || state==agentState || state==iGoal-1 || state==iGoal+1 || state==iGoal-iWidth || state==iGoal+iWidth))
                {
                    iMap[state] = true;
                }
            }
        }
    }

    public void toggleRandomWall(double random1, double random2){
        if(iMapID == sMAPID_TEST)
        {
            if (Math.random()*random1<1){
                int h=(int)((iHeight-2)*Math.random())+1;
                int w=(int)((iWidth-1)*Math.random());
                int state = h*iWidth+w;
                if (state!=iGoal)
                {
                    iMap[state] = false;
                }
            }
            if (Math.random()*random2<1){
                int h=(int)((iHeight-2)*Math.random())+1;
                int w=(int)((iWidth-1)*Math.random());
                int state = h*iWidth+w;
                if (state!=iGoal)
                {
                    iMap[state] = true;
                }
            }
        }
        if(iMapID == sMAPID_RANDOMWALLS)
            if (Math.random()*random1<1){
                int i=(int)((int)((iHeight-1)/3)*Math.random());
                while(true){
                    int state=(int)(iWidth*(i*3+2*Math.random()+1));
                    if (iMap[state]){
                        iMap[state]=false;
                        break;
                    }
                }
                while(true){
                    int state=(int)(iWidth*(i*3+2*Math.random()+1));
                    if (!iMap[state]){
                        iMap[state]=true;
                        break;
                    }
                }
                    
            }
    }

    public int getRandomStartState()
    {
        int state = 0;

        if(iMapID == sMAPID_TWOROOMS)
        {
            int start = (iHeight / 2 + 1) * iWidth; // firtst available state
            int last = iWidth * iHeight;
            
            do {  state = start + (int) ((last-start) * Math.random());  }
            while (isWall(state) || isGoal(state));
        }
        else if(iMapID == sMAPID_TEST || iMapID == sMAPID_RANDOMWALLS)
        {
            int start = (iHeight - 2) * iWidth; // firtst available state
            int last = iWidth * iHeight;
            
            do {  state = start + (int) ((last-start) * Math.random());  }
            while (isWall(state) || isGoal(state));
        }
        else if(iMapID == sMAPID_RANDOMATSTEPS)
        {
            do {  state = iWidth+1 + (int) ((iWidth-2) * Math.random()) + (int) ((iHeight-2) * Math.random())*iWidth;  }
            while (isWall(state) || isGoal(state));
        }
        else 
        {
            do {  state = (int) (iWidth * iHeight * Math.random());  }
            while (isWall(state) || isGoal(state));
        }

        return state;
    }

    public static Map createMap(int mapID, int width, int height)
    {
        Map m = null;
        switch(mapID)
        {
            case sMAPID_CLASSIC:
                m = createClassicMap(width, height);
                break;

            case sMAPID_TWOROOMS:
                m = createTwoRoomsMap(width, height);
                break;

            case sMAPID_CUSTOM:
                m = createCustomMap(width, height);
                break;

            case sMAPID_TEST:
                m = createTestMap(width, height);
                break;
                
            case sMAPID_RANDOMWALLS:
                m = createRandomWallMap(width, height);
                break;
                
            case sMAPID_RANDOMATSTEPS:
                m = createRandomAtStepsMap(width, height);
                break;
                
            default:

            case sMAPID_RANDOM:
                m = createRandomMap(width, height);
                break;
        }

        m.iMapID = mapID;
        return m;
    }


    //----------- the different maps -------------

    /**
     * Creates a random map: state, goal and walls are randomized
     */
    private static Map createRandomMap(int width, int height)
    {
        Map m = new Map(width, height);

        int tmp;
        int l = (int) Math.round(m.iMap.length * 0.2);

        for (int i = 0; i < l; i++)
        {
            do
            {
                tmp = (int) (m.iSize * Math.random());
            }
            while (m.iMap[tmp]);

            m.iMap[tmp] = true;
        }

        do
        {
            m.iGoal = (int) (m.iSize * Math.random());
        }
        while (m.iMap[m.iGoal]);

        return m;
    }
    
private static Map createRandomAtStepsMap(int width, int height)
    {
        Map m = new Map(width, height);

        int tmp;
        int l = (int) Math.round(m.iMap.length * 0.2);

        for (int i = 0; i < l; i++)
        {
            do
            {
                tmp = (int) (m.iSize * Math.random());
            }
            while (m.iMap[tmp]);

            m.iMap[tmp] = true;
        }
        
        int goalState=Math.round(height/2)*width+Math.round(width/2);
        m.iMap[goalState] = false;
        m.iMap[goalState-1] = false;
        m.iMap[goalState+1] = false;
        m.iMap[goalState-width] = false;
        m.iMap[goalState+width] = false;
        m.iGoal=goalState;

        return m;
    }
    
private static Map createTestMap(int width, int height)
    {
        Map m = new Map(width, height);

        int tmp;
        int l = (int) Math.round(width*(height-1) * 0.2);

        for (int i = 0; i < l; i++)
        {
            do
            {
                int h=(int)((height-2)*Math.random())+1;
                int w=(int)((width-1)*Math.random());
                tmp = h*width+w;
            }
            while (m.iMap[tmp]);

            m.iMap[tmp] = true;
        }
        m.iMap[width+1]=false;
        m.iGoal=width+1;

        /*do
        {
            m.iGoal = (int) (width*(height-1)+width * Math.random());
        }
        while (m.iMap[m.iGoal]);*/

        return m;
    }



private static Map createRandomWallMap(int width, int height)
    {
        Map m = new Map(width, height);
        
        for (int i=0;i<height-3;i++){
            if(i%3==0){
                int j=1;
                while(j<width){
                    int tmp=(int)((Math.random()*2+i+1)*width);
                    if(!m.iMap[tmp]){
                        m.iMap[tmp] = true;
                        j++;
                    }
                }
            }
        }
        m.iGoal=(int)width/2;

        /*do
        {
            m.iGoal = (int) (width*(height-1)+width * Math.random());
        }
        while (m.iMap[m.iGoal]);*/

        return m;
    }

    /**
     * Creates a map that is sepparated in two, goal is fixed in upper part, start
     * is randomized in lower part.
     */
    private static Map createTwoRoomsMap(int width, int height)
    {
        Map m = new Map(width, height);

        int rX = 1;
        int rY = 1;
        m.setGoal(rX,rY);

        for (int i = 0; i < width; i++)
        {
            if(i != width/2)
                m.setWall(i, height/2);
        }

        return m;
    }

    /**
     * The classic RL-map, start stade is random, goal is fixed.
     */
    private static Map createClassicMap(int width, int height)
    {
        Map m = new Map(width, height);

        int goalX = (int)(width/2.0);
        int goalY = (height==3)? 1 : 2;

        m.setGoal(goalX, goalY);

        m.setWall(goalX-1, goalY);
        m.setWall(goalX+1, goalY);
        m.setWall(goalX-1, goalY+1);
        m.setWall(goalX, goalY+1);
        m.setWall(goalX+1, goalY+1);

        return m;
    }

    private static Map createCustomMap(int width, int height)
    {
        if(sCustomMap == null || sCustomMap.iWidth != width || sCustomMap.iHeight != height)
        {
            int goalX = (int)(width/2.0);
            int goalY = (int)(height/2.0);
            sCustomMap = new Map(width, height);
            sCustomMap.setGoal(goalX, goalY);
        }

        return sCustomMap;
    }

}
