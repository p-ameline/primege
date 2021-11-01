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
public class EventData implements IsSerializable 
{
	private int    _iId ;
	
	private String _sLabel ;
	private String _sDateFrom ;
	private String _sDateTo ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public EventData() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor 
	 */
	public EventData(int iID, String sLabel, String sDateFrom, String sDateTo) 
	{
		_iId       = iID ;
		
		_sLabel    = sLabel ;
		_sDateFrom = sDateFrom ;
		_sDateTo   = sDateTo ;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param model EventData to initialize from 
	 */
	public EventData(EventData model) 
	{
		reset() ;
		
		initFromEventData(model) ;
	}
			
	/**
	 * Initialize all information from another EventData
	 * 
	 * @param model EventData to initialize from 
	 */
	public void initFromEventData(EventData model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId       = model._iId ;
		_sLabel    = model._sLabel ;
		_sDateFrom = model._sDateFrom ;
		_sDateTo   = model._sDateTo ;
	}
		
	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		_iId       = -1 ;
		_sLabel    = "" ;
		_sDateFrom = "" ;
		_sDateTo   = "" ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if ((-1 == _iId)            &&
				("".equals(_sLabel))    &&
				("".equals(_sDateFrom)) &&
				("".equals(_sDateTo)))
			return true ;
		
		return false ;
	}

	public int getId() {
		return _iId ;
	}
	public void setId(int iId) {
		_iId = iId ;
	}

	public String getLabel() {
  	return _sLabel ;
  }
	public void setLabel(String sLabel) {
		_sLabel = sLabel ;
  }

	public String getDateFrom() {
  	return _sDateFrom ;
  }
	public void setDateFrom(String sDateFrom) {
		_sDateFrom = sDateFrom ;
  }
	
	public String getDateTo() {
  	return _sDateTo ;
  }
	public void setDateTo(String sDateTo) {
		_sDateTo = sDateTo ;
  }

	/**
	  * Determine whether two EventData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  otherData EventData to compare with
	  * 
	  */
	public boolean equals(EventData otherData)
	{
		if (this == otherData) {
			return true ;
		}
		if (null == otherData) {
			return false ;
		}
		
		return (_iId == otherData._iId)  &&
		       GlobalParameters.areStringsEqual(_sLabel,    otherData._sLabel) &&
		       GlobalParameters.areStringsEqual(_sDateFrom, otherData._sDateFrom) &&
		       GlobalParameters.areStringsEqual(_sDateTo,   otherData._sDateTo) ;
	}

	/**
	  * Determine whether this EventData is exactly similar to another object
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

		final EventData formData = (EventData) o ;

		return equals(formData) ;
	}
}
