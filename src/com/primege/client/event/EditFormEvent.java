package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.primege.shared.database.FormLink;

public class EditFormEvent extends GwtEvent<EditFormEventHandler> 
{	
	public static Type<EditFormEventHandler> TYPE = new Type<EditFormEventHandler>();
	
	private FlowPanel _workspace ;
	private int       _iFormId ;
	private int       _iArchetypeId ;
	private FormLink  _formLink ;
	private boolean   _bScreenShotMode ;
	
	public static Type<EditFormEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<EditFormEventHandler>() ;
		return TYPE ;
	}
	
	public EditFormEvent(final FlowPanel flowPanel, final int iFormId, final int iArchetypeId, FormLink formLink)
	{
		_workspace       = flowPanel ;
		_iFormId         = iFormId ;
		_iArchetypeId    = iArchetypeId ;
		_formLink        = formLink ;
		_bScreenShotMode = false ;
	}
	
	public EditFormEvent(final FlowPanel flowPanel, final int iFormId, final int iArchetypeId, FormLink formLink, final boolean bScreenShotMode)
	{
		_workspace       = flowPanel ;
		_iFormId         = iFormId ;
		_iArchetypeId    = iArchetypeId ;
		_formLink        = formLink ;
		_bScreenShotMode = bScreenShotMode ;
	}
	
	public FlowPanel getWorkspace(){
		return _workspace ;
	}
	
	public int getFormId() {
		return _iFormId ;
	}
	
	public int getArchetypeId() {
		return _iArchetypeId ;
	}
	
	public FormLink getFormLink() {
		return _formLink ;
	}
	
	public boolean inScreenShotMode() {
		return _bScreenShotMode ;
	}
	
	@Override
	protected void dispatch(EditFormEventHandler handler) {
		handler.onEditEncounter(this) ;
	}

	@Override
	public Type<EditFormEventHandler> getAssociatedType() {
		return TYPE ;
	}
}
