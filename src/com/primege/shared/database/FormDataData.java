package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.primege.shared.GlobalParameters;

/**
 * A FormDataData object represents a stored information inside a form
 * 
 * Created: 16 May 2016
 * Author: PA
 * 
 */
public class FormDataData implements IsSerializable 
{
	protected int     _iId ;
	protected int     _iFormId ;
	
	protected String  _sPath ;
	protected String  _sValue ;
	protected String  _sUnit ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public FormDataData() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor 
	 */
	public FormDataData(final int iID, final int iFormID, final String sPath, final String sValue, final String sUnit) 
	{
		_iId     = iID ;
		_iFormId = iFormID ;
		
		_sPath   = sPath ;
		_sValue  = sValue ;
		_sUnit   = sUnit ;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param model FormData to initialize from 
	 */
	public FormDataData(final FormDataData model) 
	{
		reset() ;
		
		initFromFormData(model) ;
	}
			
	/**
	 * Initialize all information from another FormData
	 * 
	 * @param model FormData to initialize from 
	 */
	public void initFromFormData(final FormDataData model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId     = model._iId ;
		_iFormId = model._iFormId ;
		_sPath   = model._sPath ;
		_sValue  = model._sValue ;
		_sUnit   = model._sUnit ;
	}
		
	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		_iId     = -1 ;
		_iFormId = -1 ;
		_sPath   = "" ;
		_sValue  = "" ;
		_sUnit   = "" ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return <code>true</code> if all data are zeros, <code>false</code> if not
	 */
	public boolean isEmpty()
	{
		if ((-1 == _iId)         &&
				(-1 == _iFormId)     &&
				("".equals(_sPath))  &&
				("".equals(_sValue)) &&
				("".equals(_sUnit)))
			return true ;
		
		return false ;
	}
	
	/**
	 * Check if this object has an empty value
	 * 
	 * @return <code>true</code> if value is "", <code>false</code> if not
	 */
	public boolean hasNoData() {
		return "".equals(_sValue) ;
	}

	public int getId() {
		return _iId ;
	}
	public void setId(final int iId) {
		_iId = iId ;
	}

	public int getFormId() {
		return _iFormId ;
	}
	public void setFormId(final int iFormId) {
		_iFormId = iFormId ;
	}

	public String getPath() {
  	return _sPath ;
  }
	public void setPath(final String sPath) {
		_sPath = sPath ;
  }

	public String getValue() {
  	return _sValue ;
  }
	public void setValue(final String sValue) {
		_sValue = sValue ;
  }

	public String getUnit() {
  	return _sUnit ;
  }
	public void setUnit(final String sUnit) {
		_sUnit = sUnit ;
  }

	/**
	  * Determine whether two FormData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  formData FormData to compare with
	  * 
	  */
	public boolean equals(final FormDataData formData)
	{
		if (this == formData) {
			return true ;
		}
		if (null == formData) {
			return false ;
		}
		
		return (_iId     == formData._iId)  &&
					 (_iFormId == formData._iFormId) &&
		       GlobalParameters.areStringsEqual(_sPath,  formData._sPath) && 
		       GlobalParameters.areStringsEqual(_sValue, formData._sValue) &&
		       GlobalParameters.areStringsEqual(_sUnit,  formData._sUnit) ;
	}

	/**
	  * Determine whether this FormData is exactly similar to another object
	  * 
	  * @return true if all data are the same, false if not
	  * @param o Object to compare with
	  * 
	  */
	public boolean equals(final Object o) 
	{
		if (this == o) {
			return true ;
		}
		if (null == o || getClass() != o.getClass()) {
			return false;
		}

		final FormDataData formData = (FormDataData) o ;

		return equals(formData) ;
	}
}
