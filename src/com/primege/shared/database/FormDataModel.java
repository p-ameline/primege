package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.primege.shared.GlobalParameters;

/**
 * Document label of a form stored in database
 * 
 * Created: 16 May 2016
 * Author: PA
 * 
 */
public class FormDataModel implements IsSerializable
{
	public enum FormStatus { valid, draft, deleted } ;
	
	private int        _iFormId ;
	
	/** Action identifier if the form is another form's annotation, <code>""</code> if not */
	private String     _sActionId ;
	
	private int        _iAuthorId ;
	private String     _sEntryDateHour ;
	
	private int        _iArchetypeId ;
	private String     _sRoot ;
	
	private FormStatus _iStatus ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public FormDataModel() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor 
	 */
	public FormDataModel(int iID, final String sActionId, final String sRoot, int iAuthorID, final String sEntryDateHour, int iArchetypeID, FormStatus iStatus) 
	{
		_iFormId        = iID ;
		
		_sActionId      = sActionId ;
		
		_iAuthorId      = iAuthorID ;
		_sEntryDateHour = sEntryDateHour ;
		
		_iArchetypeId   = iArchetypeID ;
		_sRoot          = sRoot ;
		
		_iStatus        = iStatus ; 
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param model FormData to initialize from 
	 */
	public FormDataModel(FormDataModel model) 
	{
		reset() ;
		
		initFromFormDataModel(model) ;
	}
			
	/**
	 * Initialize all information from another FormData
	 * 
	 * @param model FormData to initialize from 
	 */
	public void initFromFormDataModel(FormDataModel model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iFormId        = model._iFormId ;
		_sActionId      = model._sActionId ;
		_iAuthorId      = model._iAuthorId ;
		_sEntryDateHour = model._sEntryDateHour ;
		_iArchetypeId   = model._iArchetypeId ;
		_sRoot          = model._sRoot ;
		_iStatus        = model._iStatus ;
	}
		
	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		_iFormId        = -1 ;
		_sActionId      = "" ;
		_iAuthorId      = -1 ;
		_sEntryDateHour = "" ;
		_iArchetypeId   = -1 ;
		_sRoot          = "" ;
		_iStatus        = FormStatus.valid ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if ((-1 == _iFormId)      &&
				(-1 == _iAuthorId)    &&
				("".equals(_sEntryDateHour)) &&
				("".equals(_sRoot))   &&
				(-1 == _iArchetypeId))
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
	
	public int getFormId() {
		return _iFormId ;
	}
	public void setFormId(int iFormId) {
		_iFormId = iFormId ;
	}
	
	public String getActionId() {
  	return _sActionId ;
  }
	public void setActionId(final String sActionId) {
		_sActionId = sActionId ;
  }
	
	public int getAuthorId() {
		return _iAuthorId ;
	}
	public void setAuthorId(int iAuthorId) {
		_iAuthorId = iAuthorId ;
	}

	public String getEntryDateHour() {
  	return _sEntryDateHour ;
  }
	public void setEntryDateHour(final String sDate) {
		_sEntryDateHour = sDate ;
  }
	
	public int getArchetypeId() {
		return _iArchetypeId ;
	}
	public void setArchetypeId(int iArchetypeId) {
		_iArchetypeId = iArchetypeId ;
	}
	
	public String getRoot() {
    return _sRoot ;
  }
  public void setRoot(final String sRoot) {
    _sRoot = sRoot ;
  }
	
	public boolean isReallyDeleted() {
  	return FormStatus.deleted == _iStatus ;
  }
	public void setReallyDeleted() {
		_iStatus = FormStatus.deleted ;
  }
	
	public boolean isDraft() {
  	return FormStatus.draft == _iStatus ;
  }
	public void setDraft() {
		_iStatus = FormStatus.draft ;
  }
	
	public boolean isValid() {
  	return FormStatus.valid == _iStatus ;
  }
	public void setValid() {
		_iStatus = FormStatus.valid ;
  }
	
	public void setStatus(final FormStatus iStatus) {
		_iStatus = iStatus ;
	}
	public void setStatusFromString(final String sStatus)
	{
		if      ("1".equals(sStatus))
			_iStatus = FormStatus.deleted ;
		else if ("B".equals(sStatus))
			_iStatus = FormStatus.draft ;
		else
			_iStatus = FormStatus.valid ;
  }
	public String getStatusAsString()
	{
		if (FormStatus.deleted == _iStatus)
			return "1" ;
		if (FormStatus.draft == _iStatus)
			return "B" ;
		
		return "0" ;
  }

	/**
	  * Determine whether two FormData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  formData FormData to compare with
	  * 
	  */
	public boolean equals(FormDataModel formData)
	{
		if (this == formData) {
			return true ;
		}
		if (null == formData) {
			return false ;
		}
		
		return (_iFormId      == formData._iFormId)      &&
		       (_iAuthorId    == formData._iAuthorId)    &&
		       GlobalParameters.areStringsEqual(_sActionId, formData._sActionId) &&
		       GlobalParameters.areStringsEqual(_sEntryDateHour, formData._sEntryDateHour) &&
		       GlobalParameters.areStringsEqual(_sRoot, formData._sRoot) &&
		       (_iArchetypeId == formData._iArchetypeId) &&
		       (_iStatus      == formData._iStatus) ;
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

		final FormDataModel formData = (FormDataModel) o ;

		return equals(formData) ;
	}
}
