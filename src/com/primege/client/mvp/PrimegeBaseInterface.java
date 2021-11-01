package com.primege.client.mvp;


import net.customware.gwt.presenter.client.widget.WidgetDisplay;

public interface PrimegeBaseInterface extends WidgetDisplay
{
	public String getCity() ;
	public void   setCity(String sCity) ; 
	public void   clearCities() ;	
	public void   addCity(String sCityName) ;
}
