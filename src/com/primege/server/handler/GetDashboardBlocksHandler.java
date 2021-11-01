package com.primege.server.handler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.primege.server.DBConnector;
import com.primege.server.model.GetDashboardBlocksInBase;
import com.primege.shared.model.DashboardBlocks;
import com.primege.shared.rpc.GetDashboardBlocksAction;
import com.primege.shared.rpc.GetDashboardBlocksResult;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

/** 
 * Object in charge of getting all forms for a given event that share a common city or a common date 
 *   
 */
public class GetDashboardBlocksHandler implements ActionHandler<GetDashboardBlocksAction, GetDashboardBlocksResult> 
{	
	protected final Provider<ServletContext>     _servletContext ;
	protected final Provider<HttpServletRequest> _servletRequest ;
	
	@Inject
	public GetDashboardBlocksHandler(final Provider<ServletContext> servletContext,
			                             final Provider<HttpServletRequest> servletRequest)
	{
		_servletContext = servletContext ;
		_servletRequest = servletRequest ;
	}

	@Override
	public GetDashboardBlocksResult execute(GetDashboardBlocksAction action, ExecutionContext context) throws ActionException 
	{
		GetDashboardBlocksResult result = new GetDashboardBlocksResult() ;
		
		int    iUserId  = action.getUserId() ;
		int    iEventId = action.getEventId() ;
		
		int    iCityId  = action.getConstantCityId() ;
		String sDate    = action.getConstantDate() ;
		
		DashboardBlocks DashBlocks = result.getDashboardsBlocks() ;
		DashBlocks.setConstantCityId(iCityId) ;
		DashBlocks.setConstantDate(sDate) ;
		
		// city ID or date can be unspecified, but not both
		//
		if ((iCityId <= 0) && ((null == sDate) || "".equals(sDate)))
			return result ;
		
		DBConnector dbConnector = new DBConnector(false) ;
		
		GetDashboardBlocksInBase dashboardBlocksManager = new GetDashboardBlocksInBase(iUserId, dbConnector) ;
		dashboardBlocksManager.GetDashboardBlocks(iEventId, iCityId, sDate, DashBlocks) ;
		
		return result ;
	}
	
	@Override
	public Class<GetDashboardBlocksAction> getActionType() {
		return GetDashboardBlocksAction.class;
	}

	@Override
	public void rollback(GetDashboardBlocksAction action, GetDashboardBlocksResult result,
			ExecutionContext context) throws ActionException {
		// TODO Auto-generated method stub
	}
}
