package gridworld;


import java.awt.*;
import java.util.Random;

class QTable
{
    private static final Random sRandom = new Random();
    private double iTable[][];

    public QTable(int stateDim, int actionDim)
    {
        iTable = new double[stateDim][actionDim];

        // random init...very small values to optimize action selection of "equal" values...
//        for (int s = 0; s < stateDim; s++)
//            for (int action = 0; action < actionDim; action++)
//                set(s, action, 0.01 * Math.random());
    }

    public void set(int state, int action, double value){ iTable[state][action] = value; }
    public void update(int state, int action, double value){ iTable[state][action] += value; }
    public double getStateActionValue(int state, int action){ return iTable[state][action]; }
    public double[] getAllValuesForState(int state){ return iTable[state]; }

    public double getMaximumStateActionValue(int state)
    {
        double max;

        max = getStateActionValue(state, 0);
        for (int action = 1; action < iTable[state].length; action++)
        {
            final double v = getStateActionValue(state, action);
            if (v > max)
                max = v;
        }

        return max;
    }

    private void showState(Graphics g, int state, int squareWidth, int squareHeight, int width)
    {
        double length = Math.min(squareWidth, squareHeight) / 2.0;
        double sw2 = (double)squareWidth / 2.0;
        double sh2 = (double)squareHeight / 2.0;

        // line transformations
        final int dx[] = { 0, 1, 0, -1};
        final int dy[] = {-1, 0, 1,  0};

        // arrow transformations
        final double arrowHeight = 0.75; // from zero point
        final double arrowWidth = 0.125; // from zero point
        final double dx1[] = {-arrowWidth,  arrowHeight, -arrowWidth, -arrowHeight};
        final double dy1[] = {-arrowHeight, -arrowWidth, arrowHeight, arrowWidth};
        final double dx2[] = {arrowWidth,   arrowHeight, arrowWidth,  -arrowHeight};
        final double dy2[] = {-arrowHeight, arrowWidth,  arrowHeight, -arrowWidth};

        // draw the greedy action in red (ie. action with highest value)
        int greedyAction = getGreedyAction(state);

        for (int a = 0; a < 4; a++)
        {
            if (a == greedyAction)
                g.setColor(Color.red);
            else
                g.setColor(new Color(150,150,150));

            // center point
            int x = (int) Math.round((int) (state % width) * (double) squareWidth + sw2);
            int y = (int) Math.round((int) ((double) state / (double) width) * (double) squareHeight + sh2);

            // end point
            final double lineLength = (length - 2) * getStateActionValue(state, a);
            int endX = (int) Math.round((double)x + dx[a] * lineLength);
            int endY = (int) Math.round((double)y + dy[a] * lineLength);

            g.drawLine(x, y, endX, endY);

            // draw arrow heads
            if(lineLength >= 4)
            {
                int endX1 = (int) Math.round((double)x + dx1[a] * lineLength);
                int endY1 = (int) Math.round((double)y + dy1[a] * lineLength);

                int endX2 = (int) Math.round((double)x + dx2[a] * lineLength);
                int endY2 = (int) Math.round((double)y + dy2[a] * lineLength);
                g.drawLine(endX, endY, endX1, endY1);
                g.drawLine(endX, endY, endX2, endY2);
                //g.drawLine(endX1, endY1, endX2, endY2);
            }


        }
        g.setColor(Color.black);
    }

    public void show(Graphics g, int squareWidth, int squareHeight, int iNSquaresX, int iNSquaresY, World w)
    {
        for (int y = 0; y < iNSquaresY; y++)
        {
            for(int x = 0; x < iNSquaresX; x++)
            {
                int state = y*iNSquaresX + x;
                if(! w.isWall(state))
                    showState(g, state, squareWidth, squareHeight, iNSquaresX);
            }
        }
    }

    public int getGreedyAction(int state)
    {
        double[] values = getAllValuesForState(state);
        double max = values[0];
        int maxIndex = 0;

        for (int i = 1; i < values.length; i++)
        {
            if (values[i] > max)
            {
                max = values[i];
                maxIndex = i;
            }
        }

        int maxes[] = new int[values.length];
        int maxCount = 0;
        for (int i = 0; i < values.length; i++)
        {
            if(max == values[i])
                maxes[maxCount++] = i;
        }

        if(maxCount > 1)
            return maxes[sRandom.nextInt(maxCount)];
        else
            return maxIndex;
    }


    public void resetState(int state)
    {
        int actions = iTable[0].length;
        for (int i = 0; i <actions; i++)
            iTable[state][i] = 0.0;
    }

}
