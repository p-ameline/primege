package com.primege.shared.rpc;

import com.primege.shared.database.UserData;

import net.customware.gwt.dispatch.shared.Result;

public class GetUserResult implements Result
{
	private UserData _userData = new UserData() ;
	private String   _sMessage ;
	
	/**
	 * */
	public GetUserResult()
	{
		super() ;
		
		_sMessage = "" ;
	}

	/**
	 * @param sMessage
	 * */
	public GetUserResult(String sMessage)
	{
		super() ;
		
		_sMessage = sMessage ;
	}
	
	public String getMessage() {
		return _sMessage ;
	}
	public void setMessage(String sMessage) {
		_sMessage = sMessage ;
	}

	public UserData getUserData() {
		return _userData ;
	}
	public void setUserData(UserData userData) {
		_userData.initFromUser(userData) ;
	}	
}
