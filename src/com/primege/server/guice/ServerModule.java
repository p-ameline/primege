package com.primege.server.guice;

import net.customware.gwt.dispatch.server.guice.ActionHandlerModule;

import org.apache.commons.logging.Log;

import com.google.inject.Singleton;
import com.primege.server.DbParameters;
import com.primege.server.handler.BuildCsvHandler;
import com.primege.server.handler.DeleteFormBlockHandler;
import com.primege.server.handler.GetArchetypeHandler;
import com.primege.server.handler.GetDashboardBlocksHandler;
import com.primege.server.handler.GetFormBlockHandler;
import com.primege.server.handler.GetFormsHandler;
import com.primege.server.handler.GetUserHandler;
import com.primege.server.handler.SendLoginHandler;
import com.primege.server.handler.RecordFormHandler;
import com.primege.shared.rpc.DeleteFormAction;
import com.primege.shared.rpc.GetArchetypeAction;
import com.primege.shared.rpc.GetCsvAction;
import com.primege.shared.rpc.GetDashboardBlocksAction;
import com.primege.shared.rpc.GetFormBlockAction;
import com.primege.shared.rpc.GetFormsAction;
import com.primege.shared.rpc.GetUserInfo;
import com.primege.shared.rpc.LoginUserInfo;
import com.primege.shared.rpc.RegisterFormAction;

/**
 * Module which binds the handlers and configurations
 *
 */
public class ServerModule extends ActionHandlerModule 
{
	@Override
	protected void configureHandlers() 
	{
		bindHandler(LoginUserInfo.class,            SendLoginHandler.class) ;
		bindHandler(GetUserInfo.class,              GetUserHandler.class) ;
		bindHandler(GetArchetypeAction.class,       GetArchetypeHandler.class) ;
		bindHandler(GetCsvAction.class,             BuildCsvHandler.class) ;
		bindHandler(GetFormsAction.class,           GetFormsHandler.class) ;
		bindHandler(GetFormBlockAction.class,       GetFormBlockHandler.class) ;
		bindHandler(DeleteFormAction.class,         DeleteFormBlockHandler.class) ;
		bindHandler(RegisterFormAction.class,       RecordFormHandler.class) ;
		bindHandler(GetDashboardBlocksAction.class, GetDashboardBlocksHandler.class) ;
		
		bind(Log.class).toProvider(LogProvider.class).in(Singleton.class);
		bind(DbParameters.class).toProvider(DbParametersProvider.class).in(Singleton.class);
	}
}