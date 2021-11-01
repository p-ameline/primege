package com.primege.client.util;

/**
 * Information to create a control option inside a form
 * 
 */
public class FormControlOptionData
{   
  protected String  _sPath ;
  protected String  _sCaption ;
  protected String  _sUnit ;
  
  protected String  _sExclusion ;
  protected String  _sPopupText ;
  
  protected boolean _bMandatory ;
  
  /**
   * Default Constructor
   *
   */
  public FormControlOptionData(final String sPath, final String sCaption, final String sUnit, final String sPopupText, final String sExclusion)
  {
    if (null == sPath)
    	_sPath = "" ;
    else
    	_sPath = sPath ;
    
    if (null == sCaption)
    	_sCaption = "" ;
    else
    	_sCaption = sCaption ;
    
    if (null == sUnit)
    	_sUnit = "" ;
    else
    	_sUnit = sUnit ;
    
    if (null == sPopupText)
    	_sPopupText = "" ;
    else
    	_sPopupText = sPopupText ;
    
    if (null == sExclusion)
    	_sExclusion = "" ;
    else
    	_sExclusion = sExclusion ;
  }

  /**
   * Copy Constructor
   *
   */
  public FormControlOptionData(final FormControlOptionData model)
  {
    _sPath      = "" ;
    _sCaption   = "" ;
    _sUnit      = "" ;
    _sExclusion = "" ;
    _sPopupText = "" ;
    
    if (null == model)
    	return ;
    
    if (null != model._sPath)
    	_sPath = model._sPath ;
    
    if (null != model._sCaption)
    	_sCaption = model._sCaption ;
    
    if (null != model._sUnit)
    	_sUnit = model._sUnit ;
    
    if (null != model._sExclusion)
    	_sExclusion = model._sExclusion ;
    
    if (null != model._sPopupText)
    	_sPopupText = model._sPopupText ;
  }
  
  public String getPath() {
  	return _sPath ;
  }
  
  public String getCaption() {
  	return _sCaption ;
  }
  
  public String getUnit() {
  	return _sUnit ;
  }
  
  public String getExclusion() {
  	return _sExclusion ;
  }
  
  public String getPopupText() {
  	return _sPopupText ;
  }
}
