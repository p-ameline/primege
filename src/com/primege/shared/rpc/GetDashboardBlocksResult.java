package com.primege.shared.rpc;

import com.primege.shared.model.DashboardBlocks;

import net.customware.gwt.dispatch.shared.Result;

public class GetDashboardBlocksResult implements Result 
{
	private DashboardBlocks _dashboardBlocks = new DashboardBlocks() ;
	private String          _sMessage ;
	
	public GetDashboardBlocksResult()
	{
		super() ;
		
		_sMessage = "" ;
	}
	
	public GetDashboardBlocksResult(String sMessage) 
	{
		super() ;
		
		_sMessage = sMessage ;
	}

	public DashboardBlocks getDashboardsBlocks() {
  	return _dashboardBlocks ;
  }
	public void setDashboardsBlocks(DashboardBlocks dashboardBlocks) {
		_dashboardBlocks.initFromDashboardBlocks(dashboardBlocks) ;
  }

	public String getMessage() {
  	return _sMessage ;
  }
	public void setMessage(String sMessage) {
  	_sMessage = sMessage ;
  }
}
