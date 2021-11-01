package com.primege.client.widgets;

import com.google.gwt.user.client.ui.TextBox;
import com.primege.shared.database.FormDataData;

/**
 * TextBox
 * 
 */
public class FormTextBox extends TextBox implements ControlModel
{
	protected ControlBaseWithParams _base ;
	
  /**
   * Default Constructor
   *
   */
  public FormTextBox(final String sPath)
  {
    super() ;
    
    _base = new ControlBaseWithParams(sPath) ;
  }
  
  public boolean isSingleData() {
  	return true ;
  }
  
  /**
   * Get content as the path if activated, null if not
   *
   */
	public FormDataData getContent()
	{
		String sContent = getText() ;
		if ("".equals(sContent))
			return null ;
		
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
