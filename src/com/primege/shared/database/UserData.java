package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.primege.shared.GlobalParameters;

/**
 * A UserData object represents the basic user information (login, pass, label)
 * 
 * Created: 17 May 2016
 * Author: PA
 * 
 */
public class UserData implements IsSerializable 
{
	private int    _id ;
	private String _sLogin ;
	private String _sPassword ;
	private String _sLabel ;
	private String _sEMail ;

	/**
	 * Default constructor (with zero information)
	 */
	public UserData() {
		reset() ;
	}

	/**
	 * Plain vanilla constructor 
	 */
	public UserData(int iId, String sLogin, String sPassword, String sLabel, String sEMail) 
	{
		_id        = iId ;
		_sLogin    = sLogin ;
		_sPassword = sPassword ;
		_sLabel    = sLabel ; 
		_sEMail    = sEMail ;
	}

	/**
	 * Copy constructor
	 * 
	 * @param model UserData to initialize from 
	 */
	public UserData(UserData model) {
		reset() ;
		initFromUser(model) ;
	}

	/**
	 * Initialize all information from another UserData
	 * 
	 * @param model UserData to initialize from 
	 */
	public void initFromUser(UserData model)
	{
		if (null == model)
			return ;
		
		_id        = model._id ;
		_sLogin    = model._sLogin ;
		_sPassword = model._sPassword ;
		_sLabel    = model._sLabel ;
		_sEMail    = model._sEMail ;
	}

	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		_id        = -1 ;
		_sLogin    = "" ;
		_sPassword = "" ;
		_sLabel    = "" ;
		_sEMail    = "" ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if ((-1 == _id)             &&
				("".equals(_sLogin))    &&
				("".equals(_sPassword)) &&
				("".equals(_sLabel)))
			return true ;
		
		return false ;
	}

	public int getId() {
		return _id ;
	}
	public void setId(int iId) {
		_id = iId ;
	}

	public String getLogin() {
		return _sLogin ;
	}
	public void setLogin(String sLogin) {
		_sLogin = sLogin ;
	}

	public String getPassword() {
		return _sPassword ;
	}
	public void setPassword(String sPassword) {
		_sPassword = sPassword ;
	}
	
	public String getLabel() {
		return _sLabel ;
	}
	public void setLabel(String sLabel) {
		_sLabel = sLabel ;
	}
	
	public String getEMail() {
		return _sEMail ;
	}
	public void setEMail(String sEMail) {
		_sEMail = sEMail ;
	}
	
	/**
	  * Determine whether two UserData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  userData UserData to compare with
	  * 
	  */
	public boolean equals(UserData userData)
	{
		if (this == userData) {
			return true ;
		}
		if (null == userData) {
			return false ;
		}
		
		return (_id  == userData._id)  &&
		       GlobalParameters.areStringsEqual(_sLogin,    userData._sLogin)    && 
		       GlobalParameters.areStringsEqual(_sPassword, userData._sPassword) &&
		       GlobalParameters.areStringsEqual(_sLabel,    userData._sLabel)    &&
		       GlobalParameters.areStringsEqual(_sEMail,    userData._sEMail) ;
	}

	/**
	  * Determine whether this UserData is exactly similar to another object
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
		if ((null == o) || (getClass() != o.getClass())) {
			return false ;
		}

		final UserData userData = (UserData) o ;

		return equals(userData) ;
	}
}
