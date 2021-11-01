package com.primege.client.mvp;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.primege.client.util.FormControl;
import com.primege.client.util.FormControlOptionData;

/**
 * Model of presenter from the presenter/view model for dashboards
 *
 */
public interface DashboardInterfaceModel extends PrimegeBaseInterface
{
	public void resetForRefresh() ;
		
	public HasClickHandlers getLeaveEvent() ;
	public HasClickHandlers getRefreshEvent() ;
		
	public HasChangeHandlers getLocalPivot(final String sLocalPivotPath) ;
	
	public String getPivotInformation() ;
	public String getPivotInformation(final String sPivotPath) ;
		
	public void setCaption(String sCaption) ;
	public void setBorderWidth(String sBorderWidth) ;
	public void setColDescription(final String sColPath, final String sColType, final String sColBgColor, final String sHeadFormat) ;
	public void insertNewPivotControl(final String sControlPath, final String sControlCaption, final String sControlType) ;
	public void endOfPivot() ;
		
	public void addRecord(final int iSiteId, final String sColBgColor, final String sCaption) ;
	public void endOfRecord() ;
		
	public void insertNewBlock(final String sBgColor, final String sCaption) ;
	public void endOfBlock() ;
		
	public void insertNewDataRow(final String sDataPath, final String sDataCaption, final String sDataType, final ArrayList<FormControlOptionData> aOptions) ;
	
	public int  addTable(final String sBgColor, final String sCaption) ;
	public void setColumnDescription(final String sColCaption, final String sColPath, final String sColType, final String sColBgColor, final String sColFormat) ;
	public void endOfTable() ;
	
	public int  addChart(final String sPivotPath, final String sPivotType, final String sBgColor, final String sCaption, final String sWidth, final String sHeight) ;
	public void endOfChart() ;
	public void clearChart(final int iChartIndex) ;
	
	public FormControl getLocalPivotControl(final String sLocalPivotPath) ;
}
