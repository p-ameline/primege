package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;

import com.primege.shared.GlobalParameters;

/**
 * A FormData object represents the documentary label of a form stored in database
 * 
 * Created: 16 May 2016
 * Author: PA
 * 
 */
public class FormData extends FormDataModel implements IsSerializable  
{
	private int     _iEventId ;
	private int     _iCityId ;
	private int     _iSiteId ;
	private String  _sEventDate ;
		
	/**
	 * Default constructor (with zero information)
	 */
	public FormData()
	{
		super() ;
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor 
	 */
	public FormData(int iID, final String sActionId, final String sRoot, int iEventID, int iCityID, int iSiteID, String sEventDate, int iAuthorID, String sEntryDateHour, int iArchetypeID, FormStatus iStatus) 
	{
		super(iID, sActionId, sRoot, iAuthorID, sEntryDateHour, iArchetypeID, iStatus) ;
		
		_iEventId       = iEventID ;
		_iCityId        = iCityID ;
		_iSiteId        = iSiteID ;
		_sEventDate     = sEventDate ;		
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param model FormData to initialize from 
	 */
	public FormData(FormData model) 
	{
		reset() ;
		
		initFromFormData(model) ;
	}
			
	/**
	 * Initialize all information from another FormData
	 * 
	 * @param model FormData to initialize from 
	 */
	public void initFromFormData(FormData model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		initFromFormDataModel((FormDataModel) model) ;
		
		_iEventId       = model._iEventId ;
		_iCityId        = model._iCityId ;
		_iSiteId        = model._iSiteId ;
		_sEventDate     = model._sEventDate ;
	}
		
	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		super.reset() ;
		
		_iEventId       = -1 ;
		_iCityId        = -1 ;
		_iSiteId        = -1 ;
		_sEventDate     = "" ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if (super.isEmpty()       &&
				(-1 == _iEventId)     &&
				(-1 == _iCityId)      &&
				(-1 == _iSiteId)      &&
				("".equals(_sEventDate)))
			return true ;
		
		return false ;
	}
	
	/**
	 * Build the dynamic label of this form 
	 * 
	 * @return The label dynamically built from site, city, etc.
	 */
	public String getLabel() 
	{
		return "" ;
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
	
	public String getEventDate() {
  	return _sEventDate ;
  }
	public void setEventDate(String sDate) {
		_sEventDate = sDate ;
  }
	
	/**
	  * Determine whether two FormData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  formData FormData to compare with
	  * 
	  */
	public boolean equals(FormData formData)
	{
		if (this == formData) {
			return true ;
		}
		if (null == formData) {
			return false ;
		}
		
		FormDataModel model     = (FormDataModel) formData ;
		FormDataModel modelThis = (FormDataModel) this ;
		
		return (modelThis.equals(model))  &&
					 (_iEventId == formData._iEventId) &&
					 (_iCityId  == formData._iCityId)  &&
					 (_iSiteId  == formData._iSiteId)  &&
		       GlobalParameters.areStringsEqual(_sEventDate, formData._sEventDate) ;
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

		final FormData formData = (FormData) o ;

		return equals(formData) ;
	}
}
