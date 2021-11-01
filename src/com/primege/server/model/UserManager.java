package com.primege.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.UserData;

/** 
 * Object in charge of Read/Write operations in the <code>user</code> table 
 *   
 */
public class UserManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	protected final boolean     _bUseEMail ;
	
	public UserManager(int iUserId, final DBConnector dbConnector) 
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
		_bUseEMail   = false ;
	}
	
	public UserManager(int iUserId, final DBConnector dbConnector, boolean bUseEMail) 
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
		_bUseEMail   = bUseEMail ;
	}
	
	/**
	  * Insert a UserData in database
	  * 
	  * @return true if successful, false if not
	  * @param user UserData to be inserted
	  * 
	  */
	public boolean insertUser(UserData user)
	{
		if ((null == _dbConnector) || (null == user))
			return false ;
		
		String sQuery = "" ;
		if (_bUseEMail)
			sQuery = "INSERT INTO user (userLogn, userPass, userLabel, email) VALUES (?, ?, ?, ?)" ;
		else
			sQuery = "INSERT INTO user (userLogn, userPass, userLabel) VALUES (?, ?, ?)" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("UserManager.insertUser: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, user.getLogin()) ;
		_dbConnector.setStatememtString(2, user.getPassword()) ;
		_dbConnector.setStatememtString(3, user.getLabel()) ;
		if (_bUseEMail)
			_dbConnector.setStatememtString(4, user.getEMail()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("UserManager.insertUser: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		ResultSet rs = _dbConnector.getResultSet() ;
		try
    {
			if (rs.next())
				user.setId(rs.getInt(1)) ;
			else
				Logger.trace("UserManager.insertUser: cannot get Id after query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace("UserManager.insertUser: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
    }
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		Logger.trace("UserManager.insertUser: user extension " + user.getId() + " successfuly recorded for user " + user.getLabel(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		return true ;
	}
		
	/**
	  * Update a user in database
	  * 
	  * @return true if successful, false if not
	  * @param userToUpdate UserData to be updated
	  * 
	  */
	public boolean updateUser(UserData userToUpdate)
	{
		if ((null == _dbConnector) || (null == userToUpdate))
		{
			Logger.trace("UserManager.updateUser: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Find this user in database
		//
		UserData foundUser = new UserData() ;
		if (false == existUser(userToUpdate.getId(), foundUser))
			return false ;
		
		// If nothing to change, just leave
		//
		if (foundUser.equals(userToUpdate))
		{
			Logger.trace("UserManager.updateUser: user to update (id = " + userToUpdate.getId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateUser(userToUpdate) ;
	}
		
	/**
	  * Check if there is any MedecinExt with this Id in database and, if true get its content
	  * 
	  * @return <code>true</code> if found, <code>false</code> if not
	  * @param iUserId   ID of user to check
	  * @param foundUser user to fill
	  * 
	  */
	public boolean existUser(int iUserId, UserData foundUser)
	{
		if ((null == _dbConnector) || (-1 == iUserId) || (null == foundUser))
		{
			Logger.trace("UserManager.existUser: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM user WHERE id = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iUserId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("UserManager.existUser: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("UserManager.existUser: no MedecinExt found for id = " + iUserId, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		try
		{
	    if (rs.next())
	    {
	    	fillDataFromResultSet(rs, foundUser) ;
	    	
	    	_dbConnector.closeResultSet() ;
	    	_dbConnector.closePreparedStatement() ;
	    	return true ;	    	
	    }
	    else
			{
	    	Logger.trace("UserManager.existUser: nothing found in table user for user " + iUserId, _iUserId, Logger.TraceLevel.WARNING) ;
	    	_dbConnector.closePreparedStatement() ;
				return false ;
			}
		} 
		catch (SQLException e)
		{
			Logger.trace("UserManager.existUser: exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
		
	/**
	  * Update a UserData
	  * 
	  * @return <code>true</code> if update succeeded, <code>false</code> if not
	  * 
	  * @param userToUpdate UserData to update
	  */
	private boolean forceUpdateUser(UserData userToUpdate)
	{
		if ((null == _dbConnector) || (null == userToUpdate))
		{
			Logger.trace("UserManager.forceUpdateUser: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
			
		// Prepare sql query
		//
		String sQuery = "" ;
		if (_bUseEMail)
			sQuery = "UPDATE user SET userLogn = ?, userPass = ?, userLabel = ?, email = ?" +
				                          " WHERE " +
				                               "id = '" + userToUpdate.getId() + "'" ;
		else
			sQuery = "UPDATE user SET userLogn = ?, userPass = ?, userLabel = ?" +
          " WHERE " +
               "id = '" + userToUpdate.getId() + "'" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("UserManager.forceUpdateUser: cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1,  userToUpdate.getLogin()) ;
		_dbConnector.setStatememtString(2,  userToUpdate.getPassword()) ;
		_dbConnector.setStatememtString(3,  userToUpdate.getLabel()) ;
		if (_bUseEMail)
			_dbConnector.setStatememtString(4,  userToUpdate.getEMail()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("UserManager.forceUpdateUser: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace("UserManager.forceUpdateUser: updated medextne for MedecinExt " + userToUpdate.getId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Get all UserData in database
	  * 
	  * @return True if found, else false
	  * @param aUsers Array of UserData to fill with database content
	  * 
	  */
	public boolean getThemAll(ArrayList<UserData> aUsers)
	{
		String sFctName = "UserManager.getThemAll" ;
		
		if ((null == _dbConnector) || (null == aUsers))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM user" ;
		
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
			Logger.trace(sFctName + ": no UserData found", _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		try
		{
	    while (rs.next())
	    {
	    	UserData foundData = new UserData() ;
	    	fillDataFromResultSet(rs, foundData) ;
	    	aUsers.add(foundData) ;	    	
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
	  * Initialize a UserData from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData UserData to fill
	  * 
	  */
	public void fillDataFromResultSet(ResultSet rs, UserData foundData)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		foundData.reset() ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
			foundData.setLogin(rs.getString("userLogn")) ;
			foundData.setPassword(rs.getString("userPass")) ;
			foundData.setLabel(rs.getString("userLabel")) ;
			if (_bUseEMail)
				foundData.setEMail(rs.getString("email")) ;
		} 
		catch (SQLException e) {
			Logger.trace("UserManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
	}
}
