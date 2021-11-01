package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GoToOpenDashboardEvent extends GwtEvent<GoToOpenDashboardEventHandler> 
{	
	public static Type<GoToOpenDashboardEventHandler> TYPE = new Type<GoToOpenDashboardEventHandler>() ;
	
	private int _iArchetypeId ;
	
	public static Type<GoToOpenDashboardEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToOpenDashboardEventHandler>() ;
		return TYPE ;
	}
	
	public GoToOpenDashboardEvent(int iArchetypeId) {
		_iArchetypeId = iArchetypeId ;
	}
	
	public int getArchetypeId() {
		return _iArchetypeId ;
	}
			
	@Override
	protected void dispatch(GoToOpenDashboardEventHandler handler) {
		handler.onGoToOpenDashboard(this) ;
	}

	@Override
	public Type<GoToOpenDashboardEventHandler> getAssociatedType() {
		return TYPE ;
	}
}
