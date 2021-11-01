package com.primege.server.handler;

import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Provider;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.rpc.DeleteFormAction;
import com.primege.shared.rpc.DeleteFormResult;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

public class DeleteFormBlockHandlerBase
{	
	protected final Provider<ServletContext>     _servletContext ;
	protected final Provider<HttpServletRequest> _servletRequest ;
	
	public DeleteFormBlockHandlerBase(final Provider<ServletContext> servletContext,
                                    final Provider<HttpServletRequest> servletRequest)
	{
		_servletContext = servletContext ;
		_servletRequest = servletRequest ;
	}
	
	protected DeleteFormResult deleteForm(DeleteFormAction action, ExecutionContext context) throws ActionException 
	{
		String sFctName = "DeleteFormBlockHandlerBase.execute" ;
		
		DeleteFormResult result = new DeleteFormResult() ;
		
		int iUserId = action.getUserId() ;
		if ((-1 == iUserId) || (0 == iUserId))
		{
			Logger.trace(sFctName + ": empty parameter", -1, Logger.TraceLevel.ERROR) ;
			result.setWasSuccessful(false) ;
			result.setMessage("Empty user Id") ;
			return result ;
		}
		
		int iFormId = action.getFormId() ;
		if (-1 == iFormId)
		{
			Logger.trace(sFctName + ": empty parameter", iUserId, Logger.TraceLevel.ERROR) ;
			result.setWasSuccessful(false) ;
			result.setMessage("Empty message Id") ;
			return result ;
		}
				
		DBConnector dbConnector = new DBConnector(false) ;

		// Prepare sql query
		//
		String sQuery = "UPDATE form SET deleted = \"1\"" +
				                          " WHERE " +
				                               "id = ?" ; 
		
		dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", iUserId, Logger.TraceLevel.ERROR) ;
			result.setWasSuccessful(false) ;
			result.setMessage("Server error: cannot get Statement") ;
			return result ;
		}
		
		dbConnector.setStatememtInt(1, iFormId) ;
				
		// Execute query 
		//
		int iNbAffectedRows = dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, iUserId, Logger.TraceLevel.ERROR) ;
			dbConnector.closePreparedStatement() ;
			result.setWasSuccessful(false) ;
			result.setMessage("Server error: failed query") ;
			return result ;
		}

		Logger.trace(sFctName + ": deleted form " + iFormId, iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		dbConnector.closePreparedStatement() ;
		
		result.setWasSuccessful(true) ;
		return result ;
	}
}
