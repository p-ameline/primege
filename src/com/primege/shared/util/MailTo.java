package com.primege.shared.util ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.primege.shared.GlobalParameters;

/**
 * A UserData object represents the basic user information (login, pass, label)
 * 
 * Created: 17 May 2016
 * Author: PA
 * 
 */
public class MailTo implements IsSerializable 
{
	public enum RecipientType { Undefined, To, Cc, Bcc } ;
	
	private String        _sAddress ;
	private RecipientType _iRecipientType ;

	/**
	 * Default constructor (with zero information)
	 */
	public MailTo() {
		reset() ;
	}

	/**
	 * Plain vanilla constructor 
	 */
	public MailTo(final String sAddress, final RecipientType iRecipientType) 
	{
		_sAddress       = sAddress ;
		_iRecipientType = iRecipientType ;
	}

	/**
	 * Copy constructor
	 * 
	 * @param model UserData to initialize from 
	 */
	public MailTo(final MailTo model) 
	{
		reset() ;
		initFromModel(model) ;
	}

	/**
	 * Initialize all information from another UserData
	 * 
	 * @param model UserData to initialize from 
	 */
	public void initFromModel(final MailTo model)
	{
		if (null == model)
			return ;
		
		_sAddress       = model._sAddress ;
		_iRecipientType = model._iRecipientType ;
	}

	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		_sAddress       = "" ;
		_iRecipientType = RecipientType.Undefined ;
	}
	
	public String getAddress() {
		return _sAddress ;
	}
	public void setAddress(final String sAddress) {
		_sAddress = sAddress ;
	}

	public RecipientType getRecipientType() {
		return _iRecipientType ;
	}
	public void setRecipientType(final RecipientType iRecipientType) {
		_iRecipientType = iRecipientType ;
	}
	
	/**
	  * Determine whether two UserData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  userData UserData to compare with
	  * 
	  */
	public boolean equals(MailTo userData)
	{
		if (this == userData) {
			return true ;
		}
		if (null == userData) {
			return false ;
		}
		
		return (_iRecipientType == userData._iRecipientType)  &&
		       GlobalParameters.areStringsEqual(_sAddress, userData._sAddress) ;
	}

	/**
	  * Determine whether this object is exactly similar to another object
	  * 
	  * @return <code>true</code> if all data are the same, <code>false</code> if not
	  * 
	  * @param o Object to compare to
	  */
	public boolean equals(final Object o) 
	{
		if (this == o) {
			return true ;
		}
		if ((null == o) || (getClass() != o.getClass())) {
			return false ;
		}

		final MailTo userData = (MailTo) o ;

		return equals(userData) ;
	}
}
