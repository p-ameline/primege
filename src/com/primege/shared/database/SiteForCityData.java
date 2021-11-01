package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * A SiteForCityData object represents the existence of a site inside a city
 * 
 * Created: 19 May 2016
 * Author: PA
 * 
 */
public class SiteForCityData implements IsSerializable 
{
	private int _iId ;
	private int _iSiteId ;
	private int _iCityId ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public SiteForCityData() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor 
	 */
	public SiteForCityData(int iID, int iSiteID, int iCityID) 
	{
		_iId     = iID ;
		_iSiteId = iSiteID ;		
		_iCityId = iCityID ;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param model SiteData to initialize from 
	 */
	public SiteForCityData(SiteForCityData model) 
	{
		reset() ;
		
		initFromSiteForCityData(model) ;
	}
			
	/**
	 * Initialize all information from another SiteForCityData
	 * 
	 * @param model SiteForCityData to initialize from 
	 */
	public void initFromSiteForCityData(SiteForCityData model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId     = model._iId ;
		_iSiteId = model._iSiteId ;
		_iCityId = model._iCityId ;
	}
		
	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		_iId     = -1 ;
		_iSiteId = -1 ;
		_iCityId = -1 ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if ((-1 == _iId)     &&
				(-1 == _iSiteId) &&
				(-1 == _iCityId))
			return true ;
		
		return false ;
	}

	public int getId() {
		return _iId ;
	}
	public void setId(int iId) {
		_iId = iId ;
	}

	public int getSiteId() {
		return _iSiteId ;
	}
	public void setSiteId(int iSiteId) {
		_iSiteId = iSiteId ;
	}

	public int getCityId() {
		return _iCityId ;
	}
	public void setCityId(int iCityId) {
		_iCityId = iCityId ;
	}

	/**
	  * Determine whether two SiteForCityData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  otherData SiteForCityData to compare with
	  * 
	  */
	public boolean equals(SiteForCityData otherData)
	{
		if (this == otherData) {
			return true ;
		}
		if (null == otherData) {
			return false ;
		}
		
		return (_iId     == otherData._iId)     &&
					 (_iSiteId == otherData._iSiteId) &&
					 (_iCityId == otherData._iCityId) ;
	}

	/**
	  * Determine whether this SiteForCityData is exactly similar to another object
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
		if (null == o || getClass() != o.getClass()) {
			return false;
		}

		final SiteForCityData formData = (SiteForCityData) o ;

		return equals(formData) ;
	}
}
