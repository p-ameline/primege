package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlowPanel;

public class WelcomePageEvent extends GwtEvent<WelcomePageEventHandler> {
	
	public static Type<WelcomePageEventHandler> TYPE = new Type<WelcomePageEventHandler>();
	
	private FlowPanel workspace;
	
	public static Type<WelcomePageEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<WelcomePageEventHandler>();
		return TYPE;
	}
	
	public WelcomePageEvent(FlowPanel flowPanel){
		this.workspace = flowPanel;		
	}
	
	public FlowPanel getWorkspace(){
		return workspace ;
	}

/*	public VerticalPanel getBody(){
		return body ;
	}
	*/
	
	@Override
	protected void dispatch(WelcomePageEventHandler handler) {
		handler.onWelcome(this);
	}

	@Override
	public Type<WelcomePageEventHandler> getAssociatedType() {
		return TYPE;
	}

}
