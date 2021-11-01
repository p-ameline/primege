package com.primege.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetArchetypeAction implements Action<GetArchetypeResult> 
{
	private int _iUserId ;
	private int _iArchetypeId ;

	/**
	 * Plain vanilla constructor 
	 */
	public GetArchetypeAction(final int iArchetypeId, final int iUserId) 
	{
		super() ;

		_iUserId      = iUserId ;
		_iArchetypeId = iArchetypeId ;
	}

	/**
	 * Default constructor (with zero information)
	 */
  public GetArchetypeAction() 
	{
  	super() ;

  	_iUserId      = -1 ;
  	_iArchetypeId = -1 ;
	}

  public int getUserId() {
		return _iUserId ;
	}
  
  public int getArchetypeId() {
		return _iArchetypeId ;
	}
}
