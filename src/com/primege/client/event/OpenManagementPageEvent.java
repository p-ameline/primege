package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlowPanel;

public class OpenManagementPageEvent extends GwtEvent<OpenManagementPageEventHandler> 
{	
	public static Type<OpenManagementPageEventHandler> TYPE = new Type<OpenManagementPageEventHandler>();
	
	private FlowPanel _workspace ;
	private int       _iArchetypeId ;
	
	public static Type<OpenManagementPageEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<OpenManagementPageEventHandler>();
		return TYPE;
	}
	
	public OpenManagementPageEvent(final FlowPanel flowPanel, final int iArchetypeId)
	{
		_workspace    = flowPanel ;
		_iArchetypeId = iArchetypeId ;
	}
	
	public FlowPanel getWorkspace(){
		return _workspace ;
	}
	
	public int getArchetypeId(){
		return _iArchetypeId ;
	}
	
	@Override
	protected void dispatch(OpenManagementPageEventHandler handler) {
		handler.onOpenManagementPage(this) ;
	}

	@Override
	public Type<OpenManagementPageEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
}
