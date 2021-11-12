package com.primege.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.FormData;

/** 
 * Object in charge of Read/Write operations in the <code>form</code> table 
 *   
 */
public class FormDataManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public FormDataManager(int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}
	
	/**
	  * Insert a FormData object in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToInsert FormData to be inserted
	  * 
	  */
	public boolean insertData(final FormData dataToInsert)
	{
		String sFctName = "FormDataManager.insertData" ;
		
		if ((null == _dbConnector) || (null == dataToInsert))
			return false ;
		
		String sQuery = "INSERT INTO form (archetypeID, action, eventID, cityID, siteID, formDate, userID, formEntryDate, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)" ;
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToInsert.getArchetypeId()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getActionId()) ;
		_dbConnector.setStatememtInt(3, dataToInsert.getEventId()) ;
		_dbConnector.setStatememtInt(4, dataToInsert.getCityId()) ;
		_dbConnector.setStatememtInt(5, dataToInsert.getSiteId()) ;
		_dbConnector.setStatememtString(6, dataToInsert.getEventDate()) ;
		_dbConnector.setStatememtInt(7, dataToInsert.getAuthorId()) ;
		_dbConnector.setStatememtString(8, dataToInsert.getEntryDateHour()) ;
		_dbConnector.setStatememtString(9, dataToInsert.getStatusAsString()) ;
		
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
				dataToInsert.setFormId(iDataId) ;
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
		
		Logger.trace(sFctName + ": user " + _iUserId + " successfuly recorded form " + iDataId, _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a FormData in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToUpdate FormData to be updated
	  * 
	  */
	public boolean updateData(FormData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("FormDataManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		FormData foundData = new FormData() ;
		if (false == existData(dataToUpdate.getFormId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("FormDataManager.updateData: FormData to update (id = " + dataToUpdate.getFormId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any FormData with this Id in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * @param iDataId ID of FormData to check
	  * @param foundData FormData to get existing information
	  * 
	  */
	public boolean existData(int iDataId, FormData foundData)
	{
		String sFctName = "FormDataManager.existData" ;
		
		if ((null == _dbConnector) || (-1 == iDataId) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM form WHERE id = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iDataId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no FormData found for id = " + iDataId, _iUserId, Logger.TraceLevel.WARNING) ;
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
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
		
	/**
	  * Update a FormData in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * @param  dataToUpdate FormData to update
	  * 
	  */
	private boolean forceUpdateData(FormData dataToUpdate)
	{
		String sFctName = "FormDataManager.forceUpdateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
			
		// Prepare SQL query
		//
		String sQuery = "UPDATE form SET archetypeID = ?, action = ?, eventID = ?, cityID = ?, siteID = ?, formDate = ?, userID = ?, formEntryDate = ?, deleted = ?" +
				                          " WHERE " +
				                               "id = '" + dataToUpdate.getFormId() + "'" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToUpdate.getArchetypeId()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getActionId()) ;
		_dbConnector.setStatememtInt(3, dataToUpdate.getEventId()) ;
		_dbConnector.setStatememtInt(4, dataToUpdate.getCityId()) ;
		_dbConnector.setStatememtInt(5, dataToUpdate.getSiteId()) ;
		_dbConnector.setStatememtString(6, dataToUpdate.getEventDate()) ;
		_dbConnector.setStatememtInt(7, dataToUpdate.getAuthorId()) ;
		_dbConnector.setStatememtString(8, dataToUpdate.getEntryDateHour()) ;
		_dbConnector.setStatememtString(9, dataToUpdate.getStatusAsString()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for FormData " + dataToUpdate.getFormId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}

	/**
	  * Initialize an FormData from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData FormData to fill
	  * 
	  */
	public void fillDataFromResultSet(final ResultSet rs, FormData foundData)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setFormId(rs.getInt("id")) ;
			foundData.setArchetypeId(rs.getInt("archetypeID")) ;
			foundData.setActionId(rs.getString("action")) ;
			foundData.setEventId(rs.getInt("eventID")) ;
			foundData.setCityId(rs.getInt("cityID")) ;
			foundData.setSiteId(rs.getInt("siteID")) ;
			foundData.setEventDate(rs.getString("formDate")) ;
			foundData.setAuthorId(rs.getInt("userID")) ;
			foundData.setEntryDateHour(rs.getString("formEntryDate")) ;
			foundData.setStatusFromString(rs.getString("deleted")) ;
		} 
		catch (SQLException e) {
			Logger.trace("FormDataManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
	}
}
