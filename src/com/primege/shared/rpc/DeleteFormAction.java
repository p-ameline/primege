package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object to signal that a form must be marked as deleted
 * 
 * Created: 17 May 2016
 * Author: PA
 * 
 */
public class DeleteFormAction implements Action<DeleteFormResult> 
{	
	private int _iUserId ;
	private int _iFormId ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public DeleteFormAction() 
	{
		super() ;
		
		_iUserId = -1 ;
		_iFormId = -1 ;
	}
	
	/**
	 * Plain vanilla constructor 
	 */
	public DeleteFormAction(int iUserId, int iFormId) 
	{
		super() ;
		
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
