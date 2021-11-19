package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object to signal that a form must be marked as deleted
 * 
 * Created: 17 May 2016
 * Author: PA
 * 
 */
public class DeleteAnnotationAction implements Action<DeleteAnnotationResult> 
{	
	private int _iUserId ;
	private int _iFormId ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public DeleteAnnotationAction() 
	{
		super() ;
		
		_iUserId = -1 ;
		_iFormId = -1 ;
	}
	
	/**
	 * Plain vanilla constructor 
	 */
	public DeleteAnnotationAction(int iUserId, int iFormId) 
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
