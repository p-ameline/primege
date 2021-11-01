package com.primege.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlowPanel;

public class PostLoginHeaderEvent extends GwtEvent<PostLoginHeaderEventHandler> 
{	
	public static Type<PostLoginHeaderEventHandler> TYPE = new Type<PostLoginHeaderEventHandler>();
	
	private FlowPanel _header ;
	
	public static Type<PostLoginHeaderEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<PostLoginHeaderEventHandler>();
		return TYPE;
	}
	
	public PostLoginHeaderEvent(final FlowPanel flowPanel) {
		_header = flowPanel ;
	}
	
	public FlowPanel getHeader(){
		return _header ;
	}
		
	@Override
	protected void dispatch(PostLoginHeaderEventHandler handler) {
		handler.onPostLoginHeader(this);
	}

	@Override
	public Type<PostLoginHeaderEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
}
