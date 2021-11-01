package com.primege.client.widgets;

import com.primege.client.util.FormControl;

/**
 * Base class of all controls operating in "Primege" forms
 */
public class ControlBase
{
	//Location in father
	protected FormBlockPanel _father ;
	protected int            _iRowInFather ;
	
	// View connector
	//
	FormControl              _formControl ;
	
	// Context
	protected String         _sPath ;
	protected boolean        _bInitFromPrev ;
	protected boolean        _bReadOnly ;
	protected boolean        _bMandatory ;
	
	protected String         _sDefaultValue ;
	
  /**
   * Default Constructor
   *
   */
  public ControlBase(final String sPath)
  {
    _sPath           = sPath ;
    
    _father          = null ;
    _iRowInFather    = -1 ;
    
    _formControl     = null ;
    
    _bInitFromPrev   = false ;
    _bReadOnly       = false ;
    _bMandatory      = true ;
    
    _sDefaultValue   = "" ;
  }
	
  // Getters and setters
  //
  
  public String getPath() {
		return _sPath ;
	}
	public void setPath(final String sPath) {
		_sPath = sPath ;
	}
  
	public void setInitFromPrev(final boolean bInitFromPrev) {
		_bInitFromPrev = bInitFromPrev ;
	}
	public boolean getInitFromPrev() {
		return _bInitFromPrev ;
	}
	
	public void setReadOnly(final boolean bReadOnly) {
		_bReadOnly = bReadOnly ;
	}
	public boolean isReadOnly() {
		return _bReadOnly ;
	}
	
	public void setMandatory(final boolean bMandatory) {
		_bMandatory = bMandatory ;
	}
	public boolean isMandatory() {
		return _bMandatory ;
	}
	
	public FormBlockPanel getFather() {
		return _father ;
	}
	public void setFather(final FormBlockPanel father) {
		_father = father ;
	}
	
	public int getRowInFather() {
		return _iRowInFather ;
	}
	public void setRowInFather(final int iRowInFather) {
		_iRowInFather = iRowInFather ;
	}
	
	public String getDefaultValue() {
		return _sDefaultValue ;
	}
	public void setDefaultValue(final String sDefaultValue) {
		_sDefaultValue = sDefaultValue ;
	}

	public FormControl getFormControl() {
		return _formControl ;
	}
	public void setFormControl(final FormControl formControl) {
		_formControl = formControl ;
	}
	
	/**
	 * Can the term be considered as true (true, yes, ok...) 
	 */
	protected boolean isTrue(final String sValue) {
		return (("true".equalsIgnoreCase(sValue)) ||
				    ("yes".equalsIgnoreCase(sValue))  ||
				    ("oui".equalsIgnoreCase(sValue))  ||
				    ("ok".equalsIgnoreCase(sValue))) ;
	}
}
