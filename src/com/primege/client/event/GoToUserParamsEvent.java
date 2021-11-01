package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GoToUserParamsEvent extends GwtEvent<GoToUserParamsEventHandler> {
	
	public static Type<GoToUserParamsEventHandler> TYPE = new Type<GoToUserParamsEventHandler>();
	
	public static Type<GoToUserParamsEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToUserParamsEventHandler>();
		return TYPE;
	}
	
	public GoToUserParamsEvent(){	
	}
		
	@Override
	protected void dispatch(GoToUserParamsEventHandler handler) {
		handler.onGoToUserParams(this) ;
	}

	@Override
	public Type<GoToUserParamsEventHandler> getAssociatedType() {
		return TYPE;
	}
}
