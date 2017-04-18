package DesktopSig;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * Represents the start page form including Menubar, PdfPanel and GoToPagePanel instances
 */
public class MainForm extends JFrame 
{
	private JPanel pdfContainerPanel;
	private PdfJPanel pdfJPanel;
	private GotoPagePanel gotoPanel;
	
	private JMenuItem loginMenuItem;
	private JMenuItem openMenuItem;
	private JMenuItem logOutItem;
	private JMenuItem saveMenuItem;
	private JMenuItem clearSignMenuItem;
	
	/**
	 * Sets the current user information
	 * @param userEmail : Current user's email
	 * @param token : Current user's token
	 */
	public void setUserInfo(String userEmail, String token)
	{
		this.setTitle(userEmail);
		loginMenuItem.setEnabled(false);
		openMenuItem.setEnabled(true);
		clearSignMenuItem.setEnabled(true);
		saveMenuItem.setEnabled(true);
		logOutItem.setEnabled(true);
		
		pdfJPanel.setToken(token);
	}
	
	private void clearUserInfo()
	{
		this.setTitle("");
		loginMenuItem.setEnabled(true);
		openMenuItem.setEnabled(false);
		clearSignMenuItem.setEnabled(false);
		saveMenuItem.setEnabled(false);
		logOutItem.setEnabled(false);
		
		pdfJPanel.unLoadPDF();
		gotoPanel.setVisible(false);
	}
	
	/**
	 * Constructs a new initiate of MainForm
	 */
	public MainForm()
	{
		URL imageURL = getClass().getClassLoader().getResource("Icons/signature.png");
		ImageIcon img = new ImageIcon(imageURL);
		this.setIconImage(img.getImage());
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		createMenuBars();
		
		setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        
        pdfContainerPanel = new JPanel();
        pdfJPanel = new PdfJPanel();
        pdfContainerPanel.add(pdfJPanel);
        pdfJPanel.setBorder(BorderFactory.createTitledBorder(""));
        
        gotoPanel = new GotoPagePanel(pdfJPanel);
        gotoPanel.setBorder(BorderFactory.createTitledBorder(""));
        
        
        gotoPanel.setVisible(false);
        mainPanel.add(gotoPanel, BorderLayout.WEST);
        mainPanel.add(pdfContainerPanel, BorderLayout.EAST);
	}
	
	/**
	 * The startup point for running the project
	 * @param args
	 */
	public  static void main(String[] args)
	{
		  MainForm myForm = new MainForm();
		  myForm.setVisible(true);
		  myForm.setResizable(false);
		  myForm.gotoPanel.setPreferredSize(new Dimension(300, (int) myForm.getSize().getHeight()));
		  myForm.pdfContainerPanel.setPreferredSize(new Dimension((int) myForm.getSize().getWidth() - 400, 
				  												  (int) myForm.getSize().getHeight()));
	}
	
	/**
	 * Loads the specified pdf in included PDFPanel
	 * @param pdfPath : the path of a pdf file
	 */
	public void loadPdf(String pdfPath)
	{
		pdfJPanel.setCurrentPage(0);
		pdfJPanel.loadPdf(pdfPath);
		this.pack();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		gotoPanel.setVisible(true);
		gotoPanel.setTotalPage(pdfJPanel.getTotalPages());
		gotoPanel.resetPageNo();
	}
	
	/**
	 * Saves the loaded pdf as a specific file
	 * @param outPdfPath : the path of out put file 
	 */
	public void saveAs(String outPdfPath)
	{
		pdfJPanel.saveAs(outPdfPath);
	}
	
	private void createMenuBars()
	{
		JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        menubar.add(file);
        
        loginMenuItem = new JMenuItem("Login", loadIcon("Icons/login.png"));
        openMenuItem = new JMenuItem("Open", loadIcon("Icons/open.png"));
        openMenuItem.setEnabled(false);
        clearSignMenuItem = new JMenuItem("Reset", loadIcon("Icons/Clear.png"));
        clearSignMenuItem.setEnabled(false);
        saveMenuItem = new JMenuItem("Save as", loadIcon("Icons/save.png"));
        saveMenuItem.setEnabled(false);
        logOutItem = new JMenuItem("logout", loadIcon("Icons/logout.png"));
        logOutItem.setEnabled(false);
        
        file.add(loginMenuItem);
        file.add(openMenuItem);
        file.add(clearSignMenuItem);
        file.add(saveMenuItem);
        file.add(logOutItem);
        
        openMenuItem.addActionListener(new OpenFileAction(this));
        saveMenuItem.addActionListener(new SaveAsFileAction(this));
       
        clearSignMenuItem.addActionListener((ActionEvent event) -> 
    	{
    		pdfJPanel.clearSign();
    	});
        
        loginMenuItem.addActionListener((ActionEvent event) -> 
    	{
    		LoginForm loginForm = new LoginForm(this);
    		loginForm.show();
    	});
        
        logOutItem.addActionListener((ActionEvent event) -> 
    	{
    		this.clearUserInfo();
    	});
        
        setJMenuBar(menubar);
	}
	
	private Icon loadIcon(String iconPath)
    {
    	URL imageURL = getClass().getClassLoader().getResource(iconPath);
        Icon icon = new ImageIcon(imageURL);
        
        return icon;
    }
}
