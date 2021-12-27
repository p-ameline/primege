package com.primege.server.csv;

import java.util.ArrayList;

/**
 * The CSV description from a parsed xml file 
 * 
 */
public class CsvStructure
{   
  protected String _sHeaderLine ;
  protected String _sRoots ;
  
  protected ArrayList<CsvRecord> _aRecords = new ArrayList<CsvRecord>() ;
  
  /**
   * Zero information Constructor
   *
   */
  public CsvStructure() {
  	reset() ;
  }
  
  /**
   * Default Constructor
   *
   */
  public CsvStructure(final String sHeaderLine, final String sRoots)
  {
  	if (null == sHeaderLine)
  		_sHeaderLine = "" ;
    else
    	_sHeaderLine = sHeaderLine ;
  	
  	if (null == sRoots)
  		_sRoots = "" ;
    else
    	_sRoots = sRoots ;
  }

  protected void reset()
  {
  	_sHeaderLine = "" ;
  	_sRoots      = "" ;
  }
  
  public String getHeaderLine() {
  	return _sHeaderLine ;
  }
  public void setHeaderLine(final String sHeaderLine) {
  	_sHeaderLine = sHeaderLine ;
  }
  
  public String getRoots() {
  	return _sRoots ;
  }
  public void setRoots(final String sRoots) {
  	_sRoots = sRoots ;
  }
  
  public ArrayList<CsvRecord> getRecords() {
  	return _aRecords ;
  }
  public void addToRecords(CsvRecord info) {
  	_aRecords.add(info) ;
  }
}
