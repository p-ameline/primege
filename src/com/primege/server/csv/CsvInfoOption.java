package com.primege.server.csv;

/**
 * Referencing an option for a "single choice" information
 * 
 */
public class CsvInfoOption
{   
  protected String _sPath ;
  protected String _sCaption ;
  
  /**
   * Default Constructor
   *
   */
  public CsvInfoOption(final String sPath, final String sCaption)
  {
    if (null == sPath)
    	_sPath = "" ;
    else
    	_sPath = sPath ;
    
    if (null == sCaption)
    	_sCaption = "" ;
    else
    	_sCaption = sCaption ;
  }

  public String getPath() {
  	return _sPath ;
  }
  
  public String getCaption() {
  	return _sCaption ;
  }
}
