package com.primege.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.SiteData;

/** 
 * Object in charge of Read/Write operations in the <code>site</code> table 
 *   
 */
public class SiteDataManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public SiteDataManager(int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}
	
	/**
	  * Insert an SiteData object in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToInsert SiteData to be inserted
	  * 
	  */
	public boolean insertData(SiteData dataToInsert)
	{
		if ((null == _dbConnector) || (null == dataToInsert))
			return false ;
		
		String sQuery = "INSERT INTO site (eventID, siteLabel, siteAbrev) VALUES (?, ?, ?)" ;
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("SiteDataManager.insertData: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToInsert.getEventId()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getLabel()) ;
		_dbConnector.setStatememtString(3, dataToInsert.getAbbreviation()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("SiteDataManager.insertData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
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
				Logger.trace("SiteDataManager.insertData: cannot get Id after query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace("SiteDataManager.insertData: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
    }
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		Logger.trace("SiteDataManager.insertData: user " + _iUserId + " successfuly recorded site " + iSiteDataId + " for event " + dataToInsert.getEventId(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a SiteData in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToUpdate SiteData to be updated
	  * 
	  */
	public boolean updateData(SiteData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("SiteDataManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		SiteData foundData = new SiteData() ;
		if (false == existData(dataToUpdate.getId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("SiteDataManager.updateData: SiteData to update (id = " + dataToUpdate.getId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any SiteData with this Id in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * @param iDataId ID of SiteData to check
	  * @param foundData SiteData to get existing information
	  * 
	  */
	private boolean existData(int iDataId, SiteData foundData)
	{
		if ((null == _dbConnector) || (-1 == iDataId) || (null == foundData))
		{
			Logger.trace("SiteDataManager.existData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM site WHERE id = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iDataId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("SiteDataManager.existData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("SiteDataManager.existData: no SiteData found for id = " + iDataId, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		try
		{
	    if (rs.next())
	    {
	    	foundData.setId(rs.getInt("id")) ;
	    	foundData.setEventId(rs.getInt("eventID")) ;
	    	foundData.setLabel(rs.getString("siteLabel")) ;
	    	foundData.setAbbreviation(rs.getString("siteAbrev")) ;
	    	
	    	_dbConnector.closeResultSet() ;
	    	_dbConnector.closePreparedStatement() ;
	    	
	    	return true ;	    	
	    }
		} catch (SQLException e)
		{
			Logger.trace("SiteDataManager.existData: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
		
	/**
	  * Update a SiteData in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * @param  dataToUpdate SiteData to update
	  * 
	  */
	private boolean forceUpdateData(SiteData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("SiteDataManager.forceUpdateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
			
		// Prepare SQL query
		//
		String sQuery = "UPDATE site SET eventID = ?, siteLabel = ?, siteAbrev = ?" +
				                          " WHERE " +
				                               "id = '" + dataToUpdate.getId() + "'" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("SiteDataManager.forceUpdateData: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToUpdate.getEventId()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getLabel()) ;
		_dbConnector.setStatememtString(3, dataToUpdate.getAbbreviation()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("SiteDataManager.forceUpdateData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace("SiteDataManager.forceUpdateData: updated data for SiteData " + dataToUpdate.getId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Fill a structure with all the different sites for a given event 
	  * 
	  * @param iUserId  ID of user
	  * @param aCities  SiteData array to fill
	  * @param iEventId ID of event to get sites for 
	  * 
	  */
	public void fillSitesForEvent(int iUserID, ArrayList<SiteData> aSites, int iEventId)
	{
		if ((null == _dbConnector) || (-1 == iEventId) || (null == aSites))
		{
			Logger.trace("SiteDataManager.fillCitiesForEvent: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return ;
		}
		
		String sQuery = "SELECT * FROM site WHERE eventID = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iEventId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("SiteDataManager.fillCitiesForEvent: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("SiteDataManager.fillCitiesForEvent: no site found for event " + iEventId, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return ;
		}
		
		try
		{
	    while (rs.next())
	    {
	    	SiteData foundData = new SiteData() ;
	    	fillDataFromResultSet(rs, foundData) ;
	    	aSites.add(foundData) ;
	    }
		} catch (SQLException e)
		{
			Logger.trace("SiteDataManager.fillCitiesForEvent: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return ;
	}
	
	/**
	  * Initialize a SiteData from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData SiteData to fill
	  * 
	  */
	protected void fillDataFromResultSet(ResultSet rs, SiteData foundData)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
			foundData.setEventId(rs.getInt("eventID")) ;
			foundData.setLabel(rs.getString("siteLabel")) ;
			foundData.setAbbreviation(rs.getString("siteAbrev")) ;
		} 
		catch (SQLException e) {
			Logger.trace("SiteDataManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
	}
}
