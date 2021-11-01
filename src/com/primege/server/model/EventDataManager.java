package com.primege.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.EventData;

/** 
 * Object in charge of Read/Write operations in the <code>event</code> table 
 *   
 */
public class EventDataManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public EventDataManager(int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}
	
	/**
	  * Insert a EventData object in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToInsert EventData to be inserted
	  * 
	  */
	public boolean insertData(EventData dataToInsert)
	{
		if ((null == _dbConnector) || (null == dataToInsert))
			return false ;
		
		String sQuery = "INSERT INTO event (eventLabel, eventDateFrom, eventDateTo) VALUES (?, ?, ?)" ;
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("EventDataManager.insertData: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToInsert.getLabel()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getDateFrom()) ;
		_dbConnector.setStatememtString(3, dataToInsert.getDateTo()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("EventDataManager.insertData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
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
				Logger.trace("EventDataManager.insertData: cannot get Id after query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace("EventDataManager.insertData: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
    }
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		Logger.trace("EventDataManager.insertData: event " + _iUserId + " successfuly recorded event " + iDataId, _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a EventData in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToUpdate EventData to be updated
	  * 
	  */
	public boolean updateData(EventData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("EventDataManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		EventData foundData = new EventData() ;
		if (false == existData(dataToUpdate.getId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("EventDataManager.updateData: EventData to update (id = " + dataToUpdate.getId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any EventData with this Id in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * @param iDataId ID of EventData to check
	  * @param foundData EventData to get existing information
	  * 
	  */
	private boolean existData(int iDataId, EventData foundData)
	{
		if ((null == _dbConnector) || (-1 == iDataId) || (null == foundData))
		{
			Logger.trace("EventDataManager.existData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM event WHERE id = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iDataId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("EventDataManager.existData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("EventDataManager.existData: no EventData found for id = " + iDataId, _iUserId, Logger.TraceLevel.WARNING) ;
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
			Logger.trace("EventDataManager.existData: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
		
	/**
	  * Update an EventData in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * @param  dataToUpdate EventData to update
	  * 
	  */
	private boolean forceUpdateData(EventData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("EventDataManager.forceUpdateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
			
		// Prepare SQL query
		//
		String sQuery = "UPDATE event SET eventLabel = ?, eventDateFrom = ?, eventDateTo = ?" +
				                          " WHERE " +
				                               "id = '" + dataToUpdate.getId() + "'" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("EventDataManager.forceUpdateData: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToUpdate.getLabel()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getDateFrom()) ;
		_dbConnector.setStatememtString(3, dataToUpdate.getDateTo()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("EventDataManager.forceUpdateData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace("EventDataManager.forceUpdateData: updated data for EventData " + dataToUpdate.getId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Check if there is any EventData with this Id in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * @param iDataId ID of EventData to check
	  * @param foundData EventData to get existing information
	  * 
	  */
	public boolean getCurrentEvent(EventData foundData)
	{
		if ((null == _dbConnector) || (null == foundData))
		{
			Logger.trace("EventDataManager.getCurrentEvent: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Get current date
		//
		String sNow = getDateNow() ;
		
		// Prepare query
		//
		String sQuery = "SELECT * FROM event WHERE eventDateFrom <= ? AND eventDateTo >= ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtString(1, sNow) ;
		_dbConnector.setStatememtString(2, sNow) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("EventDataManager.existData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("EventDataManager.existData: no EventData found for date = " + sNow, _iUserId, Logger.TraceLevel.WARNING) ;
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
			Logger.trace("EventDataManager.existData: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		Logger.trace("EventDataManager.existData: no EventData found for date = " + sNow, _iUserId, Logger.TraceLevel.WARNING) ;
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
	
	/**
	  * Initialize an EventData from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData EventData to fill
	  * 
	  */
	protected void fillDataFromResultSet(ResultSet rs, EventData foundData)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
			foundData.setLabel(rs.getString("eventLabel")) ;
			foundData.setDateFrom(rs.getString("eventDateFrom")) ;
			foundData.setDateTo(rs.getString("eventDateTo")) ;
		} 
		catch (SQLException e) {
			Logger.trace("EventDataManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
	}
	
	/**
	  * Return current time as a YYYYMMDD string 
	  * 
	  * @return A String in the YYYYMMDD format 
	  * 
	  */
	@SuppressWarnings("deprecation")
	protected String getDateNow()
	{
		Date tNow = new Date() ;
		
		int iYear  = tNow.getYear() + 1900 ;
		int iMonth = tNow.getMonth() + 1 ;
		int iDay   = tNow.getDate() ;
		
		return intToString(iYear, 4) + intToString(iMonth, 2) + intToString(iDay, 2) ;
	}
	
	/**
	 * Build a base 10 string of fixed size   
	 * 
	 * @param iSize size of resulting string
	 * @return A string that represents the value, or "" if any problem 
	 * 
	 **/
	public String intToString(int iValue, int iSize)
	{
		if (0 == iSize)
			return "" ;
		
		Integer intValue = new Integer(iValue) ;
		String sValue = intValue.toString() ;
		
		return setStringToSize(sValue, iSize) ;
	}
	
	/**
	 * Adapt a string for a fixed size   
	 * 
	 * @param sEntry string to be adapted
	 * @param iSize size of resulting string
	 * @return A string that represents the adapted string, or "" if any problem 
	 * 
	 **/
	public String setStringToSize(final String sEntry, final int iSize)
	{
		if (0 == iSize)
			return "" ;
		
		int iStrSize = sEntry.length() ;
		
		if ((iSize == iStrSize) || (-1 == iSize)) 
			return sEntry ;
		
		if (iSize < iStrSize)
			return "" ;
		
		String sResult = sEntry ;
		
		int iNbZero = iSize - iStrSize ; 
		for (int i = 0 ; i < iNbZero; i++)
			sResult = "0" + sResult ;
		
		return sResult ;
	}
}
