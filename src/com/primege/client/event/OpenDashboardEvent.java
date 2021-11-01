package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlowPanel;

public class OpenDashboardEvent extends GwtEvent<OpenDashboardEventHandler> 
{	
	public static Type<OpenDashboardEventHandler> TYPE = new Type<OpenDashboardEventHandler>();
	
	private FlowPanel _workspace ;
	private int       _iArchetypeId ;
	
	public static Type<OpenDashboardEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<OpenDashboardEventHandler>() ;
		return TYPE ;
	}
	
	public OpenDashboardEvent(final FlowPanel flowPanel, final int iArchetypeId)
	{
		_workspace    = flowPanel ;
		_iArchetypeId = iArchetypeId ;
	}
	
	public FlowPanel getWorkspace(){
		return _workspace ;
	}
	
	public int getArchetypeId() {
		return _iArchetypeId ;
	}
	
	@Override
	protected void dispatch(OpenDashboardEventHandler handler) {
		handler.onOpenDashboard(this) ;
	}

	@Override
	public Type<OpenDashboardEventHandler> getAssociatedType() {
		return TYPE ;
	}
}
