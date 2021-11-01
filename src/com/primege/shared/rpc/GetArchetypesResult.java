package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class GetArchetypesResult implements Result 
{
	private int    _iRecordedExtensionsId ;
	private String _sMessage ;
	
	public GetArchetypesResult()
	{
		super() ;
	}
	
	public GetArchetypesResult(int iRecordedId, String sMessage) 
	{
		_iRecordedExtensionsId = iRecordedId ;
		_sMessage              = sMessage ;
	}

	public int getRecordedId()
  {
  	return _iRecordedExtensionsId ;
  }
	public void setRecordedId(int iRecordedId)
  {
		_iRecordedExtensionsId = iRecordedId ;
  }

	public String getMessage()
  {
  	return _sMessage ;
  }
	public void setMessage(String sMessage)
  {
  	_sMessage = sMessage ;
  }
}
