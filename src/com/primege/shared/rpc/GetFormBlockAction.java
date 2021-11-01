package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetFormBlockAction implements Action<GetFormBlockResult> 
{	
	private int _iUserId ;
	private int _iFormId ;
	
	public GetFormBlockAction() 
	{
		super() ;
		
		_iUserId = -1 ;
		_iFormId = -1 ;
	}
	
	public GetFormBlockAction(int iUserId, int iFormId) 
	{
		_iUserId = iUserId ;
		_iFormId = iFormId ;
	}

	public int getUserId() {
		return _iUserId ;
	}
	public void setUserId(int iUserId) {
		_iUserId = iUserId ;
	}

	public int getFormId() {
		return _iFormId ;
	}
	public void setFormId(int iFormId) {
		_iFormId = iFormId ;
	}
}
