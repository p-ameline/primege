package com.primege.client.widgets;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.TextBox;

import com.primege.client.util.FormControlOptionData;
import com.primege.shared.database.FormDataData;

/**
 * TextBox with a drop down list from Lexicon
 * 
 * Inspired from http://sites.google.com/site/gwtcomponents/auto-completiontextbox
 */
public class TableControl extends FlowPanel implements ControlModelMulti
{
	protected ControlBaseWithParams            _base ;
	protected FlowPanel                        _commandPanel ;
	protected FormFlexTable                    _table ;
	
	protected ArrayList<FormControlOptionData> _aColumns = new ArrayList<FormControlOptionData>() ;
	
	protected static int                       _iDefaultLinesCount = 4 ;
	protected        int                       _iInitialLinesCount ;
  
  /**
   * Default Constructor
   *
   */
  public TableControl(final ArrayList<FormControlOptionData> aOptions, final String sPath)
  {
    super() ;
    
    _base = new ControlBaseWithParams(sPath) ;
    
    _iInitialLinesCount = _iDefaultLinesCount ;
    
    initCommandPanel() ;
    initTable(aOptions) ;
  }
  
  public boolean isSingleData() {
  	return false ;
  }
  
  protected void initCommandPanel()
  {
  	_commandPanel = new FlowPanel() ;
  }
  
  /**
   * Create the RadioButton controls
   *
   */
  public void initTable(final ArrayList<FormControlOptionData> aOptions)
  {
  	_table = new FormFlexTable() ;

  	_table.addClickHandler(new ClickHandler() {
  		public void onClick(ClickEvent event) {
  			Cell cell = _table.getCellForEvent(event) ;
  			editCell(cell) ;
  		}
  	});
  	
  	if ((null != aOptions) && (false == aOptions.isEmpty()))
  	{
  		// Create the first table line (with columns captions)
  		//
  		_table.initCaptionRow(aOptions) ;
  	
  		// Add empty rows
  		//
  		int iColCount = aOptions.size() ;
  	
  		for (int iRow = 0 ; iRow < _iDefaultLinesCount ; iRow++)
  			for (int iCol = 0 ; iCol < iColCount ; iCol++)
  				_table.setText(iRow + 1, iCol, "") ;
  	}	
  	
  	add(_table) ;
  }
  
  /**
   * Get table content as set of {@link FormDataData}
   *
   */
	public ArrayList<FormDataData> getMultipleContent()
	{
		if (_aColumns.isEmpty())
			return null ;
		
		ArrayList<FormDataData> aResult = new ArrayList<FormDataData>() ;
		
		int iRowCount = _table.getRowCount() ;
		
		// All elements from a same line have a path à la common/#N/local
		//
		for (int iRow = 1 ; iRow < iRowCount ; iRow++)
		{
			int iCol = 0 ;
			for (FormControlOptionData option : _aColumns)
			{
				String sCellText = _table.getText(iRow, iCol).trim() ;
				if (false == sCellText.isEmpty())
				{
					FormDataData formData = new FormDataData() ;
					formData.setPath(_base.getPath() + "/#" + iRow + "/" + option.getPath()) ;
					formData.setValue(sCellText) ;
					formData.setUnit(option.getUnit()) ;
					
					// TODO collect row elements locally in order to only add them
					// to the global row if mandatory cells are filled
					//
					aResult.add(formData) ;
				}
				iCol++ ;
			}
		}
		
		return aResult ;
	}
	
	/**
   * Initialize table cells from a content and a default value
   *
   * @param aContent Array of {@link FormDataData] used to initialize the control
   * @param sDefaultValue Configuration parameters, including default value in case there is no content
   */
	public void setMultipleContent(final ArrayList<FormDataData> aContent, final String sDefaultValue) 
	{
		_base.parseParams(sDefaultValue) ;
		
		setMultipleContent(aContent) ;
	}
	
	/**
   * Initialize table cells from a content
   *
   * @param aContent Array of {@link FormDataData] used to initialize the control
   */
	public void setMultipleContent(final ArrayList<FormDataData> aContent)
	{
		resetContent() ;
		
		if (_aColumns.isEmpty() || (null == aContent) || aContent.isEmpty())
			return ;
		
		_table.setMultipleContent(_base.getPath(), aContent, _aColumns) ;
	}
	
	/**
	 * Reset table content: erase content from all cells (except caption row) and delete exceding rows
	 */
	public void resetContent()
	{
		int iRowCount = _table.getRowCount() ;
		
		// Remove exceding rows
		//
		if (iRowCount > _iInitialLinesCount + 1)
			for (int iRow = iRowCount ; iRow > _iInitialLinesCount + 1 ; iRow--)
				_table.removeRow(iRow) ;
		
		// Reset cells
		//
		for (int iRow = 1 ; iRow < _iInitialLinesCount + 1 ; iRow++)
		{
			int iCellCount = _table.getCellCount(iRow) ;
			for (int iCell = 0 ; iCell < iCellCount ; iCell++)
				_table.setText(iRow, iCell, "") ;
		}
	}
	
	protected void editCell(Cell cell)
	{
		if (null == cell)
			return ;
		
		int iCellRow = cell.getRowIndex() ;
    int iCellCol = cell.getCellIndex() ;
    
    String sCellContent = _table.getText(iCellRow, iCellCol) ;
    
    final TextBox textBox = new TextBox() ;
    textBox.setText(sCellContent) ;
    		;
    // Get the text from the cell in some way. Maybe use flextTable.getHTML(row, column) or what ever you prefer
    // textBox.setText("Something other than this");
    textBox.addKeyDownHandler(new KeyDownHandler() {
       public void onKeyDown(KeyDownEvent event) {
            int code = event.getNativeKeyCode();
            if (KeyCodes.KEY_ENTER == code) {
               _table.setText(iCellRow, iCellCol, textBox.getText()) ;
            }
       }
    });
    
    _table.setWidget(iCellRow, iCellCol, textBox) ;
    textBox.setFocus(true) ;
	}
	
	public ControlBase getControlBase() {
		return _base ;
	}
	
	public void setInitFromPrev(boolean bInitFromPrev) {
		_base.setInitFromPrev(bInitFromPrev) ;
	}
	
	public boolean getInitFromPrev() {
		return _base.getInitFromPrev() ;
	}
}
