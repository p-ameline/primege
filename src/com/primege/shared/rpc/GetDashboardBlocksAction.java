package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetDashboardBlocksAction implements Action<GetDashboardBlocksResult> 
{	
	private int    _iUserId ;
	private int    _iEventId ;
	
	private int    _iConstantCityId ;
	private String _sConstantDate ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public GetDashboardBlocksAction() 
	{
		super() ;
		
		_iUserId         = -1 ;
		_iEventId        = -1 ;
		
		_iConstantCityId = -1 ;
		_sConstantDate   = "" ;
	}
	
	/**
	 * Constructor that specify that we want all dates for a given city
	 */
	public GetDashboardBlocksAction(final int iUserId, final int iEventId, final int iConstantCityId) 
	{
		super() ;
		
		_iUserId         = iUserId ;
		_iEventId        = iEventId ;
		_iConstantCityId = iConstantCityId ;
		_sConstantDate   = "" ;
	}
	
	/**
	 * Constructor that specify that we want all dates for a given city
	 */
	public GetDashboardBlocksAction(int iUserId, final int iEventId, String sConstantDate) 
	{
		super() ;
		
		_iUserId         = iUserId ;
		_iEventId        = iEventId ;
		_iConstantCityId = -1 ;
		_sConstantDate   = sConstantDate ;
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

	public int getConstantCityId() {
		return _iConstantCityId ;
	}
	public void setConstantCityId(int iConstantCityId) {
		_iConstantCityId = iConstantCityId ;
	}
	
	public String getConstantDate() {
		return _sConstantDate ;
	}
	public void setConstantDate(String sConstantDate) {
		_sConstantDate = sConstantDate ;
	}
}
