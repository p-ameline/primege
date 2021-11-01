package com.primege.client.widgets;

/**
 * Methods all control must implement
 */
public interface ControlModelCore
{
	public boolean      isSingleData() ;
	
	public ControlBase  getControlBase() ;
	
	public void         setInitFromPrev(final boolean bInitFromPrev) ;
	public boolean      getInitFromPrev() ;
}
