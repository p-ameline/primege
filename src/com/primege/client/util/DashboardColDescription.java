package com.primege.client.util;

/**
 * Information about what (and how) to display dashboard's heads of columns 
 * 
 */
public class DashboardColDescription
{   
  protected String _sBackgroundColor ;
  protected String _sHeadFormat ;
  
  protected String _sDisplayedData ;
  protected String _sDisplayType ;
  
  /**
   * Default Constructor
   *
   */
  public DashboardColDescription(final String sData, final String sType, final String sBgColor, final String sHeadFormat)
  {
  	_sBackgroundColor = setButNotNull(sBgColor) ;
  	_sDisplayedData   = setButNotNull(sData) ;
  	_sDisplayType     = setButNotNull(sType) ;
  	_sHeadFormat      = setButNotNull(sHeadFormat) ;
  }

  /**
   * Copy Constructor
   *
   */
  public DashboardColDescription(final DashboardColDescription model)
  {
  	_sBackgroundColor = model._sBackgroundColor ; 	
  	_sDisplayedData   = model._sDisplayedData ;
  	_sDisplayType     = model._sDisplayType ;
  	_sHeadFormat      = model._sHeadFormat ;
  }
  
  public String getBackgroundColor() {
  	return _sBackgroundColor ;
  }
  
  public String getDisplayedData() {
  	return _sDisplayedData ;
  }
  
  public String getDisplayType() {
  	return _sDisplayType ;
  }
  
  public String getHeadFormat() {
  	return _sHeadFormat ;
  }
  
  protected String setButNotNull(String sInformation)
  {
  	if (null == sInformation)
  		return "" ;
    return sInformation ;
  }
}
