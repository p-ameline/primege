package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.primege.shared.database.FormLink;

public class GoToNewFormEvent extends GwtEvent<GoToNewFormEventHandler> 
{	
	public static Type<GoToNewFormEventHandler> TYPE = new Type<GoToNewFormEventHandler>() ;
	
	private int      _iArchetypeId ;
	private FormLink _formLink ;
	
	public static Type<GoToNewFormEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToNewFormEventHandler>() ;
		return TYPE ;
	}
	
	public GoToNewFormEvent(int iArchetypeId, FormLink formLink)
	{
		_iArchetypeId = iArchetypeId ;
		_formLink     = formLink ;
	}
	
	public int getArchetypeId() {
		return _iArchetypeId ;
	}
	
	public FormLink getFormLink() {
		return _formLink ;
	}
	
	@Override
	protected void dispatch(GoToNewFormEventHandler handler) {
		handler.onGoToNewForm(this) ;
	}

	@Override
	public Type<GoToNewFormEventHandler> getAssociatedType() {
		return TYPE ;
	}
}
