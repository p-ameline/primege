package com.primege.server.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.primege.server.DBConnector;
import com.primege.server.DbParametersModel;
import com.primege.server.Logger;
import com.primege.server.model.ArchetypeDataManager;
import com.primege.server.model.ArchetypeForSiteManager;
import com.primege.server.model.CityDataManager;
import com.primege.server.model.EventDataManager;
import com.primege.server.model.SiteDataManager;
import com.primege.server.model.UserRoleDataManager;
import com.primege.shared.database.ArchetypeData;
import com.primege.shared.database.CityData;
import com.primege.shared.database.EventData;
import com.primege.shared.database.SiteData;
import com.primege.shared.database.UserData;
import com.primege.shared.database.UserRoleData;
import com.primege.shared.rpc.LoginUserInfo;
import com.primege.shared.rpc.LoginUserResult;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

/** 
 * Object in charge of user login  
 *   
 */
public class SendLoginHandler implements ActionHandler<LoginUserInfo, LoginUserResult> 
{	
	protected final Provider<ServletContext>     _servletContext ;
	protected final Provider<HttpServletRequest> _servletRequest ;
	
	@Inject
	public SendLoginHandler(final Provider<ServletContext> servletContext,       
                          final Provider<HttpServletRequest> servletRequest)
	{
		super() ;
		
		_servletContext = servletContext ;
		_servletRequest = servletRequest ;
	}
	
	/**
	  * Check if login information is valid, then return a User
	  * 
	  * @return The LoginUserResult object that answers client request
	  * 
	  * @param action  The LoginUserInfo object that conveys client's request
	  * @param context Technical context
	  * 
	  */
	@Override
	public LoginUserResult execute(LoginUserInfo action, ExecutionContext context) throws ActionException 
	{
		LoginUserResult result = new LoginUserResult() ;
		result.setVersion(DbParametersModel.getVersion()) ;
		
		String sLogin    = action.getUserName() ;
		String sPassword = action.getPassWord() ;
		
		if (sLogin.equals("") || sPassword.equals(""))
		{
			Logger.trace("SendLoginHandler.execute: empty parameter", -1, Logger.TraceLevel.ERROR) ;
			return result ;
		}
		
		// Database query to find a user from login and password
		//
		String sqlText = "SELECT * FROM user " +
		                         "WHERE userLogn = ? " +
		                           "AND userPass = ?" ;
				
		DBConnector dbConnector = new DBConnector(false) ;
		
		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtString(1, sLogin) ;
		dbConnector.setStatememtString(2, sPassword) ;
		
		if (false == dbConnector.executePreparedStatement())
		{
			Logger.trace("SendLoginHandler.execute: failed query " + sqlText, -1, Logger.TraceLevel.ERROR) ;
			return result ;
		}

		ResultSet rs = dbConnector.getResultSet() ;
		try
		{        
			if (rs.next())
			{
				// User found, fill the UserData part of the User object 
				//
				UserData userData = new UserData() ;
				
				userData.setId(rs.getInt("id")) ;
				userData.setLogin(rs.getString("userLogn")) ;
				userData.setPassword(rs.getString("userPass")) ;
				userData.setLabel(rs.getString("userLabel")) ;

				result.setUserData(userData) ;
			}
			else
			{
				// User not found, return empty answer object  
				//
				Logger.trace("SendLoginHandler.execute: no user found for pseudo " + sLogin + " and pass " + sPassword, -1, Logger.TraceLevel.WARNING) ;
				dbConnector.closePreparedStatement() ;
				return result ;
			}

		}
		catch(SQLException ex)
		{
			Logger.trace("SendLoginHandler.execute: DBConnector.dbSelectPreparedStatement: executeQuery failed for preparedStatement " + sqlText, -1, Logger.TraceLevel.ERROR) ;
			Logger.trace("SendLoginHandler.execute: SQLException: " + ex.getMessage(), -1, Logger.TraceLevel.ERROR) ;
			Logger.trace("SendLoginHandler.execute: SQLState: " + ex.getSQLState(), -1, Logger.TraceLevel.ERROR) ;
			Logger.trace("SendLoginHandler.execute: VendorError: " +ex.getErrorCode(), -1, Logger.TraceLevel.ERROR) ;
		}
		
		dbConnector.closeResultSet() ;
		dbConnector.closePreparedStatement() ;
			
		UserData userData = result.getUserData() ;
		if (null == userData)
			return result ;
		
		int iUserId = userData.getId() ;
		if (-1 == iUserId)
			return result ;
		
		// Initialize the current event information
		//
		/* boolean bEventFound = */ getCurrentEvent(iUserId, result, dbConnector) ;
		
		// User found, get her roles 
		//
		getUserRoles(iUserId, result, dbConnector) ;
		
		// Get allowed archetypes, since it depends from roles and event
		//
		getUserArchetypes(iUserId, result, dbConnector) ;
		
		// Get the list of sites for event
		//
		getSitesForEvent(iUserId, result, dbConnector) ;
		
		// Get the list of cities for event
		//
		getCitiesForEvent(iUserId, result, dbConnector) ;
		
		Logger.trace("SendLoginHandler.execute: user " + iUserId + " found for pseudo " + sLogin + " and password " + sPassword, -1, Logger.TraceLevel.WARNING) ;
		
		return result ;
	}

	/**
	  * Fills the result object with user's roles 
	  * 
	  * @param iUserId     User's Id
	  * @param result      Object to get completed before being sent back to the requesting client
	  * @param dbConnector Database connector
	  * 
	  */
	protected void getUserRoles(final int iUserId, LoginUserResult result, DBConnector dbConnector)
	{
		// Get event ID, if any
		//
		int iEventId = -1 ;
		EventData event = result.getEventData() ;
		if (null != event)
			iEventId = event.getId() ;
		
		// Create the array of roles
		//
		result.setRoles(new ArrayList<UserRoleData>()) ;
		
		// Get user's roles
		//
		UserRoleDataManager userRolesManager = new UserRoleDataManager(iUserId, dbConnector) ;
		userRolesManager.fillRolesForUser(iUserId, result.getRoles(), iEventId) ;
	}
	
	/**
	  * Fills the result object with user's allowed archetypes 
	  * 
	  * @param iUserId     User's Id
	  * @param result      Object to get completed before being sent back to the requesting client
	  * @param dbConnector Database connector
	  * 
	  */
	protected void getUserArchetypes(final int iUserId, LoginUserResult result, DBConnector dbConnector)
	{
		//
		//
		ArrayList<UserRoleData> roles = result.getRoles() ;
		
		if ((null == roles) || roles.isEmpty())
			return ;
		
		result.setArchetypes(new ArrayList<ArchetypeData>()) ;
		
		// Get event ID, if any
		//
		int iEventId = -1 ;
		EventData event = result.getEventData() ;
		if (null != event)
			iEventId = event.getId() ;
		
		ArrayList<ArchetypeData> aArchetypes = result.getArchetypes() ;
		
		if (null == aArchetypes)
			return ;
		
		// The archetype can be directly specified inside the role, or be attached to a site
		// which is specified inside the role
		//
		for (UserRoleData role : roles)
		{
			// First check if this role fits with current event
			//
			if (UserRoleDataManager.isValidRole(iEventId, role.getEventId()))
			{
				int iArchetypeId = role.getArchetypeId() ;
				
				// If this role doesn't specify an archetype, check if it specifies a site 
				// that itself specifies one or several archetype(s)
				//
				if (iArchetypeId <= 0) 
				{
					if (role.getSiteId() > 0)
					{
						ArchetypeForSiteManager A4Smanager = new ArchetypeForSiteManager(iUserId, dbConnector) ;
						A4Smanager.getArchetypesForSite(role.getSiteId(), iEventId, aArchetypes) ;
					}
				}
				else
				{
					ArchetypeDataManager archetypeManager = new ArchetypeDataManager(iUserId, dbConnector) ;
					ArchetypeData archetypeData = new ArchetypeData() ;
					if (archetypeManager.existData(iArchetypeId, archetypeData) && (false == ArchetypeDataManager.containsArchetype(iArchetypeId, aArchetypes)))
						aArchetypes.add(archetypeData) ;
				}
			}
		}
	}
	
	/**
	  * Fills the result object with current event information 
	  * 
	  * @param iUserId     User's Id
	  * @param result      Object to get completed before being sent back to the requesting client
	  * @param dbConnector Database connector
	  * 
	  * @return <code>true</code> if an event was found for "now", <code>false</code> if not 
	  * 
	  */
	protected boolean getCurrentEvent(final int iUserId, LoginUserResult result, DBConnector dbConnector)
	{
		// First, create an EventData for the result
		//
		result.setEventData(new EventData()) ;
		
		// Then go get the event that is valid now
		//
		EventDataManager eventManager = new EventDataManager(iUserId, dbConnector) ;
		return eventManager.getCurrentEvent(result.getEventData()) ;
	}
	
	/**
	  * Fills the result object with event's sites 
	  * 
	  * @param iUserId     User's Id
	  * @param result      Object to get completed before being sent back to the requesting client
	  * @param dbConnector Database connector
	  * 
	  */
	protected void getSitesForEvent(final int iUserId, LoginUserResult result, DBConnector dbConnector)
	{
		// Get event ID, if any
		//
		int iEventId = -1 ;
		EventData event = result.getEventData() ;
		if (null != event)
			iEventId = event.getId() ;
		
		// Create the array of roles
		//
		result.setSites(new ArrayList<SiteData>()) ;
		
		// Get event's sites
		//
		SiteDataManager sitesManager = new SiteDataManager(iUserId, dbConnector) ;
		sitesManager.fillSitesForEvent(iUserId, result.getSites(), iEventId) ;
	}
	
	/**
	  * Fills the result object with event's cities 
	  * 
	  * @param iUserId     User's Id
	  * @param result      Object to get completed before being sent back to the requesting client
	  * @param dbConnector Database connector
	  * 
	  */
	protected void getCitiesForEvent(final int iUserId, LoginUserResult result, DBConnector dbConnector)
	{
		// Get event ID, if any
		//
		int iEventId = -1 ;
		EventData event = result.getEventData() ;
		if (null != event)
			iEventId = event.getId() ;
		
		// Create the array of roles
		//
		result.setCities(new ArrayList<CityData>()) ;
		
		// Get event's sites
		//
		CityDataManager citiesManager = new CityDataManager(iUserId, dbConnector) ;
		citiesManager.fillCitiesForEvent(iUserId, result.getCities(), iEventId) ;
	}
	
	@Override
	public Class<LoginUserInfo> getActionType() {
		return LoginUserInfo.class;
	}

	@Override
	public void rollback(LoginUserInfo action, LoginUserResult result,
			ExecutionContext context) throws ActionException {
		// TODO Auto-generated method stub
	}
}
