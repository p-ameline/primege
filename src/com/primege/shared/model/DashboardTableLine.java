package com.primege.shared.model;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A table line as elaborated by the server 
 * 
 */
public class DashboardTableLine implements IsSerializable
{   
	protected ArrayList<String> _aCellsContent = new ArrayList<String>() ;
  
	/**
   * Zero Constructor
   */
  public DashboardTableLine() {
  }
	
  /**
   * Default Constructor
   *
   * @param iCellsCount number of cells to create
   */
  public DashboardTableLine(final int iCellsCount) 
  {
  	if (iCellsCount < 1)
  		return ;
  	
  	for (int i = 0 ; i < iCellsCount ; i++)
  		_aCellsContent.add("") ;
  }

  /**
   * Copy Constructor
   *
   */
  public DashboardTableLine(final DashboardTableLine model) {
  	initFromOther(model) ;
  }
  
  /**
   * Reinitialize from another table line 
   */
  public void initFromOther(final DashboardTableLine model)
  {
  	reinit() ;
  	
  	if ((null == model) || model._aCellsContent.isEmpty())
  		return ;
  	
  	for (String sContent : model._aCellsContent)
  		_aCellsContent.add(sContent) ;
  }

  /**
   * Get cells count 
   */
  public int getCellsCount() {
  	return _aCellsContent.size() ;
  }
  
  /**
   * Clear the structure (cell count becomes zero)
   */
  protected void reinit() {
  	_aCellsContent.clear() ; 
  }
  
  /**
   * Reinitialize all cells to <code>""</code>
   */
  protected void reset()
  {
  	int iMax = _aCellsContent.size() ;
  	
  	for (int i = 0 ; i < iMax ; i++)
  		_aCellsContent.set(i, "") ;
  }
  
  /**
   * Is the structure empty? (same as cell count being zero)
   */
  public boolean isEmpty() {
  	return _aCellsContent.isEmpty() ;
  }
  
  public ArrayList<String> getContent() {
  	return _aCellsContent ;
  }
  
  /**
   * Set line content for a given column
   *  
   * @param iIndex      Index of column in the range [0, size of array[ 
   * @param sNewContent Content to set for this column
   */
  public void setContent(final int iIndex, final String sNewContent)
  {
  	if ((null == sNewContent) || (false == isValidIndex(iIndex)))
  		return ;
  	
  	_aCellsContent.set(iIndex, sNewContent) ;
  }
  
  /**
   * Get content for a given column
   */
  public String get(final int iIndex)
  {
  	if (false == isValidIndex(iIndex))
  		return "" ;
  	
  	return _aCellsContent.get(iIndex) ;
  }
  
  /**
   * Is the index a valid index (i.e. between zero and array size)? 
   */
  protected boolean isValidIndex(final int iIndex) {
  	return (iIndex >= 0) && (iIndex < _aCellsContent.size()) ;
  }
}
