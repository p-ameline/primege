package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GoToManageFormEvent extends GwtEvent<GoToManageFormEventHandler> 
{	
	public static Type<GoToManageFormEventHandler> TYPE = new Type<GoToManageFormEventHandler>() ;
	
	private int _iArchetypeId ;
	
	public static Type<GoToManageFormEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToManageFormEventHandler>() ;
		return TYPE ;
	}
	
	public GoToManageFormEvent(int iArchetypeId) {
		_iArchetypeId = iArchetypeId ;
	}
	
	public int getArchetypeId() {
		return _iArchetypeId ;
	}
			
	@Override
	protected void dispatch(GoToManageFormEventHandler handler) {
		handler.onGoToManageForm(this) ;
	}

	@Override
	public Type<GoToManageFormEventHandler> getAssociatedType() {
		return TYPE ;
	}
}
