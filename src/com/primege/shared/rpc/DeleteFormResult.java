package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

/**
 * Object that returns information from a form deletion operation
 * 
 * Created: 17 May 2016
 * Author: PA
 * 
 */
public class DeleteFormResult implements Result 
{
	private boolean _wasSuccessful  ;
	private String  _sMessage ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public DeleteFormResult()
	{
		super() ;
		
		_wasSuccessful = false ;
		_sMessage      = "" ;
	}
	
	/**
	 * Plain vanilla constructor 
	 */
	public DeleteFormResult(boolean wasSuccessful, String sMessage) 
	{
		super() ;
		
		_wasSuccessful = wasSuccessful ;
		_sMessage      = sMessage ;
	}

	public boolean wasSuccessful() {
  	return _wasSuccessful ;
  }
	public void setWasSuccessful(boolean wasSuccessful) {
		_wasSuccessful = wasSuccessful ;
  }

	public String getMessage() {
  	return _sMessage ;
  }
	public void setMessage(String sMessage) {
  	_sMessage = sMessage ;
  }
}
