package com.primege.client.widgets;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;
import com.primege.client.loc.PrimegeViewConstants;
import com.primege.shared.database.CityData;
import com.primege.shared.database.FormDataData;

/**
 * TextBox with a drop down list from Lexicon
 * 
 * Inspired from http://sites.google.com/site/gwtcomponents/auto-completiontextbox
 */
public class EventCityControl extends ListBox implements ControlModel
{
	protected final PrimegeViewConstants constants = GWT.create(PrimegeViewConstants.class) ;

	protected ControlBaseWithParams _base ;
	protected ArrayList<CityData>   _aCities ;
  
  /**
   * Default Constructor
   *
   */
  public EventCityControl(final ArrayList<CityData> aCities, final String sPath)
  {
    super() ;
    
    _base    = new ControlBaseWithParams(sPath) ;
    _aCities = aCities ;
    
    init() ;
    
    setVisibleItemCount(1) ;
    setItemSelected(0, true) ;
  }
  
  public boolean isSingleData() {
  	return true ;
  }
  
  /**
   * Initialize the list with cities - the first being "undefined" 
   *
   */
  public void init()
  {
  	addItem(constants.Undefined()) ;
  	
  	if ((null == _aCities) || _aCities.isEmpty())
  		return ;
  	
  	for (CityData city : _aCities)
  		addItem(city.getLabel()) ;
  }
    
  /**
   * Return a FormDataData which value is filled with content
   *
   */
	public FormDataData getContent()
	{
		int iSelectedCityId = getSelectedCityId() ;
		if (-1 == iSelectedCityId)
			return null ;
		
		FormDataData formData = new FormDataData() ;
		formData.setPath(_base.getPath()) ;
		formData.setValue(Integer.toString(iSelectedCityId)) ;
		return formData ;
	}
	
	
	/**
   * Return the selected city Id is any, or <code>-1</code> if none
   *
   */
	public int getSelectedCityId()
	{
		String sSelectedCity = getSelectedValue() ;
		
		if ("".equals(sSelectedCity) || sSelectedCity.equals(constants.Undefined()))
			return -1 ;
		
		int iSelectedCityId = -1 ; 
		
		for (CityData city : _aCities)
			if (sSelectedCity.equals(city.getLabel()))
				iSelectedCityId = city.getId() ;
		
		return iSelectedCityId ; 
	}
	
	/**
   * Initialize the selected city from a content and default values
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
   * Initialize the selected city from a content 
   *
   * @param content       FormDataData used to initialize the control
   */
	public void setContent(final FormDataData content)
	{
		String sCityId = "" ;
		
		if (null == content)
			sCityId = _base.getDefaultValue() ;
		else
			sCityId = content.getValue() ;
		
		if ("".equals(sCityId))
		{
			setItemSelected(0, true) ;
			return ;
		}
		
		if ((null == sCityId) || "".equals(sCityId))
		{
			setItemSelected(0, true) ;
			return ;
		}
		
		String sCityLabel = "" ;
		
		for (CityData city : _aCities)
			if (sCityId.equals(Integer.toString(city.getId())))
				sCityLabel = city.getLabel() ;
		
		if ("".equals(sCityLabel))
			return ;
		
		int iSize = getItemCount() ;
		for (int i = 0 ; i < iSize ; i++)
			if (getItemText(i).equals(sCityLabel))
			{
				setItemSelected(i, true) ;
				return ;
			}
	}
	
	public void resetContent() {
		setItemSelected(0, true) ;
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
}
