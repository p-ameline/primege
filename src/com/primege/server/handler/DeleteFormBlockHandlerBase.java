package com.primege.server.handler;

import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Provider;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.server.model.FormLinkManager;
import com.primege.shared.database.FormLink;
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

		if (false == deleteForm(iFormId, dbConnector, iUserId))
		{
			result.setWasSuccessful(false) ;
			result.setMessage("Server issue.") ;
			return result ;
		}
		
		result.setWasSuccessful(true) ;
		
		deleteAnnotations(iFormId, dbConnector, iUserId) ;
		
		return result ;
	}
	
	/**
	 * Delete a form (to be accurate, mark it deleted)
	 * 
	 * @param iFormId     Identifier of form to delete
	 * @param dbConnector Database connector
	 * @param iUserId     User identifier
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not
	 */
	public static boolean deleteForm(int iFormId, DBConnector dbConnector, int iUserId)
	{
		String sFctName = "DeleteFormBlockHandlerBase.deleteForm" ;
		
		if ((null == dbConnector) || (iFormId < 0))
		{
			Logger.trace(sFctName + ": empty parameter", iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Prepare sql query
		//
		String sQuery = "UPDATE form SET deleted = \"1\"" +
				                                        " WHERE " +
				                                                "id = ?" ; 

		dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}

		dbConnector.setStatememtInt(1, iFormId) ;

		// Execute query 
		//
		int iNbAffectedRows = dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, iUserId, Logger.TraceLevel.ERROR) ;
			dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": successfully deleted form " + iFormId, iUserId, Logger.TraceLevel.SUBSTEP) ;

		dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	 * Delete all links with this annotation as object
	 * 
	 * @param iFormId     Identifier of annotation to delete links to
	 * @param dbConnector Database connector
	 * @param iUserId     User identifier
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not
	 */
	protected static boolean deleteAnnotations(int iFormId, DBConnector dbConnector, int iUserId)
	{
		String sFctName = "DeleteAnnotationHandler.deleteAnnotations" ;
		
		if ((null == dbConnector) || (iFormId < 0))
		{
			Logger.trace(sFctName + ": empty parameter", iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		FormLinkManager formLinkManager = new FormLinkManager(iUserId, dbConnector) ;
		
		// Get all links to annotations
		//
		ArrayList<FormLink> aLinks = new ArrayList<FormLink>() ;
		if (false == formLinkManager.getLinksForFormAsSubject(iFormId, aLinks, true))
			return false ;
		
		if (aLinks.isEmpty())
			return true ;
		
		for (FormLink link : aLinks)
		{
			deleteForm(link.getObjectFormId(), dbConnector, iUserId) ;
			formLinkManager.deleteLink(link.getLinkId()) ;
		}
		
		return true ;
	}
}
