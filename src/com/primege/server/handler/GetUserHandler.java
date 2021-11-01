package com.primege.server.handler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.primege.server.DBConnector;
import com.primege.server.model.UserManager;

import com.primege.shared.database.UserData;
import com.primege.shared.rpc.GetUserInfo;
import com.primege.shared.rpc.GetUserResult;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

/** 
 * Object in charge of getting a user from the <code>medecin</code> table 
 *   
 */
public class GetUserHandler implements ActionHandler<GetUserInfo, GetUserResult> 
{	
	protected final Provider<ServletContext>     _servletContext ;
	protected final Provider<HttpServletRequest> _servletRequest ;
	
	@Inject
	public GetUserHandler(final Provider<ServletContext>     servletContext,       
                        final Provider<HttpServletRequest> servletRequest)
	{
		_servletContext = servletContext ;
		_servletRequest = servletRequest ;
	}
	
	@Override
	public GetUserResult execute(GetUserInfo action, ExecutionContext context) throws ActionException 
	{
		GetUserResult result = new GetUserResult() ;
		
		DBConnector dbConnector = new DBConnector(false) ;
		
		UserManager userManager = new UserManager(action.getUserId(), dbConnector) ; 
		
		UserData userData = new UserData() ;
		if (userManager.existUser(action.getUserId(), userData))
			result.setUserData(userData) ;

		return result ;
	}
		
	@Override
	public Class<GetUserInfo> getActionType() {
		return GetUserInfo.class ;
	}

	@Override
	public void rollback(GetUserInfo action, GetUserResult result,
			ExecutionContext context) throws ActionException {
		// TODO Auto-generated method stub
	}
}
