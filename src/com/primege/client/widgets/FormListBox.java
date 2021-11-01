package com.primege.client.widgets;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;
import com.primege.client.loc.PrimegeViewConstants;
import com.primege.client.util.FormControlOptionData;
import com.primege.shared.database.FormDataData;

/**
 * TextBox with a drop down list from Lexicon
 * 
 * Inspired from http://sites.google.com/site/gwtcomponents/auto-completiontextbox
 */
public class FormListBox extends ListBox implements ControlModel
{
	protected final PrimegeViewConstants constants = GWT.create(PrimegeViewConstants.class) ;

	protected ControlBaseWithParams         _base ;
	protected ArrayList<FormControlOptionData> _aOptions = new ArrayList<FormControlOptionData>() ;
  
  /**
   * Default Constructor
   *
   */
  public FormListBox(final ArrayList<FormControlOptionData> aOptions, final String sPath)
  {
    super() ;
    
    _base = new ControlBaseWithParams(sPath) ;
    
    init(aOptions) ;
    
    setVisibleItemCount(1) ;
    setItemSelected(0, true) ;
  }
  
  public boolean isSingleData() {
  	return true ;
  }
  
  /**
   * Populate the list
   */
  public void init(final ArrayList<FormControlOptionData> aOptions)
  {
  	if ((null == aOptions) || aOptions.isEmpty())
  		return ;
  	
  	addItem(constants.Undefined()) ;
  	
  	for (FormControlOptionData optionData : aOptions)
  	{
  		addItem(optionData.getCaption()) ;  		
  		_aOptions.add(new FormControlOptionData(optionData)) ;
  	}
  }
      
  /**
   * Return a FormDataData which value is filled with content
   */
	public FormDataData getContent()
	{
		String sSelectedPath = getSelectedOptionPath() ;
		if ("".equals(sSelectedPath))
			return null ;
		
		FormDataData formData = new FormDataData() ;
		formData.setPath(_base.getPath() + "/" + sSelectedPath) ;
		return formData ;
	}
	
	/**
   * Return the selected option Id is any, or <code>-1</code> if none
   *
   */
	public String getSelectedOptionPath()
	{
		String sSelectedOptionLabel = getSelectedValue() ;
		
		if ("".equals(sSelectedOptionLabel) || sSelectedOptionLabel.equals(constants.Undefined()))
			return "" ;
		
		for (FormControlOptionData optionData : _aOptions)
			if (sSelectedOptionLabel.equals(optionData.getCaption()))
				return optionData.getPath() ;
		
		return "" ; 
	}
	
	/**
   * Initialize the selected option from a content and a default value
   *
   * @param content       FormDataData used to initialize the control
   * @param sDefaultValue Configuration parameters, including default value in case there is no content 
   */
	public void setContent(final FormDataData content, final String sDefaultValue)  
	{
		_base.parseParams(sDefaultValue) ;
		
		setContent(content) ;
	}
	
	/**
   * Initialize the selected option from a content
   *
   * @param content FormDataData used to initialize the control
   */
	public void setContent(final FormDataData content)
	{
		String sOptionPath = "" ;
		
		if (null == content)
			sOptionPath = _base.getDefaultValue() ;
		else
			sOptionPath = content.getPath() ;
		
		// Find corresponding option
		//
		FormControlOptionData selectedOption = null ;
		
		for (FormControlOptionData optionData : _aOptions)
		{
			String sLocalPath = _base.getPath() + "/" + optionData.getPath() ;
			if (sLocalPath.equals(sOptionPath))
			{
				selectedOption = optionData ;
				break ;
			}
		}
				
		if (null == selectedOption)
			return ;
		
		String sOptionLabel = selectedOption.getCaption() ; 
		
		// Find corresponding item in the list
		//		
		int iSize = getItemCount() ;
		for (int i = 0 ; i < iSize ; i++)
			if (getItemText(i).equals(sOptionLabel))
			{
				setItemSelected(i, true) ;
				return ;
			}
	}
	
	public void resetContent() {
		setContent(null) ;
	}

	/**
	 * Initialize the list of options
	 */
	protected void initOptions(final ArrayList<FormControlOptionData> aOptions)
	{
		_aOptions.clear() ;
		
		if ((null == aOptions) || aOptions.isEmpty())
			return ;
			
		for (FormControlOptionData option : aOptions)
			_aOptions.add(new FormControlOptionData(option)) ;
	}
	
	/**
	 * Get the option for a path 
	 * 
	 * @return The option if found, <code>null</code> if not
	 */
	public FormControlOptionData getOptionForPath(final String sOptionPath)
	{
		if ((null == sOptionPath) || "".equals(sOptionPath) || _aOptions.isEmpty())
			return null ;
		
		for (FormControlOptionData optionData : _aOptions)
		{
			String sLocalPath = _base.getPath() + "/" + optionData.getPath() ;
			if (sLocalPath.equals(sOptionPath))
				return optionData ;
		}
		
		return null ;
	}
	
	public ControlBase getControlBase() {
		return _base ;
	}
	
	public void setInitFromPrev(boolean bInitFromPrev) {
		_base.setInitFromPrev(bInitFromPrev); ;
	}
	
	public boolean getInitFromPrev() {
		return _base.getInitFromPrev() ;
	}
	
	public ArrayList<FormControlOptionData> getOptions() {
		return _aOptions ;
	}
}
