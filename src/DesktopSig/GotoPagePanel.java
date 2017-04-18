package DesktopSig;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * GotoPagePanel is a Panel for navigating between in loaded PDF pages
 */
public class GotoPagePanel extends JPanel 
{
	private JLabel totalPagesLabel;
	private DigitJTextField pageTxt;
	private int totalPage;
	
	/**
	 *  Sets total page value
	 */
	public void setTotalPage(int totalPage)
	{
		this.totalPage = totalPage;
		totalPagesLabel.setText(" / " + totalPage);
	}
	
	/**
	 * Resets the current page to the first page 
	 */
	public void resetPageNo()
	{
		pageTxt.setText("1");
	}
	
	/**
	 * Constructs a new initiate of GotoPagePanel
	 * @param pdfPanel
	 */
	public GotoPagePanel(PdfJPanel pdfPanel)
	{
		JLabel pageLabel = new JLabel("Page ");
		addComponent(pageLabel, 1, 1, 100);
		
		
		pageTxt = new DigitJTextField();
		pageTxt.setText("1");
		addComponent(pageTxt, 1, 2, 50);
		
		totalPagesLabel = new JLabel(" / " + pdfPanel.getTotalPages());
		addComponent(totalPagesLabel, 1, 3, 50);
		
		JButton okButton = new JButton("GO");
		addComponent(okButton, 2, 3, 60);
		
		okButton.addActionListener((ActionEvent event) -> 
    	{
    		int pageNo = Integer.parseInt(pageTxt.getText()) -1;
    		
    		if (pageNo <= totalPage)
    			pdfPanel.gotoPage(pageNo);
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
