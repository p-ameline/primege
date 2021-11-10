package com.primege.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.Widget;

/**
 * Presentation information for a {@link FormBlockPanel} (css style, background color, etc)
 */
public class FormBlockInformation
{
	protected String _sStyle ;
  protected String _sBgColor ;
  protected String _sBlockAlign ;
  protected String _sBlockVAlign ;
  protected String _sBlockWidth ;
  protected String _sBlockHeight ;
  
  protected String _sCaption ;
  protected String _sCaptionStyle ;
  
  protected String _sBlockHelp ;

  /**
   * Default Constructor
   */
  public FormBlockInformation(final String sBgColor, final String sBlockAlign, final String sBlockVAlign, final String sBlockWidth, final String sBlockHeight, final String sCaption, final String sBlockStyle, final String sCaptionStyle, final String sBlockHelp)
  {
  	_sStyle        = getInformation(sBlockStyle) ;
    _sBgColor      = getInformation(sBgColor) ;
    _sBlockAlign   = getInformation(sBlockAlign) ;
    _sBlockVAlign  = getInformation(sBlockVAlign) ;
    _sBlockWidth   = getInformation(sBlockWidth) ;
    _sBlockHeight  = getInformation(sBlockHeight) ;
    
    _sCaption      = getInformation(sCaption) ;
    _sCaptionStyle = getInformation(sCaptionStyle) ;
    
    _sBlockHelp    = getInformation(sBlockHelp) ;
  }

  /**
   * Constructor from an {@link Element} attributes
   */
  public FormBlockInformation(final com.google.gwt.xml.client.Element currentElement)
  {
  	reset() ;
  	
  	if (null == currentElement)
  		return ;
  	
  	initFromElementAttributes(currentElement) ;
  }
  
  public void reset()
  {
  	_sStyle        = "" ;
    _sBgColor      = "" ;
    _sBlockAlign   = "" ;
    _sBlockVAlign  = "" ;
    _sBlockWidth   = "" ;
    _sBlockHeight  = "" ;
    
    _sCaption      = "" ;
    _sCaptionStyle = "" ;
    
    _sBlockHelp    = "" ;
  }
  
  public void initFromElementAttributes(final com.google.gwt.xml.client.Element currentElement)
  {
  	if (null == currentElement)
  		return ;
  	
  	_sStyle        = getInformation(currentElement.getAttribute("style")) ;
  	_sBgColor      = getInformation(currentElement.getAttribute("bgcolor")) ;
  	_sBlockAlign   = getInformation(currentElement.getAttribute("align")) ;
  	_sBlockVAlign  = getInformation(currentElement.getAttribute("valign")) ;
  	_sBlockWidth   = getInformation(currentElement.getAttribute("width")) ;
  	_sBlockHeight  = getInformation(currentElement.getAttribute("height")) ;
		
  	_sCaption      = getInformation(currentElement.getAttribute("caption")) ;
		_sCaptionStyle = getInformation(currentElement.getAttribute("caption_style")) ;
		
		_sBlockHelp    = getInformation(currentElement.getAttribute("help")) ;
  }
  
  public void applyStyle(Widget cellElementWidget, boolean bCaptionBlock)
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
		
		if (false == bCaptionBlock)
			return ;
		
		// Styles that only apply to the caption cell
		//
		if ((null != _sBlockWidth) && (false == "".equals(_sBlockWidth)))
			cellElement.getStyle().setWidth(getBlockWidthValue(), getBlockWidthUnit()) ;
		
		if ((null != _sBlockHeight) && (false == "".equals(_sBlockHeight)))
			cellElement.getStyle().setHeight(getBlockHeightValue(), getBlockHeightUnit()) ;
		
		if ((null != _sBlockAlign) && (false == "".equals(_sBlockAlign)))
			cellElement.getStyle().setTextAlign(getTextAlign()) ;
		
		if ((null != _sBlockVAlign) && (false == "".equals(_sBlockVAlign)))
			cellElement.getStyle().setVerticalAlign(getVerticalAlign()) ;
	}
  
  public String getStyle() {
		return _sStyle ;
	}
  
  public String getCaption() {
		return _sCaption ;
	}

	public String getBgColor() {
		return _sBgColor ;
	}
	
	public String getCaptionStyle() {
		return _sCaptionStyle ;
	}
	
	public String getBlockAlign() {
		return _sBlockAlign ;
	}
	public TextAlign getTextAlign() {
		return getTextAlignFromString(_sBlockAlign) ;
	}
	
	public String getBlockVAlign() {
		return _sBlockVAlign ;
	}
	public VerticalAlign getVerticalAlign() {
		return getVerticalAlignFromString(_sBlockVAlign) ;
	}
	
	public String getBlockWidth() {
		return _sBlockWidth ;
	}
	public double getBlockWidthValue() {
		return getValueFromString(_sBlockWidth) ;
	}
	public Unit getBlockWidthUnit() {
		return getUnitFromString(_sBlockWidth) ;
	}
	
	public String getBlockHeight() {
		return _sBlockHeight ;
	}
	public double getBlockHeightValue() {
		return getValueFromString(_sBlockHeight) ;
	}
	public Unit getBlockHeightUnit() {
		return getUnitFromString(_sBlockHeight) ;
	}
	
	public String getBlockHelp() {
		return _sBlockHelp ;
	}
	
	protected String getInformation(final String sSource)
	{
		if (null == sSource)
			return "" ;
    return sSource ;
	}
	
	/**
   * Get, as a double, the numerical value that starts a string. For example "70%" returns (double) 70 
   *
   */
	public double getValueFromString(final String sInfo)
	{
		if ((null == sInfo) || "".equals(sInfo))
			return 0 ;
		
		String sNumValue = "" ;
		
		int iStrLen  = sInfo.length() ;
		int iCurChar = 0 ;
		
		// Trim starting blanks
		//
		while ((iCurChar < iStrLen) && (sInfo.charAt(iCurChar) == ' '))
			iCurChar++ ;
		
		while (iCurChar < iStrLen)
		{
			// Build proposed value as previous value + new char
			//
			String sProposedValue = sNumValue + sInfo.charAt(iCurChar) ;
			
			// If the proposed value is only made of digits, it becomes the new value.
			// If not, it means that the previous value is the largest sequence only made of digits 
			//
			if (sProposedValue.matches("\\d+"))
				sNumValue = sProposedValue ;
			else
				return Double.parseDouble(sNumValue) ;
			
			iCurChar++ ;
		}
		
		return Double.parseDouble(sNumValue) ;
	}
	
	/**
   * Get, as a Unit, the text that ends a string. For example "70%" returns (Unit) % 
   *
   */
	public Unit getUnitFromString(final String sInfo)
	{
		if ((null == sInfo) || "".equals(sInfo))
			return Unit.PX ;
		
		int iStrLen = sInfo.length() ;
		int iCursor = iStrLen - 1 ;
		
		// Trim ending blanks
		//
		while ((iCursor >= 0) && (sInfo.charAt(iCursor) == ' '))
			iCursor-- ;
		
		if (iCursor < 0)
			return Unit.PX ;
		
		// The only unit that is one char wide is "%'
		//
		if ('%' == sInfo.charAt(iCursor))
			return Unit.PCT ;
		
		// All units are two chars wide
		//
		if (iCursor < 1)
			return Unit.PX ;
		
		String sUnit = "" + sInfo.charAt(iCursor-1) + sInfo.charAt(iCursor)  ;
		
		// Enumerate through all units until finding the proper one
		//
		for (Unit unit : Unit.values())
			if (unit.getType().equals(sUnit))
				return unit ;
		
		return Unit.PX ;
	}
	
	protected TextAlign getTextAlignFromString(final String sInfo)
	{
		// Enumerate through all units until finding the proper one
		//
		for (TextAlign textAlign : TextAlign.values())
			if (textAlign.getCssName().equals(sInfo))
				return textAlign ;
			
		return TextAlign.LEFT ;
	}
	
	protected VerticalAlign getVerticalAlignFromString(final String sInfo)
	{
		// Enumerate through all units until finding the proper one
		//
		for (VerticalAlign verticalAlign : VerticalAlign.values())
			if (verticalAlign.getCssName().equals(sInfo))
				return verticalAlign ;
			
		return VerticalAlign.MIDDLE ;
	}
}
