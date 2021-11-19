package com.primege.server.handler;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.server.model.FormLinkManager;
import com.primege.shared.database.FormLink;
import com.primege.shared.rpc.DeleteAnnotationAction;
import com.primege.shared.rpc.DeleteAnnotationResult;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

public class DeleteAnnotationHandler implements ActionHandler<DeleteAnnotationAction, DeleteAnnotationResult> 
{
	protected final Provider<ServletContext>     _servletContext ;
	protected final Provider<HttpServletRequest> _servletRequest ;
	
	@Inject
	public DeleteAnnotationHandler(final Provider<ServletContext>     servletContext,
                                 final Provider<HttpServletRequest> servletRequest)
	{
		_servletContext = servletContext ;
		_servletRequest = servletRequest ;
	}
	
	@Override
	public DeleteAnnotationResult execute(DeleteAnnotationAction action, ExecutionContext context) throws ActionException 
	{
		String sFctName = "DeleteAnnotationHandler.execute" ;
		
		DeleteAnnotationResult result = new DeleteAnnotationResult() ;
		
		int iUserId = action.getUserId() ;
		if (iUserId <= 0)
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
		
		// Delete form
		//
		if (false == DeleteFormBlockHandlerBase.deleteForm(iFormId, dbConnector, iUserId))
		{
			result.setWasSuccessful(false) ;
			result.setMessage("Server issue.") ;
			return result ;
		}
		
		// Delete links
		//
		
			
		return result ;
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
	protected boolean deleteLinksForAnnotation(int iFormId, DBConnector dbConnector, int iUserId)
	{
		String sFctName = "DeleteAnnotationHandler.deleteLinksForAnnotation" ;
		
		if ((null == dbConnector) || (iFormId < 0))
		{
			Logger.trace(sFctName + ": empty parameter", iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		FormLinkManager formLinkManager = new FormLinkManager(iUserId, dbConnector) ;
		
		ArrayList<FormLink> aLinks = new ArrayList<FormLink>() ;
		if (false == formLinkManager.getLinksForAnnotationAsObject(iFormId, aLinks, true))
			return false ;
		
		if (aLinks.isEmpty())
			return true ;
		
		for (FormLink link : aLinks)
			formLinkManager.deleteLink(link.getLinkId()) ;
		
		return true ;
	}
	
	@Override
	public Class<DeleteAnnotationAction> getActionType() {
		return DeleteAnnotationAction.class;
	}

	@Override
	public void rollback(DeleteAnnotationAction action, DeleteAnnotationResult result,
			ExecutionContext context) throws ActionException {
		// TODO Auto-generated method stub
	}
}
