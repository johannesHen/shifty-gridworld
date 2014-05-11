package gridworld;


import javax.swing.*;
import java.awt.*;

/**
 */
public class MySpinner extends JSpinner
{
    public MySpinner(SpinnerModel model)
    {
        super(model);
        JTextField tf = ((JSpinner.ListEditor)this.getEditor()).getTextField();
        tf.setEditable(false);
        tf.setBackground(Color.WHITE);
//        tf.setColumns(6);
    }

    public void setFont(Font f)
    {
        JTextField tf = ((JSpinner.ListEditor)this.getEditor()).getTextField();
        tf.setFont(f);
    }
}