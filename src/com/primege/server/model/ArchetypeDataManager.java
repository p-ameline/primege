package com.primege.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.ArchetypeData;

/** 
 * Object in charge of Read/Write operations in the <code>archetype</code> table 
 *   
 */
public class ArchetypeDataManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public ArchetypeDataManager(int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}
	
	/**
	  * Insert a ArchetypeData object in database
	  * 
	  * @return true if successful, false if not
	  * 
	  * @param dataToInsert ArchetypeData to be inserted
	  * 
	  */
	public boolean insertData(ArchetypeData dataToInsert)
	{
		if ((null == _dbConnector) || (null == dataToInsert))
			return false ;
		
		String sQuery = "INSERT INTO archetype (archLabel, archFile, archType, archRoot) VALUES (?, ?, ?, ?)" ;
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("ArchetypeDataManager.insertSiteData: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToInsert.getLabel()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getFile()) ;
		_dbConnector.setStatememtString(3, dataToInsert.getType()) ;
		_dbConnector.setStatememtString(4, dataToInsert.getRoot()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("ArchetypeDataManager.insertSiteData: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
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
				Logger.trace("ArchetypeDataManager.insertSiteData: cannot get Id after query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace("ArchetypeDataManager.insertSiteData: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
    }
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		Logger.trace("ArchetypeDataManager.insertSiteData: user " + _iUserId + " successfuly recorded archetype " + dataToInsert.getId(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a ArchetypeData in database
	  * 
	  * @return true if successful, false if not
	  * 
	  * @param dataToUpdate ArchetypeData to be updated
	  * 
	  */
	public boolean updateData(ArchetypeData dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("ArchetypeDataManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		ArchetypeData foundData = new ArchetypeData() ;
		if (false == existData(dataToUpdate.getId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("ArchetypeDataManager.updateData: ArchetypeData to update (id = " + dataToUpdate.getId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any ArchetypeData with this Id in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * @param iDataId ID of ArchetypeData to check
	  * @param foundData ArchetypeData to get existing information
	  * 
	  */
	public boolean existData(int iDataId, ArchetypeData foundData)
	{
		String sFctName = "ArchetypeDataManager.existData" ;
		
		if ((null == _dbConnector) || (-1 == iDataId) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM archetype WHERE id = ?" ;
		
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
			Logger.trace(sFctName + ": no ArchetypeData found for id = " + iDataId, _iUserId, Logger.TraceLevel.WARNING) ;
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
	  * Update a ArchetypeData in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * 
	  * @param  dataToUpdate ArchetypeData to update
	  * 
	  */
	private boolean forceUpdateData(ArchetypeData dataToUpdate)
	{
		String sFctName = "ArchetypeDataManager.forceUpdateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
			
		// Prepare SQL query
		//
		String sQuery = "UPDATE archetype SET archLabel = ?, archFile = ?, archType = ?, archRoot = ?" +
				                          " WHERE " +
				                               "id = '" + dataToUpdate.getId() + "'" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToUpdate.getLabel()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getFile()) ;
		_dbConnector.setStatememtString(3, dataToUpdate.getType()) ;
		_dbConnector.setStatememtString(4, dataToUpdate.getRoot()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for ArchetypeData " + dataToUpdate.getId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Get all ArchetypeData in database
	  * 
	  * @return True if found, else false
	  * @param aArchetypes Array of ArchetypeData to fill with database content
	  * 
	  */
	public boolean getThemAll(ArrayList<ArchetypeData> aArchetypes)
	{
		String sFctName = "ArchetypeDataManager.getThemAll" ;
		
		if ((null == _dbConnector) || (null == aArchetypes))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM archetype" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no ArchetypeData found", _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		try
		{
	    while (rs.next())
	    {
	    	ArchetypeData foundData = new ArchetypeData() ;
	    	fillDataFromResultSet(rs, foundData) ;
	    	aArchetypes.add(foundData) ;	    	
	    }
	    
	    _dbConnector.closeResultSet() ;
    	_dbConnector.closePreparedStatement() ;
    	
    	return true ;	
		} 
		catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
	
	/**
	  * Get all ArchetypeData in database
	  * 
	  * @return True if found, else false
	  * @param aArchetypes Array of ArchetypeData to fill with database content
	  * 
	  */
	public boolean getArchetypesForRoot(ArrayList<ArchetypeData> aArchetypes, final String sRoot)
	{
		String sFctName = "ArchetypeDataManager.getArchetypesForRoot" ;
		
		if ((null == _dbConnector) || (null == aArchetypes) || (null == sRoot))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// No root specified, return all genuine archetypes
		//
		if (sRoot.isEmpty())
			return getArchetypesForType(aArchetypes, "F") ;
		
		String sQuery = "SELECT * FROM archetype WHERE archRoot" ;
		String sExt   = " = ?" ;
		
		// Exact query or not?
		//
		String sSearchedTrait = sRoot ;
		if ('*' == sRoot.charAt(0))
		{
			sExt = " LIKE ?" ;
			sSearchedTrait = "%" + sSearchedTrait ;
		}
		int iRootLen = sRoot.length() ;
		if ((iRootLen > 1) && ('*' == sRoot.charAt(iRootLen - 1)))
		{
			sExt = " LIKE ?" ;
			sSearchedTrait += "%" ;
		}
		
		sQuery += sExt ;
			
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, sSearchedTrait) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no ArchetypeData found", _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		try
		{
	    while (rs.next())
	    {
	    	ArchetypeData foundData = new ArchetypeData() ;
	    	fillDataFromResultSet(rs, foundData) ;
	    	aArchetypes.add(foundData) ;	    	
	    }
	    
	    _dbConnector.closeResultSet() ;
   	_dbConnector.closePreparedStatement() ;
   	
   	return true ;	
		} 
		catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
	
	/**
	  * Get all ArchetypeData in database of a given type
	  * 
	  * @param aArchetypes Array of ArchetypeData to fill with database content
	  * @param sType       Type to look for
	  * 
	  * @return True if found, else false
	  */
	public boolean getArchetypesForType(ArrayList<ArchetypeData> aArchetypes, final String sType)
	{
		String sFctName = "ArchetypeDataManager.getArchetypesForType" ;
		
		if ((null == _dbConnector) || (null == aArchetypes) || (null == sType))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		if (sType.isEmpty())
			return getThemAll(aArchetypes) ;
		
		String sQuery = "SELECT * FROM archetype WHERE archType = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, sType) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no ArchetypeData found", _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		try
		{
	    while (rs.next())
	    {
	    	ArchetypeData foundData = new ArchetypeData() ;
	    	fillDataFromResultSet(rs, foundData) ;
	    	aArchetypes.add(foundData) ;	    	
	    }
	    
	    _dbConnector.closeResultSet() ;
  	_dbConnector.closePreparedStatement() ;
  	
  	return true ;	
		} 
		catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
	
	/**
	  * Initialize a ArchetypeData from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData ArchetypeData to fill
	  * 
	  */
	protected void fillDataFromResultSet(ResultSet rs, ArchetypeData foundData)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
    	foundData.setLabel(rs.getString("archLabel")) ;
    	foundData.setFile(rs.getString("archFile")) ;
    	foundData.setType(rs.getString("archType")) ;
    	foundData.setRoot(rs.getString("archRoot")) ;
		} 
		catch (SQLException e) {
			Logger.trace("TraineeDataManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
	}
	
	/**
	  * Check if an array of ArchetypeData already contains a specific Id
	  * 
	  * @return <code>true</code> if found, else <code>false</code>
	  
	  * @param iArchetypeId ID of Archetype to find
	  * @param aArchetypes  Array of ArchetypeData to examine
	  * 
	  */
	public static boolean containsArchetype(final int iArchetypeId, final ArrayList<ArchetypeData> aArchetypes)
	{
		if ((-1 == iArchetypeId) || (null == aArchetypes) || aArchetypes.isEmpty())
			return false ;
		
		for (ArchetypeData archetype : aArchetypes)
			if (archetype.getId() == iArchetypeId)
				return true ;
		
		return false ;
	}
}
