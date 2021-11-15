package com.primege.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.FormDataData;
import com.primege.shared.model.FormBlockModel;

/** 
 * Object in charge of Read/Write operations in the <code>formData</code> table 
 *   
 */
public class FormInformationManager  
{	
	protected final DBConnector     _dbConnector ;
	protected final int             _iUserId ;
	
	protected final String          _sTableName ;
	protected final String          _sReferenceName ;
	
	/**
	 * Constructor 
	 */
	public FormInformationManager(int iUserId, final DBConnector dbConnector)
	{
		_dbConnector     = dbConnector ;
		_iUserId         = iUserId ;
		
		_sTableName     = "formData" ;
		_sReferenceName = "formID" ;
	}
	
	/**
	  * Insert a FormDataData object in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToInsert FormDataData to be inserted
	  * 
	  */
	public boolean insertData(FormDataData dataToInsert)
	{
		String sFctName = "FormInformationManager.insertData" ;
		
		if ((null == _dbConnector) || (null == dataToInsert))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "INSERT INTO " + _sTableName + " (" + _sReferenceName + ", dataPath, dataValue, dataUnit) VALUES (?, ?, ?, ?)" ;
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToInsert.getFormId()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getPath()) ;
		_dbConnector.setStatememtString(3, dataToInsert.getValue()) ;
		_dbConnector.setStatememtString(4, dataToInsert.getUnit()) ;
		
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
		
		Logger.trace(sFctName + ": formData " + _iUserId + " successfuly recorded formData " + iDataId, _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a FormDataData in database
	  * 
	  * @return true if successful, false if not
	  * @param dataToUpdate FormDataData to be updated
	  * 
	  */
	public boolean updateData(FormDataData dataToUpdate)
	{
		String sFctName = "FormInformationManager.updateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		FormDataData foundData = new FormDataData() ;
		if (false == existData(dataToUpdate.getId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace(sFctName + ": FormDataData to update (id = " + dataToUpdate.getId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any FormDataData with this Id in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * @param iDataId ID of FormDataData to check
	  * @param foundData FormDataData to get existing information
	  * 
	  */
	private boolean existData(int iDataId, FormDataData foundData)
	{
		String sFctName = "FormInformationManager.existData" ;
		
		if ((null == _dbConnector) || (-1 == iDataId) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM " + _sTableName + " WHERE id = ?" ;
		
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
			Logger.trace(sFctName + ": no FormDataData found for id = " + iDataId, _iUserId, Logger.TraceLevel.WARNING) ;
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
	  * Update an FormDataData in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * @param  dataToUpdate FormDataData to update
	  * 
	  */
	private boolean forceUpdateData(FormDataData dataToUpdate)
	{
		String sFctName = "FormInformationManager.forceUpdateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
			
		// Prepare SQL query
		//
		String sQuery = "UPDATE " + _sTableName + " SET " + _sReferenceName + " = ?, dataPath = ?, dataValue = ?, dataUnit = ?" +
				                          " WHERE " +
				                               "id = '" + dataToUpdate.getId() + "'" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToUpdate.getFormId()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getPath()) ;
		_dbConnector.setStatememtString(3, dataToUpdate.getValue()) ;
		_dbConnector.setStatememtString(4, dataToUpdate.getUnit()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for FormDataData " + dataToUpdate.getId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/** 
	 * Get information from table formData for a given form
	 * 
	 * @param  iFormId form's unique identifier
	 * @param  form    FormBlockModel to be completed by information from database
	 * @return <code>true</code> if all went well, <code>false</code> if not
	 */
	public boolean loadFormData(int iFormId, FormBlockModel<FormDataData> form) 
	{
		String sFctName = "FormInformationManager.loadFormData" ;
		
		if (null == form)
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM " + _sTableName + " WHERE formID = ?" ;

		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iFormId) ;

		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		int iNbCode = 0 ;

		ResultSet rs = _dbConnector.getResultSet() ;
		try
		{        
			while (rs.next())
			{
				FormDataData data = new FormDataData() ;
				fillDataFromResultSet(rs, data) ;
				form.addData(data) ;
				
				iNbCode++ ;
			}

			if (0 == iNbCode)
			{
				Logger.trace(sFctName + ": query form data for form " + iFormId + " gave no answer", _iUserId, Logger.TraceLevel.WARNING) ;
				_dbConnector.closeResultSet() ;
				_dbConnector.closePreparedStatement() ;
				return false ;
			}
		}
		catch(SQLException ex)
		{
			Logger.trace(sFctName + ": DBConnector.dbSelectPreparedStatement: executeQuery failed for preparedStatement " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			Logger.trace(sFctName + ": SQLException: " + ex.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
			Logger.trace(sFctName + ": SQLState: " + ex.getSQLState(), _iUserId, Logger.TraceLevel.ERROR) ;
			Logger.trace(sFctName + ": VendorERROR, getTrace(): " +ex.getErrorCode(), _iUserId, Logger.TraceLevel.ERROR) ;        
		}

		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}

	/** 
	 * Remove a record from database
	 * 
	 * @param iInformationId Identifier of record to be deleted
	 *  
	 * @return <code>true</code> if everything went well, <code>false</code> if not
	 */
	public boolean deleteFormData(int iInformationId)
	{
		String sFctName = "FormInformationManager.deleteFormData" ;
		
		if (iInformationId <= 0)
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "DELETE FROM " + _sTableName + " WHERE id = ?" ;
	   
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iInformationId) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed deleting record " + iInformationId, _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		_dbConnector.closePreparedStatement() ;
				
		return true ;
	}
	
	/**
	  * Initialize an FormDataData from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData FormDataData to fill
	  * 
	  */
	protected void fillDataFromResultSet(ResultSet rs, FormDataData foundData)
	{
		String sFctName = "FormInformationManager.fillDataFromResultSet" ;
		
		if ((null == rs) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return ;
		}
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
			foundData.setFormId(rs.getInt("formID")) ;
			foundData.setPath(rs.getString("dataPath")) ;
			foundData.setValue(rs.getString("dataValue")) ;
			foundData.setUnit(rs.getString("dataUnit")) ;
		} 
		catch (SQLException e) {
			Logger.trace(sFctName + ": exception when processing results set: " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
	}
	
	/**
	 * Store or update the set of information for a given form (known from its identifier)
	 * 
	 * @param iFormId      Form identifier
	 * @param aInformation Set of information to store or update
	 * 
	 * @return <code>true</code> if everything went well, <code>false</code> if not
	 */
	public boolean storeInformation(int iFormId, ArrayList<FormDataData> aInformation)
  {
    String sFctName = "FormInformationManager.storeInformation" ;
    
    if ((null == _dbConnector) || (null == aInformation))
    {
      Logger.trace(sFctName + ": Null parameter.", _iUserId, Logger.TraceLevel.ERROR) ;
      return false ;
    }
    
    if (aInformation.isEmpty())
    {
      Logger.trace(sFctName + ": Empty form information array", _iUserId, Logger.TraceLevel.WARNING) ;
      return true ;
    }
    
    for (FormDataData formInformation : aInformation)
    {
      formInformation.setFormId(iFormId) ;
      storeOrUpdateInformation(formInformation) ;
    }
    
    suppressDeletedInformation(iFormId, aInformation) ; 
    
    return true ;
  }

	/**
	 * Store or update an information
	 * 
	 * @param formInformation New or (potentially) updated information
	 * 
	 * @return <code>true</code> if everything went well, <code>false</code> if not
	 */
  protected boolean storeOrUpdateInformation(FormDataData formInformation)
  {
    String sFctName = "FormInformationManager.storeOrUpdateInformation" ;
    
    if (null == formInformation)
    {
      Logger.trace(sFctName + ": Null parameter.", _iUserId, Logger.TraceLevel.ERROR) ;
      return false ;
    }

    boolean bDataSaved ;

    if (formInformation.getId() == -1)
      bDataSaved = insertData(formInformation) ;
    else
      bDataSaved = updateData(formInformation) ;

    return bDataSaved ;
  }

  /** 
   * Remove all information that appear in database and are no longer in FormDataData array 
   * 
   * @param  iContactId  Contact identifier 
   * @param  soapBasket  Basket array
   * 
   * @return <code>true</code> if everything went well, <code>false</code> if not
   */
  private boolean suppressDeletedInformation(int iFormId, ArrayList<FormDataData> aInformation)
  {
    String sFctName = "FormInformationManager.suppressDeletedInformation" ;
    
    if (null == aInformation)
    {
      Logger.trace(sFctName + ": Null parameter.", _iUserId, Logger.TraceLevel.ERROR) ;
      return false ;
    }
    
    String sQuery = "SELECT id FROM formData WHERE formID = ?" ;
    
    _dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
    _dbConnector.setStatememtInt(1, iFormId) ;
    
    if (false == _dbConnector.executePreparedStatement())
    {
      Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
      return false ;
    }
    
    ResultSet rs = _dbConnector.getResultSet() ;
    if (null == rs)
    {
      Logger.trace(sFctName + ": no FormDataData found for form = " + iFormId, _iUserId, Logger.TraceLevel.WARNING) ;
      return false ;
    }

    try
    {
      while (rs.next())
      {
        int iInformationId = rs.getInt("id") ;
        if (false == isInformationInArray(iInformationId, aInformation))
        	deleteFormData(iInformationId) ;
      }
    } catch (SQLException e)
    {
      Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
    }
    
    _dbConnector.closeResultSet() ;
    _dbConnector.closePreparedStatement() ;
    
    return true ;
  }
  
  /**
   * Check if a {@link FormDataData} (known from its identifier) exists in a list
   *  
   * @param iInformationId Identifier of {@link FormDataData} to look for
   * @param aInformation   List of {@link FormDataData} to look into
   * 
   * @return <code>true</code> if found, <code>false</code> if not
   */
  private boolean isInformationInArray(int iInformationId, ArrayList<FormDataData> aInformation)
  {
    if (aInformation.isEmpty())
      return false ;

    for (FormDataData formInformation : aInformation)
      if (formInformation.getId() == iInformationId)
        return true ;

    return false ;
  }
}
