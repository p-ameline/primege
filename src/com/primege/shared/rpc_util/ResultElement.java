package com.primege.shared.rpc_util;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.primege.shared.database.Dictionary;

public class ResultElement implements IsSerializable
{
  protected Dictionary _flex ;
  protected int        _iCallbackIndex ;
  
	/**
	 * Void constructor
	 */
	public ResultElement()
	{
		super() ;
		
		_flex = null ;
		
		_iCallbackIndex = -1 ;
	}

	/**
	 * Plain vanilla constructor
	 */
	public ResultElement(final Dictionary flex, int iCallbackIndex)
	{
		super() ;
		
		_flex = flex ;
		
		_iCallbackIndex = iCallbackIndex ;
 	}
	
	public Dictionary getFlex() {
		return _flex ;
	}
	public void setFlex(final Dictionary flex) {
	  _flex = flex ;
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
  public boolean equals(final ResultElement other)
  {
    if (this == other) {
      return true ;
    }
    if (null == other) {
      return false ;
    }
    
    if (_iCallbackIndex != other._iCallbackIndex)
      return false ;
    
    if ((null == _flex) && (null != other._flex))
      return false ;
    
    return (_flex.equals(other._flex)) ;
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

    final ResultElement other = (ResultElement) o ;

    return (this.equals(other)) ;
  }
}
