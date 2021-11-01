package com.primege.client.util;

import com.google.gwt.user.client.ui.Widget;
import com.primege.shared.database.FormDataData;

/**
 * Referencing a control inside a form
 * 
 */
public class DashboardStructures
{   
  protected String       _sPath ;
  protected FormDataData _content ;
  
  protected Widget       _widget ;
  
  /**
   * Default Constructor
   *
   */
  public DashboardStructures(String sPath, Widget widget, FormDataData content)
  {
    if (null == sPath)
    	_sPath = "" ;
    else
    	_sPath = sPath ;
    
    _content = content ;    
    _widget  = widget ;
  }

  public String getPath() {
  	return _sPath ;
  }
  
  public FormDataData getContent() {
  	return _content ;
  }
  
  public Widget getWidget() {
  	return _widget ;
  }
}
