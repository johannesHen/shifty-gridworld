package gridworld;


import javax.swing.*;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: boll
 * Date: 2005-mar-01
 * Time: 16:00:16
 * To change this template use File | Settings | File Templates.
 */
//public class RLApplet extends JApplet
public class RLApplet extends JApplet
{
    //====== COMPONENTS ======
    Font iDefaultFont = new Font("Times", Font.PLAIN, 10);
    JLabel iLblStatus = new JLabel("Right Mouse Button toggles Walls. Left Mouse Button is defined above...");
    JComboBox iCmbSpeed;
    JToggleButton iBtnToggleRun = new JToggleButton("Run", false);
    JButton iBtnInit = new JButton("Init");
    JLabel iLblEta = new JLabel("Learning rate (Eta):");
    JTextField iTxtEta = new JTextField(4);
    JLabel iLblGamma = new JLabel("Discount (Gamma):");
    JTextField iTxtGamma = new JTextField(4);
    JLabel iLblEpsilon = new JLabel("Exploration (Epsilon):");
    JTextField iTxtEpsilon = new JTextField(4);
    JLabel iLblSize = new JLabel("Size (col, row):");
    JTextField iTxtSizeX = new JTextField(3);
    JTextField iTxtSizeY = new JTextField(3);
    JLabel iLblTrials = new JLabel("Episodes:");
    JTextField iTxtTrials = new JTextField(4);
    JLabel iLblCurrentTrial = new JLabel("(0)");
    JLabel iLblActionCount = new JLabel("(0)");
    JComboBox iCmbMaps;
    JComboBox iCmbLClickAction;
    JComboBox iCmbUpdate;
    
    JTextField iTxtReward = new JTextField(4);
    JLabel iLblReward = new JLabel("Reward:");
    JTextField iTxtRandom1 = new JTextField(4);
    JLabel iLblRandom1 = new JLabel("Randomness1:");
    JTextField iTxtRandom2 = new JTextField(4);
    JLabel iLblRandom2 = new JLabel("Randomness2:");
    
    //============================


    boolean iFinishedLoading = false;
    JLabel iStatusLabel;
    GridWorld iGridWorld;
    private boolean iIsRunning;
    private Thread iWorkerThread;
    private boolean iAsApplication = false;
    private String iFocusValue; // remember the value when got focus ( for text fields )

    private HashMap iSpeedMap = new HashMap();
    private int iDelay = -1; //
    private boolean iIsStepMode = false; // if speed set to step...
    private int iTrialStart = 0;
    private int iTrialCurrent;
    private Update iUpdate;
    private int iActionCount;


    public RLApplet()
    {
        super();
    }

    public RLApplet(boolean asApplication)
    {
        super();
        iAsApplication = asApplication;
    }

    /**
     * Creates the GUI, buttons etc.
     */
    protected void buildGUI()
    {
        JPanel gui = new JPanel(new BorderLayout());
        setContentPane(gui);
        JPanel[] UIPanels = new JPanel[3];
        UIPanels[0] = new JPanel(new FlowLayout(FlowLayout.LEFT));
        UIPanels[1] = new JPanel(new FlowLayout(FlowLayout.LEFT));
        UIPanels[2] = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel UIPanelMain = new JPanel(new GridLayout(3, 1));
        UIPanelMain.add(UIPanels[0]);
        UIPanelMain.add(UIPanels[1]);
        UIPanelMain.add(UIPanels[2]);
        UIPanelMain.setBorder(BorderFactory.createEtchedBorder());

        //-----------
        iLblStatus.setFont(iDefaultFont);
        iLblStatus.setBorder(BorderFactory.createEtchedBorder());

        // TODO: g�r p� samma s�tt som iCmbUpdate!
        iCmbSpeed = new JComboBox(new String[]{"Slow", "Medium", "Fast", "Very Fast", "Step"}); // MATCHES iSpeedMap
        iCmbLClickAction = new JComboBox(new String[] {GridWorld.sLMOUSE_GETVALUE, GridWorld.sLMOUSE_SETGOAL, GridWorld.sLMOUSE_SETSTART});

        iCmbMaps = new JComboBox();
        String[] maps = Map.getMaps();
        for (int i = 0; i <maps.length; i++)
            iCmbMaps.addItem(maps[i]);

        iCmbUpdate = new JComboBox();
        String[] updateMethods = Update.getsUpdateMethods();
        for (int i = 0; i <updateMethods.length; i++)
            iCmbUpdate.addItem(updateMethods[i]);
        

        iCmbSpeed.setFocusable(false);
        iCmbMaps.setFocusable(false);
        iCmbUpdate.setFocusable(false);
        iCmbLClickAction.setFocusable(false);

        iBtnInit.setFocusable(false);
        iBtnToggleRun.setFocusable(false);
        //-----------


        // construct layout
        UIPanels[0].add(iCmbMaps);
        UIPanels[0].add(iCmbLClickAction);
        UIPanels[0].add(iCmbSpeed);
        UIPanels[0].add(iCmbUpdate);
        UIPanels[0].add(iBtnToggleRun);
        UIPanels[0].add(iBtnInit);
        UIPanels[0].add(iLblTrials);
        UIPanels[0].add(iTxtTrials);
        UIPanels[0].add(iLblCurrentTrial);
        UIPanels[0].add(iLblActionCount);

        UIPanels[1].add(iLblEta);
        UIPanels[1].add(iTxtEta);
        UIPanels[1].add(iLblGamma);
        UIPanels[1].add(iTxtGamma);
        UIPanels[1].add(iLblEpsilon);
        UIPanels[1].add(iTxtEpsilon);
        UIPanels[1].add(iLblSize);
        UIPanels[1].add(iTxtSizeX);
        UIPanels[1].add(new JLabel("x"));
        UIPanels[1].add(iTxtSizeY);
        
        UIPanels[2].add(iLblReward);
        UIPanels[2].add(iTxtReward);
        UIPanels[2].add(iLblRandom1);
        UIPanels[2].add(iTxtRandom1);
        UIPanels[2].add(iLblRandom2);
        UIPanels[2].add(iTxtRandom2);


        iTxtSizeX.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));
        iTxtSizeY.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));
        iTxtEta.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
        iTxtGamma.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
        iTxtEpsilon.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
        iTxtTrials.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));
        
        iTxtReward.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));

        // DEFAULT VALUES
        iTxtSizeX.setText("5");
        iTxtSizeY.setText("7");
        iTxtEta.setText("0.1");
        iTxtGamma.setText("0.95");
        iTxtEpsilon.setText("0.1");
        iTxtTrials.setText("500");
        
        iTxtReward.setText("0");
        iTxtRandom1.setText("100");
        iTxtRandom2.setText("100");

        iTxtSizeX.setMargin(new Insets(1, 3, 1, 3));
        iTxtSizeY.setMargin(new Insets(1, 3, 1, 3));
        iTxtEta.setMargin(new Insets(1, 3, 1, 3));
        iTxtGamma.setMargin(new Insets(1, 3, 1, 3));
        iTxtEpsilon.setMargin(new Insets(1, 3, 1, 3));
        iTxtTrials.setMargin(new Insets(1, 3, 1, 3));

        iTxtReward.setMargin(new Insets(1, 3, 1, 3));
        iTxtRandom1.setMargin(new Insets(1, 3, 1, 3));
        iTxtRandom2.setMargin(new Insets(1, 3, 1, 3));
        
        //SET fonts of all components
        {
            Component[] c = UIPanels[0].getComponents();
            for (int i = 0; i < c.length; i++)
                c[i].setFont(iDefaultFont);

            c = UIPanels[1].getComponents();
            for (int i = 0; i < c.length; i++)
                c[i].setFont(iDefaultFont);

            c = UIPanels[2].getComponents();
            for (int i = 0; i < c.length; i++)
                c[i].setFont(iDefaultFont);
        }

        gui.add(iGridWorld, BorderLayout.CENTER);
        gui.add(UIPanelMain, BorderLayout.NORTH);
        gui.add(iLblStatus, BorderLayout.SOUTH);

        //=============== ACTION LISTENERS ================
        iGridWorld.addKeyListener(new KeyAdapter(){ public void keyReleased(KeyEvent e){ onKeyPressed(e);}});

        // take over the up,down,left,right arrows completely
        //KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        //manager.addKeyEventDispatcher(new KeyEventDispatcher(){ public boolean dispatchKeyEvent(KeyEvent e){ return onKeyPressed(e); } });

        iBtnToggleRun.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){onBtnToggleRun();}});
        iBtnInit.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){onBtnInit();}});

        iCmbMaps.addItemListener(new ItemListener(){public void itemStateChanged(ItemEvent e){if(e.getStateChange() == ItemEvent.SELECTED) onCmbMaps();}});
        iCmbMaps.addPopupMenuListener(new PopupMenuListener(){
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e){ iGridWorld.repaint(); }
            public void popupMenuCanceled(PopupMenuEvent e){}
            public void popupMenuWillBecomeVisible(PopupMenuEvent e){}
        } );
        iCmbLClickAction.addItemListener(new ItemListener(){public void itemStateChanged(ItemEvent e){if(e.getStateChange() == ItemEvent.SELECTED) onCmbLClickAction();}});
        iCmbLClickAction.addPopupMenuListener(new PopupMenuListener(){
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e){ iGridWorld.repaint(); }
            public void popupMenuCanceled(PopupMenuEvent e){}
            public void popupMenuWillBecomeVisible(PopupMenuEvent e){}
        } );
        iCmbSpeed.addItemListener(new ItemListener(){public void itemStateChanged(ItemEvent e){if(e.getStateChange() == ItemEvent.SELECTED) onCmbSpeed();}});
        iCmbSpeed.addPopupMenuListener(new PopupMenuListener(){
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e){ iGridWorld.repaint(); }
            public void popupMenuCanceled(PopupMenuEvent e){}
            public void popupMenuWillBecomeVisible(PopupMenuEvent e){}
        } );
//        iCmbUpdate.addItemListener(new ItemListener(){public void itemStateChanged(ItemEvent e){ if(e.getStateChange() == ItemEvent.SELECTED) onCmbUpdate();}});
//        iCmbUpdate.addPopupMenuListener(new PopupMenuListener(){
//            public void popupMenuWillBecomeInvisible(PopupMenuEvent e){ iGridWorld.repaint(); }
//            public void popupMenuCanceled(PopupMenuEvent e){}
//            public void popupMenuWillBecomeVisible(PopupMenuEvent e){}
//        } );

        iTxtTrials.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent e){ onFocus(false, iTxtTrials);}
            public void focusGained(FocusEvent e){ onFocus(true, iTxtTrials);}
        });
        iTxtEta.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent e){ onFocus(false, iTxtEta);}
            public void focusGained(FocusEvent e){ onFocus(true, iTxtEta);}
        });
        iTxtGamma.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent e){ onFocus(false, iTxtGamma);}
            public void focusGained(FocusEvent e){ onFocus(true, iTxtGamma);}
        });
        iTxtEpsilon.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent e){ onFocus(false, iTxtEpsilon);}
            public void focusGained(FocusEvent e){ onFocus(true, iTxtEpsilon);}
        });
        iTxtSizeX.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent e){ onFocus(false, iTxtSizeX);}
            public void focusGained(FocusEvent e){ onFocus(true, iTxtSizeX);}
        });
        iTxtSizeY.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent e){ onFocus(false, iTxtSizeY);}
            public void focusGained(FocusEvent e){ onFocus(true, iTxtSizeY);}
        });
        
        iTxtReward.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent e){ onFocus(false, iTxtReward);}
            public void focusGained(FocusEvent e){ onFocus(true, iTxtReward);}
        });
        
        iTxtRandom1.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent e){ onFocus(false, iTxtRandom1);}
            public void focusGained(FocusEvent e){ onFocus(true, iTxtRandom1);}
        });
        
        iTxtRandom2.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent e){ onFocus(false, iTxtRandom2);}
            public void focusGained(FocusEvent e){ onFocus(true, iTxtRandom2);}
        });
    }



    //-----------------------------------------------------------
    //  applet methods
    //-----------------------------------------------------------
    /**
     * initiate the applet, create worker and gridworld
     */
    public void init()
    {
        iSpeedMap.put("Slow", new Integer(500));
        iSpeedMap.put("Medium", new Integer(100));
        iSpeedMap.put("Fast", new Integer(1));
        iSpeedMap.put("Very Fast", new Integer(0));
        iSpeedMap.put("Step", new Integer(1));
        iDelay = ((Integer)iSpeedMap.get("Slow")).intValue();

        iUpdate = new Update();

//        setLookAndFeel();
        MediaTracker m = new MediaTracker(this);

        Image agentImage = null;
        Image goalImage = null;
        Image wallImage = null;

        URL agentURL = RLApplet.class.getResource("../img/agent.gif");
        URL goalURL = RLApplet.class.getResource("../img/goal.gif");
        URL wallURL = RLApplet.class.getResource("../img/wall.jpg");

        if(iAsApplication) // running as application
        {
            Toolkit tk = Toolkit.getDefaultToolkit();
            agentImage = tk.getImage(agentURL);
            goalImage = tk.getImage(goalURL);
            wallImage = tk.getImage(wallURL);
        }
        else // running as applet
        {
            agentImage = getImage(agentURL);
            goalImage = getImage(goalURL);
            wallImage = getImage(wallURL);
        }

        // WAIT FOR IMAGES
        m.addImage(agentImage, 0);
        m.addImage(goalImage, 1);
        m.addImage(wallImage, 2);
        try { m.waitForAll(); } catch (InterruptedException e)
        {
            System.out.println("interrupted waiting for images");
        }

        iGridWorld = new GridWorld(this, agentImage, goalImage, wallImage);
        setIsRunning(false);

        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
        try { javax.swing.SwingUtilities.invokeAndWait(new Runnable() { public void run() { buildGUI(); } }); }
        catch (Exception e)
        {
            System.err.println("buildGUI didn't successfully complete");
            System.exit(-1);
        }

        iWorkerThread = new Thread(new Runnable(){ public void run(){ doWork(); }});
        iWorkerThread.start();

    }

    public void start()
    {
//        setLookAndFeel();
//        String mapName = getSelectedMap();
//        int mapID = Map.getMapID(mapName);
        int mapID = getSelectedMap();

        iGridWorld.init(getEta(), getGamma(), getEpsilon(), mapID, getSquaresX(), getSquaresY());
        onCmbLClickAction();
        iBtnToggleRun.setSelected(false);
        iGridWorld.repaint();
    }

    public void stop()
    {
        iBtnToggleRun.setSelected(false);
        iWorkerThread.interrupt();
    }

    public String getAppletInfo()
    {
        return "Gridworld RL-Applet, (c)2005 Uppsala University";
    }
    //-----------------------------------------------------------
    // end applet methods
    //-----------------------------------------------------------
//    private void setLookAndFeel()
//    {
//        try {
//            UIManager.setLookAndFeel(
//                    UIManager.getCrossPlatformLookAndFeelClassName());
//            SwingUtilities.updateComponentTreeUI(this);
//        } catch (Exception e) { }
//    }


    /**
     * The worker thread main function.
     */
    private void doWork()
    {
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                synchronized (this)
                {
                    while (!iIsRunning)
                        wait();

                    if(iIsStepMode)
                    {
                        // do the field unlock and btnToggle stuff manually, seems to not work asych. using iBtnToggleRun.doClick();
                        iIsRunning = false;
                        toggleFieldLocks(true);
                        iBtnToggleRun.setSelected(false);
                    }
                }

                boolean isGoal = iGridWorld.tic();
                int trial = iGridWorld.getTrialCount();
                setTrialCount(trial);

                if(!isGoal) iActionCount++;

                setActionCount(iActionCount);

                int delay = getDelay();

                int lim = getTrialLimit() + iTrialStart;
                if (lim > 0 && trial >= lim)
                {
                    if(iIsRunning)    // can be stopped above if STEP mode
                    {
                        // do the field unlock and btnToggle stuff manually, seems to not work asych. using iBtnToggleRun.doClick();
                        toggleFieldLocks(true);
                        iBtnToggleRun.setSelected(false);
                        iBtnToggleRun.setText("Run");
                    }

                    iIsRunning = false;
                    delay += 20; // just in case.
                }

                if (delay > 0)
                    Thread.sleep(delay);
            }
        }
        catch (InterruptedException e)
        {
        }
    }

    private synchronized void setStepMode(boolean b)
    {
        iIsStepMode = b;
        if(b)
            iBtnToggleRun.setText("Step");
        else
            iBtnToggleRun.setText("Run");

//        iGridWorld.setStepMode(b);
    }

    public synchronized void setIsRunning(boolean state)
    {
        iIsRunning = state;
        if (state)
            notifyAll();
    }

    private void setTrialCount(int trial)
    {
        iLblCurrentTrial.setText("(" + trial + ")");
        iTrialCurrent = trial;
    }

    private void setActionCount(int action)
    {
        iLblActionCount.setText("(" + action + ")");
    }

    public void setStatus(String s)
    {
        iLblStatus.setText(s);
        iLblStatus.repaint();
    }

    private int getTrialLimit()
    {
        String s = iTxtTrials.getText();
        int d = Integer.parseInt(s);
        return d;
    }

    private int getSquaresX()
    {
        String s = iTxtSizeX.getText();
        int i = Integer.parseInt(s);
        return i;
    }

    private int getSquaresY()
    {
        String s = iTxtSizeY.getText();
        int i = Integer.parseInt(s);
        return i;
    }

    private int getDelay() { return iDelay; }
    private double getEta() { return Double.parseDouble(iTxtEta.getText()); }
    private double getGamma() { return Double.parseDouble(iTxtGamma.getText()); }
    private double getEpsilon() { return Double.parseDouble(iTxtEpsilon.getText()); }
    
    private double getReward() { return Double.parseDouble(iTxtReward.getText()); }
    private double getRandom1() { return Double.parseDouble(iTxtRandom1.getText()); }
    private double getRandom2() { return Double.parseDouble(iTxtRandom2.getText()); }
//    private String getSelectedMap() { return (String)iCmbMaps.getSelectedItem(); }
    private int getSelectedMap() { return iCmbMaps.getSelectedIndex(); }
    private int getUpdateMethod(){ return iCmbUpdate.getSelectedIndex(); }


    private void toggleFieldLocks(boolean s)
    {
        iTxtEpsilon.setEditable(s);
        iTxtEta.setEditable(s);
        iTxtGamma.setEditable(s);
        iTxtTrials.setEditable(s);
        iCmbMaps.setEnabled(s);
        iTxtSizeX.setEditable(s);
        iTxtSizeY.setEditable(s);
        iCmbUpdate.setEnabled(s);
        iTxtReward.setEditable(s);
        iTxtRandom1.setEditable(s);
        iTxtRandom2.setEditable(s);
    }
    //-----------------------------------------------------------------------------------
    //    action mapping... event handling buttons, timer, etc
    //-----------------------------------------------------------------------------------
    private void onBtnToggleRun()
    {
        if(iIsStepMode)
        {
            iGridWorld.requestFocusInWindow();
            iGridWorld.setParameters(getEta(), getGamma(), getEpsilon(), getUpdateMethod(), getReward(), getRandom1(), getRandom2());
            setIsRunning(true);
        }
        else
        {
            if (iBtnToggleRun.isSelected())
            {
                iTrialStart = iTrialCurrent;
                toggleFieldLocks(false);
                iGridWorld.setParameters(getEta(), getGamma(), getEpsilon(), getUpdateMethod(), getReward(), getRandom1(), getRandom2());
                setIsRunning(true);
                iBtnToggleRun.setText("Halt");
            }
            else
            {
                iBtnToggleRun.setText("Run");
                setIsRunning(false);
                toggleFieldLocks(true);
            }
        }
    }

    private void onKeyPressed(KeyEvent e)
    {
        int key = e.getKeyCode();

        if(iIsStepMode)
        {
            boolean doStep = true;
            if (key == KeyEvent.VK_DOWN)
                iGridWorld.setNextAction(World.sDOWN);
            else
            if (key == KeyEvent.VK_UP)
                iGridWorld.setNextAction(World.sUP);
            else
            if (key == KeyEvent.VK_LEFT)
                iGridWorld.setNextAction(World.sLEFT);
            else
            if (key == KeyEvent.VK_RIGHT)
                iGridWorld.setNextAction(World.sRIGHT);
            else
                doStep = false;

            if(doStep)
                iBtnToggleRun.doClick();
        }
    }


    private void onBtnInit()
    {
        if (iBtnToggleRun.isSelected())
            iBtnToggleRun.doClick(); // stop and reset button state

//        String mapName = getSelectedMap();
//        int mapID = Map.getMapID(mapName);
        int mapID = getSelectedMap();
        validateParameter(iTxtSizeX);
        validateParameter(iTxtSizeY);


        iGridWorld.init(getEta(), getGamma(), getEpsilon(), mapID, getSquaresX(), getSquaresY());
        onCmbLClickAction();

        setStatus(" ");
        setTrialCount(0);
        iActionCount = 0;
        setActionCount(0);
    }

    private void validateParameter(JTextField c)
    {
        if(c == iTxtSizeX || c == iTxtSizeY)
        {
            int d = Integer.parseInt(c.getText());
            if(d < 3)
                c.setText("3");
            else
            if(d > 20)
                c.setText("20");
        }

        // add more validation here if needed.

    }


    // don't accept empty fields
    private void onFocus(boolean gainedFocus, JTextField c)
    {
        if(gainedFocus)
        {
            iFocusValue = c.getText();
        }
        else
        {
            if(c.getText().equals(""))
                c.setText(iFocusValue);

            validateParameter(c);
        }

    }

//    private void onCmbUpdate()
//    {
//        int i = iCmbUpdate.getSelectedIndex();
//        iGridWorld.setUpdateMethod(i);
//    }

    private void onCmbMaps()
    {
//        String s = (String)iCmbMaps.getSelectedItem();
//        iGridWorld.setMap(Map.getMapID(s));
        onBtnInit();
    }

    private void onCmbLClickAction()
    {
        String s = (String)iCmbLClickAction.getSelectedItem();
        iGridWorld.setLMouseAction(s);
    }

    private void onCmbSpeed()
    {
        String s = (String)iCmbSpeed.getSelectedItem();

        int delay = ((Integer)iSpeedMap.get(s)).intValue();
        iDelay = delay;

        if(s.equals("Step"))
        {
            iGridWorld.requestFocusInWindow();
            setStepMode(true);
        }
        else
        {
            setStepMode(false);
        }

        if (s.equals("Very Fast"))
            iGridWorld.setShowAgent(false);
        else
            iGridWorld.setShowAgent(true);

    }


    //-----------------------------------------------------------------------------------



    // f�r att till�ta bara FLOATS / NUMERIC i textboxar...
    public class JTextFieldFilter extends PlainDocument
    {
        public static final String LOWERCASE =
                "abcdefghijklmnopqrstuvwxyz";
        public static final String UPPERCASE =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String ALPHA =
                LOWERCASE + UPPERCASE;
        public static final String NUMERIC =
                "0123456789";
        public static final String FLOAT =
                NUMERIC + ".";
        public static final String ALPHA_NUMERIC =
                ALPHA + NUMERIC;

        protected String acceptedChars = null;
        protected boolean negativeAccepted = false;

        public JTextFieldFilter()
        {
            this(ALPHA_NUMERIC);
        }

        public JTextFieldFilter(String acceptedchars)
        {
            acceptedChars = acceptedchars;
        }

//        public void setNegativeAccepted(boolean negativeaccepted)
//        {
//            if (acceptedChars.equals(NUMERIC) ||
//                    acceptedChars.equals(FLOAT) ||
//                    acceptedChars.equals(ALPHA_NUMERIC))
//            {
//                negativeAccepted = negativeaccepted;
//                acceptedChars += "-";
//            }
//        }

        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
        {
            if (str == null) return;

            if (acceptedChars.equals(UPPERCASE))
                str = str.toUpperCase();
            else if (acceptedChars.equals(LOWERCASE))
                str = str.toLowerCase();

            for (int i = 0; i < str.length(); i++)
            {
                if (acceptedChars.indexOf(String.valueOf(str.charAt(i))) == -1)
                    return;
            }

            if (acceptedChars.equals(FLOAT) || (acceptedChars.equals(FLOAT + "-") && negativeAccepted) )
            {
                if (str.indexOf(".") != -1)
                {
                    if (getText(0, getLength()).indexOf(".") != -1)
                    {
                        return;
                    }
                }
            }

            if (negativeAccepted && str.indexOf("-") != -1)
            {
                if (str.indexOf("-") != 0 || offset != 0)
                {
                    return;
                }
            }

            super.insertString(offset, str, attr);
        }
    }


    // ifall vi vill k�ra som applikation inte applet.
    public static void main(String[] args)
    {
        boolean asApplication = true;
        RLApplet app = new RLApplet(asApplication);
        app.init();
        app.start();

        final JFrame frame = new JFrame("");
        frame.setContentPane(app.getRootPane());
        frame.setSize(820,680);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}