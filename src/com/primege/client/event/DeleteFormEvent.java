package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DeleteFormEvent extends GwtEvent<DeleteFormEventHandler> 
{	
	public static Type<DeleteFormEventHandler> TYPE = new Type<DeleteFormEventHandler>();
	
	private int _iFormIdToDelete ; 
	
	public static Type<DeleteFormEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<DeleteFormEventHandler>();
		return TYPE;
	}
	
	public void setFormIdToDelete(final int iFormIdToDelete) {
		_iFormIdToDelete = iFormIdToDelete ;
	}	
	public int getFormIdToDelete() {
		return _iFormIdToDelete ;
	}

	@Override
	protected void dispatch(DeleteFormEventHandler handler) {
		handler.onDeleteForm(this) ;		
	}

	@Override
	public Type<DeleteFormEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
}
