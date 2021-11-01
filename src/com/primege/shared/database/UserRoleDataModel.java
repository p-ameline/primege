package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.primege.shared.GlobalParameters;

/**
 * A UserRoleData object establish the role of a user for a given site, city, event (connected or not)
 * 
 * Created: 17 May 2016
 * Author: PA
 * 
 */
public class UserRoleDataModel implements IsSerializable 
{
	protected int    _id ;
	protected int    _iUserId ;
	protected int    _iArcheId ;
	protected String _sRole ;

	/**
	 * Default constructor (with zero information)
	 */
	public UserRoleDataModel() {
		reset4Model() ;
	}

	/**
	 * Plain vanilla constructor 
	 */
	public UserRoleDataModel(int iId, int iUserId, int iArcheId, String sRole) 
	{
		_id       = iId ;
		_iUserId  = iUserId ;
		_iArcheId = iArcheId ;
		_sRole    = sRole ; 
	}

	/**
	 * Copy constructor
	 * 
	 * @param model UserRoleData to initialize from 
	 */
	public UserRoleDataModel(UserRoleDataModel model) 
	{
		reset4Model() ;
		initFromUserRoleModel(model) ;
	}

	/**
	 * Initialize all information from another UserRoleData
	 * 
	 * @param model UserRoleData to initialize from 
	 */
	public void initFromUserRoleModel(UserRoleDataModel model)
	{
		if (null == model)
			return ;
		
		_id       = model._id ;
		_iUserId  = model._iUserId ;
		_iArcheId = model._iArcheId ;
		_sRole    = model._sRole ;
	}

	/**
	 * Zeros all information
	 */
	public void reset4Model() 
	{
		_id       = -1 ;
		_iUserId  = -1 ;
		_iArcheId = -1 ;
		_sRole    = "" ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmptyModel()
	{
		if ((-1 == _id)       &&
				(-1 == _iUserId)  &&
				(-1 == _iArcheId) &&
				("".equals(_sRole)))
			return true ;
		
		return false ;
	}

	public int getId() {
		return _id ;
	}
	public void setId(int iId) {
		_id = iId ;
	}
	
	public int getUserId() {
		return _iUserId ;
	}
	public void setUserId(int iUserId) {
		_iUserId = iUserId ;
	}
		
	public int getArchetypeId() {
		return _iArcheId ;
	}
	public void setArchetypeId(int iArcheId) {
		_iArcheId = iArcheId ;
	}
	
	public String getUserRole() {
		return _sRole ;
	}
	public void setUserRole(String sUserRole) {
		_sRole = sUserRole ;
	}
	
	/**
	  * Determine whether two UserRoleData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  user4cityData UserRoleData to compare with
	  * 
	  */
	public boolean equals(UserRoleDataModel userRoleData)
	{
		if (this == userRoleData) {
			return true ;
		}
		if (null == userRoleData) {
			return false ;
		}
		
		return (_id       == userRoleData._id)       &&
				   (_iUserId  == userRoleData._iUserId)  &&
				   (_iArcheId == userRoleData._iArcheId) &&
				   GlobalParameters.areStringsEqual(_sRole, userRoleData._sRole)  ;
	}

	/**
	  * Determine whether this UserRoleData is exactly similar to another object
	  * 
	  * @return true if all data are the same, false if not
	  * @param o Object to compare with
	  * 
	  */
	public boolean equals(Object o) 
	{
		if (this == o) {
			return true ;
		}
		if ((null == o) || (getClass() != o.getClass())) {
			return false ;
		}

		final UserRoleDataModel userRoleData = (UserRoleDataModel) o ;

		return equals(userRoleData) ;
	}
}
