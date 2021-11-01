package com.primege.shared.rpc_util;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to get a list of Flex records from a list of codes 
 */
public class GetDictionnaryListFromCodesAction implements Action<GetDictionnaryListFromCodesResult> 
{
  protected int                      _iUserId ;
	protected ArrayList<SearchElement> _aCodes = new ArrayList<SearchElement>() ;

	/**
	 * Plain vanilla constructor where user identifier is expressed as an <code>int</code>
	 */
	public GetDictionnaryListFromCodesAction(final int iUserId, final ArrayList<SearchElement> aCodes) 
	{
		super() ;
		
		reset() ;
		
		_iUserId = iUserId ;
		
		if (null != aCodes)
		  _aCodes.addAll(aCodes) ;
	}
	
	/**
   * Void constructor for serialization purposes
   */
	@SuppressWarnings("unused")
  public GetDictionnaryListFromCodesAction() 
  {
    super() ;
    
    reset() ;
  }
	
	protected void reset()
	{
	  _iUserId = -1 ;
    
    _aCodes.clear() ;
	}
	
	public int getUserId() {
		return _iUserId ;
	}
	
	public ArrayList<SearchElement> getCodes() {
	  return _aCodes ;
	}
}
