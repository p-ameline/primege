package com.primege.server.handler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.primege.server.DBConnector;
import com.primege.server.DbParametersModel;
import com.primege.server.Logger;
import com.primege.server.model.ArchetypeDataManager;
import com.primege.server.model.XmlArchetypeManager;
import com.primege.shared.database.ArchetypeData;
import com.primege.shared.rpc.GetArchetypeAction;
import com.primege.shared.rpc.GetArchetypeResult;

public class GetArchetypeHandlerBase
{
	protected final Provider<ServletContext>     _servletContext ;
	protected final Provider<HttpServletRequest> _servletRequest ;
	
	@Inject
	public GetArchetypeHandlerBase(final Provider<ServletContext>     servletContext,
                                 final Provider<HttpServletRequest> servletRequest)
	{
		_servletContext = servletContext ;
		_servletRequest = servletRequest ;
	}
	
	public GetArchetypeResult getArchetype(final GetArchetypeAction action,
       					                        final ExecutionContext context) throws ActionException 
  {
		String sFctName = "GetArchetypeHandlerBase.getArchetype" ;
		
		if (null == action)
			return new GetArchetypeResult(false, null, null, "Server error (null action object)") ;
		
		int iArchetypeId = action.getArchetypeId() ;
 		int iUserId      = action.getUserId() ;
		
 		Logger.trace(sFctName + ": loading archetype for Id = " + iArchetypeId, iUserId, Logger.TraceLevel.STEP) ;
 		
		DBConnector dbConnector = new DBConnector(false) ;
		
		// Get the corresponding ArchetypeData 
		//
		ArchetypeData archetypeData = new ArchetypeData() ;
		
		ArchetypeDataManager archetypeDataManager = new ArchetypeDataManager(iUserId, dbConnector) ;
		if (false == archetypeDataManager.existData(iArchetypeId, archetypeData))
			return new GetArchetypeResult(false, null, null, "Server error (archetype not found)") ;
		
		String sFileName = archetypeData.getFile() ;
		
		if ("".equals(sFileName))
			return new GetArchetypeResult(false, null, archetypeData, "Server error (unknown archetype file)") ;

 		// Get full file name
 		//
 		String sFullFileName = DbParametersModel.getArchetypeDir() + sFileName ; 
 		
 		//
 		//
 		XmlArchetypeManager archetypeManager = new XmlArchetypeManager(sFullFileName) ;
 		String sArchetype = archetypeManager.openDocument(iUserId) ;
 		if ("".equals(sArchetype))
 			return new GetArchetypeResult(false, "", archetypeData, "Server error (cannot parse Archetype file)") ;
 		
 		Logger.trace(sFctName + ": user " + iUserId + " successfuly loaded archetype " + sFileName, iUserId, Logger.TraceLevel.STEP) ;
 		
 		return new GetArchetypeResult(true, sArchetype, archetypeData, "") ;
  }
}
