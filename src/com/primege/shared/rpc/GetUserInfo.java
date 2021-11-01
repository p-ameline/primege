package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetUserInfo implements Action<GetUserResult> 
{
	private int _iUserId ;

	public GetUserInfo(final int iUserId) 
	{
		super() ;
		
		_iUserId = iUserId ;
	}

	public GetUserInfo() 
	{
		super() ;
		
		_iUserId = -1 ;
	}
	
	public int getUserId() 
	{
		return _iUserId ;
	}
}
