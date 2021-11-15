package com.primege.client.widgets;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.primege.client.util.FormControl;
import com.primege.shared.database.FormDataData;
import com.primege.shared.model.FormBlock;

/**
 * A Form block panel is a table with a single row and two columns.
 * The left column contains the caption while the right one will be filled with another block and/or a set of controls
 */
public class FormBlockPanel extends FlexTable
{
	// Location in father
	protected FormBlockPanel          _father ;
	protected int                     _iRowInFather ;
	
	/** presentation information */
	protected FormBlockInformation    _presentation ;
  
  /** current row for insertion of a control or a child block */
  protected int                     _iCurrentRow ;
  
  /** Label side controls holder */
  protected FlexTable               _labelControlsTable ; 
  protected int                     _iCurrentRowInLabel ;
  
  // Sub-blocks management
  //
  private   int                     _blockStackSize ;
	private   int                     _blockStackTop ;
  private   FormBlockPanel[]        _blockStackArr ;
  
  /** Action identifier (<code>""</code> if not an annotation panel) */
  protected String                  _sActionID ;
  
  /** Form's information (<code>null</code> for a new form) */
  protected FormBlock<FormDataData> _editedBlock ;
  
  /** Controls (empty except for the master block of a form or an action) */
  protected ArrayList<FormControl>  _aControls = new ArrayList<FormControl>() ;
  
  /**
   * Default Constructor
   */
  public FormBlockPanel(final FormBlockInformation presentation)
  {
    super() ;
    
    _father             = null ;
    _iRowInFather       = -1 ;

    _presentation       = presentation ;
    
    _iCurrentRow        = 0 ;
    
    _labelControlsTable = null ;
    _iCurrentRowInLabel = 0 ;
    
    _editedBlock        = null ;
    
    _sActionID          = "" ;
    
    initBlockStack(10) ;
  }

  public void reset()
  {
  	removeAllRows() ;
  	
  	_iCurrentRow        = 0 ;
  	
  	_labelControlsTable = null ;
    _iCurrentRowInLabel = 0 ;
    
    initBlockStack(10) ;
    
    _editedBlock        = null ;
    
    _sActionID          = "" ;
    
    _aControls.clear() ;
  }
  
  /**
   * Insert a new block as a new row, with its label in left column and the block in the right one
   */
  public void insertBlock(FormBlockPanel newBlock)
  {
  	// The label is inserted in a VerticalPanel, in case controls are added later on label's side
  	//
  	VerticalPanel labelPanel = new VerticalPanel() ;
  	Label blockLabel = new Label(newBlock.getCaption()) ;
  	
  	String sCaptionStyle = newBlock.getCaptionStyle() ;
  	if (false == sCaptionStyle.isEmpty())
  		blockLabel.addStyleName(sCaptionStyle) ;
  	
  	labelPanel.add(blockLabel) ;
  	
  	// Insert bloc's label and the bloc itself
   	//
  	setWidget(_iCurrentRow, 0, labelPanel) ;
		setWidget(_iCurrentRow, 1, newBlock) ;
		
		// Set styles
		//
		newBlock.applyStyle(getWidget(_iCurrentRow, 0), true) ;
		newBlock.applyStyle(blockLabel, true) ;
		newBlock.applyStyle(getWidget(_iCurrentRow, 1), false) ;
		
		// Set location information
		//
		newBlock.setFather(this) ;
		newBlock.setRowInFather(_iCurrentRow) ;
		
		_iCurrentRow++ ;
  }
  
  /**
   * Insert a control, with its label in left column and the control (as a supertype of Widget) in the right one 
   * 
   * @param sCaption      Control's label
   * @param sCaptionStyle RSS style for the label
   * @param widget        Control to be added
   * @param controlBase   Base information of control to be added
   * @param bInBlockCell  <code>true</code> if control is to be inserted in controls area, <code>false</code> if in caption area
   */
  public void insertControl(final String sCaption, final String sCaptionStyle, Widget widget, ControlBase controlBase, final boolean bInBlockCell)
  {
  	if (false == bInBlockCell)
  	{
  		insertLabelControl(sCaption, sCaptionStyle, widget, controlBase) ;
  		return ;
  	}
  	
  	// The label is inserted in a VerticalPanel, in case controls are added later on label's side
   	//
  	VerticalPanel labelPanel = new VerticalPanel() ;
  	// Label newLabel = new Label(sCaption) ;
  	HTML newLabel = new HTML(interpretCaption(sCaption)) ;
  	
  	if ((null != sCaptionStyle) && false == "".equals(sCaptionStyle))
  		newLabel.addStyleName(sCaptionStyle) ;
  	
  	labelPanel.add(newLabel) ;
  	
  	setWidget(_iCurrentRow, 0, labelPanel) ;
		setWidget(_iCurrentRow, 1, widget) ;
		
		if (null != controlBase)
		{
			controlBase.setFather(this) ;
			controlBase.setRowInFather(_iCurrentRow) ;
		}
		
		_iCurrentRow++ ;
  }
  
  /**
   * Insert a control, with its label in left column and the control (as a supertype of Widget) in the right one 
   * 
   * @param sCaption      Control's label
   * @param sCaptionStyle RSS style for the label
   * @param widget        Control to be added
   */
  public void insertLabelControl(final String sCaption, final String sCaptionStyle, Widget widget, ControlBase controlBase)
  {
  	// Get already created table in the label cell, or ask the father block to create a new one
  	//
  	FlexTable labelTable = getLabelTable() ;
  	
  	if (null == labelTable)
  		return ;
  	
  	Label newLabel = new Label(sCaption) ;
  	
  	if ((null != sCaptionStyle) && false == "".equals(sCaptionStyle))
  		newLabel.addStyleName(sCaptionStyle) ;
  	
  	labelTable.setWidget(_iCurrentRowInLabel, 0, newLabel) ;
  	labelTable.setWidget(_iCurrentRowInLabel, 1, widget) ;
		
  	if (null != controlBase)
		{
			controlBase.setFather(this) ;
			controlBase.setRowInFather(_iCurrentRowInLabel) ;
		}
  	
  	_iCurrentRowInLabel++ ;
  }
  
  /**
   * Return the label table (i.e. the table that hosts controls on label side)
   * 
   * @return The table if everything went well, <code>null</code> if not
   */
  protected FlexTable getLabelTable()
  {
  	// It the table already exist, then we are done
  	//
  	if (null != _labelControlsTable)
  		return _labelControlsTable ;
  	
  	// If not, we have to ask the father to create it in the left column of the row this block is located into
  	//
  	if (null == _father)
  		return null ;
  	
  	_labelControlsTable = _father.createLabelTable(_iRowInFather) ;
  	
  	return _labelControlsTable ;
  }
  
  /**
   * Create a table in the label cell
   * 
   * @param iRow row of the cell to crate a table into
   * 
   * @return The table if created, <code>null</code> if not 
   */
  protected FlexTable createLabelTable(int iRow)
  {
  	// Get the widget in the left cell of the given row
  	//
  	Widget labelWidget = getWidget(iRow, 0) ;
  	
  	if (null == labelWidget)
  		return null ;
  	
  	FlexTable labelTable = null ;
  	
  	try
  	{
  		VerticalPanel labelPanel = (VerticalPanel) labelWidget ;
  	
  		HTML separator = new HTML("&nbsp;") ;
  		labelPanel.add(separator) ;
  		
  		labelTable = new FlexTable() ;
    	labelPanel.add(labelTable) ;
  	}
  	catch (ClassCastException e) {
  		return null ;
  	}
  	
  	return labelTable ;
  }
  
  protected String interpretCaption(final String sCaption)
  {
  	if ((null == sCaption) || "".equals(sCaption))
  		return "" ;
  	
  	int iPosOpen = sCaption.indexOf("[[") ;
  	if (-1 == iPosOpen)
  		return sCaption ;
  	
  	String sResult = sCaption ;
  	
  	while (-1 != iPosOpen)
  	{
  		int iPosClose = sResult.indexOf("]]") ;
  		
  		String sInterPreted = "" ;
  		if (-1 == iPosClose)
  			iPosClose = sResult.length() - 3 ;
  		else
  		{
  			String sParams = sResult.substring(iPosOpen + 2, iPosClose) ;
  			sInterPreted = interpretCaptionParams(sParams) ;
  		}
  		
  		String sStart = "" ;
  		if (iPosOpen > 0)
  			sStart = sResult.substring(0, iPosOpen) ;
  		
  		String sEnd = "" ;
  		if (iPosClose < sResult.length() - 3)
  			sEnd = sResult.substring(iPosClose + 2, sResult.length()) ;
  		
  		sResult = sStart + sInterPreted + sEnd ;
  		
  		iPosOpen = sResult.indexOf("[[") ;
  	}
  	
  	return sResult ;
  }
  
  protected String interpretCaptionParams(final String sCaption)
  {
  	if ((null == sCaption) || "".equals(sCaption))
  		return "" ;
  	
  	String sLabel  = interpretCaptionParamsGetParam(sCaption, "caption=") ;
  	String sUrl    = interpretCaptionParamsGetParam(sCaption, "url=") ;
  	String sType   = interpretCaptionParamsGetParam(sCaption, "type=") ;
  	String sClass  = interpretCaptionParamsGetParam(sCaption, "class=") ;
  	String sStyle  = interpretCaptionParamsGetParam(sCaption, "style=") ;
  	
  	String sResult = "" ;
  	
  	if ("button".equalsIgnoreCase(sType))
  	{
  		String sButtonTag = "<button type=\"button\"" ;
  		if (false == "".equals(sClass))
  			sButtonTag += " class=\"" + sClass + "\"" ;
  		
  		if (false == "".equals(sStyle))
  			sButtonTag += " style=\"" + sStyle + "\"" ;
  		
  		sButtonTag += ">" ;
  		
  		sResult = sButtonTag + "<a href=\"" + sUrl + "\" target=\"_blank\">" + sLabel + "</a></button>" ;
  	}
  	else
  		sResult = "<a href=\"" + sUrl + "\" target=\"_blank\">" + sLabel + "</a>" ;
  	
  	return sResult ;
  }
  
  protected String interpretCaptionParamsGetParam(final String sCaption, final String sParam)
  {
  	if ((null == sCaption) || "".equals(sCaption))
  		return "" ;
  	
  	int iPosCaption = sCaption.indexOf(sParam) ;
  	if (-1 == iPosCaption)
  		return "" ;
  		
  	int iPosCaptionStart = sCaption.indexOf("'", iPosCaption) ;
  	if (-1 == iPosCaptionStart)
  		return "" ;
  	
  	int iPosCaptionEnd   = sCaption.indexOf("'", iPosCaptionStart + 1) ;
  	if (-1 == iPosCaptionEnd)
  		return "" ;
  	
  	return sCaption.substring(iPosCaptionStart + 1, iPosCaptionEnd) ;
  }
  
	public String getCaption() {
		return _presentation.getCaption() ;
	}

	public String getBgColor() {
		return _presentation.getBgColor() ;
	}
	
	public String getBlockWidth() {
		return _presentation.getBlockWidth() ;
	}
	
	public String getBlockHeight() {
		return _presentation.getBlockHeight() ;
	}
	
	public String getBlockAlign() {
		return _presentation.getBlockAlign() ;
	}
	
	public String getBlockVAlign() {
		return _presentation.getBlockVAlign() ;
	}
	
	public void applyStyle(Widget cellElementWidget, boolean bCaptionBlock)
	{
		if (null == cellElementWidget)
			return ;
		
		_presentation.applyStyle(cellElementWidget, bCaptionBlock) ;
	}
	
	/**
	 * Remove the last created row
	 */
	public void removeLastSonBlock()
	{
		int iRowCount = getRowCount() ;
		if (0 == iRowCount)
			return ;
		
		this.removeRow(iRowCount - 1) ;
	}
	
	protected String getInformation(final String sSource)
	{
		if (null == sSource)
			return "" ;
    return sSource ;
	}
	
	public FormBlockPanel getFather() {
		return _father ;
	}
	public void setFather(final FormBlockPanel father) {
		_father = father ;
	}
	
	public int getRowInFather() {
		return _iRowInFather ;
	}
	public void setRowInFather(final int iRowInFather) {
		_iRowInFather = iRowInFather ;
	}
	
	public String getCaptionStyle() {
		return _presentation.getCaptionStyle() ;
	}
	
	public FormBlock<FormDataData> getEditedBlock() {
		return _editedBlock ;
	}
	public void setEditedBlock(FormBlock<FormDataData> editedBlock) {
		_editedBlock = editedBlock ;
	}
	
	/**
	 * Get the form identifier
	 * 
	 * @return <code>-1</code> for a new form or annotation, the form's identifier is an edited form or annotation
	 */
	public int getFormIdentifier()
	{
		if (null == _editedBlock)
			return -1 ;
		
		return _editedBlock.getFormId() ;
	}
	
	public String getActionIdentifier() {
		return _sActionID ;
	}
	public void setActionIdentifier(final String sActionID) {
		_sActionID = sActionID ;
	}
	
	public ArrayList<FormControl> getControls() {
		return _aControls ;
	}
	
	public void addControl(FormControl control) {
		if (null == control)
			return ;
		
		_aControls.add(control) ;
	}
	
	/** 
	 * Initialize the block stack with a specified depth
	 * 
	 * @param    iSize depth of the stack
	 */
	protected void initBlockStack(int iSize)
	{
		_blockStackSize = iSize ;
		_blockStackTop  = -1 ;
	  _blockStackArr  = new FormBlockPanel[_blockStackSize] ;
	}
	
	/**
   * Add a new entry to the top of the stack
   */
	public void pushBlockStack(FormBlockPanel entry)
	{
		if (isBlockStackFull())
			return ;  // consider resizing ?
    
		_blockStackArr[++_blockStackTop] = entry ;
  }

  /**
   * Remove an entry from the top of the stack
   * 
   * @return The removed bloc 
   */
  public FormBlockPanel popBlockStack() 
  {
  	if (isBlockStackEmpty()) 
  		return null ;

  	int iPreviousTop = _blockStackTop ;
  	
  	FormBlockPanel entry = _blockStackArr[_blockStackTop--] ;
  	_blockStackArr[iPreviousTop] = null ;
  	
  	return entry ;
  }
   
  /**
   * Return top of the stack without removing it
   */
  public FormBlockPanel peekBlockStack() {
  	return _blockStackArr[_blockStackTop] ;
  }

  /**
   * Returns <code>true</code> if the stack is empty
   */
  public boolean isBlockStackEmpty() {
  	return (_blockStackTop == -1) ;
  }

  /**
   * Return <code>true</code> if the stack is full
   */
  public boolean isBlockStackFull() {
  	return (_blockStackTop == _blockStackSize - 1) ;
  }

}
