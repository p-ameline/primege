package com.primege.shared.model;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A chart inside the dashboard
 * 
 */
public class DashboardChart implements IsSerializable
{   
	protected int    _iID ;
	
	protected String _sRoots ;
  protected String _sPivot ;
  
  protected ArrayList<DashboardTableCol> _aCols  = new ArrayList<DashboardTableCol>() ;
  protected ArrayList<String>            _aTypes = new ArrayList<String>() ;
  
  protected String _sYCaption ;
  
  /**
   * Empty Constructor
   */
  public DashboardChart() {
  	reinit() ;
  }
  
  /**
   * Default Constructor
   */
  public DashboardChart(final int iID, final String sRoots, final String sPivot, final String sTypes, final String sYCaption)
  {
  	reinit() ;
  	
  	_iID       = iID ;
  	_sRoots    = sRoots ;
  	_sPivot    = sPivot ;
  	_sYCaption = sYCaption ;
  	
    setTypes(sTypes) ;
  }

  /**
   * Copy Constructor
   *
   */
  public DashboardChart(final DashboardChart model) {
  	initFromDashboardTable(model) ;
  }
  
  /**
   * Copy Constructor
   *
   */
  public void initFromDashboardTable(final DashboardChart model)
  {
  	reinit() ;
  	
  	if (null == model)
  		return ;
  	
  	_iID       = model._iID ;
  	_sRoots    = model._sRoots ;
  	_sPivot    = model._sPivot ;
  	_sYCaption = model._sYCaption ;
  	
  	if ((null != model._aCols) && (false == model._aCols.isEmpty()))
  		for (Iterator<DashboardTableCol> it = model._aCols.iterator() ; it.hasNext() ; )
  			_aCols.add(new DashboardTableCol(it.next())) ;
  	
  	if ((null != model._aTypes) && (false == model._aTypes.isEmpty()))
  		for (Iterator<String> it = model._aTypes.iterator() ; it.hasNext() ; )
  			_aTypes.add(new String(it.next())) ;
  }
  
  protected void reinit()
  {
  	_iID       = -1 ;
  	_sRoots    = "" ;
  	_sPivot    = "" ;
  	_sYCaption = "" ;
  	
  	_aCols.clear() ;
  	_aTypes.clear() ;
  }
  
  /**
   * Initialize types by parsing types as a comma separated string
   */
  public void setTypes(final String sTypes)
  {
  	_aTypes.clear() ;
  	
  	if ((null == sTypes) || "".equals(sTypes))
  		return ;
  	
  	// Split the string using the ';' separator
  	//
  	String[] typesList = sTypes.split(";") ;
  		
  	for (int iType = 0 ; iType < typesList.length ; iType++)
  		_aTypes.add(typesList[iType]) ;
  }
  
  /**
   * Get the column for Abscissa, defined as the column which type is "X"
   */
  public DashboardTableCol getPivotForAbscissa()
  {
  	if ((null == _aTypes) || _aTypes.isEmpty() || (null == _aCols) || _aCols.isEmpty())
  		return null ;
  	
  	Iterator<String>            typesIter = _aTypes.iterator() ;
  	Iterator<DashboardTableCol> colsIter  = _aCols.iterator() ;
  	
  	while (typesIter.hasNext() && colsIter.hasNext())
  	{
  		DashboardTableCol col = colsIter.next() ; 
  		
  		if ("X".equalsIgnoreCase(typesIter.next()))
  			return col ;
  	}
  	
  	return null ;
  }
  
  public int getId() {
  	return _iID ;
  }
  
  public String getPivot() {
  	return _sPivot ;
  }
  
  public String getRoots() {
  	return _sRoots ;
  }
  
  public String getYCaption() {
  	return _sYCaption ;
  }
  
  public ArrayList<DashboardTableCol> getCols() {
  	return _aCols ;
  }
  
  public void addColumn(final String sColPath, final String sColType, final String sColFormat, final String sColBgColor, final String sColCaption) {
  	_aCols.add(new DashboardTableCol(sColPath, sColType, sColFormat, sColBgColor, sColCaption)) ;
  }
  
  public ArrayList<String> getTypes() {
  	return _aTypes ;
  }
  
  /**
   * Get the type for a given column (index in [0 - size of types array]
   * 
   * @return <code>null</code> if index is invalid, the type if not
   */
  public String getTypeForIndex(final int iIndex)
  {
  	if ((iIndex < 0) || (iIndex >= _aTypes.size()))
  		return null ;
  	
  	return _aTypes.get(iIndex) ;
  }
  
  public void addType(final String sType) {
  	_aTypes.add(new String(sType)) ;
  }
}
