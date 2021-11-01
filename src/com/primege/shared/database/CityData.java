package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.primege.shared.GlobalParameters;

/**
 * A CityData object represents a city hosting one or several sites
 * 
 * Created: 19 May 2016
 * Author: PA
 * 
 */
public class CityData implements IsSerializable 
{
	private int    _iId ;
	private int    _iEventId ;
	
	private String _sLabel ;
	private String _sAbbreviation ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public CityData() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor 
	 */
	public CityData(int iID, int iEventID, String sLabel, String sAbbreviation) 
	{
		_iId           = iID ;
		_iEventId      = iEventID ;
		
		_sLabel        = sLabel ;
		_sAbbreviation = sAbbreviation ;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param model CityData to initialize from 
	 */
	public CityData(CityData model) 
	{
		reset() ;
		
		initFromCityData(model) ;
	}
			
	/**
	 * Initialize all information from another CityData
	 * 
	 * @param model CityData to initialize from 
	 */
	public void initFromCityData(CityData model)
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
	  * Determine whether two FormData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  formData FormData to compare with
	  * 
	  */
	public boolean equals(CityData formData)
	{
		if (this == formData) {
			return true ;
		}
		if (null == formData) {
			return false ;
		}
		
		return (_iId      == formData._iId)  &&
					 (_iEventId == formData._iEventId) &&
		       GlobalParameters.areStringsEqual(_sLabel,        formData._sLabel) && 
		       GlobalParameters.areStringsEqual(_sAbbreviation, formData._sAbbreviation) ;
	}

	/**
	  * Determine whether this FormData is exactly similar to another object
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

		final CityData formData = (CityData) o ;

		return equals(formData) ;
	}
}
