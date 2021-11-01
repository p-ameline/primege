package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * A ArchetypeForSiteData object connects a site with its tracking archetype
 * 
 * Created: 19 May 2016
 * Author: PA
 * 
 */
public class ArchetypeForSiteData implements IsSerializable 
{
	private int _iId ;
	private int _iEventId ;
	private int _iSiteId ;
	private int _iArchetypeId ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public ArchetypeForSiteData() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor 
	 */
	public ArchetypeForSiteData(int iID, int iEventID, int iSiteID, int iArchetypeID) 
	{
		_iId          = iID ;
		_iEventId     = iEventID ;
		_iSiteId      = iSiteID ;		
		_iArchetypeId = iArchetypeID ;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param model SiteData to initialize from 
	 */
	public ArchetypeForSiteData(ArchetypeForSiteData model) 
	{
		reset() ;
		
		initFromArchetypeForSiteData(model) ;
	}
			
	/**
	 * Initialize all information from another ArchetypeForSiteData
	 * 
	 * @param model ArchetypeForSiteData to initialize from 
	 */
	public void initFromArchetypeForSiteData(ArchetypeForSiteData model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId          = model._iId ;
		_iEventId     = model._iEventId ;
		_iSiteId      = model._iSiteId ;
		_iArchetypeId = model._iArchetypeId ;
	}
		
	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		_iId          = -1 ;
		_iEventId     = -1 ;
		_iSiteId      = -1 ;
		_iArchetypeId = -1 ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if ((-1 == _iId)      &&
				(-1 == _iEventId) &&
				(-1 == _iSiteId)  &&
				(-1 == _iArchetypeId))
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
	
	public int getSiteId() {
		return _iSiteId ;
	}
	public void setSiteId(int iSiteId) {
		_iSiteId = iSiteId ;
	}

	public int getArchetypeId() {
		return _iArchetypeId ;
	}
	public void setArchetypeId(int iArchetypeId) {
		_iArchetypeId = iArchetypeId ;
	}

	/**
	  * Determine whether two ArchetypeForSiteData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  otherData ArchetypeForSiteData to compare with
	  * 
	  */
	public boolean equals(ArchetypeForSiteData otherData)
	{
		if (this == otherData) {
			return true ;
		}
		if (null == otherData) {
			return false ;
		}
		
		return (_iId          == otherData._iId)     &&
				   (_iEventId     == otherData._iSiteId) &&
					 (_iSiteId      == otherData._iSiteId) &&
					 (_iArchetypeId == otherData._iArchetypeId) ;
	}

	/**
	  * Determine whether this ArchetypeForSiteData is exactly similar to another object
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

		final ArchetypeForSiteData formData = (ArchetypeForSiteData) o ;

		return equals(formData) ;
	}
}
