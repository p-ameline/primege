package com.primege.client.mvp;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import com.primege.client.loc.PrimegeViewConstants;

public class PrimegeBaseDisplay extends Composite implements PrimegeBaseInterface 
{
	protected final PrimegeViewConstants constants = GWT.create(PrimegeViewConstants.class) ;
	
	protected TextBox _ZipTextBox  = new TextBox() ;
	protected ListBox _CityListBox = new ListBox() ;
	
	private static final int _STARTYEAR = 1900 ;
	// private static final int _ENDYEAR   = 2011 ;
	
	/**
	 * Returns selected month (January = 1, February = 2...)
	 * Returns -1 if no month selected
	 * 
	 * */
	protected int getSelectedMonth(ListBox monthListBox)
	{
		int iMonthIndex = monthListBox.getSelectedIndex() ;
		
		if (iMonthIndex < 1)
			return -1 ;
		
		return iMonthIndex ; 
	}
	
	protected void initDaysListBox(ListBox dayListBox, int iNbDays) 
	{
		if (null == dayListBox)
			return ;
		
		if (dayListBox.getItemCount() > 0)
			dayListBox.clear() ;
		
		for (int i = 1 ; i <= iNbDays ; i++) 
			dayListBox.addItem(Integer.toString(i)) ;
		
		dayListBox.setVisibleItemCount(1) ;
		dayListBox.setItemSelected(0, true) ;
	}
	
	public void initMonthListBox(ListBox listBox) 
	{
		if (null == listBox)
			return ;
		
		if (listBox.getItemCount() > 0)
			listBox.clear() ;
		
		listBox.addItem(constants.generalMonthUndefined()) ;
		listBox.addItem(constants.generalMonthJanuary()) ; 
		listBox.addItem(constants.generalMonthFebruary()) ; 
		listBox.addItem(constants.generalMonthMarch()) ;
		listBox.addItem(constants.generalMonthApril()) ;
		listBox.addItem(constants.generalMonthMay()) ;
		listBox.addItem(constants.generalMonthJune()) ;
		listBox.addItem(constants.generalMonthJully()) ;
		listBox.addItem(constants.generalMonthAugust()) ;
		listBox.addItem(constants.generalMonthSeptember()) ;
		listBox.addItem(constants.generalMonthOctober()) ;
		listBox.addItem(constants.generalMonthNovember()) ;
		listBox.addItem(constants.generalMonthDecember()) ;
		
		listBox.setVisibleItemCount(1) ;
		listBox.setItemSelected(0, true) ;
	}
	
	/**
	 * Initializes a date Listbox from _STARTYEAR to current year 
	 * 
	 * */
	@SuppressWarnings("deprecation")
	public void initYearListBox(ListBox listBox) 
	{
		if (null == listBox)
			return ;
		
		if (listBox.getItemCount() > 0)
			listBox.clear() ;
		
		Date tNow = new Date() ;
		int iYearIndex = tNow.getYear() + 1900 ;
		
		listBox.addItem(constants.generalYearUndefined()) ;
		
		for (int i = _STARTYEAR ; i <= iYearIndex ; i++) 
			listBox.addItem("" + i) ;	
	
		listBox.setVisibleItemCount(1) ;
		listBox.setItemSelected(0, true) ;
	}
	
	/**
	 * Initialize Year list box
	 * 
	 * Usually, it only includes current year and previous year, but it is possible
	 * to force inclusion of a given year
	 * 
	 * @param listBox Listbox to initialize
	 * @param iYearToInclude year to force inclusion of if needed, or -1 if not needed
	 * 
	 * */
	@SuppressWarnings("deprecation")
	public void initCurrentYearListBox(ListBox listBox, int iYearToInclude) 
	{
		if (null == listBox)
			return ;
		
		if (listBox.getItemCount() > 0)
			listBox.clear() ;
		
		Date tNow = new Date() ;
		int iYearIndex = tNow.getYear() + 1900 ;
		
		// Adding more recent years if needed
		//
		if ((-1 != iYearToInclude) && (iYearToInclude > iYearIndex))
		{
			for (int i = iYearToInclude ; i > iYearIndex ; i--)
				listBox.addItem("" + i) ;
		}
		
		// Adding this year and previous one
		//
		listBox.addItem("" + iYearIndex) ;
		listBox.addItem("" + (iYearIndex - 1)) ;
		
		// Adding older years if needed
		//
		if ((-1 != iYearToInclude) && (iYearToInclude < iYearIndex - 1))
		{
			for (int i = iYearIndex - 2 ; i >= iYearToInclude ; i--)
				listBox.addItem("" + i) ;
		}
		
		listBox.setVisibleItemCount(1) ;
		listBox.setItemSelected(1, true) ;
	}
	
	/**
	 * Verification of the date invoked when month changed
	 * 
	 * */
	protected class MonthChangeHandler implements ChangeHandler 
	{
		private int     currYear ;// selected year
		private ListBox yearListBox ;
		private ListBox monthListBox ;
		private ListBox dayListBox ;
		
		public MonthChangeHandler(ListBox monthListBox, ListBox yearListBox, ListBox dayListBox) 
		{
			this.monthListBox = monthListBox ;
			this.yearListBox  = yearListBox ;
			this.dayListBox   = dayListBox ;
			//initDayListBox(get31days) ;
		}

		@Override
		public void onChange(ChangeEvent event) 
		{
			int iMonthIndex = monthListBox.getSelectedIndex() ;
			
			// 0 = undefined
			// 1 = January etc 
			//
			if (0 == iMonthIndex)
				dayListBox.clear() ;
			else if ((4 == iMonthIndex) || (6 == iMonthIndex) || (9 == iMonthIndex) || (11 == iMonthIndex)) 
				initDaysListBox(dayListBox, 30) ;
			else if (2 == iMonthIndex) 
			{
				currYear = Integer.parseInt(yearListBox.getValue(yearListBox.getSelectedIndex())) ;
				if (currYear % 400 == 0 || (currYear % 4 == 0 && currYear % 100 != 0))
					initDaysListBox(dayListBox, 29) ;		
				else 
					initDaysListBox(dayListBox, 29) ;
			}
			else
				initDaysListBox(dayListBox, 31) ;	
		}
	}//end MonthChargeHandler
	
	public String getDateAsString(ListBox dayListBox, ListBox monthListBox, ListBox yearListBox) 
	{
		String sYear = yearListBox.getValue(yearListBox.getSelectedIndex()) ;
		
		String sDay  = dayListBox.getValue(dayListBox.getSelectedIndex()) ;
		if (sDay.equals(""))
			sDay = "00" ;
		else if (sDay.length() == 1)
			sDay = "0" + sDay ;
		
		String sMonth = "00" ;
		int iMonth = getSelectedMonth(monthListBox) ;
		if (iMonth > 0)
		{
			sMonth = Integer.toString(iMonth) ;
			if (sMonth.length() == 1)
				sMonth = "0" + sMonth ;
		}
		
		return sYear + sMonth + sDay ;
	}
	
	public void setDateAsString(String sDate, ListBox dayListBox, ListBox monthListBox, ListBox yearListBox) 
	{
		if (sDate.length() < 8)
			return ;
		
		String sYear  = sDate.substring(0, 4) ; 
		String sMonth = sDate.substring(4, 6) ;
		String sDay   = sDate.substring(6, 8) ;
		
		// int iYearIndex = Integer.parseInt(sYear) - 2011 ;
		// if (iYearIndex >= 0)
		// 	yearListBox.setItemSelected(iYearIndex, true) ;
		int iYear = Integer.parseInt(sYear) ;
		initCurrentYearListBox(yearListBox, iYear) ;  // Make certain iYear is in the listbox
		initListboxIndex(yearListBox, iYear) ;
		
		int iMonthIndex = Integer.parseInt(sMonth) ;
		if ((iMonthIndex > 0) && (iMonthIndex <= 12)) 
			monthListBox.setItemSelected(iMonthIndex, true) ;
		
		int iDayIndex = Integer.parseInt(sDay) ;
		if ((iDayIndex >= 0) && (iDayIndex < 31))
			dayListBox.setItemSelected(iDayIndex - 1, true) ;
	}
	
	@SuppressWarnings("deprecation")
  public void setDate(Date tDate, ListBox dayListBox, ListBox monthListBox, ListBox yearListBox)
	{
		if (null == tDate)
			return ;
		
		// int iYearIndex = tDate.getYear() - 111 ;
		// yearListBox.setItemSelected(iYearIndex, true) ;
		int iYear = tDate.getYear() + 1900 ;
		initCurrentYearListBox(yearListBox, iYear) ;  // Make certain iYear is in the listbox
		initListboxIndex(yearListBox, iYear) ;
		
		int iMonthIndex = tDate.getMonth() + 1 ;
		monthListBox.setItemSelected(iMonthIndex, true) ;
		
		int iDayIndex = tDate.getDate() - 1 ;
		dayListBox.setItemSelected(iDayIndex, true) ;
	}
	
	private void initListboxIndex(ListBox listBox, int iValue)
	{
		if (null == listBox)
			return ;
		
		int iItemsCount = listBox.getItemCount() ;
		if (0 == iItemsCount)
			return ;
		
		String sValueString = "" + iValue ;
		
		for (int i = 0 ; i < iItemsCount ; i++)
  	{
  		String sItemText = listBox.getItemText(i) ;
  		if (sItemText.equals(sValueString))
  		{
  			listBox.setSelectedIndex(i) ;
    		return ;
    	}
  	}
	}
	
	//
	public String getZipCode() {
		return _ZipTextBox.getText() ;
	}
	
	public void setZipCode(String sZipCode) {
		_ZipTextBox.setText(sZipCode) ;
	}
	
	public String getCity() 
	{
		int iSelected = _CityListBox.getSelectedIndex() ;
		if (-1 == iSelected)
			return "" ;

		return _CityListBox.getValue(iSelected) ;
	}
	
	public void setCity(String sCity) 
	{
		if (_CityListBox.getItemCount() == 0)
			return ;
		
		for (int i = 0 ; i < _CityListBox.getItemCount() ; i++)
		{
			String sBoxCity = _CityListBox.getItemText(i) ; 
			if (sCity.equals(sBoxCity))
				_CityListBox.setItemSelected(i, true) ;
			else
				_CityListBox.setItemSelected(i, false) ;
		}
	}
	
	public TextBox getZipTextBox() {
		return _ZipTextBox ;
	}
	
	public void clearCities()
	{
		_CityListBox.clear() ;
	}
	
	public void addCity(String sCityName)
	{
		_CityListBox.addItem(sCityName) ;
	}
	
	public static void switchToWaitCursor() {
    // DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
		RootPanel.getBodyElement().setAttribute("cursor", "wait") ;
	}

	public static void switchToDefaultCursor() {
    RootPanel.getBodyElement().setAttribute("cursor", "default") ;
	}

	@Override
	public Widget asWidget()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
