package com.primege.client.widgets;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

import com.primege.client.loc.PrimegeViewConstants;
import com.primege.shared.database.EventData;
import com.primege.shared.database.FormDataData;

/**
 * Date widget
 * 
 */
public class EventDateControl extends HorizontalPanel implements ControlModel, HasChangeHandlers
{
	protected final PrimegeViewConstants constants = GWT.create(PrimegeViewConstants.class) ;
	
	/**
	 * Specific parameters manager
	 * 
	 * @author Philippe
	 */
	protected class LocalControlBase extends ControlBaseWithParams
	{
		public LocalControlBase(final String sPath) {
	  	super(sPath) ;
	  }
		
		/**
		 * Initialize a parameter from its name
		 * 
		 * @param sParam Name of parameter to initialize
		 * @param sValue Value to initialize this parameter with (can be null to "default value")
		 */
		public void fillParam(final String sParam, final String sValue)
		{
			if ((null == sParam) || "".equals(sParam))
				return ;
			
			// Don't forget to call superclass function in order to initialize global parameters,
			// such as mandatory status or default value 
			//
			super.fillParam(sParam, sValue) ;
			
			String sVal = "" ;
			if (null != sValue)
				sVal = sValue ;
			
			// can be YYYY or +YYYY (or -YYYY) from now
			// "minYear=-3|maxYear=+10"
			if      ("minYear".equalsIgnoreCase(sParam))
				_sDateFrom = setYearFromParam(_sDateFrom, sVal) ;
			else if ("maxYear".equalsIgnoreCase(sParam))
				_sDateTo = setYearFromParam(_sDateTo, sVal) ;
			else if ("minDate".equalsIgnoreCase(sParam))
				_sDateFrom = setDateFromParam(_sDateFrom, sVal) ;
			else if ("maxDaye".equalsIgnoreCase(sParam))
				_sDateTo = setDateFromParam(_sDateTo, sVal) ;
		}
	}
	
	protected LocalControlBase _base ;
	
  protected ListBox          _DayListBox ;
  protected ListBox          _MonthListBox ;
  protected ListBox          _YearListBox ;
  
  protected EventData        _event ;
  
  protected int              _iStartMonth ;  // What month appears on top of months list
  
  /** <code>true</code> if the form is being edited, <code>false</code> if it is new */
  protected boolean          _bEdited ;
  
  /** Lower limit */
  protected String           _sDateFrom ;
  /** Upper limit */
  protected String           _sDateTo ;
  
  /**
   * Default Constructor
   *
   */
  public EventDateControl(final EventData event, final String sPath)
  {
    super() ;
    
    _event        = event ;
    
    _base = new LocalControlBase(sPath) ;
    
    _DayListBox   = new ListBox() ;
    _MonthListBox = new ListBox() ;
    _YearListBox  = new ListBox() ;
    
    add(_DayListBox) ;
    add(_MonthListBox) ;
    add(_YearListBox) ;
    
    if (null == event)
    {
    	_sDateFrom = "00000000" ;
    	_sDateTo   = "99991231" ;
    }
    else
    {
    	_sDateFrom = event.getDateFrom() ;
    	_sDateTo   = event.getDateTo() ;
    }
    
    _DayListBox.addChangeHandler(new DayChangeHandler()) ;
    _MonthListBox.addChangeHandler(new MonthChangeHandler()) ;
    _YearListBox.addChangeHandler(new YearChangeHandler()) ;
  }
  
  public boolean isSingleData() {
  	return true ;
  }
  
  public void init(final String sContent)
  {
  	boolean bCheckValidity = true ;
  	
  	if ((null == sContent) || "".equals(sContent))
  	{
  		initFromDate(new Date(), bCheckValidity) ;
  		return ;
  	}
  	
  	int iYear  = getYearForDate(sContent) ;
  	int iMonth = getMonthForDate(sContent) ;
  	int iDay   = getDayForDate(sContent) ;
  	
  	// Initialize the ListBox
    //
  	
  	// If an old form is being edited, we have better not paint it red because it would not be valid for a new form
  	//
  	if (_bEdited)
  		bCheckValidity = false ;
  	
  	initFromInt(iDay, iMonth, iYear, bCheckValidity) ;
  }
  
  @SuppressWarnings("deprecation")
	public void initFromDate(final Date tDate, boolean bCheckValidity)
  {
  	int iYear  = -1 ;
  	int iMonth = -1 ;
  	int iDay   = -1 ;
  	
  	Date tWorkDate = tDate ;
  	
  	// Get the information to initialize from
  	//
  	if (null == tWorkDate)
  		tWorkDate = new Date() ;
  	
  	iYear  = tWorkDate.getYear() + 1900 ;
  	iMonth = tWorkDate.getMonth() + 1 ;
  	iDay   = tWorkDate.getDate() ;
  	
  	int iHour  = tWorkDate.getHours() ;
		int iMin   = tWorkDate.getMinutes() ;
  	
  	int iTimeZoneMinutes = tWorkDate.getTimezoneOffset() ;
  	
  	// Initialize the ListBox
    //
  	initFromInt(iDay, iMonth, iYear, bCheckValidity) ;
  }
  
  /**
   * Initialize the 3 ListBox
   *
   */
  public void initFromInt(final int iDay, final int iMonth, final int iYear, boolean bCheckValidity)
  {
  	initYearListBox(iYear) ;
  	initMonthListBox(iYear, iMonth) ;
  	initDayListBox(iYear, iMonth, iDay) ;
  	
  	if (bCheckValidity)
  		checkIfValid() ;
  }
   
  /**
   * Return selected date as a YYYYMMDD String
   * 
   * @return <code>""</code> if selected date is not valid, the YYYYMMDD formated date if it is.
   */
	public String getContentAsString()
	{
		if (false == isValid())
			return "" ;
		
		String sYear  = getSelectedYearAsString() ;
		String sMonth = getSelectedMonthAsString() ;
		String sDay   = getSelectedDayAsString() ;

		return sYear + sMonth + sDay ;
	}
  
  /**
   * Return a {@link FormDataData} which value is filled with content
   * 
   * @return <code>null</code> if selected date is not valid, a {@link FormDataData} if it is.
   */
	public FormDataData getContent()
	{
		String sDate = getContentAsString() ;
		if (sDate.isEmpty())
			return null ;
		
		FormDataData formData = new FormDataData() ;
		formData.setPath(_base.getPath()) ;
		formData.setValue(sDate) ;
		return formData ;
	}
	
	/**
	 * Returns selected month (January = 1, February = 2...)
	 * Returns -1 if no month selected
	 * 
	 * */
	protected int getSelectedMonth()
	{
		int iMonthIndex = _MonthListBox.getSelectedIndex() ;
		
		if (iMonthIndex < 0)
			return -1 ;
		
		return iMonthIndex + _iStartMonth ; 
	}
	
	/**
	 * Returns selected month (January = "01", February = "02"...)
	 * Returns "00" if no month selected
	 * 
	 * */
	protected String getSelectedMonthAsString()
	{
		String sMonth = "00" ;
		int iMonth = getSelectedMonth() ;
		if (iMonth > 0)
		{
			sMonth = Integer.toString(iMonth) ;
			if (sMonth.length() == 1)
				sMonth = "0" + sMonth ;
		}
		return sMonth ;
	}
	
	/**
	 * Returns selected year 
	 * 
	 * */
	protected int getSelectedYear()
	{
		String sYear = getSelectedYearAsString() ;
		return getIntFromString(sYear) ; 
	}
	
	/**
	 * Returns selected year as "YYYY"
	 * 
	 * */
	protected String getSelectedYearAsString()
	{
		return _YearListBox.getValue(_YearListBox.getSelectedIndex()) ;
	}
	
	/**
	 * Returns selected day 
	 * 
	 * */
	protected int getSelectedDay()
	{
		String sDay = getSelectedDayAsString() ;
		return getIntFromString(sDay) ; 
	}
	
	/**
	 * Returns selected day as "DD"
	 * 
	 * */
	protected String getSelectedDayAsString()
	{
		String sDay = _DayListBox.getValue(_DayListBox.getSelectedIndex()) ;
		
		// Since, the first day is displayed as "1" and not "01" we have to adapt it 
		//
		if ("".equals(sDay))
			sDay = "00" ;
		else if (sDay.length() == 1)
			sDay = "0" + sDay ;
		return sDay ;
	}

	/**
   * Initialize control elements from a content and a defalut value
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
   * Initialize control elements from a content 
   *
   * @param content       FormDataData used to initialize the control
   * @param sDefaultValue Configuration parameters, including default value in case there is no content
   */
	public void setContent(final FormDataData content)
	{
		String sDate = "" ;
		
		if (null == content)
			sDate = _base.getDefaultValue() ;
		else
			sDate = content.getValue() ;
		if (sDate.length() < 8)
			return ;
		
		init(sDate) ;
	}
	
	public void resetContent() {
		setContent(null) ;
	}
	
	/**
	 * Initialize Year list box
	 * 
	 * @param iYearToSelect year to force selection of if needed, or -1 if not needed
	 * 
	 * */
	@SuppressWarnings("deprecation")
	public void initYearListBox(int iYearToSelect) 
	{
		if (null == _YearListBox)
			return ;
		
		if (_YearListBox.getItemCount() > 0)
			_YearListBox.clear() ;
		
		int iMinYear = -1 ;
		int iMaxYear = -1 ;
		int iYearNow = -1 ;
		
		// Initialize years interval from event starting and ending dates
		//
		String sMinYear = _sDateFrom.substring(0, 4) ;
		if ("0000".equals(sMinYear))
			sMinYear = "" ;
		
		String sMaxYear = _sDateTo.substring(0, 4) ;
		if ("9999".equals(sMaxYear))
			sMaxYear = "" ;
				
		Date tNow = new Date() ;
		iYearNow = tNow.getYear() + 1900 ;
		
		if (false == sMinYear.isEmpty())
			iMinYear = getYearFromParam(sMinYear, iYearNow) ;
		if (false == sMaxYear.isEmpty())
			iMaxYear = getYearFromParam(sMaxYear, iYearNow) ;
		
		// If one or both years are not defined, define them according to "now"
		//
		if ((-1 == iMinYear) || (-1 == iMaxYear))
		{
			
			
			if ((-1 == iMinYear) && (-1 == iMaxYear))
			{
				iMinYear = iYearNow ;
				iMaxYear = iYearNow + 3 ;
			}
			else if (-1 == iMinYear)
			{
				if (iYearNow < iMaxYear)
					iMinYear = iYearNow ;
				else
					iMinYear = iMaxYear ;
			}
			else if (-1 == iMaxYear)
			{
				if (iYearNow > iMinYear)
					iMaxYear = iYearNow + 3 ;
				else
					iMaxYear = iMinYear + 3 ;
			}
		}
		
		// Finally, make certain that iYearToSelect (if any) is included in interval
		//
		if (-1 != iYearToSelect)
		{
			if (iYearToSelect < iMinYear)
				iMinYear = iYearToSelect ;
			if (iYearToSelect > iMaxYear)
				iMaxYear = iYearToSelect ;
		}
		
		// Adding the interval in list
		//
		for (int i = iMinYear ; i <= iMaxYear ; i++)
			_YearListBox.addItem("" + i) ;
		
		_YearListBox.setVisibleItemCount(1) ;
		
		if (-1 != iYearToSelect)
			_YearListBox.setItemSelected(iYearToSelect - iMinYear, true) ;
		else
			_YearListBox.setItemSelected(0, true) ;
	}

	/**
	 * Get a year count from a parameter in the form YYYY or +YYYY or -YYYY (can be +5 or -2350)
	 * 
	 * @param sParam   Absolute or relative year count
	 * @param iYearNow Current year
	 * 
	 * @return <code>-1</code> if a problem occurs, a count of years if not
	 */
	protected int getYearFromParam(final String sParam, final int iYearNow)
	{
		if ((null == sParam) || sParam.isEmpty())
			return -1 ;
		
		String sDeltaYear = sParam ;
		 
		char cSign = sParam.charAt(0) ;
		if (('+' == cSign) || ('-' == cSign))
			sDeltaYear = sParam.substring(1) ;
		
		int iYearCount = getIntFromString(sDeltaYear) ;
		if (-1 == iYearCount)
			return -1 ;
		
		if ('+' == cSign)
			return iYearNow + iYearCount ;
		if ('-' == cSign)
			return iYearNow - iYearCount ;
		
		return iYearCount ;
	}
	
	/**
	 * Set the YYYY part of the YYYYMMDD date string according to a parameter in the form YYYY or +YYYY or -YYYY (can be +5 or -2350)
	 * 
	 * @param sDate  Date to adapt
	 * @param sParam Parameter used to adapt the date from
	 * 
	 * @return The modified date
	 */
	@SuppressWarnings("deprecation")
	protected String setYearFromParam(final String sDate, final String sParam)
	{
		if ((null == sDate) || (null == sParam) || sParam.isEmpty())
			return sDate ;
		
		Date tNow = new Date() ;
		int iYearNow = tNow.getYear() + 1900 ;
		
		int iYear = getYearFromParam(sParam, iYearNow) ;
		
		if (sDate.length() <= 4)
			return "" + iYear ;
		
		return "" + iYear + sDate.substring(4) ;
	}
	
	/**
	 * Set a YYYYMMDD date string according to a parameter in the form YYYYMMDD or "now" or +YYYYMMDD or -YYYYMMDD
	 * 
	 * @param sDate  Date to adapt
	 * @param sParam Parameter used to adapt the date, 
	 * 
	 * @return The modified date
	 */
	@SuppressWarnings("deprecation")
	protected String setDateFromParam(final String sDate, final String sParam)
	{
		if ((null == sDate) || (null == sParam) || sParam.isEmpty())
			return sDate ;
		
		Date tNow = new Date() ;
		int iYearNow  = tNow.getYear() + 1900 ;
  	int iMonthNow = tNow.getMonth() + 1 ;
  	int iDayNow   = tNow.getDate() ;
  	
  	// Return current date
  	//
  	if ("now".equalsIgnoreCase(sParam))
  		return "" + iYearNow + getTwoDigitsInt(iMonthNow) + getTwoDigitsInt(iDayNow) ;
		
  	// If param doesn't start with a '+' or a '-', then it is the date to set to
  	//
  	char cSign = sParam.charAt(0) ;
  	if (('+' != cSign) && ('-' != cSign))
  		return sParam ;
  	
  	// When there, the parameter located after the + or - sign must be in the form yyyymmdd
  	// as yyyy years, mm months and dd days to add or substract from now
  	//
  	String sRealDate = sParam.substring(1) ;
  	if (sRealDate.length() != 8)
  		return sDate ;
  	
  	// Add or subtract years
  	//
		int iYears = getIntFromString(sRealDate.substring(0, 4)) ;
		if (iYears > 0)
		{
			if      ('+' == cSign)
				iYearNow += iYears ;
			else if ('-' == cSign)
				iYearNow -= iYears ;
		}
		
		// Add or subtract months
	  //
		int iMonths = getIntFromString(sRealDate.substring(4, 6)) ;
		if (iMonths > 0)
		{
			int iPlainYears = 0 ;
			int iRealMonths = iMonths ;
			if (iMonths > 12)
			{
				double dYears = iMonths / 12 ;
				iPlainYears = (int) Math.floor(dYears) ;
				iRealMonths = iMonths - (12 * iPlainYears) ;
			}
			if      ('+' == cSign)
			{
				iYearNow  += iPlainYears ;
				iMonthNow += iRealMonths ;
				if (iMonthNow > 12)
				{
					iYearNow++ ;
					iMonthNow -= 12 ;
				}
			}
			else if ('-' == cSign)
			{
				iYearNow  -= iPlainYears ;
				iMonthNow -= iRealMonths ;
				if (iMonthNow < 1)
				{
					iYearNow-- ;
					iMonthNow += 12 ;
				}
			}
		}
		
		// Add or subtract days
	  //
		int iDays = getIntFromString(sRealDate.substring(6, 8)) ;
		if (iDays > 0)
		{
			if      ('+' == cSign)
			{
				int iMaxDay = getMaxDayFormMonth(iMonthNow, iYearNow) ;
				for (int i = 0 ; i < iDays ; i++)
				{
					iDayNow++ ;
					if (iDayNow > iMaxDay)
					{
						iDayNow = 1 ;
						iMonthNow++ ;
						if (iMonthNow > 12)
						{
							iMonthNow = 1 ;
							iYearNow++ ;
						}
						iMaxDay = getMaxDayFormMonth(iMonthNow, iYearNow) ;
					}
				}
			}
			else if ('-' == cSign)
			{
				for (int i = 0 ; i < iDays ; i++)
				{
					iDayNow-- ;
					if (iDayNow < 1)
					{
						iMonthNow-- ;
						if (iMonthNow < 1)
						{
							iMonthNow = 12 ;
							iYearNow-- ;
						}
						iDayNow = getMaxDayFormMonth(iMonthNow, iYearNow) ;
					}
				}
			}
		}
		
		// Finally, make certain that iDayNow is valid for month and year
		//
		int iMaxDay = getMaxDayFormMonth(iMonthNow, iYearNow) ;
		if (iDayNow > iMaxDay)
			iDayNow = iMaxDay ;
		
		return getFourDigitsInt(iYearNow) + getTwoDigitsInt(iMonthNow) + getTwoDigitsInt(iDayNow) ;
	}
	
	protected String getTwoDigitsInt(int iValue)
	{
		if (iValue > 9)
			return "" + iValue ;
		return "0" + iValue ;
	}
	
	protected String getFourDigitsInt(int iValue)
	{
		if (iValue > 999)
			return "" + iValue ;
		
		if (iValue > 99)
			return "0" + iValue ;
		
		if (iValue > 9)
			return "00" + iValue ;
		
		return "000" + iValue ;
	}
	
	/**
	 * Initialize the year selection list box
	 * 
	 * @param iMinYear      lower limit of year to display
	 * @param iMaxYear      higher limit of year to display
	 * @param iYearToSelect year to select (should be between lower and higher limits)
	 */
	public void initYearListBox(int iMinYear, int iMaxYear, int iYearToSelect) 
	{
		if (null == _YearListBox)
			return ;
		
		if (_YearListBox.getItemCount() > 0)
			_YearListBox.clear() ;
				
		// Make certain that iYearToSelect (if any) is included in interval
		//
		if (-1 != iYearToSelect)
		{
			if (iYearToSelect < iMinYear)
				iMinYear = iYearToSelect ;
			if (iYearToSelect > iMaxYear)
				iMaxYear = iYearToSelect ;
		}
		
		// Adding the interval in list
		//
		for (int i = iMinYear ; i <= iMaxYear ; i++)
			_YearListBox.addItem("" + i) ;
		
		_YearListBox.setVisibleItemCount(1) ;
		
		if (-1 != iYearToSelect)
			_YearListBox.setItemSelected(iYearToSelect - iMinYear, true) ;
		else
			_YearListBox.setItemSelected(0, true) ;
	}
	
	/**
	 * Initialize Month list box
	 * 
	 * @param iSelectedYear  year for which months have to be set
	 * @param iMonthToSelect month to force selection of if needed, or -1 if not needed
	 * 
	 * */
	public void initMonthListBox(int iSelectedYear, int iMonthToSelect) 
	{
		if (null == _MonthListBox)
			return ;
		
		if (_MonthListBox.getItemCount() > 0)
			_MonthListBox.clear() ;
		
		int iMinMonth = 1 ;
		int iMaxMonth = 12 ;
		
		// Initialize years interval from from and to dates
		//
		int iMinYear = getYearForDate(_sDateFrom) ;
		int iMaxYear = getYearForDate(_sDateTo) ;
			
		// Event entirely scheduled during a single year, just insert the concerned months
		//
		if (iMinYear == iMaxYear)
		{
			iMinMonth = getMonthForDate(_sDateFrom) ;
			iMaxMonth = getMonthForDate(_sDateTo) ;
		}
		// Event scheduled over several years
		//
		else
		{
			if (iSelectedYear == iMinYear)
			{
				iMinMonth = getMonthForDate(_sDateFrom) ;
				iMaxMonth = 12 ;
			}
			else if (iSelectedYear == iMaxYear)
			{
				iMinMonth = 1 ;
				iMaxMonth = getMonthForDate(_sDateTo) ;
			}
		}
		
		// Finally, make certain that iMonthToSelect (if any) is included in interval
		//
		if (-1 != iMonthToSelect)
		{
			if (iMonthToSelect < iMinMonth)
				iMinMonth = iMonthToSelect ;
			if (iMonthToSelect > iMaxMonth)
				iMaxMonth = iMonthToSelect ;
		} 
		
		// Adding the interval in list
		//
		for (int i = iMinMonth ; i <= iMaxMonth ; i++)
			_MonthListBox.addItem(getMonthLabel(i)) ;
		
		_iStartMonth = iMinMonth ;
			
		_MonthListBox.setVisibleItemCount(1) ;
		
		if (-1 != iMonthToSelect)
			_MonthListBox.setItemSelected(iMonthToSelect - iMinMonth, true) ;
		else
			_MonthListBox.setItemSelected(0, true) ;
	}
	
	/**
	 * Initialize Day list box
	 * 
	 * @param iSelectedMonth month for which days have to be set
	 * @param iSelectedYear  year for which months have to be set
	 * @param iDayToSelect   day to force selection of if needed, or -1 if not needed
	 * 
	 * */
	public void initDayListBox(int iSelectedYear, int iSelectedMonth, int iDayToSelect) 
	{
		if (null == _DayListBox)
			return ;
		
		if (_DayListBox.getItemCount() > 0)
			_DayListBox.clear() ;
		
		int iMinDay = 1 ;
		int iMaxDay = getMaxDayFormMonth(iSelectedMonth, iSelectedYear) ;
		
		// Initialize years interval from from and to dates
		//
		int iMinYear = getYearForDate(_sDateFrom) ;
		int iMaxYear = getYearForDate(_sDateTo) ;

		int iMinMonth = getMonthForDate(_sDateFrom) ;
		int iMaxMonth = getMonthForDate(_sDateTo) ;

		// Event entirely scheduled during a single month, just insert the concerned days
		//
		if ((iMinYear == iMaxYear) && (iMinMonth == iMaxMonth))
		{
			iMinMonth = getDayForDate(_sDateFrom) ;
			iMaxMonth = getDayForDate(_sDateTo) ;
		}
		// Event scheduled over several years
		//
		else
		{
			// Starting month
			//
			if ((iSelectedYear == iMinYear) && (iSelectedMonth == iMinMonth))
			{
				iMinDay = getDayForDate(_sDateFrom) ;
				iMaxDay = getMaxDayFormMonth(iSelectedMonth, iSelectedYear) ;
			}
			// Ending month
			//
			else if ((iSelectedYear == iMaxYear) && (iSelectedMonth == iMaxMonth))
			{
				iMinDay = 1 ;
				iMaxDay = getMonthForDate(_sDateTo) ;
			}
		}
		
		// Finally, make certain that iDayToSelect (if any) is included in interval
		//
		if (-1 != iDayToSelect)
		{
			if (iDayToSelect < iMinDay)
				iMinDay = iDayToSelect ;
			if (iDayToSelect > iMaxDay)
				iMaxDay = iDayToSelect ;
		} 
		
		// Adding the interval in list
		//
		for (int i = iMinDay ; i <= iMaxDay ; i++)
			_DayListBox.addItem("" + i) ;
			
		_DayListBox.setVisibleItemCount(1) ;
		
		if (-1 != iDayToSelect)
			_DayListBox.setItemSelected(iDayToSelect - iMinDay, true) ;
		else
			_DayListBox.setItemSelected(0, true) ;
	}
	
	public String getMonthLabel(int iMonth)
	{
		if ((iMonth < 0) || (iMonth > 12))
			return "" ;
		
		if (1 == iMonth)
			return constants.generalMonthJanuary() ;
		if (2 == iMonth)
			return constants.generalMonthFebruary() ;
		if (3 == iMonth)
			return constants.generalMonthMarch() ;
		if (4 == iMonth)
			return constants.generalMonthApril() ;
		if (5 == iMonth)
			return constants.generalMonthMay() ;
		if (6 == iMonth)
			return constants.generalMonthJune() ;
		if (7 == iMonth)
			return constants.generalMonthJully() ;
		if (8 == iMonth)
			return constants.generalMonthAugust() ;
		if (9 == iMonth)
			return constants.generalMonthSeptember() ;
		if (10 == iMonth)
			return constants.generalMonthOctober() ;
		if (11 == iMonth)
			return constants.generalMonthNovember() ;
		if (12 == iMonth)
			return constants.generalMonthDecember() ;
		
		return "" ;
	}
			
	/**
	 * Return the year as int from a YYYYMMDD string
	 * 
	 * @param  sDate date as a YYYYMMDD string
	 * @return year if the date is valid, -1 if not 
	 * 
	 * */
	public int getYearForDate(String sDate) 
	{
		if (sDate.length() < 8)
			return -1 ;

		return getIntFromString(sDate.substring(0, 4)) ;
	}
	
	/**
	 * Return the month as int from a YYYYMMDD string
	 * 
	 * @param  sDate date as a YYYYMMDD string
	 * @return month (from 1 for January to 12 for December) if the date is valid, -1 if not 
	 * 
	 * */
	public int getMonthForDate(String sDate) 
	{
		if (sDate.length() < 8)
			return -1 ;

		int iMonth = getIntFromString(sDate.substring(4, 6)) ;
		if ((iMonth < 1) || (iMonth > 12))
			return -1 ;
		
		return iMonth ;
	}
	
	/**
	 * Return the day as int from a YYYYMMDD string
	 * 
	 * @param  sDate date as a YYYYMMDD string
	 * @return day if the date is valid, -1 if not 
	 * 
	 * */
	public int getDayForDate(String sDate) 
	{
		if (sDate.length() < 8)
			return -1 ;

		int iDay = getIntFromString(sDate.substring(6, 8)) ;
		if ((iDay < 1) || (iDay > 31))
			return -1 ;
		
		return iDay ;
	}
	
	/**
	 * Return the max day count for a given month (in a given year)
	 * 
	 * @param  iMonth month in the 1-12 interval
	 * @param  iYear  year - only useful if month is 2 (february)
	 *  
	 * @return days count in the 28-31 interval 
	 * 
	 * */
	public static int getMaxDayFormMonth(int iMonth, int iYear)
	{
		if ((4 == iMonth) || (6 == iMonth) || (9 == iMonth) || (11 == iMonth)) 
			return 30 ;
		if (2 == iMonth) 
		{
			if (iYear % 400 == 0 || (iYear % 4 == 0 && iYear % 100 != 0))
				return 28 ;		
			return 29 ;
		}
		return 31 ;
	}
	
	protected void checkIfValid()
	{
		if (isValid())
			paintItRegular() ;
		else
			paintItWarning() ;
	}
	
	/**
	 * Is the date valid (per se and considering constraints)?
	 */
	protected boolean isValid()
	{
		int iSelectedDay   = getSelectedDay() ;
		int iSelectedMonth = getSelectedMonth() ;
		int iSelectedYear  = getSelectedYear() ;
		
		if (false == isValidDate(iSelectedDay, iSelectedMonth, iSelectedYear))
			return false ;
			
		if (false == isInsideEvent(iSelectedDay, iSelectedMonth, iSelectedYear))
			return false ;
		
		return true ;
	}
	
	/**
	 * Is the date valid "per se" (for example September 31 is not valid)
	 */
	protected boolean isValidDate(int iDay, int iMonth, int iYear)
	{
		int iMaxDay = getMaxDayFormMonth(iMonth, iYear) ;
		
		return (iDay <= iMaxDay) ;
	}
	
	/**
	 * Check if a given day is inside the event (always <code>true</code> if no specified event)
	 * 
	 * @return <code>true</code> is the date is inside the event or not event was specified, <code>false</code> elsewhere
	 * 
	 * */
	protected boolean isInsideEvent(int iDay, int iMonth, int iYear)
	{
		int iMinYear = getYearForDate(_sDateFrom) ;
		int iMaxYear = getYearForDate(_sDateTo) ;
		
		// Surely outside
		//
		if ((iYear < iMinYear) || (iYear > iMaxYear))
			return false ;
		
		// Obviously inside
		//
		if ((iYear > iMinYear) && (iYear < iMaxYear))
			return true ;
		
		// Now, exploring the edges
		//
		if (iYear == iMinYear)
		{
			int iMinMonth = getMonthForDate(_sDateFrom) ;
			
			if (iMonth < iMinMonth)
				return false ;

			if (iMonth == iMinMonth)
			{
				int iMinDay = getDayForDate(_sDateFrom) ;
				if (iDay < iMinDay)
					return false ;
			}
		}
		
		if (iYear == iMaxYear)
		{
			int iMaxMonth = getMonthForDate(_sDateTo) ;
			
			if (iMonth > iMaxMonth)
				return false ;
		
			if (iMonth == iMaxMonth)
			{
				int iMaxDay = getDayForDate(_sDateTo) ;
			
				if (iDay > iMaxDay)
					return false ;
			}
		}
		
		return true ;
	}
	
	protected void paintItRegular() {
		paintIt("#FFFFFF") ;
	}
	
	protected void paintItWarning() {
		paintIt("#FF0000") ;
	}
	
	protected void paintIt(String sBgColor)
	{
		applyColor(_DayListBox,   sBgColor) ;
		applyColor(_MonthListBox, sBgColor) ;
		applyColor(_YearListBox,  sBgColor) ;
	}
	
	public void applyColor(ListBox listBox, String sBgColor) 
	{
		if (null == listBox)
			return ;
		
		Element contentElement = listBox.getElement() ;
		if (null == contentElement)
			return ;
		
		if ((null != sBgColor) && (false == "".equals(sBgColor)))
			contentElement.getStyle().setBackgroundColor(sBgColor) ;		
	}
	
	/**
	 * 
	 * 
	 * */
	protected class GlobalChangeHandler implements ChangeHandler 
	{
		public GlobalChangeHandler() 
		{
		}

		@Override
		public void onChange(ChangeEvent event)
		{
			int iSelectedDay   = getSelectedDay() ;
			int iSelectedMonth = getSelectedMonth() ;
			int iSelectedYear  = getSelectedYear() ;
			
			initDayListBox(iSelectedYear, iSelectedMonth, iSelectedDay) ;
			
			checkIfValid() ;
		}
	} //end GlobalChangeHandler
	
	/**
	 * Verification of the date invoked when day changed
	 * 
	 * */
	protected class DayChangeHandler implements ChangeHandler 
	{
		public DayChangeHandler() 
		{
		}

		@Override
		public void onChange(ChangeEvent event) 
		{
			reactOnChange() ;
		}
	} //end DayChargeHandler
	
	/**
	 * Verification of the date invoked when month changed
	 * 
	 * */
	protected class MonthChangeHandler implements ChangeHandler 
	{
		public MonthChangeHandler() 
		{
		}

		@Override
		public void onChange(ChangeEvent event) 
		{
			reactOnChange() ;
		}
	}//end MonthChargeHandler
	
	/**
	 * Verification of the date invoked when year changed
	 * 
	 * */
	protected class YearChangeHandler implements ChangeHandler 
	{
		public YearChangeHandler() 
		{
		}

		@Override
		public void onChange(ChangeEvent event) 
		{
			reactOnChange() ;
			initMonthListBox(getSelectedYear(), getSelectedMonth()) ;
		}
	} //end DayChargeHandler
	
	public void reactOnChange()
	{
		int iSelectedDay   = getSelectedDay() ;
		int iSelectedMonth = getSelectedMonth() ;
		int iSelectedYear  = getSelectedYear() ;
		
		initDayListBox(iSelectedYear, iSelectedMonth, iSelectedDay) ;
		
		checkIfValid() ;
	}
	
	public EventData getEvent() {
		return _event ;
	}
	public void reinitForEvent(EventData newEvent)
	{
		if (null == newEvent)
			return ;
		
		_event = newEvent ;
		
		_sDateFrom = newEvent.getDateFrom() ;
  	_sDateTo   = newEvent.getDateTo() ;
		
		// Reinit the 3 ListBox taking new event into account
		//
		init(getContentAsString()) ;
	}
	
  public String getPath() {   
  	return _base.getPath() ;
  }

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler)
	{
		_DayListBox.addChangeHandler(handler) ;
    _MonthListBox.addChangeHandler(handler) ;
    _YearListBox.addChangeHandler(handler) ;
		
		return null;
	}
	
	public ControlBase getControlBase() {
		return _base ;
	}
	
	public void setInitFromPrev(boolean bInitFromPrev) {
		_base.setInitFromPrev(bInitFromPrev) ;
	}
	
	public boolean getInitFromPrev() {
		return _base.getInitFromPrev() ;
	}
	
	public void setEdited(boolean bEdited) {
		_bEdited = bEdited ;
	}
	
	/**
	 * Get an integer from a String
	 * 
	 * @param sValue The string to parse
	 * 
	 * @return <code>-1</code> if parsing failed, the parsed integer if OK
	 */
	protected int getIntFromString(final String sValue)
	{
		try {
			return Integer.parseInt(sValue) ;
		} catch (NumberFormatException e) {
			return -1 ;
		}
	}
}
