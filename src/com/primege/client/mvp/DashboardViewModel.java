package com.primege.client.mvp;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.google.inject.Inject;

import com.googlecode.gwt.charts.client.ChartObject;
import com.googlecode.gwt.charts.client.ChartWrapper;
import com.googlecode.gwt.charts.client.corechart.ComboChartOptions;

import com.primege.client.global.PrimegeSupervisorModel;
import com.primege.client.loc.PrimegeViewConstants;
import com.primege.client.util.DashboardCol;
import com.primege.client.util.DashboardColDescription;
import com.primege.client.util.DashboardLine;
import com.primege.client.util.FormControl;
import com.primege.client.util.FormControlOptionData;
import com.primege.client.widgets.ControlModel;
import com.primege.shared.database.FormDataData;

public class DashboardViewModel extends PrimegeBaseDisplay implements DashboardInterfaceModel
{	
	private final PrimegeViewConstants constants = GWT.create(PrimegeViewConstants.class) ;

	protected VerticalPanel            _globalPanel ;
	protected FlowPanel                _titlePanel ;
	protected FlowPanel                _selectionPanel ;
	protected FlexTable                _dashboardPannel ;
	protected VerticalPanel            _tablesPanel ;
	
	protected ArrayList<FlexTable>                       _aTables = new ArrayList<FlexTable>() ;
	protected ArrayList<ChartWrapper<ComboChartOptions>> _aCharts = new ArrayList<ChartWrapper<ComboChartOptions>>() ;
	
	protected Button                   _submitButton ;
	protected Button                   _leaveButton ;
	
	protected ArrayList<FormControl>   _aPivotControls = new ArrayList<FormControl>() ;
	protected ArrayList<FormControl>   _aLocalPivotCtr = new ArrayList<FormControl>() ;
	protected ArrayList<DashboardLine> _aLines         = new ArrayList<DashboardLine>() ;
	protected ArrayList<DashboardCol>  _aCols          = new ArrayList<DashboardCol>() ;
	
	protected DashboardColDescription  _colDesc ;
	
	protected int                      _iNextCol ;
	protected int                      _iFirstDataCol = 3 ;
	
	// Cursors for dashboard creation
	//
	protected int                      _currentSiteId ;
	protected int                      _iCurrentGlobalRow ;
	protected int                      _iCurrentRecordRow ;
	protected int                      _iCurrentBlockRow ;
	protected int                      _iCurrentBlockCol ;
	
	protected final PrimegeSupervisorModel _supervisor ;
	
	@Inject
	public DashboardViewModel(final PrimegeSupervisorModel supervisor)
	{
		_supervisor = supervisor ;
		
		_colDesc   = null ;    
		
		_iNextCol  = 1 ;
		
		_currentSiteId     = -1 ;
		// _currentRecord     = null ;
		// _currentBlock      = null ;
		_iCurrentGlobalRow = 1 ;    // 0 is reserved for columns headers
		_iCurrentRecordRow = -1 ;
		_iCurrentBlockRow  = -1 ;
		_iCurrentBlockCol  = 0 ;
		
		// Initialize form panel
		//
		_dashboardPannel   = new FlexTable() ;
		// _formPannel.addStyleName("encounterContextPanels") ;
			
		// Initialize the tables panel
		//
		_tablesPanel = new VerticalPanel() ;
		
		// Initialize control buttons
		//
		_submitButton = new Button(constants.generalDisplay()) ;
		_submitButton.addStyleName("button red dashboardSubmitButton") ;
		_leaveButton = new Button(constants.generalLeave()) ;
		_leaveButton.addStyleName("button red dashboardLeaveButton") ;
		
		_titlePanel     = new FlowPanel() ;
		
		_selectionPanel = new FlowPanel() ;
		_selectionPanel.addStyleName("formContextPanelBlock") ;
		_selectionPanel.add(_leaveButton) ;
		
		// Initialize the global panel
		//
		_globalPanel = new VerticalPanel() ;
		_globalPanel.setWidth("100%") ;
		// _globalPanel.addStyleName("encounterGlobalPanel") ;
		_globalPanel.add(_titlePanel) ;
		_globalPanel.add(_selectionPanel) ;
		_globalPanel.add(_dashboardPannel) ;
		_globalPanel.add(_tablesPanel) ;
		
		initWidget(_globalPanel) ;
		
		// Initialize "this"
		//
		setWidth("100%") ;
	}

	@Override
	public void setCaption(String sCaption) 
	{
		Label captionLabel = new Label(sCaption) ;
		_titlePanel.add(captionLabel) ;
	}
	
	@Override
	public void setBorderWidth(String sBorderWidth)
	{
		if ((null == sBorderWidth) || "".equals(sBorderWidth))
			return ;
		
		int iBorderWidth = Integer.parseInt(sBorderWidth) ;
		
		_dashboardPannel.setBorderWidth(iBorderWidth) ;
	}
	
	/** 
	 * Insert a new control to the pivot panel... code to be specified by derived classes 
	 * 
	 * @param sControlPath    control's path (arborescent identifier)
	 * @param sControlCaption control's caption
	 * @param sControlType    control's type (Edit, Buttons...)
	 */
	@Override
	public void insertNewPivotControl(final String sControlPath, final String sControlCaption, final String sControlType)
	{
		// Derived class should provide their specific implementation
	}
	
	/** 
	 * Create a new control as a local pivot 
	 * 
	 * @param sControlPath    control's path (arborescent identifier)
	 * @param sControlType    control's type (Edit, Buttons...)
	 */
	public void createLocalPivotControl(final String sControlPath, final String sControlType)
	{
		// Derived class should provide their specific implementation
	}
	
	public void addPivotLabel(String sCaption)
	{
		if ((null == sCaption) || "".equals(sCaption))
			return ;
		
		Label pivotLabel = new Label(sCaption) ;
		pivotLabel.addStyleName("dashboardPivotLabel") ;
		
		_selectionPanel.add(pivotLabel) ;
	}
	
	/** 
	 * Signal that all selection controls for pivot information have been added
	 * The "refresh" button can now be inserted
	 * 
	 */
	public void endOfPivot() {
		_selectionPanel.add(_submitButton) ;
	}
	
	/** 
	 * Reset everything to display a new dashboard
	 * 
	 */
	public void resetAll4Model()
	{
		_dashboardPannel.removeAllRows() ;
		_tablesPanel.clear() ;
		_titlePanel.clear() ;
		_selectionPanel.clear() ;
		_selectionPanel.add(_leaveButton) ;
		
		_aPivotControls.clear() ;
		_aLocalPivotCtr.clear() ;
		_aLines.clear() ;
		_aCols.clear() ;
		
		_aTables.clear() ;
		
		_colDesc = null ;
		
		_iNextCol  = 1 ;
		
		_currentSiteId     = -1 ;
		_iCurrentGlobalRow = 1 ;
		_iCurrentRecordRow = -1 ;
		_iCurrentBlockRow  = -1 ;
	}
	
	/** 
	 * Reset everything to display a new dashboard
	 * 
	 */
	@Override
	public void resetForRefresh()
	{
		// Remove all datas
		//
		int iNbCols = _aCols.size() ; 
		if (iNbCols > 0)
		{
			// Remove headers
			//
			try
			{ _dashboardPannel.removeCells(0, _iFirstDataCol, iNbCols) ;
			} catch (IndexOutOfBoundsException e) {
			}
			
			// Remove data
			//
			for (DashboardLine line : _aLines)
			{
				try
				{ _dashboardPannel.removeCells(line.getLine(), line.getRefCol() + 1, iNbCols) ;
				} catch (IndexOutOfBoundsException e) {
				}
			}
			
			_aCols.clear() ;
		}
		
		_iNextCol  = 1 ;
		
		// Empty tables
		//
		if (false == _aTables.isEmpty())
			for (FlexTable table : _aTables)
				table.removeAllRows() ;
	}

	@Override
	public void setColDescription(final String sColPath, final String sColType, final String sColBgColor, final String sHeadFormat) 
	{
		if (null == _colDesc)
			_colDesc = new DashboardColDescription(sColPath, sColType, sColBgColor, sHeadFormat) ;
	}
	
	/** 
	 * Insert a new record descriptor (information from a specific site)
	 * 
	 * @param iSiteId  site identifier this record is to display information from
	 * @param sBgColor block's background color
	 * @param sCaption block's title
	 */
	@Override
	public void addRecord(final int iSiteId, final String sBgColor, final String sCaption)
	{
		_currentSiteId = iSiteId ;
		
		_dashboardPannel.setWidget(_iCurrentGlobalRow, 0, new Label(sCaption)) ;
		applyStyle(_dashboardPannel.getWidget(_iCurrentGlobalRow, 0), sBgColor) ;
		
		// Initialize cursors
		//
		_iCurrentRecordRow = _iCurrentGlobalRow ; // The first block will start at same line 
		_iCurrentBlockRow  = _iCurrentGlobalRow ; // Idem for the first data line for first block
	}
	
	@Override
	public void endOfRecord()
	{
		int iMaxRow = _iCurrentBlockRow ;
		if (_iCurrentRecordRow > _iCurrentBlockRow)  // Integer.max(int, int) not supported by GWT
			iMaxRow = _iCurrentRecordRow ;
		
		if (_iCurrentGlobalRow < iMaxRow)
		{
			int iRowSpan = iMaxRow - _iCurrentGlobalRow + 1 ;
		
			// Set height of record's title block 
			//
			_dashboardPannel.getFlexCellFormatter().setRowSpan(_iCurrentGlobalRow, 0, iRowSpan) ;
			
			_iCurrentGlobalRow = iMaxRow + 1 ;
		}
		else
			_iCurrentGlobalRow++ ;
		
		_iCurrentRecordRow = -1 ;
		_iCurrentBlockRow  = -1 ;
	}
	
	/** 
	 * Insert a new block to the form
	 * 
	 * @param sBgColor block's background color
	 * @param sCaption block's sCaption
	 */
	@Override
	public void insertNewBlock(final String sBgColor, final String sCaption)
	{
		// The block should be in column 1, but when the record will span, it will
		// push it one column away, so we have to insert it to line 0 instead
		//
		_iCurrentBlockCol = 1 ;
		if (_iCurrentRecordRow > _iCurrentGlobalRow)
			_iCurrentBlockCol = 0 ;
		
		_dashboardPannel.setWidget(_iCurrentRecordRow, _iCurrentBlockCol, new Label(sCaption)) ;
		applyStyle(_dashboardPannel.getWidget(_iCurrentRecordRow, _iCurrentBlockCol), sBgColor) ;
		
		_iCurrentBlockRow = _iCurrentRecordRow ;  // The first data will start at same line
	}
	
	/** 
	 * Signal that current block already received all its elements
	 * 
	 */
	@Override
	public void endOfBlock() 
	{
		if (_iCurrentRecordRow < _iCurrentBlockRow)
		{
			int iRowSpan = _iCurrentBlockRow - _iCurrentRecordRow + 1 ;
		
			int iBlockCol = 1 ;
			if (_iCurrentRecordRow > _iCurrentGlobalRow)
				iBlockCol = 0 ;
			
			// Set height of record's title block 
			//
			_dashboardPannel.getFlexCellFormatter().setRowSpan(_iCurrentRecordRow, iBlockCol, iRowSpan) ;
			
			_iCurrentRecordRow = _iCurrentBlockRow + 1 ;
		}
		else
			_iCurrentRecordRow++ ;
		
		_iCurrentBlockRow  = -1 ;
	}
	
	/** 
	 * Insert a new row
	 * 
	 * @param iSiteId  site identifier this record is to display information from
	 * @param sBgColor block's background color
	 * @param sCaption block's title
	 */
	@Override
	public void insertNewDataRow(final String sDataPath, final String sDataCaption, final String sDataType, final ArrayList<FormControlOptionData> aOptions)
	{
		// The block should be in column 1, but when the record will span, it will
		// push it one column away, so we have to insert it to line 0 instead
		//
		int iDataCol = _iCurrentBlockCol + 1 ;
		if (_iCurrentBlockRow > _iCurrentRecordRow)
				// iDataCol = _iCurrentBlockCol ;
			iDataCol = 0 ;
		
		DashboardLine newLine = new DashboardLine(_currentSiteId, sDataPath, sDataType, _dashboardPannel, _iCurrentBlockRow, iDataCol, aOptions) ;
		_aLines.add(newLine) ;
		
		_dashboardPannel.setWidget(_iCurrentBlockRow, iDataCol, new Label(sDataCaption)) ;
		
		_iCurrentBlockRow++ ;
	}

	/** 
	 * Insert a new table
	 * 
	 * @param sBgColor  block's background color
	 * @param sCaption  block's title
	 */
	@Override
	public int addTable(final String sBgColor, final String sCaption)
	{
		Label captionLabel = new Label(sCaption) ;
		captionLabel.addStyleName("dashboardTableLabel") ;
		
		_tablesPanel.add(captionLabel) ;
		
		FlexTable newTable = new FlexTable() ;
		newTable.addStyleName("dashboardTable") ;
		
		_aTables.add(newTable) ;
		_tablesPanel.add(newTable) ;
		
		return _aTables.size() - 1 ;
	}
	
	/** 
	 * Insert column to the new table
	 * 
	 * @param sBgColor block's background color
	 * @param sCaption block's title
	 */
	@Override
	public void setColumnDescription(final String sColCaption, final String sColPath, final String sColType, final String sColBgColor, final String sColFormat)
	{
		
	}
	
	@Override
	public void endOfTable() {
	}
	
	/** 
	 * Insert a new chart
	 * 
	 * @param sBgColor  block's background color
	 * @param sCaption  block's title
	 */
	@Override
	public int addChart(final String sPivotPath, final String sPivotType, final String sBgColor, final String sCaption, final String sWidth, final String sHeight)
	{
		// First, check if a new control is needed for the pivot
		//
		boolean bMustCreateControl = (null == getPivotControl(sPivotPath)) ;
		
		FlowPanel chartHeaderPanel = null ;
		
		// Insert caption
		//
		if (false == bMustCreateControl)
		{
			Label captionLabel = new Label(sCaption) ;
			captionLabel.addStyleName("dashboardChartLabel") ;
			_tablesPanel.add(captionLabel) ;
		}
		else
		{
			chartHeaderPanel = new FlowPanel() ;
			
			// Insert label
			//
			Label captionLabel = new Label(sCaption) ;
			captionLabel.addStyleName("dashboardChartHeaderLabel") ;
			chartHeaderPanel.add(captionLabel) ;
			
			// Pivot control creation is deferred until the chart receive an index so we can get a local pivot path
			//
			_tablesPanel.add(chartHeaderPanel) ;
		}
		
		// Insert chart
		//
		ChartWrapper<ComboChartOptions> newChartWrapper = new ChartWrapper<ComboChartOptions>() ;
		newChartWrapper.addStyleName("dashboardChart") ;
		
		newChartWrapper.setWidth(sWidth + "px") ;
		newChartWrapper.setHeight(sHeight + "px") ;
		
		_aCharts.add(newChartWrapper) ;
		_tablesPanel.add(newChartWrapper) ;
		
		int iIndex = _aCharts.size() - 1 ; 
			
		// Now that we got an index, we can create pivot control
		//
		if (bMustCreateControl)
		{
			String sLocalPivotPath = getLocalPivotPath(sPivotPath, iIndex, false) ; 
			
			// Ask for the creation of a local pivot
			//
			createLocalPivotControl(sLocalPivotPath, sPivotType) ;
			
			// Get control from its path to add it to chart header panel
			//
			FormControl formControl = getLocalPivotControl(sLocalPivotPath) ;
			if (null != formControl)
				chartHeaderPanel.add(formControl.getWidget()) ;
			
			// Since the filling the pivot is mandatory for displaying the chart, better hide it as long as it is empty
			//
			newChartWrapper.setVisible(false) ;
		}
		
		return iIndex ;
	}
	
	/**
	 * Clear chart at a given index
	 */
	@Override
	public void clearChart(final int iChartIndex)
	{
		if ((iChartIndex < 0) || (iChartIndex >= _aCharts.size()))
			return ;
		
		ChartWrapper<ComboChartOptions> chartWrapper = _aCharts.get(iChartIndex) ;
		
		ChartObject chart = chartWrapper.getChart() ;
		if (null == chart)
			return ;
		
		chart.clearChart() ;
	}
	
	@Override
	public void endOfChart() {
	}
	
	/** 
	 * Set the background color for a Widget
	 * 
	 */
	protected void applyStyle(Widget cellElementWidget, final String sBackgroundColor) 
	{
		if ((null == cellElementWidget) || (null == sBackgroundColor) || "".equals(sBackgroundColor))
			return ;
		
		Element cellContentElement = cellElementWidget.getElement() ;
		if (null == cellContentElement)
			return ;
		
		Element cellElement = cellContentElement.getParentElement() ;
		if (null == cellElement)
			return ;
		
		cellElement.getStyle().setBackgroundColor(sBackgroundColor) ;
	}
	
	/**
	 * Get value of the (first/only) pivot
	 */
	@Override
	public String getPivotInformation()
	{
		if (_aPivotControls.isEmpty())
			return "" ;
			
		Iterator<FormControl> it = _aPivotControls.iterator() ;
		FormControl firstControl = it.next() ;
		
		return getPivotValue(firstControl) ;
	}
	
	/**
	 * Get pivot control from its path
	 */
	protected FormControl getPivotControl(final String sPivotPath)
	{
		if ((_aPivotControls.isEmpty()) || (null == sPivotPath) || "".equals(sPivotPath))
			return null ;
			
		for (FormControl control : _aPivotControls)
			if (sPivotPath.equals(control.getPath()))
				return control ;
	
		return null ;
	}
	
	/**
	 * Get local pivot control from its extended path
	 */
	@Override
	public FormControl getLocalPivotControl(final String sLocalPivotPath)
	{
		if ((_aLocalPivotCtr.isEmpty()) || (null == sLocalPivotPath) || "".equals(sLocalPivotPath))
			return null ;
			
		for (FormControl control : _aLocalPivotCtr)
			if (sLocalPivotPath.equals(control.getPath()))
				return control ;
	
		return null ;
	}
	
	/**
	 * Get a control as a change handler from its local pivot path
	 */
	public HasChangeHandlers getLocalPivot(final String sLocalPivotPath)
	{
		FormControl control = getLocalPivotControl(sLocalPivotPath) ;
		if (null == control)
			return null ;
		
		return (HasChangeHandlers) control ;
	}
	
	/**
	 * Get value of a pivot from its path
	 */
	@Override
	public String getPivotInformation(final String sPivotPath)
	{
		if ((_aPivotControls.isEmpty()) || (null == sPivotPath) || "".equals(sPivotPath))
			return "" ;
			
		FormControl control = getPivotControl(sPivotPath) ;
		if (null == control)
			return "" ;
		
		return getPivotValue(control) ;
	}
	
	/**
	 * Get the value of a FormControl
	 */
	protected String getPivotValue(final FormControl control)
	{
		if (null == control)
			return "" ;
		
		ControlModel controlModel = (ControlModel) control.getWidget() ;
		if (null == controlModel)
			return null ;
		
		FormDataData content = controlModel.getContent() ;
		if (null == content)
			return null ;
		
		return controlModel.getContent().getValue() ;
	}
	
	/**
	 * Build a local pivot path from a pivot path and the index of the table or chart
	 */
	public static String getLocalPivotPath(final String sPivotPath, final int iIndex, final boolean bIsTable)
	{
		String sType = bIsTable ? "T" : "C" ;
		
		if (null == sPivotPath)
			return sType + iIndex ;
		
		return sPivotPath + sType + iIndex ;
	}
	
	/** 
	 * Get the table line for this kind of path for a given site
	 * 
	 */
	protected DashboardLine getDashboardLine(final FormDataData information, final int iSiteId)
	{
		if ((null == information) || _aLines.isEmpty())
			return null ;
		
		for (DashboardLine dashboardLine : _aLines)
			if ((dashboardLine.getSiteId() == iSiteId) && isProperLine(dashboardLine, information))
				return dashboardLine ;
		
		return null ;
	}
	
	protected boolean isProperLine(final DashboardLine dashboardLine, final FormDataData information)
	{
		if (dashboardLine.getPath().equals(information.getPath()))
			return true ;
		
		if ((null == dashboardLine.getOptions()) || dashboardLine.getOptions().isEmpty())
			return false ;
		
		FormControlOptionData option = getOption(dashboardLine, information.getPath()) ;
		if (null != option)
			return true ;
		
		return false ;
	}
	
	protected String getDisplayed(final DashboardLine dashboardLine, final FormDataData information)
	{
		if (dashboardLine.getPath().equals(information.getPath()))
		{
			if ("Integer".equals(dashboardLine.getType()))
				return information.getValue() ;
		}
		
		FormControlOptionData option = getOption(dashboardLine, information.getPath()) ;
		if (null != option)
			return option.getCaption() ;
		
		return "" ;
	}

	protected FormControlOptionData getOption(final DashboardLine dashboardLine, final String sPath)
	{
		for (FormControlOptionData option : dashboardLine.getOptions())
		{
			String sPotentialPath = dashboardLine.getPath() + "/" + option.getPath() ;
			
			if (sPotentialPath.equals(sPath))
				return option ;
		}
		
		return null ;
	}
	
	protected void displayColumHeader(DashboardCol newCol)
	{
	}
	
	@Override
	public HasClickHandlers getLeaveEvent() {
		return _leaveButton ;
	}
	
	@Override
	public HasClickHandlers getRefreshEvent() {
		return _submitButton ;
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}
}
