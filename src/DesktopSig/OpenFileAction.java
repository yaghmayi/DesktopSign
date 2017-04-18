package DesktopSig;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Represents an open file dialog 
 */
public class OpenFileAction extends AbstractAction 
{
	MainForm parentForm = null;
	
	/**
	 * Constructs a new initiate of  OpenFileAction
	 * @param paretntForm : an initiate of MainForm
	 */
	public OpenFileAction(MainForm paretntForm)
	{
		this.parentForm = paretntForm;
	}
	
	/**
	 * Displays an open file dialog box 
	 */
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        JFileChooser fChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Pdf files", "pdf");
        fChooser.setFileFilter(filter);

        int ret = fChooser.showDialog(this.parentForm, "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) 
        {
            File file = fChooser.getSelectedFile();
            parentForm.loadPdf(file.getPath());
        }
    }
}
