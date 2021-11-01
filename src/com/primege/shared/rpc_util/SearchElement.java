package com.primege.shared.rpc_util;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchElement implements IsSerializable
{
  protected String _sCode ;
  protected String _sLanguage ;
	
  protected int    _iCallbackIndex ;
  
	/**
	 * Void constructor
	 */
	public SearchElement()
	{
		super() ;
		
		_sCode     = "" ;
		_sLanguage = "" ;
		
		_iCallbackIndex = -1 ;
	}

	/**
	 * Plain vanilla constructor
	 */
	public SearchElement(final String sCode, final String sLanguage, int iCallbackIndex)
	{
		super() ;
		
		if (null == sCode)
		  _sCode = "" ;
		else
		  _sCode = sCode ;
		
		if (null == _sLanguage)
		  _sLanguage = "" ;
		else
		  _sLanguage = sLanguage ;
		
		_iCallbackIndex = iCallbackIndex ;
 	}
	
	public String getCode() {
		return _sCode ;
	}
	public void setCode(final String sCode) {
		_sCode = sCode ;
	}
	
	public String getLanguage() {
		return _sLanguage ;
	}
	public void setLanguage(final String sLanguage) {
	  _sLanguage = sLanguage ;
	}

	public int getCallbackIndex() {
    return _iCallbackIndex ;
  }
  public void setCallbackIndex(final int iCallbackIndex) {
    _iCallbackIndex = iCallbackIndex ;
  }
	
  /**
   * Determine whether two objects are exactly similar
   * 
   * @param other Other object to compare to
   * 
   * @return true if all data are the same, false if not
   */
public boolean equals(final SearchElement other)
{
  if (this == other) {
    return true ;
  }
  if (null == other) {
    return false ;
  }
  
  return (_sCode.equals(other._sCode) && 
          _sLanguage.equals(other._sLanguage) &&
          (_iCallbackIndex == other._iCallbackIndex)) ;
}

/**
  * Determine whether an object is exactly similar to this object
  * 
  * designed for ArrayList.contains(Obj) method
  * because by default, contains() uses equals(Obj) method of Obj class for comparison
  * 
  * @param o Generic object to compare to
  * 
  * @return true if all data are the same, false if not
  */
public boolean equals(final Object o) 
{
  if (this == o) {
    return true ;
  }
  if (null == o || getClass() != o.getClass()) {
    return false;
  }

  final SearchElement other = (SearchElement) o ;

  return (this.equals(other)) ;
}
}
