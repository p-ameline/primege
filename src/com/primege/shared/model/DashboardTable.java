package com.primege.shared.model;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A table inside the dashboard
 * 
 */
public class DashboardTable implements IsSerializable
{   
	protected int       _iID ;
	
  protected String    _sRoots ;
  protected String    _sPivot ;
  protected TOTALTYPE _iTotalsLine ;
  protected String    _sTotalCaption ;
  
  protected String    _sStartingDate ;
  protected String    _sEndingDate ;
  
  protected ArrayList<DashboardTableCol> _aCols = new ArrayList<DashboardTableCol>() ;
  
  public enum TOTALTYPE { totalNone, totalSum, totalMean } ;
  
  protected ArrayList<DashboardTableLineModel> _aLines = new ArrayList<DashboardTableLineModel>() ;
  
  /**
   * Empty Constructor
   *
   */
  public DashboardTable()
  {
  	_iID           = -1 ;
  	_sRoots        = "" ;
  	_sPivot        = "" ;
  	_iTotalsLine   = TOTALTYPE.totalNone ;
  	_sTotalCaption = "" ;
  	
  	_sStartingDate = "" ;
    _sEndingDate   = "" ;
  }
  
  /**
   * Default Constructor
   *
   */
  public DashboardTable(final int iID, final String sRoots, final String sPivot, final String sTotalLigne, final String sTotalCaption, final String sLines)
  {
  	_iID           = iID ;
  	_sRoots        = sRoots ;
  	_sPivot        = sPivot ;
  	_iTotalsLine   = getTotalsLineFromString(sTotalLigne) ;
  	_sTotalCaption = sTotalCaption ;
  	
  	_sStartingDate = "" ;
    _sEndingDate   = "" ;
    
    if (false == "".equals(sLines))
    	parseLinesString(sLines) ;
  }

  /**
   * Copy Constructor
   *
   */
  public DashboardTable(final DashboardTable model) {
  	initFromDashboardTable(model) ;
  }
  
  /**
   * Copy Constructor
   *
   */
  public void initFromDashboardTable(final DashboardTable model)
  {
  	reinit() ;
  	
  	if (null == model)
  		return ;
  	
  	_iID           = model._iID ;
  	_sRoots        = model._sRoots ;
  	_sPivot        = model._sPivot ;
  	_iTotalsLine   = model._iTotalsLine ;
  	_sTotalCaption = model._sTotalCaption ;
  	
  	if ((null != model._aCols) && (false == model._aCols.isEmpty()))
  		for (Iterator<DashboardTableCol> it = model._aCols.iterator() ; it.hasNext() ; )
  			_aCols.add(new DashboardTableCol(it.next())) ;
  	
  	if ((null != model._aLines) && (false == model._aLines.isEmpty()))
  		for (Iterator<DashboardTableLineModel> it = model._aLines.iterator() ; it.hasNext() ; )
  			_aLines.add(new DashboardTableLineModel(it.next())) ;
  }
  
  protected void reinit()
  {
  	_iID         = -1 ;
  	_sRoots      = "" ;
  	_sPivot      = "" ;
  	_iTotalsLine = TOTALTYPE.totalNone ;
  	
  	_aCols.clear() ;
  	_aLines.clear() ;
  }
  
  public int getId() {
  	return _iID ;
  }
  
  public String getRoots() {
  	return _sRoots ;
  }
  
  public String getPivot() {
  	return _sPivot ;
  }
  
  public TOTALTYPE getTotalLine() {
  	return _iTotalsLine ;
  }
  
  public String getTotalCaption() {
  	return _sTotalCaption ;
  }
  
  public ArrayList<DashboardTableCol> getCols() {
  	return _aCols ;
  }
  
  public ArrayList<DashboardTableLineModel> getLines() {
  	return _aLines ;
  }
  
  public void addColumn(final String sColPath, final String sColType, final String sColFormat, final String sColBgColor, final String sColCaption) {
  	_aCols.add(new DashboardTableCol(sColPath, sColType, sColFormat, sColBgColor, sColCaption)) ;
  }
  
  protected TOTALTYPE getTotalsLineFromString(final String sTotalLigne)
  {
  	if ((null == sTotalLigne) || "".equals(sTotalLigne))
  		return TOTALTYPE.totalNone ;
  		
  	if ("sum".equalsIgnoreCase(sTotalLigne))
  		return TOTALTYPE.totalSum ;
  	
  	if ("mean".equalsIgnoreCase(sTotalLigne))
  		return TOTALTYPE.totalMean ;
  	
  	return TOTALTYPE.totalNone ;
  }
  
  /**
   * Return the label of the line attached to a given path 
   *
   * @return The label if a line was found, or <code>""</code> if not
   */
  public String getLineLabelForPath(final String sPath)
  {
  	if ((null == sPath) || "".equals(sPath) || _aLines.isEmpty())
  		return "" ;
  	
  	for (Iterator<DashboardTableLineModel> it = _aLines.iterator() ; it.hasNext() ; )
  	{
  		DashboardTableLineModel line = it.next() ;
  		
  		if (line.getPath().equals(sPath))
  			return line.getLabel() ;
  	}
  	
  	return "" ;
  }
  
  /**
   * Parse a String in the form "label1:path1;label2:path2;..."
   *
   */
  protected void parseLinesString(final String sLines)
  {
  	_aLines.clear() ;
  	
  	if ((null == sLines) || "".equals(sLines))
  		return ;
  	
  	// First split the string using the ';' separator
  	//
  	String[] aLines = sLines.split(";") ;
  	
  	for (int iLine = 0 ; iLine < aLines.length ; iLine++)
  	{
  		String sLineDescription = aLines[iLine] ;
  		
  		if (false == "".equals(sLineDescription))
  		{
  			// For each line, in the form "label1:path1" split the string using the ':' separator
  			//
  			String[] aLineElements = sLineDescription.split("\\:") ;
  			if      (2 == aLineElements.length)
  				_aLines.add(new DashboardTableLineModel(aLineElements[0], aLineElements[1])) ;
  			else if (1 == aLineElements.length)
  				_aLines.add(new DashboardTableLineModel(aLineElements[0], "")) ;
  		}
  	}
  }
  
	/**
	 * Get a vector of roots from a '|' separated string
	 * 
	 * @param sRoots String in the form "root1|root2|etc"
	 * 
	 * @return The vector if parsing was successful, <code>null</code> if not
	 */
	public static String[] getRoots(final String sRoots)
	{
		if ((null == sRoots) || sRoots.isEmpty())
			return null ;
		
		String aRawRoots[] = sRoots.split("\\|") ;
			
		// count non empty roots
		int iNonEmptyCount = 0 ;
		for (int i = 0 ; i < aRawRoots.length ; i++)
			if ((null != aRawRoots[i]) && (false == aRawRoots[i].isEmpty()))
				iNonEmptyCount++ ;
			
		if (0 == iNonEmptyCount)
			return null ;
			
		String[] aRoots = new String[iNonEmptyCount] ;
		int iCursor = 0 ;
		for (int i = 0 ; i < aRawRoots.length ; i++)
			if ((null != aRawRoots[i]) && (false == aRawRoots[i].isEmpty()))
				aRoots[iCursor++] = aRawRoots[i] ;
		
		return aRoots ;
	}
}
