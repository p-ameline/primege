package com.primege.client.mvp;

import java.util.ArrayList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import com.google.inject.Inject;

import com.primege.client.event.GoToLoginResponseEvent;
import com.primege.client.event.OpenDashboardEvent;
import com.primege.client.event.OpenDashboardEventHandler;
import com.primege.client.event.PostLoginHeaderDisplayEvent;
import com.primege.client.global.PrimegeSupervisorModel;
import com.primege.client.util.FormControl;
import com.primege.client.util.FormControlOptionData;
import com.primege.shared.GlobalParameters;
import com.primege.shared.model.DashboardChart;
import com.primege.shared.model.DashboardTable;
import com.primege.shared.rpc.GetArchetypeAction;
import com.primege.shared.rpc.GetArchetypeResult;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

/**
 * Model of presenter from the presenter/view model for dashboards
 *
 */
public abstract class DashboardPresenterModel<D extends DashboardInterfaceModel> extends WidgetPresenter<D>
{
	protected final DispatchAsync          _dispatcher ;
	protected final PrimegeSupervisorModel _supervisor ;
	private         FlowPanel              _dashboardSpace ; //content of view
	
	private int                            _iArchetypeId ;
	private Document                       _archetype ;
	
	protected String                       _sDashboardCaption ;
	
	/** String that contains all valid roots separated by '|' */
	protected String                       _sRoots ;
	
	/** String that contains all pivots path separated by '|' */
	protected String                       _sPivot ;
  /** So far, there is at most one static pivot information (in the form "$city$=7") */
	protected String                       _sStaticPivotValue ;  
	protected boolean                      _bAddedRefreshButton ;
	
	protected ArrayList<DashboardTable>    _aTables = new ArrayList<DashboardTable>() ;
	protected DashboardTable               _currentTable ;
	
	protected ArrayList<DashboardChart>    _aCharts = new ArrayList<DashboardChart>() ;
	protected DashboardChart               _currentChart ;
	
	@Inject
	public DashboardPresenterModel(final D                      display, 
							                   final EventBus               eventBus,
							                   final DispatchAsync          dispatcher,
							                   final PrimegeSupervisorModel supervisor) 
	{
		super(display, eventBus) ;
    
		_dispatcher = dispatcher ;
		_supervisor = supervisor ;
	
		_iArchetypeId        = -1 ;
		_archetype           = null ;
		_sDashboardCaption   = "" ;
		_sRoots              = "" ;
		_sPivot              = "" ;
		_sStaticPivotValue   = "" ;
		_bAddedRefreshButton = false ;
		
		bind() ;
	}
	
	@Override
	protected void onBind() 
	{
		Log.debug("Entering DashboardPresenter::onBind()") ;
			
		/**
		 * receive parameters from precedent presenter
		 * pass parameters to next presenter
		 * @param event : SimplePanel
		 *  
		 * */
		eventBus.addHandler(OpenDashboardEvent.TYPE, new OpenDashboardEventHandler() {
			@Override
			public void onOpenDashboard(OpenDashboardEvent event) 
			{
				Log.info("Handling NewEncounterEvent event") ;
				// event.getWorkspace().clear() ;
				 
				_dashboardSpace = event.getWorkspace() ;
				_dashboardSpace.clear() ;
				
				resetAll() ;
				
				_dashboardSpace.add(display.asWidget()) ;
				
				_iArchetypeId = event.getArchetypeId() ;
				
				getArchetype() ;
			}
		});
		
		/**
		 * leave  
		 * */
		display.getLeaveEvent().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new GoToLoginResponseEvent()) ;
			}
		}) ;
		
		/**
		 * fill the dashboard with information from the database 
		 * */
		display.getRefreshEvent().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				display.resetForRefresh() ;
				fillDashboard() ;
			}
		}) ;
	}

	protected void resetAll() {
		resetAll4Model() ;
	}
	
	protected void resetAll4Model()
	{
		_iArchetypeId        = -1 ;
		_archetype           = null ;
		_sDashboardCaption   = "" ;
		_sPivot              = "" ;
		_sRoots              = "" ;
		_sStaticPivotValue   = "" ;
		_bAddedRefreshButton = false ;
		
		_aTables.clear() ;
	}
	
	/**
	 * Fill the dashboard with information from the database
	 *
	 */
	protected void fillDashboard()
	{
	}
	
	protected void getArchetype()
	{
		if (-1 == _iArchetypeId)
			return ;
		
		_dispatcher.execute(new GetArchetypeAction(_iArchetypeId, _supervisor.getUserId()), new getArchetypeCallback()) ;
	}
	
	protected class getArchetypeCallback implements AsyncCallback<GetArchetypeResult> 
	{
		public getArchetypeCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("getArchetypeCallback: Unhandled error", cause) ;
		}//end handleFailure

		@Override
		public void onSuccess(GetArchetypeResult value) 
		{
			if (value.wasSuccessful())
			{
				String sArchetype = value.getArchetype() ;
				if ("".equals(sArchetype))
					return ;
				
				try {
					_archetype = XMLParser.parse(sArchetype) ;
				} catch (DOMException e) {
			    Window.alert("Could not parse XML document : " + e.getMessage()) ;
			  }
				
				if (null != _archetype)
					initFromArchetype() ;
			}
		}
	}
	
	/**
	 * Parse the root level of an archetype
	 *
	 */
	protected void initFromArchetype()
	{
		if (null == _archetype)
			return ;
		
		// Get root tag and check that it conveys an "archetype" tag
		//
		Element rootElement = _archetype.getDocumentElement() ;
		if (null == rootElement)
			return ;
		
		String sRootTagName = rootElement.getTagName() ;
		if ((null == sRootTagName) || (false == "archetype".equalsIgnoreCase(sRootTagName)))
			return ;
		
		// Explore first children
		//
		Node current = rootElement.getFirstChild() ;
		
		while (null != current)
		{
			String sCurrentTagName = current.getNodeName() ;
			
			if ("dashboard".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				if (null != currentElement)
				{
					_sDashboardCaption = currentElement.getAttribute("caption") ;
					
					String sBorderWidth = currentElement.getAttribute("border") ;
					if (null != sBorderWidth)
						display.setBorderWidth(sBorderWidth) ;
					
					String sStaticPivot = currentElement.getAttribute("staticPivot") ;
					if ((null != sStaticPivot) && (false == "".equals(sStaticPivot)))
						parseStaticPivot(sStaticPivot) ;
					
					String sRoots = currentElement.getAttribute("roots") ;
					if ((null != sRoots) && (false == sRoots.isEmpty()))
						_sRoots = sRoots ;
					
					eventBus.fireEvent(new PostLoginHeaderDisplayEvent(_sDashboardCaption)) ;
					// display.setCaption(_sDashboardCaption) ;
					
					initFormFromArchetypeElement(currentElement) ;
				}
				
				current = currentElement.getNextSibling() ;
			}
			else
				current = current.getNextSibling() ;
		}
		
		// In case there is no pivot
		//
		if (false == _bAddedRefreshButton)
		{
			display.endOfPivot() ;
			_bAddedRefreshButton = true ;
		}
		
		// In case there are tables to fill, ask the server
		//
		if (false == _aTables.isEmpty())
			askServerForTablesContent() ;
	}
	
	/**
	 * Ask the server for tables information. To be implemented by derived classes
	 */
	protected void askServerForTablesContent() {
	}
	
	/**
	 * Parse sub-levels of an archetype (recursively)
	 *
	 */
	protected void initFormFromArchetypeElement(Element father)
	{
		if (null == father)
			return ;
		
		Node current = father.getFirstChild() ;
		
		while (null != current)
		{
			String sCurrentTagName = current.getNodeName() ;
			
			if      ("pivot".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sBlockBgColor = currentElement.getAttribute("bgcolor") ;
				String sBlockCaption = currentElement.getAttribute("caption") ;
				
				initFormFromArchetypeElement(currentElement) ;
				
				display.endOfPivot() ;
				
				_bAddedRefreshButton = true ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("column".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sColPath    = currentElement.getAttribute("path") ;
				String sColType    = currentElement.getAttribute("type") ;
				String sColBgColor = currentElement.getAttribute("bgcolor") ;
				String sHeadFormat = currentElement.getAttribute("format") ;
				
				display.setColDescription(sColPath, sColType, sColBgColor, sHeadFormat) ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("record".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sSiteSpec = currentElement.getAttribute("staticData") ;
				String sBgColor  = currentElement.getAttribute("bgcolor") ;
				String sCaption  = currentElement.getAttribute("caption") ;
				
				int iSiteId = -1 ;
				if ((null != sSiteSpec) && (false == "".equals(sSiteSpec)))
				{
					ArrayList<String> parsedPair = GlobalParameters.ParseString(sSiteSpec, "=") ;
					if (parsedPair.size() == 2)
					{
						if (parsedPair.get(0).equals("$site$"))
							iSiteId = Integer.parseInt(parsedPair.get(1)) ;
					}
				}
				
				display.addRecord(iSiteId, sBgColor, sCaption) ;
				
				initFormFromArchetypeElement(currentElement) ;
				
				display.endOfRecord() ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("table".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sRoots    = currentElement.getAttribute("roots") ;
				String sPivot    = currentElement.getAttribute("pivot") ;
				String sBgColor  = currentElement.getAttribute("bgcolor") ;
				String sCaption  = currentElement.getAttribute("caption") ;
				String sAddTotal = currentElement.getAttribute("include_total_line") ;
				String sTotalCap = currentElement.getAttribute("total_caption") ;
				String sLines    = currentElement.getAttribute("lines") ;
				
				// Create a table on the display and get its Id to be able to fill it when datas are ready
				//
				int iTableId = display.addTable(sBgColor, sCaption) ;
				
				// Create a table object to get information from the server 
				//
				DashboardTable newTable = new DashboardTable(iTableId, sRoots, sPivot, sAddTotal, sTotalCap, sLines) ;
				
				_currentTable = newTable ;
				_currentChart = null ;
				
				// Get the columns
				//
				initFormFromArchetypeElement(currentElement) ;
				
				display.endOfTable() ;
				
				_aTables.add(newTable) ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("chart".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sRoots     = currentElement.getAttribute("roots") ;
				String sPivot     = currentElement.getAttribute("pivot") ;
				String sPivotType = currentElement.getAttribute("pivot_type") ;
				String sBgColor   = currentElement.getAttribute("bgcolor") ;
				String sCaption   = currentElement.getAttribute("caption") ;
				String sWidth     = currentElement.getAttribute("width") ;
				String sHeight    = currentElement.getAttribute("height") ;
				String sTypes     = currentElement.getAttribute("types") ;
				String sYCaption  = currentElement.getAttribute("Ycaption") ;
				
				// Create a chart on the display and get its Id to be able to fill it when data are ready
				//
				final int iChartId = display.addChart(sPivot, sPivotType, sBgColor, sCaption, sWidth, sHeight) ;
				
				// Create a chart object to get information from the server 
				//
				DashboardChart newChart = new DashboardChart(iChartId, sRoots, sPivot, sTypes, sYCaption) ;
				
				_currentChart = newChart ;
				_currentTable = null ;
				
				// Is there a pivot control available?
				//
				FormControl form = display.getLocalPivotControl(DashboardViewModel.getLocalPivotPath(sPivot, iChartId, false)) ;
				if (null != form)
				{
					// Connect a change handler
					//
					Widget control = form.getWidget() ;
					((HasChangeHandlers) control).addChangeHandler(new ChangeHandler(){
						public void onChange(ChangeEvent event)
						{
							refreshChart(iChartId) ;
						}
					});
				}
				
				// Get the columns
				//
				initFormFromArchetypeElement(currentElement) ;
				
				display.endOfChart() ;
				
				_aCharts.add(newChart) ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("block".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sBlockBgColor = currentElement.getAttribute("bgcolor") ;
				String sBlockCaption = currentElement.getAttribute("caption") ;
				display.insertNewBlock(sBlockBgColor, sBlockCaption) ;
				
				initFormFromArchetypeElement(currentElement) ;
				
				display.endOfBlock() ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("control".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sControlPath    = currentElement.getAttribute("path") ;
				String sControlCaption = currentElement.getAttribute("caption") ;
				String sControlType    = currentElement.getAttribute("type") ;
				
				// It provides us with a pivotal concept
				//
				if (false == "".equals(_sPivot))
					_sPivot += "|" ;
				_sPivot += sControlPath ; 
				
				// Get options, if any
				//
				ArrayList<FormControlOptionData> aOptions = new ArrayList<FormControlOptionData>() ;
				
				NodeList nodes = current.getChildNodes() ;
				for (int iNode = 0 ; iNode < nodes.getLength() ; iNode++)
				{
					Node optionNode = nodes.item(iNode) ;
					String sOptionTagName = optionNode.getNodeName() ;
					
					if ("option".equalsIgnoreCase(sOptionTagName))
					{
						Element currentOption = (Element) optionNode ;
						
						String sOptionPath    = currentOption.getAttribute("path") ;
						String sOptionCaption = currentOption.getAttribute("caption") ;
						String sOptionUnit    = currentOption.getAttribute("unit") ;
						String sOptionPopup   = currentOption.getAttribute("popup") ;
						String sOptionExclu   = currentOption.getAttribute("exclusion") ;
						
						aOptions.add(new FormControlOptionData(sOptionPath, sOptionCaption, sOptionUnit, sOptionPopup, sOptionExclu)) ;
					}
				}
				
				display.insertNewPivotControl(sControlPath, sControlCaption, sControlType) ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("information".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sDataPath    = currentElement.getAttribute("path") ;
				String sDataCaption = currentElement.getAttribute("caption") ;
				String sDataType    = currentElement.getAttribute("type") ;
				
				// Get options, if any
				//
				ArrayList<FormControlOptionData> aOptions = new ArrayList<FormControlOptionData>() ;
				
				NodeList nodes = current.getChildNodes() ;
				for (int iNode = 0 ; iNode < nodes.getLength() ; iNode++)
				{
					Node optionNode = nodes.item(iNode) ;
					String sOptionTagName = optionNode.getNodeName() ;
					
					if ("option".equalsIgnoreCase(sOptionTagName))
					{
						Element currentOption = (Element) optionNode ;
						
						String sOptionPath    = currentOption.getAttribute("path") ;
						String sOptionCaption = currentOption.getAttribute("caption") ;
						String sOptionUnit    = currentOption.getAttribute("unit") ;
						String sOptionPopup   = currentOption.getAttribute("popup") ;
						String sOptionExclu   = currentOption.getAttribute("exclusion") ;
						
						aOptions.add(new FormControlOptionData(sOptionPath, sOptionCaption, sOptionUnit, sOptionPopup, sOptionExclu)) ;
					}
				}
				
				display.insertNewDataRow(sDataPath, sDataCaption, sDataType, aOptions) ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("table_col".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sColBgColor = currentElement.getAttribute("bgcolor") ;
				String sColCaption = currentElement.getAttribute("caption") ;
				String sColPath    = currentElement.getAttribute("path") ;
				String sColType    = currentElement.getAttribute("type") ;
				String sColFormat  = currentElement.getAttribute("format") ;
				
				_currentTable.addColumn(sColPath, sColType, sColFormat, sColBgColor, sColCaption) ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("chart_col".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sColBgColor = currentElement.getAttribute("bgcolor") ;
				String sColCaption = currentElement.getAttribute("caption") ;
				String sColPath    = currentElement.getAttribute("path") ;
				String sColType    = currentElement.getAttribute("type") ;
				String sColFormat  = currentElement.getAttribute("format") ;
				
				_currentChart.addColumn(sColPath, sColType, sColFormat, sColBgColor, sColCaption) ;
				
				current = currentElement.getNextSibling() ;
			}
			else
				current = current.getNextSibling() ;
		}		
	}
	
	/**
	 * Parse the static information, as a string of the kind "$city$=5"
	 *
	 */
	protected void parseStaticPivot(final String sStaticPivot)
	{
		if ((null == sStaticPivot) || "".equals(sStaticPivot))
			return ;
		
		// Static parameters are in the form "pivot=value1"
		//
		// The first step is to parse the string in a "paramN=valueN" vector
		//
		ArrayList<String> parsedStatics = GlobalParameters.ParseString(sStaticPivot, "=") ;
		
		if (parsedStatics.isEmpty())
			return ;
		
		if (parsedStatics.size() == 2)
		{
			_sPivot            = parsedStatics.get(0) ;
			_sStaticPivotValue = parsedStatics.get(1) ;
		}
	}
	
	/**
	 * Chart with a given ID has to be refreshed because it's pivot information changed
	 */
	protected void refreshChart(int iChartId)
	{
		// To be implemented by derived objects
		//
	}
	
	@Override
	protected void onRevealDisplay()
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected void onUnbind()
	{
		// TODO Auto-generated method stub
	}	
}
