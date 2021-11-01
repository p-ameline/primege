package com.primege.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.CityData;

/** 
 * Object in charge of Read/Write operations in the <code>city</code> table 
 *   
 */
public class CityDataManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public CityDataManager(int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}
	
	/**
	  * Insert a CityData object in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToInsert CityData to be inserted
	  * 
	  */
	public boolean insertData(CityData dataToInsert)
	{
		if ((null == _dbConnector) || (null == dataToInsert))
			return false ;
		
		String sQuery = "INSERT INTO city (eventID, cityLabel, cityAbrev) VALUES (?, ?, ?)" ;
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("CityDataManager.insertData: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
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
			Logger.trace("CityDataManager.insertData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
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
				Logger.trace("CityDataManager.insertData: cannot get Id after query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace("CityDataManager.insertData: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
    }
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		Logger.trace("CityDataManager.insertData: user " + _iUserId + " successfuly recorded city " + iDataId + " for event " + dataToInsert.getEventId(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a CityData in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToUpdate CityData to be updated
	  * 
	  */
	public boolean updateData(CityData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("CityDataManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		CityData foundData = new CityData() ;
		if (false == existData(dataToUpdate.getId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("CityDataManager.updateData: HealthData to update (id = " + dataToUpdate.getId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any CityData with this Id in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * @param iDataId ID of CityData to check
	  * @param foundData CityData to get existing information
	  * 
	  */
	private boolean existData(int iDataId, CityData foundData)
	{
		if ((null == _dbConnector) || (-1 == iDataId) || (null == foundData))
		{
			Logger.trace("CityDataManager.existData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM city WHERE id = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iDataId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("CityDataManager.existData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("CityDataManager.existData: no CityData found for id = " + iDataId, _iUserId, Logger.TraceLevel.WARNING) ;
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
			Logger.trace("CityDataManager.existData: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
		
	/**
	  * Update a CityData in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * @param  dataToUpdate CityData to update
	  * 
	  */
	private boolean forceUpdateData(CityData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("CityDataManager.forceUpdateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
			
		// Prepare SQL query
		//
		String sQuery = "UPDATE city SET eventID = ?, cityLabel = ?, cityAbrev = ?" +
				                          " WHERE " +
				                               "id = '" + dataToUpdate.getId() + "'" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("CityDataManager.forceUpdateData: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
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
			Logger.trace("CityDataManager.forceUpdateData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace("CityDataManager.forceUpdateData: updated data for CityData " + dataToUpdate.getId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Fill a structure with all the different cities for a given event 
	  * 
	  * @param iUserId  ID of user
	  * @param aCities  CityData array to fill
	  * @param iEventId ID of event to get cities for 
	  * 
	  */
	public void fillCitiesForEvent(int iUserID, ArrayList<CityData> aCities, int iEventId)
	{
		if ((null == _dbConnector) || (-1 == iEventId) || (null == aCities))
		{
			Logger.trace("CityDataManager.fillCitiesForEvent: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return ;
		}
		
		String sQuery = "SELECT * FROM city WHERE eventID = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iEventId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("CityDataManager.fillCitiesForEvent: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("CityDataManager.fillCitiesForEvent: no city found for event " + iEventId, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return ;
		}
		
		try
		{
	    while (rs.next())
	    {
	    	CityData foundData = new CityData() ;
	    	fillDataFromResultSet(rs, foundData) ;
	    	aCities.add(foundData) ;
	    }
		} catch (SQLException e)
		{
			Logger.trace("CityDataManager.fillCitiesForEvent: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return ;
	}
	
	/**
	  * Initialize a CityData from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData CityData to fill
	  * 
	  */
	protected void fillDataFromResultSet(ResultSet rs, CityData foundData)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
    	foundData.setEventId(rs.getInt("eventID")) ;
    	foundData.setLabel(rs.getString("cityLabel")) ;
    	foundData.setAbbreviation(rs.getString("cityAbrev")) ;
		} 
		catch (SQLException e) {
			Logger.trace("CityDataManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
	}
}
