package com.primege.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * TextBox with a drop down list from Lexicon
 * 
 * Inspired from http://sites.google.com/site/gwtcomponents/auto-completiontextbox
 */
public class DashboardBlockPanel extends FlexTable
{   
  protected String _sCaption ;
  protected String _sBgColor ;
  
  private int      _iCurrentRow ;
  
  /**
   * Default Constructor
   *
   */
  public DashboardBlockPanel(String sCaption, String sBgColor)
  {
    super() ;

    if (null == sCaption)
    	_sCaption = "" ;
    else
    	_sCaption = sCaption ;
    
    if (null == sBgColor)
    	_sBgColor = "" ;
    else
    	_sBgColor = sBgColor ;
    
    _iCurrentRow = 0 ;
  }
  
  public void insertBlock(DashboardBlockPanel newBlock)
  {
  	setWidget(_iCurrentRow, 0, new Label(newBlock.getCaption())) ;
		setWidget(_iCurrentRow, 1, newBlock) ;
		
		newBlock.applyStyle(getWidget(_iCurrentRow, 0)) ;
		newBlock.applyStyle(getWidget(_iCurrentRow, 1)) ;
		
		_iCurrentRow++ ;
  }
  
  public void insertControl(String sCaption, Widget widget)
  {
  	setWidget(_iCurrentRow, 0, new Label(sCaption)) ;
		setWidget(_iCurrentRow, 1, widget) ;
		
		_iCurrentRow++ ;
  }
  
	public String getCaption() {
		return _sCaption ;
	}

	public String getBgColor() {
		return _sBgColor ;
	}
	
	public void applyStyle(Widget cellElementWidget) 
	{
		if (null == cellElementWidget)
			return ;
		
		Element cellContentElement = cellElementWidget.getElement() ;
		if (null == cellContentElement)
			return ;
		
		Element cellElement = cellContentElement.getParentElement() ;
		if (null == cellElement)
			return ;
		
		if ((null != _sBgColor) && (false == "".equals(_sBgColor)))
			cellElement.getStyle().setBackgroundColor(_sBgColor) ;
	}
}
