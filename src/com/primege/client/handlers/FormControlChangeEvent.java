package com.primege.client.handlers;

import com.google.gwt.event.shared.GwtEvent;

public class FormControlChangeEvent extends GwtEvent<FormControlChangeHandler> 
{	
	public static Type<FormControlChangeHandler> TYPE = new Type<FormControlChangeHandler>();
	
	private String _sPath ; 
	
	public static Type<FormControlChangeHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<FormControlChangeHandler>();
		return TYPE;
	}
	
	public void setPath(final String sPath) {
		_sPath = sPath ;
	}	
	public String getPath() {
		return _sPath ;
	}

	@Override
	protected void dispatch(FormControlChangeHandler handler) {
		handler.onFormControlChange(this) ;		
	}

	@Override
	public Type<FormControlChangeHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
}
