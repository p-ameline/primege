package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class HeaderButtonsEvent extends GwtEvent<HeaderButtonsEventHandler> {
	
	public static Type<HeaderButtonsEventHandler> TYPE = new Type<HeaderButtonsEventHandler>();
	
	private boolean _bSearchButtonUp ;
	private boolean _bCreateButtonUp ;
	
	public static Type<HeaderButtonsEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<HeaderButtonsEventHandler>();
		return TYPE;
	}
	
	public HeaderButtonsEvent(boolean bSearch, boolean bCreate){
		_bSearchButtonUp = bSearch ;
		_bCreateButtonUp = bCreate ;
	}
	
	public boolean isSearchButtonUp() {
		return _bSearchButtonUp ;
	}

	public boolean isCreateButtonUp() {
		return _bCreateButtonUp ;
	}
	
	@Override
	protected void dispatch(HeaderButtonsEventHandler handler) {
		handler.onHeaderButtonsEvent(this);
	}

	@Override
	public Type<HeaderButtonsEventHandler> getAssociatedType() {
		return TYPE;
	}

}
