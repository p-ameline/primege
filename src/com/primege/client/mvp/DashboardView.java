package com.primege.client.mvp;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.google.inject.Inject;

import com.primege.client.global.PrimegeSupervisor;
import com.primege.client.util.DashboardCol;
import com.primege.client.util.DashboardLine;
import com.primege.client.util.FormControl;
import com.primege.client.widgets.EventCityControl;
import com.primege.client.widgets.EventDateControl;
import com.primege.shared.GlobalParameters;
import com.primege.shared.database.CityData;
import com.primege.shared.database.EventData;
import com.primege.shared.database.FormData;
import com.primege.shared.database.FormDataData;
import com.primege.shared.model.CityForDateBlock;
import com.primege.shared.model.FormBlock;

public class DashboardView extends DashboardViewModel implements DashboardPresenter.Display
{	
	// private final PrimegeViewConstants constants = GWT.create(PrimegeViewConstants.class) ;

	protected EventData         _event ;	
	protected ArrayList<CityData>  _aCities ;
	
	@Inject
	public DashboardView(final PrimegeSupervisor supervisor)
	{
		super(supervisor) ;		
	}
	
	/** 
	 * Insert a new control to the pivot pannel
	 * 
	 * @param sControlPath    control's path (arborescent identifier)
	 * @param sControlCaption control's caption
	 * @param sControlType    control's type (Edit, Buttons...)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void insertNewPivotControl(final String sControlPath, final String sControlCaption, final String sControlType)
	{
		// Get the panel to add the control to
		// 
		if (null == _selectionPanel)
			return ;
		
		if ("EventDate".equalsIgnoreCase(sControlType))
		{
			EventDateControl dateControl = new EventDateControl(_event, sControlPath) ;
			dateControl.addStyleName("dashboardPivotControl") ;
			
			// Initialize with "now" or "yesterday" 
			//
			FormDataData fakeContent = new FormDataData() ;
				
			Date tNow = new Date() ;
				
			String sContentToInitialize = "" ;
				
			// Before 10 AM, we suppose that the event occurred the day before
			//
			if (tNow.getHours() < 10)
			{
				long lTime = tNow.getTime() ;
				long lM24H = 1000 * 60 * 60 * 24 ;
				long lYest = lTime - lM24H ;
					
				Date tYesterday = new Date(lYest) ;
				sContentToInitialize = GlobalParameters.getDateAsString(tYesterday) ;
			}
			else
				sContentToInitialize = GlobalParameters.getDateAsString(tNow) ;
				
			fakeContent.setValue(sContentToInitialize) ;
			dateControl.setContent(fakeContent, "") ;
			
			_aPivotControls.add(new FormControl(dateControl, null, sControlPath, "")) ;
			
			addPivotLabel(sControlCaption) ;
			_selectionPanel.add(dateControl) ;
		}
		else if ("EventCity".equalsIgnoreCase(sControlType))
		{
			EventCityControl cityControl = new EventCityControl(_aCities, sControlPath) ;
			cityControl.addStyleName("dashboardPivotControl") ;
			
			_aPivotControls.add(new FormControl(cityControl, null, sControlPath, "")) ;
			
			addPivotLabel(sControlCaption) ;
			_selectionPanel.add(cityControl) ;
		}
	}
	
	/** 
	 * Insert all information from a CityForDateBlock in a new column
	 * 
	 * A CityForDateBlock contains all the sites' information with a common city and a common date
	 * 
	 */
	@Override
	public void insertColumn(final CityForDateBlock blockForCol)
	{
		if (null == blockForCol)
			return ;
		
		// Create a column manager and display the column header 
		//
		DashboardCol newCol = new DashboardCol(blockForCol.getCityId(), blockForCol.getDate(), _iNextCol) ;
		_aCols.add(newCol) ;
		
		displayColumHeader(newCol) ;
		
		_iNextCol++ ;
		
		if (blockForCol.isEmpty())
			return ;
		
		// Display each form information for this column
		//
		for (FormBlock formBlock : blockForCol.getInformation())
			insertRowForSite(formBlock, newCol.getCol()) ;
	}
	
	/** 
	 * Reset everything to display a new dashboard
	 * 
	 */
	@Override
	public void resetAll()
	{
		resetAll4Model() ;
	}
	
	protected void displayColumHeader(DashboardCol newCol)
	{
		if (null == newCol)
			return ;
		
		String sToDisplay = "" ;
		
		// What must be displayed is a date
		//
		if ("$date$".equals(_colDesc.getDisplayedData()))
		{
			String sDate   = newCol.getDate() ;
			String sFormat = _colDesc.getHeadFormat() ;
			
			if ((null == sFormat) || "".equals(sFormat))
				sFormat = "DD/MM/YYYY" ;
			
			sFormat = sFormat.replace("YYYY", sDate.substring(0, 4)) ;
			sFormat = sFormat.replace("MM", sDate.substring(4, 6)) ;
			sFormat = sFormat.replace("DD", sDate.substring(6, 8)) ;
			
			sToDisplay = sFormat ;
		}
		// What must be displayed is a city name
		//
		else if ("$city$".equals(_colDesc.getDisplayedData()))
		{
			CityData city = _aCities.get(newCol.getCityId() - 1) ;
			if (null != city)
				sToDisplay = city.getLabel() ;
		}
		
		_dashboardPannel.setWidget(0, newCol.getCol() + _iFirstDataCol - 1, new Label(sToDisplay)) ;
	}
	
	/** 
	 * Insert all information from a given form in current column
	 * 
	 */
	public void insertRowForSite(final FormBlock formBlock, final int iDashCol)
	{
		if ((null == formBlock) || (iDashCol < 0))
			return ;
		
		ArrayList<FormDataData> blockInformation = formBlock.getInformation() ;
		if ((null == blockInformation) || blockInformation.isEmpty())
			return ;
		
		FormData formData = (FormData) formBlock.getDocumentLabel() ;
		if (null == formData)
			return ;
		
		int iSiteId = formData.getSiteId() ;
		
		for (FormDataData information : blockInformation)
		{
			DashboardLine dashLine = getDashboardLine(information, iSiteId) ;
			
			if (null != dashLine)
			{
				String sValue  = getDisplayed(dashLine, information) ;
				int    iRow    = dashLine.getLine() ;
				int    iColumn = iDashCol + dashLine.getRefCol() ;
				
				// Insert information in table
				//
				FlexTable table = dashLine.getFlexTable() ;
				table.setWidget(iRow, iColumn, new Label(sValue)) ;
				
				// Format the new cell
				//
				if ("Integer".equals(dashLine.getType()))
					table.getFlexCellFormatter().setHorizontalAlignment(iRow, iColumn,  HasHorizontalAlignment.ALIGN_RIGHT) ;
				else
					table.getFlexCellFormatter().setHorizontalAlignment(iRow, iColumn,  HasHorizontalAlignment.ALIGN_CENTER) ;
			}
		}
	}
	
	@Override
	public void setEvent(EventData event) {
		_event = event ;
	}
	
	@Override
	public void setCities(ArrayList<CityData> aCities) {
		_aCities = aCities ;
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}
}
