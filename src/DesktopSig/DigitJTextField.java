package DesktopSig;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

/**
 * 
 * DigitJTextField is a customized TextField. 
 * The user will be able to enter only numeric value inside of DigitJTextFile.  
 */
public class DigitJTextField extends JTextField 
{
	/**
	 * Constructs a new initiate of DigitJTextField 
	 */
    public DigitJTextField()
    {
        addKeyListener(new KeyAdapter() 
        {
            public void keyTyped(KeyEvent e) 
            {
                char ch = e.getKeyChar();

                if (!isNumber(ch)) 
                {
                    e.consume();
                }
            }
        });

    }

    private boolean isNumber(char ch)
    {
        return ch >= '0' && ch <= '9';
    }
}