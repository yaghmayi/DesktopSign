package DesktopSig;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Represents a save as dialog box 
 */
public class SaveAsFileAction extends AbstractAction 
{
	MainForm parentForm = null;
	
	/**
	 * Constructs a new initiate of  SaveAsFileAction
	 * @param paretntForm : an initiate of MainForm
	 */
	public SaveAsFileAction(MainForm paretntForm)
	{
		this.parentForm = paretntForm;
	}
	
	/**
	 * Displays a save as file dialog box
	 */
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        JFileChooser fChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Pdf files", "pdf");
        fChooser.setFileFilter(filter);
        

        int ret = fChooser.showDialog(this.parentForm, "Save file as");

        if (ret == JFileChooser.APPROVE_OPTION) 
        {
        	 File file = fChooser.getSelectedFile();
        	 String outPutFile = file.getPath().toLowerCase();
             if (!outPutFile.endsWith(".pdf")) 
                 outPutFile += ".pdf";
             
            parentForm.saveAs(outPutFile);
        }
    }
}
