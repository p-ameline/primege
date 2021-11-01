package com.primege.server.csv;

import java.util.ArrayList;

/**
 * The CSV description from a parsed xml file 
 * 
 */
public class CsvStructure
{   
  protected String _sHeaderLine ;
  
  protected ArrayList<CsvRecord> _aRecords = new ArrayList<CsvRecord>() ;
  
  /**
   * Zero information Constructor
   *
   */
  public CsvStructure()
  {
  	_sHeaderLine = "" ;
  }
  
  /**
   * Default Constructor
   *
   */
  public CsvStructure(final String sHeaderLine)
  {
  	if (null == sHeaderLine)
  		_sHeaderLine = "" ;
    else
    	_sHeaderLine = sHeaderLine ;
  }

  public String getHeaderLine() {
  	return _sHeaderLine ;
  }
  public void setHeaderLine(String sHeaderLine) {
  	_sHeaderLine = sHeaderLine ;
  }
  
  public ArrayList<CsvRecord> getRecords() {
  	return _aRecords ;
  }
  public void addToRecords(CsvRecord info) {
  	_aRecords.add(info) ;
  }
}
