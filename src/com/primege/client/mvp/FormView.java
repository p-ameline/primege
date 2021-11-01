package com.primege.client.mvp;

import java.util.Date;
import java.util.ArrayList;

import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.ui.Widget;

import com.google.inject.Inject;

import com.primege.client.global.PrimegeSupervisor;
import com.primege.client.util.FormControl;
import com.primege.client.util.FormControlOptionData;
import com.primege.client.widgets.EventCityControl;
import com.primege.client.widgets.EventDateControl;
import com.primege.client.widgets.FormBlockPanel;
import com.primege.shared.GlobalParameters;
import com.primege.shared.database.CityData;
import com.primege.shared.database.EventData;
import com.primege.shared.database.FormDataData;

public class FormView extends FormViewModel implements FormPresenter.Display
{	
	protected EventData           _event ;	
	protected ArrayList<CityData> _aCities ;
	
	@Inject
	public FormView(final PrimegeSupervisor supervisor)
	{
		super(supervisor) ;
	}
	
	/** 
	 * Insert a new control to the form
	 * 
	 * @param sControlPath    control's path (arborescent identifier)
	 * @param content         control's description
	 * @param sControlCaption control's caption
	 * @param sControlType    control's type (Edit, Buttons...)
	 * @param sControlSubtype control's sub-type (for example FreeText or Number for Edit control)
	 * @param sControlUnit    control's unit for numbers
	 * @param sControlValue   control's initialization value
	 * @param aOptions        list of options
	 * @param sControlStyle   CSS style for the control
	 * @param sCaptionStyle   CSS style for the label
	 * @param bInitFromPrev   <code>true</code> if the process is to initialize information from the previous form
	 * @param bInBlockCell    <code>true</code> if the control is to be inserted in the block, <code>false</code> if in label's cell
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void insertNewControl(final String sControlPath, final ArrayList<FormDataData> aContent, final String sControlCaption, final String sControlType, final String sControlSubtype, final String sControlUnit, final String sControlValue, final ArrayList<FormControlOptionData> aOptions, final String sControlStyle, final String sCaptionStyle, final boolean bInitFromPrev, final String sExclusion, final boolean bInBlockCell, final boolean bInPdfWhenEmpty, FormBlockPanel masterBlock)
	{
		FormBlockPanel referenceBlock = masterBlock ;
		if (null == referenceBlock)
			referenceBlock = _formPannel ;
		
		// Get the panel to add the control to
		// 
		FormBlockPanel currentBlock = getCurrentFormPanel(referenceBlock) ;
		if (null == currentBlock)
			return ;

		if ("EventDate".equalsIgnoreCase(sControlType))
		{
			EventDateControl dateControl = new EventDateControl(_event, sControlPath) ;
			
			if ((null != sControlStyle) && false == "".equals(sControlStyle))
				dateControl.addStyleName(sControlStyle) ;
			
			dateControl.setInitFromPrev(bInitFromPrev) ;
			
			FormDataData content = getSingleInformationContent(aContent) ;
			
			// If no date specified, initialize with "now" or "yesterday" 
			//
			if (null == content)
			{
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
				dateControl.setContent(fakeContent, sControlValue) ;
			}
			else
				dateControl.setContent(content, sControlValue) ;
			
			_aControls.add(new FormControl(dateControl.getControlBase(), dateControl, content, sExclusion)) ;
			currentBlock.insertControl(sControlCaption, sCaptionStyle, dateControl, dateControl.getControlBase(), bInBlockCell) ;
		}
		else if ("EventCity".equalsIgnoreCase(sControlType))
		{
			FormDataData content = getSingleInformationContent(aContent) ;
			
			EventCityControl cityControl = new EventCityControl(_aCities, sControlPath) ;
			cityControl.setContent(content, sControlValue) ;
			
			if ((null != sControlStyle) && false == "".equals(sControlStyle))
				cityControl.addStyleName(sControlStyle) ;
			
			cityControl.setInitFromPrev(bInitFromPrev) ;
			
			_aControls.add(new FormControl(cityControl.getControlBase(), cityControl, content, sExclusion)) ;
			currentBlock.insertControl(sControlCaption, sCaptionStyle, cityControl, cityControl.getControlBase(), bInBlockCell) ;
		}
		else
			insertNewGenericControl(sControlPath, aContent, sControlCaption, sControlType, sControlSubtype, sControlUnit, sControlValue, aOptions, sControlStyle, sCaptionStyle, bInitFromPrev, sExclusion, bInBlockCell, bInPdfWhenEmpty, referenceBlock) ;
	}
	
	@Override
	public HasChangeHandlers getCityChanged() 
	{
		Widget widget = getControlForPath("$city$") ;
		if (null == widget)
			return null ;
		
		EventCityControl cityControl = (EventCityControl) widget ;
		return cityControl ;
	}
	
	@Override
	public HasChangeHandlers getSiteChanged()
	{
		return null ;
	}
	
	@Override
	public void setEventDate(String sStartDate) 
	{
		Widget dateWidget = getControlForPath("$date$") ;
		if (null == dateWidget)
			return ;
	
		EventDateControl dateControl = (EventDateControl) dateWidget ;
		
		FormDataData fakeContent = new FormDataData() ;
		fakeContent.setValue(sStartDate) ;
		
		dateControl.setContent(fakeContent, "") ;
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
	public void initEventDate(Date tDate) {	
	}
	
	@Override
	public void setEventCity(int iCityId) {
	}
					
	@Override
	public Widget asWidget() {
		return this;
	}
}
