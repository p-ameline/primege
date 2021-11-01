package com.primege.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.ArchetypeData;
import com.primege.shared.database.ArchetypeForSiteData;

/** 
 * Object in charge of Read/Write operations in the <code>archetype4site</code> table 
 *   
 */
public class ArchetypeForSiteManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public ArchetypeForSiteManager(int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}
	
	/**
	  * Insert a ArchetypeForSiteData object in database
	  * 
	  * @return true if successful, false if not
	  * 
	  * @param dataToInsert ArchetypeForSiteData to be inserted
	  * 
	  */
	public boolean insertData(ArchetypeForSiteData dataToInsert)
	{
		String sFctName = "ArchetypeForSiteManager.insertSiteData" ;
		
		if ((null == _dbConnector) || (null == dataToInsert))
			return false ;
		
		String sQuery = "INSERT INTO archetype4site (eventID, siteID, archetypeID) VALUES (?, ?, ?)" ;
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToInsert.getEventId()) ;
		_dbConnector.setStatememtInt(2, dataToInsert.getSiteId()) ;
		_dbConnector.setStatememtInt(3, dataToInsert.getArchetypeId()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		int iDataId = 0 ;
		
		ResultSet rs = _dbConnector.getResultSet() ;
		try
    {
			if (rs.next())
			{
				iDataId = rs.getInt(1) ;
				dataToInsert.setId(iDataId) ;
			}
			else
				Logger.trace(sFctName + ": cannot get Id after query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
    }
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		Logger.trace(sFctName + ": user " + _iUserId + " successfuly recorded archetype4site " + dataToInsert.getId(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a ArchetypeForSiteData in database
	  * 
	  * @return true if successful, false if not
	  * 
	  * @param dataToUpdate ArchetypeForSiteData to be updated
	  * 
	  */
	public boolean updateData(ArchetypeForSiteData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("ArchetypeForSiteManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		ArchetypeForSiteData foundData = new ArchetypeForSiteData() ;
		if (false == existData(dataToUpdate.getId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("ArchetypeForSiteManager.updateData: ArchetypeForSiteData to update (id = " + dataToUpdate.getId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any ArchetypeForSiteData with this Id in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * @param iDataId ID of ArchetypeForSiteData to check
	  * @param foundData ArchetypeForSiteData to get existing information
	  * 
	  */
	public boolean existData(int iDataId, ArchetypeForSiteData foundData)
	{
		if ((null == _dbConnector) || (-1 == iDataId) || (null == foundData))
		{
			Logger.trace("ArchetypeForSiteManager.existData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM archetype4site WHERE id = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iDataId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("ArchetypeForSiteManager.existData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("ArchetypeForSiteManager.existData: no ArchetypeForSiteData found for id = " + iDataId, _iUserId, Logger.TraceLevel.WARNING) ;
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
			Logger.trace("ArchetypeForSiteManager.existData: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
		
	/**
	  * Update a ArchetypeForSiteData in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * 
	  * @param  dataToUpdate ArchetypeForSiteData to update
	  * 
	  */
	private boolean forceUpdateData(ArchetypeForSiteData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("ArchetypeForSiteManager.forceUpdateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
			
		// Prepare SQL query
		//
		String sQuery = "UPDATE archetype4site SET eventID = ?, siteID = ?, archetypeID = ?" +
				                          " WHERE " +
				                               "id = '" + dataToUpdate.getId() + "'" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("ArchetypeForSiteManager.forceUpdateData: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToUpdate.getEventId()) ;
		_dbConnector.setStatememtInt(2, dataToUpdate.getSiteId()) ;
		_dbConnector.setStatememtInt(3, dataToUpdate.getArchetypeId()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("ArchetypeForSiteManager.forceUpdateData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace("ArchetypeForSiteManager.forceUpdateData: updated data for ArchetypeForSiteData " + dataToUpdate.getId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Check if there is any ArchetypeForSiteData with this Id in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * @param iDataId ID of ArchetypeForSiteData to check
	  * @param foundData ArchetypeForSiteData to get existing information
	  * 
	  */
	public void getArchetypesForSite(final int iSiteId, final int iEventId, ArrayList<ArchetypeData> aArchetypes)
	{
		if ((null == _dbConnector) || (-1 == iSiteId) || (null == aArchetypes))
		{
			Logger.trace("ArchetypeForSiteManager.getArchetypesForSite: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return ;
		}
		
		// Prepare request to get all ArchetypeForSiteData for the specified site 
		//
		String sQuery = "SELECT * FROM archetype4site WHERE siteID = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iSiteId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("ArchetypeForSiteManager.getArchetypesForSite: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("ArchetypeForSiteManager.getArchetypesForSite: no ArchetypeForSiteData found for Siteid = " + iSiteId, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return ;
		}
		
		// Insert in an array all ArchetypeForSiteData that were found 
		//
		ArrayList<ArchetypeForSiteData> aArchetypesForSite = new ArrayList<ArchetypeForSiteData>() ; 
		
		try
		{
	    while (rs.next())
	    {
	    	int iA4SEventId = rs.getInt("eventID") ;
	    	
	    	if (UserRoleDataManager.isValidRole(iEventId, iA4SEventId))
	    	{
	    		ArchetypeForSiteData foundData = new ArchetypeForSiteData() ;
	    		fillDataFromResultSet(rs, foundData) ;
	    		aArchetypesForSite.add(foundData) ;
	    	}
	    }
		} catch (SQLException e)
		{
			Logger.trace("ArchetypeForSiteManager.getArchetypesForSite: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;

		if (aArchetypesForSite.isEmpty())
			return ;
		
		// Get all ArchetypeData that correspond to ArchetypeForSiteData that are not already in aArchetypes    
		//
		ArchetypeDataManager archetypeManager = new ArchetypeDataManager(_iUserId, _dbConnector) ;
		
		for (ArchetypeForSiteData A4S : aArchetypesForSite)
		{
			if (false == ArchetypeDataManager.containsArchetype(A4S.getArchetypeId(), aArchetypes))
			{
				ArchetypeData archetypeData = new ArchetypeData() ;
				if (archetypeManager.existData(A4S.getArchetypeId(), archetypeData))
					aArchetypes.add(archetypeData) ;
			}
		}
	}
	
	/**
	  * Initialize an ArchetypeForSiteData from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData ArchetypeForSiteData to fill
	  * 
	  */
	protected void fillDataFromResultSet(ResultSet rs, ArchetypeForSiteData foundData)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
			foundData.setEventId(rs.getInt("eventID")) ;
			foundData.setSiteId(rs.getInt("siteID")) ;
			foundData.setArchetypeId(rs.getInt("archetypeID")) ;
		} 
		catch (SQLException e) {
			Logger.trace("ArchetypeForSiteManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
	}
}
