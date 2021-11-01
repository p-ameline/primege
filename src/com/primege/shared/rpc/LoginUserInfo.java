package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class LoginUserInfo implements Action<LoginUserResult> 
{
	private String _sLogin ;
	private String _sPassword ;

	public LoginUserInfo(final String username, final String password) 
	{
		super() ;
		
		_sLogin    = username ;
		_sPassword = password ;
	}

	@SuppressWarnings("unused")
	private LoginUserInfo() 
	{
	}

	public String getUserName() {
		return _sLogin ;
	}

	public String getPassWord() {
		return _sPassword ;
	}
}
