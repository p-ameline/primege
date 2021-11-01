package com.primege.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Model of line for a table inside the dashboard
 * 
 */
public class DashboardTableLineModel implements IsSerializable
{   
	protected String _sLabel ;
	protected String _sPath ;
		
	public DashboardTableLineModel()
	{
		_sLabel = "" ;
		_sPath  = "" ;
	}
		
	public DashboardTableLineModel(final String sLabel, final String sPath)
	{
		_sLabel = sLabel ;
		_sPath  = sPath ;
	}
		
	public DashboardTableLineModel(final DashboardTableLineModel model)
	{
		_sLabel = model._sLabel ;
		_sPath  = model._sPath ;
	}
		
	public String getLabel() {
		return _sLabel ;
	}
		
	public String getPath() {
		return _sPath ;
	}
}
