package com.primege.server.handler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.primege.shared.rpc.GetArchetypeAction;
import com.primege.shared.rpc.GetArchetypeResult;

public class GetArchetypeHandler extends GetArchetypeHandlerBase implements ActionHandler<GetArchetypeAction, GetArchetypeResult>
{
	@Inject
	public GetArchetypeHandler(final Provider<ServletContext>     servletContext,       
                             final Provider<HttpServletRequest> servletRequest)
	{
		super(servletContext, servletRequest) ;
	}

	/**
	  * Constructor dedicated to unit tests 
	  */
	public GetArchetypeHandler() {
		super(null, null) ;
	}
	
	@Override
	public GetArchetypeResult execute(final GetArchetypeAction action,
       					                    final ExecutionContext context) throws ActionException 
  {
		return getArchetype(action, context) ;  
	}
	
	@Override
	public void rollback(final GetArchetypeAction action,
        							 final GetArchetypeResult result,
        final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<GetArchetypeAction> getActionType()
	{
		return GetArchetypeAction.class;
	}
}
