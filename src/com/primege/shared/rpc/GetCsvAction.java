package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetCsvAction implements Action<GetCsvResult> 
{	
	private int _iUserId ;
	private int _iEventId ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public GetCsvAction() 
	{
		super() ;
		
		_iUserId  = -1 ;
		_iEventId = -1 ;
	}
	
	/**
	 * Constructor for all forms of a given user 
	 */
	public GetCsvAction(final int iUserId, final int iEventId) 
	{
		super() ;
		
		_iUserId  = iUserId ;
		_iEventId = iEventId ;
	}

	public int getUserId() {
		return _iUserId ;
	}
	public void setUserId(int iUserId) {
		_iUserId = iUserId ;
	}
	
	public int getEventId() {
		return _iEventId ;
	}
	public void setEventId(int iEventId) {
		_iEventId = iEventId ;
	}
}
