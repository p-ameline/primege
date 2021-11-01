package com.primege.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.FormLink;

/** 
 * Object in charge of Read/Write operations in the <code>formLink</code> table 
 *   
 */
public class FormLinkManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public FormLinkManager(int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}
	
	/**
	  * Insert a {@link FormLink} object in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToInsert FormLink to be inserted
	  * 
	  */
	public boolean insertData(final FormLink dataToInsert)
	{
		String sFctName = "FormLinkManager.insertData" ;
		
		if ((null == _dbConnector) || (null == dataToInsert))
			return false ;
		
		String sQuery = "INSERT INTO formLink (subjectFormID, predicate, objectFormID, entryDate) VALUES (?, ?, ?, ?)" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToInsert.getSubjectFormId()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getPredicate()) ;
		_dbConnector.setStatememtInt(3, dataToInsert.getObjectFormId()) ;
		_dbConnector.setStatememtString(4, dataToInsert.getEntryDateHour()) ;
		
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
				dataToInsert.setLinkId(iDataId) ;
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
		
		Logger.trace(sFctName + ": user " + _iUserId + " successfuly recorded form link " + iDataId, _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a {@link FormLink} in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToUpdate FormLink to be updated
	  * 
	  */
	public boolean updateData(FormLink dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("FormLinkManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		FormLink foundData = new FormLink() ;
		if (false == existData(dataToUpdate.getLinkId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("FormLinkManager.updateData: FormLink to update (id = " + dataToUpdate.getLinkId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any {@link FormLink} with this Id in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * @param iDataId ID of FormLink to check
	  * @param foundData FormLink to get existing information
	  * 
	  */
	private boolean existData(int iDataId, FormLink foundData)
	{
		String sFctName = "FormLinkManager.existData" ;
		
		if ((null == _dbConnector) || (-1 == iDataId) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM formLink WHERE id = ?" ;
		
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
			Logger.trace(sFctName + ": no FormLink found for id = " + iDataId, _iUserId, Logger.TraceLevel.WARNING) ;
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
	  * Check if there is any {@link FormLink} with this Id as subject in database and, if true get its content
	  * 
	  * @param iFormId ID of Form to get links it is the subject of
	  * @param aLinks  List to get filled with found links
	  * 
	  * @return <code>true</code> if found, <code>false</code> if not
	  */
	public boolean getLinksForFormAsSubject(int iFormId, ArrayList<FormLink> aLinks)
	{
		String sFctName = "FormLinkManager.getLinksForFormAsSubject" ;
		
		if ((null == _dbConnector) || (-1 == iFormId))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM formLink WHERE subjectFormID = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iFormId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no FormLink found for form " + iFormId + " as subject", _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return true ;
		}
		
		int iNbLinks = 0 ;

		try
		{
			while (rs.next())
			{
				FormLink link = new FormLink() ;
				fillDataFromResultSet(rs, link) ;
				aLinks.add(link) ;

				iNbLinks++ ;
			}
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		Logger.trace(sFctName + ": " + iNbLinks + " FormLink found for form = " + iFormId, _iUserId, Logger.TraceLevel.WARNING) ;
		
		return true ;
	}
		
	/**
	  * Update a {@link FormLink} in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * @param  dataToUpdate FormLink to update
	  * 
	  */
	private boolean forceUpdateData(FormLink dataToUpdate)
	{
		String sFctName = "FormLinkManager.forceUpdateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
			
		// Prepare SQL query
		//
		String sQuery = "UPDATE formLink SET subjectFormID = ?, predicate = ?, objectFormID = ?, entryDate = ?" +
				                          " WHERE " +
				                               "id = '" + dataToUpdate.getLinkId() + "'" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToUpdate.getSubjectFormId()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getPredicate()) ;
		_dbConnector.setStatememtInt(3, dataToUpdate.getObjectFormId()) ;
		_dbConnector.setStatememtString(4, dataToUpdate.getEntryDateHour()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for FormLink " + dataToUpdate.getLinkId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}

	/**
	  * Initialize an {@link FormLink} from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData FormLink to fill
	  * 
	  */
	public void fillDataFromResultSet(final ResultSet rs, FormLink foundData)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setLinkId(rs.getInt("id")) ;
			foundData.setSubjectFormId(rs.getInt("subjectFormID")) ;
			foundData.setPredicate(rs.getString("predicate")) ;
			foundData.setObjectFormId(rs.getInt("objectFormID")) ;
			foundData.setEntryDateHour(rs.getString("entryDate")) ;
		} 
		catch (SQLException e) {
			Logger.trace("FormLinkManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
	}
}
