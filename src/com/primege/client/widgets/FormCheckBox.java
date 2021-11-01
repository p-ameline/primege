package com.primege.client.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.CheckBox;

import com.primege.shared.database.FormDataData;

/**
 * CheckBox
 */
public class FormCheckBox extends CheckBox implements ControlModel, HasChangeHandlers
{
	protected ControlBaseWithParams _base ;
	protected ChangeHandler         _changeHandler ;
	
  /**
   * Default Constructor
   *
   */
  public FormCheckBox(final String sCaption, final String sPath)
  {
    super(sCaption) ;
  
    _base          = new ControlBaseWithParams(sPath) ;
    _changeHandler = null ;
    
    setValue(false) ;
    
    addValueChangeHandler(new stateChangeHandler()) ;
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
		if (false == getValue())
			return null ;
		
		FormDataData formData = new FormDataData() ;
		formData.setPath(_base.getPath()) ;
		
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
		boolean bValue ;
		if (null == content)
		{
			String sDefVal = _base.getDefaultValue() ;
			bValue = (false == "".equals(sDefVal)) && (false == "0".equals(sDefVal)) ;
		}
		else
			bValue = true ;
			
		setValue(bValue) ;
	}

	public void resetContent() {
		setValue(false) ;
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
	
	/**
	 * Get informed when state changes
	 */
	protected class stateChangeHandler implements ValueChangeHandler<Boolean> 
	{
		public stateChangeHandler() 
		{
		}

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent)
		{
			NativeEvent event = Document.get().createChangeEvent();
/*
			if (null != _changeHandler)
				_changeHandler.onChange(event) ;
*/
			DomEvent.fireNativeEvent(event, (HasHandlers) this) ;
		}
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler)
	{
		// _changeHandler = handler ;
		// return null ;
		
		return addDomHandler(handler, ChangeEvent.getType());
	}
}
