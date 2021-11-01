package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GoToLoginResponseEvent extends GwtEvent<GoToLoginResponseEventHandler> {
	
	public static Type<GoToLoginResponseEventHandler> TYPE = new Type<GoToLoginResponseEventHandler>();
	
	public static Type<GoToLoginResponseEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToLoginResponseEventHandler>();
		return TYPE;
	}
	
	public GoToLoginResponseEvent(){	
	}
		
	@Override
	protected void dispatch(GoToLoginResponseEventHandler handler) {
		handler.onGoToLoginResponse(this) ;
	}

	@Override
	public Type<GoToLoginResponseEventHandler> getAssociatedType() {
		return TYPE;
	}
}
