package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * A UserRoleData object establish the role of a user for a given site, city, event (connected or not)
 * 
 * Created: 17 May 2016
 * Author: PA
 * 
 */
public class UserRoleData extends UserRoleDataModel implements IsSerializable 
{
	private int    _iEventId ;
	private int    _iCityId ;
	private int    _iSiteId ;

	/**
	 * Default constructor (with zero information)
	 */
	public UserRoleData() {
		reset() ;
	}

	/**
	 * Plain vanilla constructor 
	 */
	public UserRoleData(int iId, int iUserId, int iEventId, int iCityId, int iSiteId, int iArcheId, String sRole) 
	{
		super(iId, iUserId, iArcheId, sRole) ;
		
		_iEventId = iEventId ;
		_iCityId  = iCityId ;
		_iSiteId  = iSiteId ;
	}

	/**
	 * Copy constructor
	 * 
	 * @param model UserRoleData to initialize from 
	 */
	public UserRoleData(UserRoleData model) {
		reset() ;
		initFromUserRole(model) ;
	}

	/**
	 * Initialize all information from another UserRoleData
	 * 
	 * @param model UserRoleData to initialize from 
	 */
	public void initFromUserRole(UserRoleData model)
	{
		if (null == model)
			return ;
		
		initFromUserRoleModel(model) ;
		
		_iEventId = model._iEventId ;
		_iCityId  = model._iCityId ;
		_iSiteId  = model._iSiteId ;
	}

	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		reset4Model() ;
		
		_iEventId = -1 ;
		_iCityId  = -1 ;
		_iSiteId  = -1 ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if (isEmptyModel()    &&
				(-1 == _iEventId) &&
				(-1 == _iCityId)  &&
				(-1 == _iSiteId))
			return true ;
		
		return false ;
	}
	
	public int getEventId() {
		return _iEventId ;
	}
	public void setEventId(int iEventId) {
		_iEventId = iEventId ;
	}
	
	public int getCityId() {
		return _iCityId ;
	}
	public void setCityId(int iCityId) {
		_iCityId = iCityId ;
	}

	public int getSiteId() {
		return _iSiteId ;
	}
	public void setSiteId(int iSiteId) {
		_iSiteId = iSiteId ;
	}
	
	/**
	  * Determine whether two UserRoleData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  user4cityData UserRoleData to compare with
	  * 
	  */
	public boolean equals(UserRoleData userRoleData)
	{
		if (this == userRoleData) {
			return true ;
		}
		if (null == userRoleData) {
			return false ;
		}
		
		UserRoleDataModel model     = (UserRoleDataModel) userRoleData ;
		UserRoleDataModel modelThis = (UserRoleDataModel) this ;
		
		return (modelThis.equals(model)  &&
				   (_iEventId == userRoleData._iEventId) &&
				   (_iCityId  == userRoleData._iCityId)  &&
				   (_iSiteId  == userRoleData._iSiteId))  ;
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

		final UserRoleData userRoleData = (UserRoleData) o ;

		return equals(userRoleData) ;
	}
}
