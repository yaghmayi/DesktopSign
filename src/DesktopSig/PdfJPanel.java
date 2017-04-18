package DesktopSig;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;

import com.teamdev.jxdocument.Document;
import com.teamdev.jxdocument.Page;

/**
 * Display a pdf file with signing functionality
 */
class PdfJPanel extends JPanel 
{
	  private String pdfPath;
	  private Image pdfImage;
	  private List<List<Point>> signatureVector = new ArrayList<>();
	  private int mouseX;
	  private int mouseY;
	  private boolean sign = false;
	  private boolean unloadFile = false;
	  private String token;
	  private String pdfHashCode;
	  
	  private int totalPages = 0;
	  private int currentPage = 0;
	  
	  /**
	   * Constructs a new initiate of PdfJPanel
	   */
	  public PdfJPanel() 
	  {
	    setLayout(null);
	    this.setBorder(BorderFactory.createTitledBorder(""));
	    PdfJPanel thisPanel = this;
	    
	    MouseAdapter mouseListener = new MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
            	thisPanel.mouseX = e.getX();
            	thisPanel.mouseY = e.getY();
            	thisPanel.sign = true;
            	
            	if (signatureVector.size() == 0)
            	{
            		SignService signService = new SignService();
    		    	signatureVector = signService.getSignature(thisPanel.token, thisPanel.pdfHashCode);
            	}
            	
            	if (signatureVector.size() == 0)
            	{
            		//show message -> time out or invalid signature or ...
            	}
            	else
            		thisPanel.updateUI();
            }
        };
        
        thisPanel.addMouseListener(mouseListener);
	  }
	  
	  /**
	   * Gets total pages numbers
	   * @return the number of total pages as a integer value
	   */
	  public int getTotalPages() 
	  {
		  return totalPages;
	  }

	  /**
	   * Get the current page number
	   * @return the current page number as a integer value.
	   * Note: the current page value will start from zero.
	   */
	  public int getCurrentPage() 
	  {
		  return currentPage;
	  }

	  /**
	   * Sets the current page value
	   * @param currentPage : the specified page number; starts from zero
	   */
	  public void setCurrentPage(int currentPage) 
	  {
		  this.currentPage = currentPage;
	  }
	  
	  /**
	   * Goes to a specific number and changes the current page value
	   * @param pageNo : the page number
	   */
	  public void gotoPage(int pageNo) 
	  {
		  this.currentPage = pageNo;
		  loadPdf(pdfPath);
	  }
	  
	  /**
	   * Clears the user's signature and resets the loaded pdf to unmodified state. 
	   */
	  public void clearSign()
	  {
		  this.sign = false;
		  updateUI();
	  }
	  
	  /**
	   * Unloads the loaded pdf and displays an empty panel. It also clear the current user's signature from cache data.
	   */
	  public void unLoadPDF()
	  {
		  this.unloadFile = true;
		  this.token = null;
		  this.pdfHashCode = null;
		  this.signatureVector.clear();
		  this.sign = false;
		  
		  updateUI();
	  }
	  
	  /**
	   * Sets the current user's token
	   * @param token : the current user's token
	   */
	  public void setToken(String token)
	  {
		  this.token = token;
	  }
	  
	  /**
	   * Loads a specified pdf
	   * @param pdfPath : the path of pdf file
	   */
	  public void loadPdf(String pdfPath)
	  {
		  this.unloadFile = false;
		  this.sign = false;
		  this.signatureVector.clear();
		  
		  this.pdfPath = pdfPath;
		  File file = new File(pdfPath);
		  Document document = new Document(file);
		  this.totalPages = document.getPageCount();
		  
		  String pdfContent = "";
		  for (Page page : document.getPages())
			pdfContent += page.getText();  
		  
		  this.pdfHashCode = Integer.toString(pdfContent.hashCode());
		  
		  
		  if (this.totalPages > 0) 
		  {
			  Page page = document.getPageAt(this.currentPage);
			  Dimension pageSize = page.getSize();
			  Image pageImage = page.convertToImage(pageSize.width, pageSize.height);
			  this.setPreferredSize(new Dimension(pageSize.width, pageSize.height));
			  this.pdfImage = pageImage;
			    
			  updateUI();
		  }
		  
		  document.close();
	  }
	 
	  /**
	   * Overrides JPanel.paintComponent
	   */
	  @Override
	  public void paintComponent(Graphics g) 
	  {
		if (unloadFile)
		{
			 Dimension size = this.getSize();
		     g.clearRect(0, 0, size.width, size.height);
		}
		else if (this.pdfImage != null)
		{
		    g.drawImage(pdfImage, 0, 0, null);
		    
		    if (this.sign)
		    {
		    	List<Point> firstList = signatureVector.get(0);
		    	int startX = (int) firstList.get(0).getX();
	    		int startY = (int) firstList.get(0).getY();
	    		
		    	for (List<Point> pList : signatureVector)
		    	{
		    		for (int i=0; i<pList.size()-1; i++)
		    		{
		    			Point p1 = pList.get(i);
		    			Point p2 = pList.get(i + 1);
		    			
		    			int x1 = (int) (mouseX + p1.getX() - startX);
		    			int y1 = (int) (mouseY + p1.getY() - startY);
		    			int x2 = (int) (mouseX + p2.getX() - startX);
		    			int y2 = (int) (mouseY + p2.getY() - startY);
		    			
		    			g.drawLine(x1, y1, x2, y2);
		    		}
		    	}
		    }
		}
	  }
	  
	  /**
	   * Saves the modified pdf as out put pdf
	   * @param outPutFile : the path of out put pdf
	   */
	  public void saveAs(String outPutFile)
	  {
         PDDocument doc = null;
         try
         {
             doc = PDDocument.load( new File(pdfPath) );
             PDPage page = doc.getPage(this.currentPage);
             
             if (this.sign)
             {
                 org.apache.pdfbox.pdmodel.PDPageContentStream contentStream = 
                		 new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page, AppendMode.APPEND, true);

                int startX = getStartSignX((int) page.getMediaBox().getWidth());
                
                List<Point> firstList = signatureVector.get(0);
                int fistPointX = (int) firstList.get(0).getX();
		    	int firstPointY = (int) firstList.get(0).getY();
                
                for (List<Point> pList : signatureVector)
 		    	{
 		    		for (int i=0; i<pList.size()-1; i++)
 		    		{
 		    			Point p1 = pList.get(i);
 		    			Point p2 = pList.get(i + 1);
 		    			
 		    			int x1 = (int) (startX + p1.getX() - fistPointX);
 		    			int y1 = (int) (this.mouseY + p1.getY() - firstPointY);
 		    			y1 = (int) page.getMediaBox().getHeight() -  y1;
 		    			
 		    			int x2 = (int) (startX + p2.getX() - fistPointX);
 		    			int y2 = (int) (p2.getY() + this.mouseY - firstPointY);
 		    			y2 = (int) page.getMediaBox().getHeight() - y2;
 		    			
 		    			contentStream.drawLine(x1, y1, x2, y2);
 		    		}
 		    	}
                 
                 contentStream.close();
             }
             
             doc.save(outPutFile);
             doc.close();
         }
         catch (Exception ex)
         {
        	 ex.printStackTrace();
         }
	  }
	
	  private int getStartSignX(int pageWidth) 
	  {
			int maxX = 0;
			int minX = Integer.MAX_VALUE;
			for (List<Point> pList : signatureVector)
			{
				for (Point p : pList)
				{
					if (p.getX() > maxX)
						maxX = (int) p.getX();
					
					if (p.getX() < minX)
						minX = (int) p.getX();
				}
			}
			int signatureWidth = maxX - minX;
			
			int startX = this.mouseX;
			if (this.mouseX + signatureWidth > pageWidth)
				 startX = (int) pageWidth - signatureWidth;
			 
			return startX;
	  }
	
}
