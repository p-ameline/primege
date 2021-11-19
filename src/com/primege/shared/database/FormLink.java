package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.primege.shared.GlobalParameters;

/**
 * Link between two forms, in the form of a dated predicate, stored in database
 * 
 * Created: 28 October 2021
 * Author: PA
 * 
 */
public class FormLink implements IsSerializable
{
	public enum LinkStatus { valid, deleted } ;
	
	/** Identifier of database record */
	private int        _iLinkId ;
	
	/** Identifier of the predicate's subject form */
	private int        _iSubjectFormId ;	
	/** Predicate */
	private String     _sPredicate ;
	/** Identifier of the predicate's object form */
	private int        _iObjectFormId ;

	/** Entry time stamp */
	private String     _sEntryDateHour ;
	
	private LinkStatus _iStatus ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public FormLink() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public FormLink(int iID, int iSubjectFormId, final String sPredicate, int iObjectFormId, final String sEntryDateHour, LinkStatus iStatus) 
	{
		_iLinkId        = iID ;

		_iSubjectFormId = iSubjectFormId ;
		_sPredicate     = sPredicate ;
		_iObjectFormId  = iObjectFormId ;

		_sEntryDateHour = sEntryDateHour ;
		
		_iStatus        = iStatus ;
	}
	
	/**
	 * Constructor for a new link to a form being created
	 */
	public FormLink(int iSubjectFormId, final String sPredicate) 
	{
		reset() ;
		
		_iSubjectFormId = iSubjectFormId ;
		_sPredicate     = sPredicate ;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param model FormData to initialize from 
	 */
	public FormLink(FormLink model) 
	{
		reset() ;
		
		initFromFormDataModel(model) ;
	}
			
	/**
	 * Initialize all information from another FormData
	 * 
	 * @param model FormData to initialize from 
	 */
	public void initFromFormDataModel(FormLink model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iLinkId        = model._iLinkId ;
		
		_iSubjectFormId = model._iSubjectFormId ;
		_sPredicate     = model._sPredicate ;
		_iObjectFormId  = model._iObjectFormId ;
		
		_sEntryDateHour = model._sEntryDateHour ;
		
		_iStatus        = model._iStatus ;
	}
		
	/**
	 * Zeros all information
	 */
	public void reset()
	{
		_iLinkId        = -1 ;
		
		_iSubjectFormId = -1 ;
		_sPredicate     = "" ;
		_iObjectFormId  = -1 ;
		
		_sEntryDateHour = "" ;
		
		_iStatus        = LinkStatus.valid ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if ((-1 == _iLinkId)            &&
				(-1 == _iSubjectFormId)     &&
				(-1 == _iObjectFormId)      &&
				(_sEntryDateHour.isEmpty()) &&
				(_sPredicate.isEmpty()))
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
	
	public int getLinkId() {
		return _iLinkId ;
	}
	public void setLinkId(int iLinkId) {
		_iLinkId = iLinkId ;
	}
	
	public int getSubjectFormId() {
		return _iSubjectFormId ;
	}
	public void setSubjectFormId(int iSubjectFormId) {
		_iSubjectFormId = iSubjectFormId ;
	}
	
	public String getPredicate() {
    return _sPredicate ;
  }
  public void setPredicate(final String sPredicate) {
    _sPredicate = sPredicate ;
  }
	
	public int getObjectFormId() {
		return _iObjectFormId ;
	}
	public void setObjectFormId(int iObjectFormId) {
		_iObjectFormId = iObjectFormId ;
	}

	public String getEntryDateHour() {
  	return _sEntryDateHour ;
  }
	public void setEntryDateHour(final String sDate) {
		_sEntryDateHour = sDate ;
  }
	
	public boolean isReallyDeleted() {
  	return LinkStatus.deleted == _iStatus ;
  }
	public void setReallyDeleted() {
		_iStatus = LinkStatus.deleted ;
  }
	
	public boolean isValid() {
  	return LinkStatus.valid == _iStatus ;
  }
	public void setValid() {
		_iStatus = LinkStatus.valid ;
  }
	
	public void setStatus(final LinkStatus iStatus) {
		_iStatus = iStatus ;
	}
	public void setStatusFromString(final String sStatus)
	{
		if      ("1".equals(sStatus))
			_iStatus = LinkStatus.deleted ;
		else
			_iStatus = LinkStatus.valid ;
  }
	public String getStatusAsString()
	{
		if (LinkStatus.deleted == _iStatus)
			return "1" ;
		
		return "0" ;
  }
	
	/**
	  * Determine whether two FormLink are exactly similar
	  * 
	  * @param  other FormLink to compare with
	  *
	  * @return <code>true</code> if all data are the same, <code>false</code> if not
	  */
	public boolean equals(FormLink other)
	{
		if (this == other)
			return true ;
		
		if (null == other)
			return false ;
		
		return (_iLinkId        == other._iLinkId) &&
		       (_iObjectFormId  == other._iObjectFormId) &&
		       GlobalParameters.areStringsEqual(_sEntryDateHour, other._sEntryDateHour) &&
		       GlobalParameters.areStringsEqual(_sPredicate, other._sPredicate) &&
		       (_iSubjectFormId == other._iSubjectFormId) &&
		       (_iStatus        == other._iStatus) ;
	}

	/**
	  * Determine whether this FormLink is exactly similar to another object
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

		final FormLink formData = (FormLink) o ;

		return equals(formData) ;
	}
}
