package com.primege.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.IntegerBox;
import com.primege.shared.database.FormDataData;

import java.text.ParseException;

/**
 * TextBox
 * 
 */
public class FormIntegerBox extends IntegerBox implements ControlModel
{
	protected ControlBaseWithParams _base ;
	
  /**
   * Default Constructor
   *
   */
  public FormIntegerBox(final String sPath)
  {
    super() ;
    
    _base = new ControlBaseWithParams(sPath) ;
    
    addIntegrityChecker() ;
  }
  
  public boolean isSingleData() {
  	return true ;
  }
  
  /**
   * Get content as FormDataData if content is an integer or "ND", <code>null</code> if not
   *
   */
	public FormDataData getContent()
	{
		String sContent = getText() ;
		if ("".equals(sContent))
			return null ;
		
		if (false == "ND".equals(sContent))
		{
			try {
				Integer.parseInt(sContent) ;
			} catch (NumberFormatException e) {
				return null ;
			}
		}
		
		FormDataData formData = new FormDataData() ;
		formData.setPath(_base.getPath()) ;
		formData.setValue(sContent) ;
		
		return formData ;
	}
	
	/**
   * Initialize state from a content and a default value
   *
   * @param content       FormDataData used to initialize the control
   * @param sDefaultValue Configuration parameters, including default value in case there is no content 
   */
	public void setContent(final FormDataData content, final String sDefaultValue)
	{
		_base.parseParams(sDefaultValue) ;
		
		setContent(content) ;
	}
	
	/**
   * Initialize state from a content
   *
   * @param content FormDataData used to initialize the control
   */
	public void setContent(final FormDataData content)
	{
		if ((null == content) || (null == content.getValue()))
		{
			setText(_base.getDefaultValue()) ;
			return ;
		}
		
		setText(content.getValue()) ;
	}
	
	public void resetContent() {
		setText("") ;
	}
	
	protected void paintItRegular() {
		paintIt("#FFFFFF") ;
	}
	
	protected void paintItWarning() {
		paintIt("#FF0000") ;
	}
	
	protected void paintIt(String sBgColor)
	{
		Element contentElement = getElement() ;
		if (null == contentElement)
			return ;
		
		if ((null != sBgColor) && (false == "".equals(sBgColor)))
			contentElement.getStyle().setBackgroundColor(sBgColor) ;
	}
	
	protected void addIntegrityChecker()
	{
		addKeyUpHandler(new KeyUpHandler() {
	   public void onKeyUp(KeyUpEvent event) {
	      try 
	      {
	      	String sContent = getText() ;
	      	if ("ND".equals(sContent))
	  			{
	  				paintItRegular() ;
	  				return ;
	  			}
	      	
	        getValueOrThrow() ;
	        paintItRegular() ;
	      } 
	      catch(ParseException e) {
	      	paintItWarning() ;
	      }
	   }
		});
	}

	public ControlBase getControlBase() {
		return _base ;
	}
	
	public void setInitFromPrev(final boolean bInitFromPrev) {
		_base.setInitFromPrev(bInitFromPrev) ;
	}
	
	public boolean getInitFromPrev() {
		return _base.getInitFromPrev() ;
	}
}
