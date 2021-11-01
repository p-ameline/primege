package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GoToEditFormEvent extends GwtEvent<GoToEditFormEventHandler> 
{	
	public static Type<GoToEditFormEventHandler> TYPE = new Type<GoToEditFormEventHandler>();
	
	private int _iFormId ;
	
	public static Type<GoToEditFormEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToEditFormEventHandler>() ;
		return TYPE ;
	}
	
	public GoToEditFormEvent(int iFormId){
		_iFormId = iFormId ;
	}
			
	public int getFormId() {
		return _iFormId ;
	}
	
	@Override
	protected void dispatch(GoToEditFormEventHandler handler) {
		handler.onGoToEditForm(this) ;
	}

	@Override
	public Type<GoToEditFormEventHandler> getAssociatedType() {
		return TYPE ;
	}
}
