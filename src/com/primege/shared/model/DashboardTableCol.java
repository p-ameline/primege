package com.primege.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Referencing a dashboard's column for a given var 
 * 
 */
public class DashboardTableCol implements IsSerializable
{   
	protected String _sColPath ;
  protected String _sColType ;
  protected String _sColFormat ;
	
	protected String _sColBgColor ;
  protected String _sColCaption ;
  
  /**
   * Zero Constructor
   *
   */
  public DashboardTableCol()
  {
  	_sColPath    = "" ;
    _sColType    = "" ;
    _sColFormat  = "" ;
  	
  	_sColBgColor = "" ;
    _sColCaption = "" ;
  }
  
  /**
   * Default Constructor
   *
   */
  public DashboardTableCol(final String sColPath, final String sColType, final String sColFormat, final String sColBgColor, final String sColCaption)
  {
  	_sColPath    = sColPath ;
    _sColType    = sColType ;
    _sColFormat  = sColFormat ;
  	
  	_sColBgColor = sColBgColor ;
    _sColCaption = sColCaption ;
  }

  /**
   * Copy Constructor
   *
   */
  public DashboardTableCol(final DashboardTableCol model)
  {
  	reinit() ;
  	
  	if (null == model)
  		return ;
  	
  	_sColPath    = model._sColPath ;
    _sColType    = model._sColType ;
    _sColFormat  = model._sColFormat ;
  	
  	_sColBgColor = model._sColBgColor ;
    _sColCaption = model._sColCaption ;
  }
  
  protected void reinit()
  {
  	_sColPath    = "" ;
    _sColType    = "" ;
    _sColFormat  = "" ;
  	
  	_sColBgColor = "" ;
    _sColCaption = "" ;
  }
  
  public String getColPath() {
  	return _sColPath ;
  }
  
  public String getColType() {
  	return _sColType ;
  }
  
  public String getColFormat() {
  	return _sColFormat ;
  }

  public String getColBgColor() {
  	return _sColBgColor ;
  }
  
  public String getColCaption() {
  	return _sColCaption ;
  }  
}
