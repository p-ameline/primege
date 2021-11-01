package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class GetCsvResult implements Result 
{
	private String _sCsvFileUrl ;
	private String _sMessage ;

	/**
	 * Default constructor (with zero information)
	 */
	public GetCsvResult()
	{
		super() ;
		
		_sCsvFileUrl = "" ;
		_sMessage    = "" ;
	}
	
	/**
	 * Constructor for all forms of a given user 
	 */
	public GetCsvResult(String sCsvFileUrl, String sMessage) 
	{
		super() ;
		
		_sCsvFileUrl = sCsvFileUrl ;
		_sMessage    = sMessage ;
	}

	public String getCsvResult() {
  	return _sCsvFileUrl ;
  }
	public void setCsvResult(String sCsvFileUrl) {
		_sCsvFileUrl = sCsvFileUrl ;
  }
	
	public String getMessage() {
  	return _sMessage ;
  }
	public void setMessage(String sMessage) {
  	_sMessage = sMessage ;
  }
}
