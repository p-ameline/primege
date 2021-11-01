package com.primege.client.util;

/**
 * Referencing a dashboard's column for a given var 
 * 
 */
public class DashboardCol
{   
	protected int    _iCityId ;
  protected String _sDate ;
  protected int    _iCol ;
  
  /**
   * Default Constructor
   *
   */
  public DashboardCol(final int iCityId, final String sDate, final int iCol)
  {
  	_iCityId = iCityId ;
  	
    if (null == sDate)
    	_sDate = "" ;
    else
    	_sDate = sDate ;
    
    _iCol = iCol ;    
  }

  /**
   * Copy Constructor
   *
   */
  public DashboardCol(final DashboardCol model)
  {
  	_iCityId = model._iCityId ; 	
    _sDate   = model._sDate ;
    _iCol    = model._iCol ;    
  }
  
  public int getCityId() {
  	return _iCityId ;
  }
  
  public String getDate() {
  	return _sDate ;
  }
  
  public int getCol() {
  	return _iCol ;
  }
}
