package com.primege.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.UserRoleData;

/** 
 * Object in charge of Read/Write operations in the <code>userRole</code> table 
 *   
 */
public class UserRoleDataManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public UserRoleDataManager(int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}
	
	/**
	  * Insert a UserRoleData object in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToInsert UserRoleData to be inserted
	  * 
	  */
	public boolean insertData(UserRoleData dataToInsert)
	{
		if ((null == _dbConnector) || (null == dataToInsert))
			return false ;
		
		String sQuery = "INSERT INTO userRole (userID, eventID, cityID, siteID, archeID, userRole) VALUES (?, ?, ?, ?, ?)" ;
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("UserRoleDataManager.insertData: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToInsert.getUserId()) ;
		_dbConnector.setStatememtInt(2, dataToInsert.getEventId()) ;
		_dbConnector.setStatememtInt(3, dataToInsert.getCityId()) ;
		_dbConnector.setStatememtInt(4, dataToInsert.getSiteId()) ;
		_dbConnector.setStatememtInt(5, dataToInsert.getArchetypeId()) ;
		_dbConnector.setStatememtString(6, dataToInsert.getUserRole()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("UserRoleDataManager.insertData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		int iSiteDataId = 0 ;
		
		ResultSet rs = _dbConnector.getResultSet() ;
		try
    {
			if (rs.next())
			{
				iSiteDataId = rs.getInt(1) ;
				dataToInsert.setId(iSiteDataId) ;
			}
			else
				Logger.trace("UserRoleDataManager.insertData: cannot get Id after query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace("UserRoleDataManager.insertData: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
    }
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		Logger.trace("UserRoleDataManager.insertData: userRole " + _iUserId + " successfuly recorded site " + iSiteDataId + " for event " + dataToInsert.getEventId(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a UserRoleData in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToUpdate UserRoleData to be updated
	  * 
	  */
	public boolean updateData(UserRoleData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("UserRoleDataManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		UserRoleData foundData = new UserRoleData() ;
		if (false == existData(dataToUpdate.getId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("UserRoleDataManager.updateData: UserRoleData to update (id = " + dataToUpdate.getId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any UserRoleData with this Id in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * @param iDataId ID of UserRoleData to check
	  * @param foundData UserRoleData to get existing information
	  * 
	  */
	private boolean existData(int iDataId, UserRoleData foundData)
	{
		if ((null == _dbConnector) || (-1 == iDataId) || (null == foundData))
		{
			Logger.trace("UserRoleDataManager.existData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM userRole WHERE id = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iDataId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("UserRoleDataManager.existData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("UserRoleDataManager.existData: no SiteData found for id = " + iDataId, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		try
		{
	    if (rs.next())
	    {
	    	fillDataFromResultSet(rs, foundData) ;
	    	
	    	_dbConnector.closeResultSet() ;
	    	_dbConnector.closePreparedStatement() ;
	    	
	    	return true ;	    	
	    }
		} catch (SQLException e)
		{
			Logger.trace("UserRoleDataManager.existData: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
		
	/**
	  * Fill a structure with all the different roles for a same user 
	  * 
	  * @param iUserId  ID of user to get roles for
	  * @param aRoles   UserRoleData array to fill
	  * @param iEventId EventId (if > 0) in order to filter roles that are specific to other events 
	  * 
	  */
	public void fillRolesForUser(int iUserID, ArrayList<UserRoleData> aRoles, int iEventId)
	{
		if ((null == _dbConnector) || (-1 == iUserID) || (null == aRoles))
		{
			Logger.trace("UserRoleDataManager.fillRolesForUser: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return ;
		}
		
		String sQuery = "SELECT * FROM userRole WHERE userID = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iUserID) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("UserRoleDataManager.fillRolesForUser: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("UserRoleDataManager.fillRolesForUser: no role found for user " + iUserID, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return ;
		}
		
		try
		{
	    while (rs.next())
	    {
	    	int iRoleEventId = rs.getInt("eventID") ;
	    	
	    	if (isValidRole(iEventId, iRoleEventId))
	    	{
	    		UserRoleData foundData = new UserRoleData() ;
	    		fillDataFromResultSet(rs, foundData) ;
	    		aRoles.add(foundData) ;
	    	}
	    }
		} catch (SQLException e)
		{
			Logger.trace("UserRoleDataManager.fillRolesForUser: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return ;
	}
	
	/**
	  * Update a UserRoleData in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * @param  dataToUpdate UserRoleData to update
	  * 
	  */
	private boolean forceUpdateData(UserRoleData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("UserRoleDataManager.forceUpdateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
			
		// Prepare SQL query
		//
		String sQuery = "UPDATE userRole SET userID = ?, eventID = ?, cityID = ?, siteID = ?, archeID = ?, userRole = ?" +
				                          " WHERE " +
				                               "id = '" + dataToUpdate.getId() + "'" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("UserRoleDataManager.forceUpdateData: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToUpdate.getUserId()) ;
		_dbConnector.setStatememtInt(2, dataToUpdate.getEventId()) ;
		_dbConnector.setStatememtInt(3, dataToUpdate.getCityId()) ;
		_dbConnector.setStatememtInt(4, dataToUpdate.getSiteId()) ;
		_dbConnector.setStatememtInt(5, dataToUpdate.getArchetypeId()) ;
		_dbConnector.setStatememtString(6, dataToUpdate.getUserRole()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("UserRoleDataManager.forceUpdateData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace("UserRoleDataManager.forceUpdateData: updated data for UserRoleData " + dataToUpdate.getId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Initialize a UserRoleData from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData UserRoleData to fill
	  * 
	  */
	protected void fillDataFromResultSet(ResultSet rs, UserRoleData foundData)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
    	foundData.setUserId(rs.getInt("userID")) ;
    	foundData.setEventId(rs.getInt("eventID")) ;
    	foundData.setCityId(rs.getInt("cityID")) ;
    	foundData.setSiteId(rs.getInt("siteID")) ;
    	foundData.setArchetypeId(rs.getInt("archeID")) ;
    	foundData.setUserRole(rs.getString("userRole")) ;
		} 
		catch (SQLException e) {
			Logger.trace("UserRoleDataManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
	}

	/**
	  * Is a role to be considered for a given event? 
	  * 
	  * @param iEventId     Event id of current id
	  * @param iRoleEventId Event id specified inside role
	  * 
	  * @return <code>true</code> if both events fit
	  * 
	  */
	public static boolean isValidRole(final int iEventId, final int iRoleEventId)
	{
		// return true if:
  	// - no event is specified in parameters OR
  	// - this role is not "event specific"   OR
  	// - this role is event specific and defined for the event specified in parameters
  	//
		return (iEventId <= 0) || (iRoleEventId <= 0) || (iEventId == iRoleEventId) ;
	}
}
