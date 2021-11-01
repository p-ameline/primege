package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.primege.shared.GlobalParameters;

/**
 * A SiteData object represents a site as the most precise location for the event
 * 
 * Created: 19 May 2016
 * Author: PA
 * 
 */
public class SiteData implements IsSerializable 
{
	private int    _iId ;
	private int    _iEventId ;
	
	private String _sLabel ;
	private String _sAbbreviation ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public SiteData() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor 
	 */
	public SiteData(int iID, int iEventID, String sLabel, String sAbbreviation) 
	{
		_iId           = iID ;
		_iEventId      = iEventID ;
		
		_sLabel        = sLabel ;
		_sAbbreviation = sAbbreviation ;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param model SiteData to initialize from 
	 */
	public SiteData(SiteData model) 
	{
		reset() ;
		
		initFromSiteData(model) ;
	}
			
	/**
	 * Initialize all information from another SiteData
	 * 
	 * @param model SiteData to initialize from 
	 */
	public void initFromSiteData(SiteData model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId           = model._iId ;
		_iEventId      = model._iEventId ;
		_sLabel        = model._sLabel ;
		_sAbbreviation = model._sAbbreviation ;
	}
		
	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		_iId           = -1 ;
		_iEventId      = -1 ;
		_sLabel        = "" ;
		_sAbbreviation = "" ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if ((-1 == _iId)         &&
				(-1 == _iEventId)    &&
				("".equals(_sLabel)) &&
				("".equals(_sAbbreviation)))
			return true ;
		
		return false ;
	}

	public int getId() {
		return _iId ;
	}
	public void setId(int iId) {
		_iId = iId ;
	}

	public int getEventId() {
		return _iEventId ;
	}
	public void setEventId(int iEventId) {
		_iEventId = iEventId ;
	}

	public String getLabel() {
  	return _sLabel ;
  }
	public void setLabel(String sLabel) {
		_sLabel = sLabel ;
  }

	public String getAbbreviation() {
  	return _sAbbreviation ;
  }
	public void setAbbreviation(String sAbbreviation) {
		_sAbbreviation = sAbbreviation ;
  }

	/**
	  * Determine whether two SiteData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  otherData SiteData to compare with
	  * 
	  */
	public boolean equals(SiteData otherData)
	{
		if (this == otherData) {
			return true ;
		}
		if (null == otherData) {
			return false ;
		}
		
		return (_iId      == otherData._iId)  &&
					 (_iEventId == otherData._iEventId) &&
		       GlobalParameters.areStringsEqual(_sLabel,        otherData._sLabel) && 
		       GlobalParameters.areStringsEqual(_sAbbreviation, otherData._sAbbreviation) ;
	}

	/**
	  * Determine whether this SiteData is exactly similar to another object
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

		final SiteData formData = (SiteData) o ;

		return equals(formData) ;
	}
}
