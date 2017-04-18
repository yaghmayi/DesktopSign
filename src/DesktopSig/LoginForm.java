package DesktopSig;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * A form for user login functionality
 */
public class LoginForm extends JFrame 
{
	/**
	 * Constructs a new initiate of LoginForm
	 * @param parentForm : is an initiate of MainForm
	 */
	public LoginForm(MainForm parentForm)
	{
		URL imageURL = getClass().getClassLoader().getResource("Icons/login.png");
		ImageIcon img = new ImageIcon(imageURL);
		this.setIconImage(img.getImage());
		this.setSize(new Dimension(350, 200));
		this.setResizable(false);
		
		setLayout(null);
		
		JLabel emailLable = new JLabel("Email ");
		addComponent(emailLable, 1, 1, 100);
		
		
		JTextField emailTxt = new JTextField();
		addComponent(emailTxt, 1, 2, 200);
		
		JLabel passwordLabel = new JLabel("Password ");
		addComponent(passwordLabel, 2, 1, 100);
		
		JPasswordField passwordField = new JPasswordField();
		addComponent(passwordField, 2, 2, 200);
		
		JLabel messageLabel = new JLabel("");
		messageLabel.setForeground(Color.RED);
		addComponent(messageLabel, 3, 2, 250);
		
		JButton okButton = new JButton("Login");
		addComponent(okButton, 4, 2, 200);
		
		okButton.addActionListener((ActionEvent event) -> 
    	{
    		String email = emailTxt.getText().toLowerCase().trim();
    		String password = passwordField.getText();
    		
    		SignService signService = new SignService();
    		String token = signService.getToken(email, password);
    		if (token != null)
    		{
    			parentForm.setUserInfo(email, token);
        		setVisible(false); //you can't see me!
        		dispose();
    		}
    		else
    			messageLabel.setText("Your email or password is incorrect.");
    	});
	}
	
	private void addComponent(JComponent component, int row, int column, int width)
	{
		int height = 25;
		int space = 10;
		int y = (row * height) + space;
		int x = (column - 1) * 100 + space;
		component.setBounds(x, y, width, height);
		
		component.setPreferredSize(new Dimension(width, height));
		add(component);
	}
}
