package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class RegisterFormResult implements Result 
{
	private int    _iRecordedId ;
	private String _sMessage ;
	
	public RegisterFormResult()
	{
		super() ;
		
		_iRecordedId = -1 ;
		_sMessage    = "" ;
	}
	
	public RegisterFormResult(int iRecordedId, String sMessage) 
	{
		super() ;
		
		_iRecordedId = iRecordedId ;
		_sMessage    = sMessage ;
	}

	public int getRecordedId() {
  	return _iRecordedId ;
  }
	public void setRecordedId(int iRecordedId) {
  	_iRecordedId = iRecordedId ;
  }

	public String getMessage() {
  	return _sMessage ;
  }
	public void setMessage(String sMessage) {
  	_sMessage = sMessage ;
  }
}
