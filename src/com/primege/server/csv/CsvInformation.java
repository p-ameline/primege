package com.primege.server.csv;

import java.util.ArrayList;

/**
 * Referencing an information
 * 
 */
public class CsvInformation
{   
  protected String _sPath ;
  protected String _sCaption ;
  protected String _sType ;
  protected String _sFormat ;
  
  protected ArrayList<CsvInfoOption> _aOptions = new ArrayList<CsvInfoOption>() ;
  
  /**
   * Default Constructor
   *
   */
  public CsvInformation(final String sPath, final String sCaption, final String sType, final String sFormat)
  {
    if (null == sPath)
    	_sPath = "" ;
    else
    	_sPath = sPath ;
    
    if (null == sCaption)
    	_sCaption = "" ;
    else
    	_sCaption = sCaption ;
    
    if (null == sType)
    	_sType = "" ;
    else
    	_sType = sType ;
    
    if (null == sFormat)
    	_sFormat = "" ;
    else
    	_sFormat = sFormat ;
  }

  public String getPath() {
  	return _sPath ;
  }
  
  public String getCaption() {
  	return _sCaption ;
  }
  
  public String getType() {
  	return _sType ;
  }
  
  public String getFormat() {
  	return _sFormat ;
  }
  
  public ArrayList<CsvInfoOption> getOptions() {
  	return _aOptions ;
  }
  public void addToOptions(CsvInfoOption option) {
  	_aOptions.add(option) ;
  }
}
