package com.primege.client.mvp;

import java.util.ArrayList;
import java.util.Iterator;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.CDATASection;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import com.google.inject.Inject;

import com.primege.client.event.DeleteFormEvent;
import com.primege.client.event.DeleteFormEventHandler;
import com.primege.client.event.EditFormEvent;
import com.primege.client.event.EditFormEventHandler;
import com.primege.client.event.GoToLoginResponseEvent;
import com.primege.client.event.GoToNewFormEvent;
import com.primege.client.event.PostLoginHeaderDisplayEvent;
import com.primege.client.global.DataDictionaryCallBack;
import com.primege.client.global.PrimegeSupervisorModel;
import com.primege.client.util.FormControl;
import com.primege.client.util.FormControlOptionData;
import com.primege.client.widgets.ControlModel;
import com.primege.client.widgets.ControlModelMulti;
import com.primege.client.widgets.FormBlockInformation;
import com.primege.client.widgets.FormBlockPanel;
import com.primege.shared.GlobalParameters;
import com.primege.shared.database.Dictionary;
import com.primege.shared.database.FormDataData;
import com.primege.shared.database.FormLink;
import com.primege.shared.model.Action;
import com.primege.shared.model.FormBlock;
import com.primege.shared.model.FormBlockModel;
import com.primege.shared.model.TraitPath;
import com.primege.shared.rpc.GetArchetypeAction;
import com.primege.shared.rpc.GetArchetypeResult;
import com.primege.shared.rpc.GetFormsResult;
import com.primege.shared.rpc.RegisterFormResult;
import com.primege.shared.util.MailTo;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

public abstract class FormPresenterModel<D extends FormInterfaceModel> extends PrimegeBasePresenter<D>
{	
	protected final DispatchAsync          _dispatcher ;
	protected final PrimegeSupervisorModel _supervisor ;
	protected       FlowPanel              _encounterSpace ; //content of view
		
	protected int                     _iFormId ;
	protected String                  _sRecordDate ;
	protected int                     _iArchetypeId ;
	protected Document                _archetype ;

	protected FormBlock<FormDataData> _editedBlock ;
	protected FormLink                _formLink ;
	
	protected boolean                 _bFormAlreadyExist ;
	protected boolean                 _bCheckingFormExist ;
	
	//Is there a control that need to be initialized from the most recently saved identical information
	//
	protected boolean                 _bInitFromPreviousNeeded ;
	
	protected String                  _sFormCaption ;
	protected boolean                 _bAllowSaveDraft ;
	protected boolean                 _bInPdfWhenEmpty ;
	
	protected boolean                 _bSaveInProgress ;
	protected boolean                 _bSavingDraft ;
	
	protected int                     _iCurrentDeleteForm ;
	
	protected ArrayList<FormControl>  _aStatics = new ArrayList<FormControl>() ;
	protected ArrayList<TraitPath>    _aTraits  = new ArrayList<TraitPath>() ;
	protected ArrayList<Action>       _aActions = new ArrayList<Action>() ;
	
	protected boolean                 _bHasMailSection ;
	protected String                  _sMailTemplate ;
	protected String                  _sMailFrom ;
	protected ArrayList<MailTo>       _aMailAddresses = new ArrayList<MailTo>() ;
	protected String                  _sMailCaption ;
	
	protected ChangeHandler           _CheckExistChangeHandler ;
	protected ClickHandler            _NewAnnotationClickHandler ;
	protected ClickHandler            _ActionClickHandler ;
	
	@Inject
	public FormPresenterModel(D                       display, 
							              final EventBus          eventBus,
							              final DispatchAsync     dispatcher,
							              final PrimegeSupervisorModel supervisor) 
	{
		super(display, eventBus, dispatcher, supervisor) ;
		
		_dispatcher              = dispatcher ;
		_supervisor              = supervisor ;
		
		_iFormId                 = -1 ;
		_sRecordDate             = "" ;
		_iArchetypeId            = -1 ;
		_archetype               = null ;
		
		_editedBlock             = null ;
		_formLink                = null ;
		
		_bFormAlreadyExist       = false ;
		_bCheckingFormExist      = false ;
		
		_bInitFromPreviousNeeded = false ;
		_bSaveInProgress         = false ;
		_bSavingDraft            = false ;
		
		_sFormCaption            = "" ;
		_bAllowSaveDraft         = false ;
		_bInPdfWhenEmpty         = false ;
		
		_bHasMailSection         = false ;
		_sMailTemplate           = "" ;
		_sMailFrom               = "" ;
		_sMailCaption            = "" ;
		
		_iCurrentDeleteForm      = -1 ;
		
		_CheckExistChangeHandler   = null ;
		_NewAnnotationClickHandler = null ;
		_ActionClickHandler        = null ;
			
		// Don't call bind since it is already called by super PrimegeBasePresenter
		//
		// bind() ;
		
		prepareSreen() ;
	}
	
	@Override
	protected void onBind() 
	{
		Log.debug("Entering FormPresenterModel::onBind()") ;
		// super.onBind() ;
		
		/**
		 * receive parameters from precedent presenter
		 * pass parameters to next presenter
		 * @param event : SimplePanel
		 *  
		 * */
		eventBus.addHandler(EditFormEvent.TYPE, new EditFormEventHandler() {
			@Override
			public void onEditEncounter(EditFormEvent event) 
			{
				String sInfo = "Handling EditFormEvent event" ;
				if (event.inScreenShotMode())
					sInfo = " (in screenshot mode)." ;
				else
					sInfo = " (in standard mode)." ;
				Log.info(sInfo) ;
				
				// event.getWorkspace().clear() ;
				 
				_encounterSpace = event.getWorkspace() ;
				_encounterSpace.clear() ;
				
				resetAll(event.inScreenShotMode()) ;
				
				// Are we just displaying information for a screenshot, or using genuine entry controls?
				//
				getDisplay().setScreenShotMode(event.inScreenShotMode()) ;
				
				_encounterSpace.add(getDisplay().asWidget()) ;
				
				_iFormId      = event.getFormId() ;
				_iArchetypeId = event.getArchetypeId() ;
				_formLink     = event.getFormLink() ;
				_sRecordDate  = "" ;
				
				// Allow derived classes to initialize specific information
				//
				initialize() ;
				
				// If there is no form Id to edit, simply get archetype
				//
				// Warning, if there is a form Id, we will first get the saved information, then get the archetype that
				//          had been used to create them.
				//
				if (-1 == _iFormId)
					getArchetype() ;
				else
					initFromExistingInformation() ;
				
				// These functions are asynchronous, so don't call any function here that
				// would depend on their outcomes
				//
			}
		});
		
		/**
		 * Reacts to delete code event coming from CodePresenter
		 * */
		eventBus.addHandler(DeleteFormEvent.TYPE, new DeleteFormEventHandler() {
			@Override
			public void onDeleteForm(DeleteFormEvent event) 
			{
				Log.info("Handling DeleteFormEvent event") ;
				// event.getWorkspace().clear() ;
				 
				_iCurrentDeleteForm = event.getFormIdToDelete() ;
				
				display.popupDeleteConfirmationMessage(false) ;
			}
		});
				
		/**
		 * Reacts to Ok button in delete confirmation dialog box
		 * */
/*
		display.getDeleteConfirmationOk().addClickHandler(new ClickHandler(){
			public void onClick(final ClickEvent event)
			{
			  display.closeDeleteConfirmationDialog() ;
			  
			  deleteCode(_currentDeleteObject.getBasketId(), _currentDeleteObject.getSoapSlot(), _currentDeleteObject.getCode()) ;
			}
		});
*/		
		/**
		 * Reacts to Cancel button in delete confirmation dialog box
		 * */
		display.getDeleteConfirmationCancel().addClickHandler(new ClickHandler(){
			public void onClick(final ClickEvent event)
			{
			  display.closeDeleteConfirmationDialog() ; 
			}
		});
		
		/**
		 * cancel edition
		 * */
		display.getReset().addClickHandler(new CancelHandler()) ; 
		
		/**
		 * Reacts to Ok button in warning dialog box
		 * */
		display.getWarningOk().addClickHandler(new ClickHandler(){
			public void onClick(final ClickEvent event)
			{
			  display.closeWarningDialog() ; 
			}
		});		
	}
		
	/**
	 * Reset the screen and set default values
	 * 
	 */
	protected void prepareSreen()
	{
		resetAll(false) ;
		
		display.setDefaultValues() ;
	}
	
	/**
	 * This reset function is usually to be super-charged by super-classes, specifically to reset the display
	 * 
	 */
	protected void resetAll(boolean bScreenShotMode) {
		resetAll4Model() ;
	}
	
	/**
	 * Nothing to be done there. Allows derived classes to initialize specific information when page loads
	 */
	protected void initialize() {
	}
	
	/**
	 * This reset function only takes care of presenter's internal information, it shouldn't interfere with the display
	 * 
	 * */
	protected void resetAll4Model()
	{
		_iFormId            = -1 ;
		_sRecordDate        = "" ;
		_editedBlock        = null ;
		_formLink           = null ;
		_archetype          = null ;
		_iArchetypeId       = -1 ;
		
		_bSaveInProgress    = false ;
		_bFormAlreadyExist  = false ;
		_bCheckingFormExist = false ;
		
		_bInitFromPreviousNeeded = false ;
		_bSaveInProgress         = false ;
		_bSavingDraft            = false ;
		
		_sFormCaption            = "" ;
		_bAllowSaveDraft         = false ;
		_bInPdfWhenEmpty         = false ;
		
		_iCurrentDeleteForm = -1 ;
		
		_aStatics.clear() ;
		_aTraits.clear() ;
		_aActions.clear() ;
		_aMailAddresses.clear() ;
		
		_bHasMailSection         = false ;
		_sMailTemplate           = "" ;
		_sMailFrom               = "" ;
		_sMailCaption            = "" ;
	}
	
	/**
	 * Derived classes may create handlers that react to information change in form
	 */
	protected void createChangeHandlers() {
	}
	
	/**
	 * Create a click handler for the "initialize from previous form" button
	 */
	protected void connectInitFromPreviousHandler()
	{
		HasClickHandlers handler = display.getInitFromPreviousButton() ;
		if (null == handler)
			return ;
		
		/**
		 * Reacts to Ok button in warning dialog box
		 * */
		handler.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				initFromPreviousInformation() ; 
			}
		});	
	}

	/**
	 * Get the archetype information from a path, either from user entry or from static information 
	 * 
	 * */
	protected String getValueForPath(String sPath)
	{
		if ((null == sPath) || "".equals(sPath))
			return "" ;
		
		// Get the information entered in form
		//
		FormDataData formData = display.getContentForPath(sPath) ;
		
		// If no information found in form, get it into static information (if any)
		//
		if (null == formData)
			return getStaticInformation(sPath) ;
		else
			return formData.getValue() ;
	}
		
	/**
	 * Get the archetype information from a path as an int, either from user entry or from static information 
	 * 
	 * */
	protected int getIntValueForPath(String sPath)
	{
		if ((null == sPath) || "".equals(sPath))
			return -1 ;
		
		String sValue = getValueForPath(sPath) ;
		
		if ("".equals(sValue))
			return -1 ;
			
		try {
			return Integer.parseInt(sValue) ;
		} catch (NumberFormatException e) {
			return -1 ;
		}
	}
	
	/**
	 * Get the archetype information from a path as a double, either from user entry or from static information 
	 * 
	 * */
	protected double getDoubleValueForPath(final String sPath)
	{
		if ((null == sPath) || "".equals(sPath))
			return -1 ;
		
		String sValue = getValueForPath(sPath) ;
		
		if ("".equals(sValue))
			return -1 ;
			
		try {
			return Double.parseDouble(sValue) ;
		} catch (NumberFormatException e) {
			return -1 ;
		}
	}
	
	/**
	 * Remove a FormDataData, specified from its path from the ArrayList
	 * 
	 * */
	protected void removeFromInformation(ArrayList<FormDataData> aFormInformation, String sPath) 
	{
		if ((null == aFormInformation) || aFormInformation.isEmpty())
			return ;
		
		if ((null == sPath) || "".equals(sPath))
			return ;
		
		for (Iterator<FormDataData> it = aFormInformation.iterator() ; it.hasNext() ; )
		{
			FormDataData info = it.next() ;
			if (info.getPath().equals(sPath))
			{
				it.remove() ;
				return ;
			}
		}
	}
	
	protected class CancelHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) {
			leave() ;
		}
	}
		
	protected class RegisterFormCallback implements AsyncCallback<RegisterFormResult> 
	{
		public RegisterFormCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			_bSaveInProgress = false ;
			display.showDefaultCursor() ;
		}//end handleFailure

		@Override
		public void onSuccess(RegisterFormResult value) 
		{
			display.showDefaultCursor() ;
			// No user found
			if (0 == value.getRecordedId())
			{
				Log.error("No form created") ;
			}
			else {
				Log.info("Form saved") ;
				
				_iFormId = value.getRecordedId() ;
				
				leaveWhenSaved() ;
			}
			
			_bSaveInProgress = false ;
		}
	}
		
	protected class CheckExistFormCallback implements AsyncCallback<GetFormsResult> 
	{
		public CheckExistFormCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error in CheckExistFormCallback", cause) ;
		}

		@Override
		public void onSuccess(GetFormsResult value) 
		{
			if ((null != value) && (false == value.getForms().isEmpty()))
			{
				_bFormAlreadyExist = true ;			
				display.popupWarningMessage("WARNING_FORM_ALREADY_EXIST") ;
			}
			else
				_bFormAlreadyExist = false ;
			
			_bCheckingFormExist = false ;
		}
	}
	
	protected void leaveWhenSaved()
	{
		display.setDefaultValues() ;
		
		leave() ;
	}
	
	protected boolean areDataOk(FormBlockModel<FormDataData> formBlock)
	{
		if (null == formBlock)
			return false ;
		
/*
		if (_aBaskets.isEmpty())
		{
			display.popupWarningMessage("ERROR_MANDATORY_BASKET") ;
			return false ;
		}
*/
					
		return true ;
	}
		
	/**
	 * In edit mode, fill the form with information to be edited
	 * 
	 * To be implemented in subclasses
	 * 
	 * */
	protected void initFromExistingInformation() {
	}
	
	/**
	 * In new mode, fill the form with information from the previous identical form 
	 * 
	 * To be implemented in subclasses
	 * 
	 * */
	protected void initFromPreviousInformation() {
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
			
			if ("form".equalsIgnoreCase(sCurrentTagName))
			{
				Log.info("Entering archetype's form section.") ;
				
				Element currentElement = (Element) current ;
				
				_sFormCaption = currentElement.getAttribute("caption") ;
				
				// Are non instantiated controls visible in the PDF export?
				//
				String sInPdfWhenEmpty = currentElement.getAttribute("InPdfWhenEmpty") ;
				if ((null != sInPdfWhenEmpty) && ("yes".equalsIgnoreCase(sInPdfWhenEmpty) || "oui".contentEquals(sInPdfWhenEmpty)))
					_bInPdfWhenEmpty = true ;
				
				// Should a "save draft" button be present?
				//
				String sFormAllowDraft = currentElement.getAttribute("allow_draft") ;
				if ((null != sFormAllowDraft) && ("yes".equalsIgnoreCase(sFormAllowDraft) || "oui".contentEquals(sFormAllowDraft)))
					_bAllowSaveDraft = true ;
				
				display.setSubmitDraftButtonVisible(_bAllowSaveDraft) ;
				
				// Asks header to display form's caption
				//
				eventBus.fireEvent(new PostLoginHeaderDisplayEvent(_sFormCaption)) ;
					
				String sStaticInformation = currentElement.getAttribute("staticData") ;
				if ((null != sStaticInformation) && (false == "".equals(sStaticInformation)))
					parseStaticInformation(sStaticInformation) ;
				
				// Create the form
				//
				initFormFromArchetypeElement(currentElement, display.getMasterForm(), _editedBlock) ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("traits".equalsIgnoreCase(sCurrentTagName))
			{
				Log.info("Entering archetype's traits section.") ;
				
				Element currentElement = (Element) current ;
				
				initTraitsFromArchetypeElement(currentElement) ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("actions".equalsIgnoreCase(sCurrentTagName))
			{
				Log.info("Entering archetype's actions section.") ;
				
				Element currentElement = (Element) current ;
				
				initActionsFromArchetypeElement(currentElement) ;
				
				current = currentElement.getNextSibling() ;
			}
			else if ("mail".equalsIgnoreCase(sCurrentTagName))
			{
				Log.info("Entering archetype's mail section.") ;
				
				_bHasMailSection = true ;
				
				Element currentElement = (Element) current ;
				
				_aMailAddresses.clear() ;
				
				String sMailTo = currentElement.getAttribute("to") ;
				if ((null != sMailTo) && (false == "".equals(sMailTo)))
						addMailsTo("to", sMailTo) ;
				
				String sMailCc = currentElement.getAttribute("cc") ;
				if ((null != sMailCc) && (false == "".equals(sMailCc)))
						addMailsTo("cc", sMailCc) ;
				
				String sMailBcc = currentElement.getAttribute("bcc") ;
				if ((null != sMailBcc) && (false == "".equals(sMailBcc)))
						addMailsTo("bcc", sMailBcc) ;
				
				_sMailFrom    = currentElement.getAttribute("from") ;
				_sMailCaption = currentElement.getAttribute("caption") ;
				
				Node currentNode = currentElement.getFirstChild() ;
				
				while (null != currentNode)
				{
					String sCurrentNodeTagName = currentNode.getNodeName() ;
					
					if ("#cdata-section" == sCurrentNodeTagName)
					{
						Log.info("Entering mail's CDATA section.") ;
						
						CDATASection cDataSection = (CDATASection) currentNode ;
						_sMailTemplate = cDataSection.getData() ;
						
						if (false == _sMailTemplate.isEmpty())
							Log.info("Found a mail template.") ;
					}
					
					currentNode = currentNode.getNextSibling() ;
				}
				
				current = currentElement.getNextSibling() ;
			}
			else
				current = current.getNextSibling() ;
		}
		
		// Create handlers that react to information change in form
		//
		createChangeHandlers() ;
		
		// Create a click handler for the "initialize from previous form" button (if any)
		//
		if ((true == _bInitFromPreviousNeeded) && (-1 == _iFormId))
			connectInitFromPreviousHandler() ;
		
		// Derived class can insert some code here, benefiting from all controls being created
		//
		executePostDisplayProcesses() ;
		
		// Time to display annotations related controls
		//
		displayAnnotationsControls() ;
	}
	
	/**
	 * Add an attribute Ã  la <code>to="$trainee$"</code> into the list of MailTo
	 * 
	 * @param sAttributeName    Attribute name, either to, cc or bcc
	 * @param sAttributeContent Content in the form of roles separated by a ';'
	 */
	protected void addMailsTo(final String sAttributeName, final String sAttributeContent)
	{
		if ((null == sAttributeName) || "".equals(sAttributeName) || (null == sAttributeContent) || "".equals(sAttributeContent))
			return ;
		
		MailTo.RecipientType iType = MailTo.RecipientType.Undefined ;
		
		if      ("to".equalsIgnoreCase(sAttributeName))
			iType = MailTo.RecipientType.To ;
		else if ("cc".equalsIgnoreCase(sAttributeName))
			iType = MailTo.RecipientType.Cc ;
		else if ("bcc".equalsIgnoreCase(sAttributeName))
			iType = MailTo.RecipientType.Bcc ;
		
		_aMailAddresses.add(new MailTo(sAttributeContent, iType)) ;
	}
	
	/**
	 * After the form has been displayed, some subclasses may wish to do something 
	 * 
	 * To be implemented in subclasses
	 * 
	 * */
	protected void executePostDisplayProcesses() {
	}
	
	/**
	 * Parse sub-levels of an archetype's form section (recursively)
	 * 
	 * @param father       XML element the sons of must be processed
	 * @param masterBlock  Reference panel controls must be built into
	 * @param aInformation Edited data. Must be <code>null</code> for a new form or annotation.
	 *
	 */
	protected void initFormFromArchetypeElement(final Element father, FormBlockPanel masterBlock, FormBlockModel<FormDataData> aInformation)
	{
		if (null == father)
			return ;
		
		Node current = father.getFirstChild() ;
		
		while (null != current)
		{
			String sCurrentTagName = current.getNodeName() ;
			
			// Block
			//
			if      ("block".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				FormBlockInformation blockInformation = new FormBlockInformation(currentElement) ;
				
				display.insertNewBlock(blockInformation, masterBlock) ;
				
				// Recursive call to initialize block content
				//
				initFormFromArchetypeElement(currentElement, masterBlock, aInformation) ;
				
				display.endOfBlock(_bInPdfWhenEmpty, masterBlock) ;
				
				current = currentElement.getNextSibling() ;
			}
			// Control
			//
			else if (("information".equalsIgnoreCase(sCurrentTagName)) || ("label_information".equalsIgnoreCase(sCurrentTagName)))
			{
				Element currentElement = (Element) current ;
				
				String sControlPath    = currentElement.getAttribute("path") ;
				String sControlCaption = currentElement.getAttribute("caption") ;
				String sControlType    = currentElement.getAttribute("type") ;
				String sControlSubtype = currentElement.getAttribute("subtype") ;
				String sControlUnit    = currentElement.getAttribute("unit") ;
				String sControlValue   = currentElement.getAttribute("value") ;
				String sControlStyle   = currentElement.getAttribute("style") ;
				String sCaptionStyle   = currentElement.getAttribute("caption_style") ;
				String sInitFromPrev   = currentElement.getAttribute("init_from_latest") ;
				String sExclusion      = currentElement.getAttribute("exclusion") ;
				
				boolean bInitFromPrev = false ;
				if ((null != sInitFromPrev) && "true".equalsIgnoreCase(sInitFromPrev))
				{
					bInitFromPrev            = true ;
					_bInitFromPreviousNeeded = true ;
				}
				
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
				
				boolean bInsertInBlock = true ;
				if ("label_information".equalsIgnoreCase(sCurrentTagName))
					bInsertInBlock = false ;
				
				insertNewControl(sControlPath, sControlCaption, sControlType, sControlSubtype, sControlUnit, sControlValue, aOptions, sControlStyle, sCaptionStyle, bInitFromPrev, sExclusion, bInsertInBlock, _bInPdfWhenEmpty, masterBlock, aInformation) ;
				
				current = currentElement.getNextSibling() ;
			}
			else
				current = current.getNextSibling() ;
		}
	}
	
	/**
	 * Parse sub-levels of an archetype's traits section
	 */
	protected void initTraitsFromArchetypeElement(final Element father)
	{
		if (null == father)
			return ;
		
		Node current = father.getFirstChild() ;
		
		while (null != current)
		{
			String sCurrentTagName = current.getNodeName() ;
			
			// Block
			//
			if      ("trait".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sName      = currentElement.getAttribute("name") ;
				String sPath      = currentElement.getAttribute("path") ;
				String sComposite = currentElement.getAttribute("composite") ;
				
				_aTraits.add(new TraitPath(sName, sPath, sComposite)) ;
				
				current = currentElement.getNextSibling() ;
			}
			else
				current = current.getNextSibling() ;
		}
	}
	
	/**
	 * Parse sub-levels of an archetype's actions section
	 */
	protected void initActionsFromArchetypeElement(final Element father)
	{
		if (null == father)
			return ;
		
		Node current = father.getFirstChild() ;
		
		while (null != current)
		{
			String sCurrentTagName = current.getNodeName() ;
			
			// Block
			//
			if      ("action".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sIdentifier = currentElement.getAttribute("identifier") ;
				String sType       = currentElement.getAttribute("type") ;
				String sCaption    = currentElement.getAttribute("caption") ;
				String sArcheID    = currentElement.getAttribute("archetypeID") ;
				
				// If the "archetypeID" parameter is not specified, it means that this action is managed inside current archetype
				//
				if ((null != sArcheID) && (false == sArcheID.isEmpty()))
					_aActions.add(new Action(sIdentifier, sType, sCaption, sArcheID, currentElement)) ;
				else
					_aActions.add(new Action(sIdentifier, sType, sCaption, _iArchetypeId, currentElement)) ;
				
				current = currentElement.getNextSibling() ;
			}
			else
				current = current.getNextSibling() ;
		}
	}
	
	/**
	 * Display all annotations related controls: "new annotation" buttons and previous annotations
	 */
	protected void displayAnnotationsControls()
	{
		// Annotations are not available for a new document
		//
		if (null == _editedBlock)
			return ;
		
		if (_aActions.isEmpty())
			return ;
		
		display.initializeActionControls() ;
		
		// Check available action buttons 
		//
		ArrayList<Action> aAvailableActions = new ArrayList<Action>() ;
		
		for (Action action : _aActions)
			if (isActionAvailable(action))
				aAvailableActions.add(action) ;
				
		if (false == aAvailableActions.isEmpty())
		{
			// Create click handlers
			//
			if (null == _NewAnnotationClickHandler)
			{
				_NewAnnotationClickHandler = new ClickHandler()
				{
					public void onClick(final ClickEvent event) 
					{
						Widget sender = (Widget) event.getSource() ;
						String sActionId = display.getNewAnnotationID(sender) ;
						addNewAnnotation(sActionId) ;
					}
				} ;
			}

			for (Action action : aAvailableActions)
			{
				display.addNewActionButton(action.getCaption(), _NewAnnotationClickHandler, action.getIdentifier()) ;
			}
		}
		
		displayExistingAnnotations() ;
	}

	/**
	 * Display already recorded annotations
	 */
	protected void displayExistingAnnotations()
	{
		display.initializeActionHistory() ;
		
		if (null == _editedBlock)
			return ;
		
		createActionsClickHandler() ;
		
		ArrayList<FormLink> aLinks = _editedBlock.getLinks() ;
		if ((null == aLinks) || aLinks.isEmpty())
			return ;
		
		for (FormLink link : aLinks)
			displayAnnotation(link) ;
	}
	
	/**
	 * Display a recorded information from its {@link FormLink}
	 */
	protected void displayAnnotation(final FormLink link)
	{
		if (null == link)
			return ;
		
		String sCaption = link.getPredicate() ;
		
		// Get the proper action in archetype in order to display proper controls
		//
		Action action = getActionFromType(link.getPredicate()) ;
/*
		if (null == action)
		{
			Log.info("Cannot find action for type \"" + link.getPredicate() + "\" in archetype. Annotation in form " + link.getObjectFormId() + " cannot be displayed.") ;
			return ;
		}
*/
		if (null != action)
			sCaption = action.getCaption() ;
		else
		{
			Dictionary dico = _supervisor.getDataDictionaryProxy().getDictionnaryFromExactCode(link.getPredicate(), new annotationNamingCallBack(), _supervisor.getUserId()) ;
			if (null != dico)
				sCaption = dico.getLabel() ;
		}
		
		// Get a block from display and populate it with annotation's controls
		//
		FormBlockPanel annotationPanel = display.getNewActionBlock(sCaption, link.getObjectFormId(), _ActionClickHandler) ;
		if (null == annotationPanel)
		{
			Log.info("Cannot get a block from display. Annotation for form " + link.getObjectFormId() + " cannot be displayed.") ;
			return ;
		}
		
		// initFormFromArchetypeElement(action.getModel(), annotationPanel, annotationBlock) ;
	}
	
	public class annotationNamingCallBack implements DataDictionaryCallBack
	{
		public annotationNamingCallBack() {
      super() ;
    }
		
		@Override
		public void onFailure(Throwable caught) {
		}

		@Override
		public boolean onSuccess()
		{
			// TODO
			return true ;
		}
	}
	
	/**
	 * Create creation click handlers for every existing actions
	 */
	protected void createActionsClickHandler()
	{
		if (null != _ActionClickHandler)
			return ;
		
		_ActionClickHandler = new ClickHandler()
		{
			public void onClick(final ClickEvent event) 
			{
				Widget sender = (Widget) event.getSource() ;
				String sArchetypeId = display.getNewAnnotationID(sender) ;
				addNewAnnotation(sArchetypeId) ;
			}
		} ;
	}
	
	/**
	 * Create a new annotation from its identifier
	 */
	protected void addNewAnnotation(final String sAnnotationId)
	{
		Action selectedAction = getActionFromId(sAnnotationId) ;
		
		if (null == selectedAction)
			return ;
		
		int iActionArchetypeId = selectedAction.getArchetypeID() ;
		
		// If selected action has a specific archetype, open a new page
		//
		if (iActionArchetypeId != _iArchetypeId)
		{
			// 
			//
			FormLink formLink = new FormLink(_iFormId, selectedAction.getType()) ;
			
			eventBus.fireEvent(new GoToNewFormEvent(iActionArchetypeId, formLink)) ;
			return ;
		}
		
		// createActionsClickHandler() ;
		
		// Create a new panel
		//
		FormBlockPanel actionRootBlock = display.getNewActionBlock(selectedAction.getCaption(), -1, _ActionClickHandler) ;
		
		// Populate the panel with action's interface elements
		//
		initFormFromArchetypeElement(selectedAction.getModel(), actionRootBlock, null) ;
	}
	
	protected Action getActionFromId(final String sID)
	{
		if ((null == sID) || sID.isEmpty() || _aActions.isEmpty())
			return null ;
		
		for (Action action : _aActions)
			if (action.getIdentifier().equals(sID))
				return action ;
		
		return null ;
	}
	
	protected Action getActionFromType(final String sType)
	{
		if ((null == sType) || sType.isEmpty() || _aActions.isEmpty())
			return null ;
		
		for (Action action : _aActions)
			if (action.getType().equals(sType))
				return action ;
		
		return null ;
	}
	
	/**
	 * To be instantiated by derived classes
	 */
	protected boolean isActionAvailable(final Action action) {
		return false ;
	}
	
	/**
	 * Parse sub-levels of an archetype's form action section
	 *
	 */
	protected void initAnnotationFromArchetypeElement(final Element father)
	{
/*
		if (null == father)
			return ;
		
		Node current = father.getFirstChild() ;
		
		while (null != current)
		{
			String sCurrentTagName = current.getNodeName() ;
			
			// Block
			//
			if      ("block".equalsIgnoreCase(sCurrentTagName))
			{
				Element currentElement = (Element) current ;
				
				String sBlockBgColor = currentElement.getAttribute("bgcolor") ;
				String sBlockAlign   = currentElement.getAttribute("align") ;
				String sBlockVAlign  = currentElement.getAttribute("valign") ;
				String sBlockWidth   = currentElement.getAttribute("width") ;
				String sBlockHeight  = currentElement.getAttribute("height") ;
				
				String sBlockCaption = currentElement.getAttribute("caption") ;
				String sBlockStyle   = currentElement.getAttribute("style") ;
				String sCaptionStyle = currentElement.getAttribute("caption_style") ;
				String sBlockHelp    = currentElement.getAttribute("help") ;
				
				FormBlockPanel actionBlock = display.newActionBlockinsertNewBlock(sBlockBgColor, sBlockAlign, sBlockVAlign, sBlockWidth, sBlockHeight, sBlockCaption, sBlockStyle, sCaptionStyle, sBlockHelp) ;
				
				// Recursive call to initialize block content
				//
				initFormFromArchetypeElement(currentElement, actionBlock) ;
				
				display.endOfBlock(_bInPdfWhenEmpty) ;
				
				current = currentElement.getNextSibling() ;
			}
			// Control
			//
			else if (("information".equalsIgnoreCase(sCurrentTagName)) || ("label_information".equalsIgnoreCase(sCurrentTagName)))
			{
				Element currentElement = (Element) current ;
				
				String sControlPath    = currentElement.getAttribute("path") ;
				String sControlCaption = currentElement.getAttribute("caption") ;
				String sControlType    = currentElement.getAttribute("type") ;
				String sControlSubtype = currentElement.getAttribute("subtype") ;
				String sControlUnit    = currentElement.getAttribute("unit") ;
				String sControlValue   = currentElement.getAttribute("value") ;
				String sControlStyle   = currentElement.getAttribute("style") ;
				String sCaptionStyle   = currentElement.getAttribute("caption_style") ;
				String sInitFromPrev   = currentElement.getAttribute("init_from_latest") ;
				String sExclusion      = currentElement.getAttribute("exclusion") ;
				
				boolean bInitFromPrev = false ;
				if ((null != sInitFromPrev) && "true".equalsIgnoreCase(sInitFromPrev))
				{
					bInitFromPrev            = true ;
					_bInitFromPreviousNeeded = true ;
				}
				
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
				
				boolean bInsertInBlock = true ;
				if ("label_information".equalsIgnoreCase(sCurrentTagName))
					bInsertInBlock = false ;
				
				insertNewControl(sControlPath, sControlCaption, sControlType, sControlSubtype, sControlUnit, sControlValue, aOptions, sControlStyle, sCaptionStyle, bInitFromPrev, sExclusion, bInsertInBlock, _bInPdfWhenEmpty) ;
				
				current = currentElement.getNextSibling() ;
			}
			else
				current = current.getNextSibling() ;
		}
*/
	}
	
	/**
	 * Add an interface control to the display
	 * 
	 * This function has been created in order to allow subclasses to adapt it
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
	 * @param masterBlock     Form's root panel (if null, then the form global panel is used)
	 * @param aInformation    Edited data. Must be <code>null</code> for a new form or annotation
	 *
	 */
	public void insertNewControl(final String sControlPath, final String sControlCaption, final String sControlType, final String sControlSubtype, final String sControlUnit, final String sControlValue, final ArrayList<FormControlOptionData> aOptions, final String sControlStyle, final String sCaptionStyle, final boolean bInitFromPrev, final String sExclusion, final boolean bInBlockCell, final boolean bInPdfWhenEmpty, FormBlockPanel masterBlock, FormBlockModel<FormDataData> aInformation)
	{
		boolean bEdited = (null != aInformation) ;
		
		display.insertNewControl(sControlPath, getEditedInformationForPath(sControlPath, aOptions, aInformation), sControlCaption, sControlType, sControlSubtype, sControlUnit, sControlValue, aOptions, sControlStyle, sCaptionStyle, bInitFromPrev, sExclusion, bInBlockCell, bInPdfWhenEmpty, masterBlock, bEdited) ;
	}
	
	/**
	 * Get information from path - to be redefined by super-classes if dedicated paths must be handled<br>
	 * <br>
	 * Take care to implement such a method in super classes with "fake path" (à la "$date"") that get information from document's label
	 */
	protected ArrayList<FormDataData> getEditedInformationForPath(final String sPath, final ArrayList<FormControlOptionData> aOptions, FormBlockModel<FormDataData> aInformation) {
		return getInformationForRegularPath(aInformation, sPath, aOptions) ;
	}
	
	/**
	 * Get information in edited block from a regular path
	 *
	 */
	protected ArrayList<FormDataData> getEditedInformationForRegularPath(final String sPath, final ArrayList<FormControlOptionData> aOptions) {
		return getInformationForRegularPath(_editedBlock, sPath, aOptions) ;
	}
	
	/**
	 * Get information from path in a given block - to be redefined by super-classes if dedicated paths must be handled 
	 *
	 */
	protected ArrayList<FormDataData> getInformationForPath(final FormBlockModel<FormDataData> block, final String sPath, final ArrayList<FormControlOptionData> aOptions) {
		return getInformationForRegularPath(block, sPath, aOptions) ;
	}
	
	/**
	 * Get information from a regular path in a given block
	 *
	 * @param block    Data to look into (can be <code>null</code>)
	 * @param sPath    Path to look for
	 * @param aOptions Options as potential "extensions" to the path
	 */
	protected ArrayList<FormDataData> getInformationForRegularPath(final FormBlockModel<FormDataData> block, final String sPath, final ArrayList<FormControlOptionData> aOptions)
	{
		if ((null == block) || (null == sPath) || "".equals(sPath))
			return null ;
				
		// Regular path
		//
		ArrayList<FormDataData> aInformation = block.getInformation() ;
		if ((null == aInformation) || aInformation.isEmpty())
			return null ;
		
		ArrayList<FormDataData> aResult = new ArrayList<FormDataData>() ;
		
		for (FormDataData currentData : aInformation)
			if (isValidForPath(currentData, sPath, aOptions))
				aResult.add(currentData) ;
		
		return aResult ;
	}
	
	/**
	 * Check if a {@link FormDataData} is valid for a given path (ie same path or path + option or multiple data for path
	 * 
	 * @param currentData Data to check for validity
	 * @param sPath       Path the data must be conform to
	 * @param aOptions    Options as potential "extensions" to the path
	 * 
	 * @return <code>true</code> if the {@link FormDataData} is attached to the path, <code>false</code> if not
	 */
	protected boolean isValidForPath(FormDataData currentData, final String sPath, final ArrayList<FormControlOptionData> aOptions)
	{
		if ((null == currentData) || (null == sPath) || sPath.isEmpty())
			return false ;
		
		String sDataPath = currentData.getPath() ;
		if ((null == sDataPath) || sDataPath.isEmpty())
			return false ;
		
		// Easy case, same path
		//
		if (sPath.equals(sDataPath))
			return true ;
		
		// Is data path in the form path + option?
		//
		if ((null != aOptions) && (false == aOptions.isEmpty()))
		{
			for (FormControlOptionData currentOption : aOptions)
			{
				String sOptionPath = sPath + "/" + currentOption.getPath() ; 
				if (sOptionPath.equals(sDataPath))
					return true ;
			}
		}
		
		// Finally, is it a multiple data path, in the form path + #N + local path?
		//
		String sModelPath = sPath + "/#" ;
		
		if (sDataPath.startsWith(sModelPath))
			return true ;
		
		return false ;
	}
	
	/**
	 * Get information from a regular path in a given block
	 *
	 * @param sPath Path to look for
	 *
	 * @return <code>true</code> if we find information which path is in the form path/#N
	 */
	protected boolean hasMultipleInformationPath(final String sPath) {
		return hasMultipleInformationPath(_editedBlock, sPath) ;
	}
	
	/**
	 * Get information from a regular path in a given block
	 *
	 * @param block Block of information to look into
	 * @param sPath Path to look for
	 *
	 * @return <code>true</code> if we find information which path is in the form path/#N
	 */
	protected boolean hasMultipleInformationPath(final FormBlockModel<FormDataData> block, final String sPath)
	{
		if ((null == block) || block.isEmpty() || (null == sPath) || sPath.isEmpty())
			return false ;
		
		ArrayList<FormDataData> aInformation = block.getInformation() ;
		if ((null == aInformation) || aInformation.isEmpty())
			return false ;
		
		String sMultipleDataSignature = sPath + "/#" ;
		
		for (FormDataData currentData : aInformation)
			if (currentData.getPath().startsWith(sMultipleDataSignature))
				return true ;
		
		return false ;
	}
	
	protected void initControlsFromPreviousInformation(final FormBlockModel<FormDataData> previousBlock)
	{
		if (null == previousBlock)
			return ;
		
		ArrayList<FormControl> aControls = display.getControls() ;
		if ((null == aControls) || aControls.isEmpty())
			return ;
		
		for (Iterator<FormControl> it = aControls.iterator() ; it.hasNext() ; )
		{
			FormControl formCtrl = it.next() ;
			
			// Single information control
			//
			if (formCtrl.getWidget() instanceof ControlModel)
			{
				ControlModel controlModel = (ControlModel) formCtrl.getWidget() ;
				if ((null != controlModel) && controlModel.getInitFromPrev())
				{
					ArrayList<FormDataData> aFormData = getInformationForPath(previousBlock, formCtrl.getPath(), null) ;
					if ((null != aFormData) && (false == aFormData.isEmpty()))
						controlModel.setContent(aFormData.get(0), "") ;
					else
						controlModel.setContent(null, "") ;
				}
			}
			// Multiple information control
			//
			else if (formCtrl.getWidget() instanceof ControlModelMulti)
			{
				ControlModelMulti controlModel = (ControlModelMulti) formCtrl.getWidget() ;
				if ((null != controlModel) && controlModel.getInitFromPrev())
				{
					ArrayList<FormDataData> aFormData = getInformationForPath(previousBlock, formCtrl.getPath(), null) ;
					controlModel.setMultipleContent(aFormData, "") ;
				}
			}
		}
	}

	/**
	 * Parse the static information, as a string of the kind "$site$=3|$city$=7"
	 *
	 */
	protected void parseStaticInformation(final String sStaticInformation)
	{
		_aStatics.clear() ;
		
		if ((null == sStaticInformation) || "".equals(sStaticInformation))
			return ;
		
		// Static parameters are in the form "param1=value1|param2=value2|..."
		//
		// The first step is to parse the string in a "paramN=valueN" ArrayList
		//
		ArrayList<String> parsedStatics = GlobalParameters.ParseString(sStaticInformation, "|") ;
		
		if (parsedStatics.isEmpty())
			return ;
		
		for (Iterator<String> it = parsedStatics.iterator() ; it.hasNext() ; )
		{
			String sParamValue = it.next() ;
			ArrayList<String> parsedPair = GlobalParameters.ParseString(sParamValue, "=") ;
			if (parsedPair.size() == 2)
			{
				FormDataData fakeContent = new FormDataData() ;
				fakeContent.setValue(parsedPair.get(1)) ;
				_aStatics.add(new FormControl(null, fakeContent, parsedPair.get(0), "")) ;
			}
		}
	}

	protected String getStaticInformation(String sPath)
	{
		if ((null == sPath) || "".equals(sPath) || _aStatics.isEmpty())
			return "" ;
		
		for (Iterator<FormControl> it = _aStatics.iterator() ; it.hasNext() ; )
		{
			FormControl formControl = it.next() ;
			if (formControl.getPath().equals(sPath))
			{
				FormDataData content = formControl.getContent() ;
				if (null == content)
					return "" ;
				
				return content.getValue() ;
			}
		}
		
		return "" ;
	}

	/**
	 * Going back to the page the "new form" or "edit form" action was executed from
	 */
	protected void leave() {
		eventBus.fireEvent(new GoToLoginResponseEvent()) ;
	}
	
	@Override
	protected void onUnbind() {
	}

	@Override
	public void revealDisplay() {
	}
}
