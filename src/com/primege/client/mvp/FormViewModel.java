package com.primege.client.mvp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.primege.client.global.PrimegeSupervisorModel;
import com.primege.client.loc.PrimegeViewConstants;
import com.primege.client.util.FormControl;
import com.primege.client.util.FormControlOptionData;
import com.primege.client.widgets.ControlModel;
import com.primege.client.widgets.ControlModelMulti;
import com.primege.client.widgets.EventDateControl;
import com.primege.client.widgets.FormBlockInformation;
import com.primege.client.widgets.FormBlockPanel;
import com.primege.client.widgets.FormCheckBox;
import com.primege.client.widgets.FormFlexTable;
import com.primege.client.widgets.FormIntegerBox;
import com.primege.client.widgets.FormListBox;
import com.primege.client.widgets.FormRadioButtons;
import com.primege.client.widgets.FormTextArea;
import com.primege.client.widgets.FormTextBox;
import com.primege.client.widgets.StaticButton;
import com.primege.client.widgets.TableControl;
import com.primege.shared.GlobalParameters;
import com.primege.shared.database.FormDataData;

public abstract class FormViewModel extends PrimegeBaseDisplay implements FormInterfaceModel
{
	private final PrimegeViewConstants constants = GWT.create(PrimegeViewConstants.class) ;
	
	protected boolean                _bScreenShotMode ;
	
	private   FlowPanel              _globalPanel ;
	
	private   VerticalPanel          _mainFormPanel ;
	protected FormBlockPanel         _formPanel ;
	
	protected FlowPanel              _actionsCommandPannel ;
	protected FlowPanel              _actionsButtonsPannel ;
	protected FlowPanel              _actionsHistoryPannel ;
	protected ArrayList<FormBlockPanel> _aActions           = new ArrayList<FormBlockPanel>() ;
	protected ArrayList<Button>         _aNewActionsButtons = new ArrayList<Button>() ;
	
	private   Button                 _submitButton ;
	private   Button                 _submitDraftButton ;
	private   Button                 _cancelButton ;
	
	protected DialogBox              _WarnindDialogBox ;
	protected Label                  _WarnindDialogBoxLabel ;
	private   Button                 _WarningDialogBoxOkButton ;
	
	private   DialogBox              _DeleteConfirmationDialogBox ;
	private   Label                  _DeleteConfirmationDialogBoxLabel ;
	private   Button                 _DeleteConfirmationDialogBoxOkButton ;
	private   Button                 _DeleteConfirmationDialogBoxCancelButton ;
	
	protected final PrimegeSupervisorModel _supervisor ;
				
	@Inject
	public FormViewModel(final PrimegeSupervisorModel supervisor)
	{
		_supervisor = supervisor ;
		
		_bScreenShotMode = false ;
		
		// Initialize control buttons
		//				
		_submitButton = new Button(constants.validateNewForm()) ;
		_submitButton.addStyleName("button green") ;
		_submitDraftButton = new Button(constants.validateDraftForm()) ;
		_submitDraftButton.addStyleName("button orange draft_button") ;
		_cancelButton = new Button(constants.generalCancel()) ;
		_cancelButton.getElement().setAttribute("id", "encounter_cancel-id") ;
		_cancelButton.addStyleName("cancel_button button red") ;
		
		FlowPanel buttonsPanel = new FlowPanel() ;
		buttonsPanel.addStyleName("formButtonsPanel") ;
		buttonsPanel.add(_submitButton) ;
		buttonsPanel.add(_submitDraftButton) ;
		buttonsPanel.add(_cancelButton) ;
		
		// Initialize form panel
		//
		_formPanel = new FormBlockPanel(new FormBlockInformation(null)) ;
		_formPanel.addStyleName("formBlockPanel") ;
		
		// Initialize the global panel
		//
		_mainFormPanel = new VerticalPanel() ;
		// _globalPanel.setWidth("100%");
		_mainFormPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER) ;
		_mainFormPanel.addStyleName("mainFormPanel") ;
		_mainFormPanel.add(_formPanel) ;
		_mainFormPanel.add(buttonsPanel) ;
		
		// Initialize dialog boxes 
		//
		initWarningDialogBox() ;
		initDeleteConfirmationDialogBox() ;

		_actionsCommandPannel = null ;
		_actionsButtonsPannel = null ;
		_actionsHistoryPannel = null ;
		
		_globalPanel = new FlowPanel() ;
		_globalPanel.addStyleName("formGlobalPanel") ;
		_globalPanel.add(_mainFormPanel) ;
		
		initWidget(_globalPanel) ;
	}

	/** 
	 * Reset everything to display a new archetype
	 * 
	 */
	@Override
	public void resetAll()
	{
		_bScreenShotMode = false ;
		
		_formPanel.reset() ;
		
		resetAnnotations() ;
	}
	
	protected void resetAnnotations()
	{
		if (null != _actionsCommandPannel)
		{
			_actionsCommandPannel.clear() ;
			_actionsCommandPannel = null ;
		}
		if (null != _actionsButtonsPannel)
		{
			_actionsButtonsPannel.clear() ;
			_actionsButtonsPannel = null ;
		}
		if (null != _actionsHistoryPannel)
		{
			_actionsHistoryPannel.clear() ;
			_actionsHistoryPannel = null ;
		}
		
		_aActions.clear() ;
		_aNewActionsButtons.clear() ;
	}
	
	/** 
	 * Get all the FormDataData contained in the FormControl inside this form
	 * 
	 * @param aInformation Array of {@link FormDataData} to be filled with form's content
	 * 
	 * @return <code>true</code> if all mandatory controls contained information, <code>false</code> if not
	 */
	@Override
	public boolean getContent(ArrayList<FormDataData> aInformation, FormBlockPanel masterBlock) 
	{
		if (null == aInformation)
			return false ;
	
		FormBlockPanel referenceBlock = masterBlock ;
		if (null == referenceBlock)
			referenceBlock = _formPanel ;
		
		aInformation.clear() ; 
		
		boolean bEveryControlFilled = true ;
		
		for (FormControl formControl : referenceBlock.getControls())
		{
			if (formControl.isInformationProvider())
			{
				if (false == formControl.isMultiple())
				{
					// Get widget information
					//
					FormDataData formDataData = getInformationFromControl(formControl) ;
					if (null != formDataData)
					{
						// Complete with database information (if any)
						//
						FormDataData initialDataData = formControl.getContent() ;
						if (null != initialDataData)
						{
							formDataData.setId(initialDataData.getId());
							formDataData.setFormId(initialDataData.getFormId()) ;
						}
						
						aInformation.add(new FormDataData(formDataData)) ;
					}
					else if (formControl.isMandatory())
					{
						// Check if another control, that excludes this one, is activated
						//
						if (false == isExcludedByAnActiveControl(formControl))
							bEveryControlFilled = false ;
					}
				}
				else
				{
					ArrayList<FormDataData> aNewData = getMultipleInformationFromControl(formControl) ;
					if ((null != aNewData) && (false == aNewData.isEmpty()))
					{
						ArrayList<FormDataData> aInitialData = formControl.getMultipleContent() ;
						if ((null != aInitialData) && (false == aInitialData.isEmpty()))
							injectDatabaseIds(aNewData, aInitialData) ;
						
						for (FormDataData data : aNewData)
							aInformation.add(new FormDataData(data)) ;
					}
					else if (formControl.isMandatory())
					{
						// Check if another control, that excludes this one, is activated
						//
						if (false == isExcludedByAnActiveControl(formControl))
							bEveryControlFilled = false ;
					}
				}
			}
		}
		
		return bEveryControlFilled ;
	}
	
	/**
	 * Complete a set of edited information with identifiers from previous version in database
	 * 
	 * @param aNewData     New information to be completed
	 * @param aInitialData Previous (database stored) information
	 */
	protected void injectDatabaseIds(ArrayList<FormDataData> aNewData, final ArrayList<FormDataData> aInitialData)
	{
		if ((null == aNewData) || aNewData.isEmpty() || (null == aInitialData) || aInitialData.isEmpty())
			return ;
		
		for (FormDataData newData : aNewData)
		{
			for (FormDataData previousData : aInitialData)
				if (newData.getPath().equals(previousData.getPath()))
				{
					newData.setId(previousData.getId());
					newData.setFormId(previousData.getFormId()) ;
					break ;
				}
		}
	}
	
	/**
	 * Check if another control, that excludes the given control, is activated
	 * 
	 * @param formControl
	 * 
	 * @return
	 */
	public boolean isExcludedByAnActiveControl(final FormControl formControl)
	{
		if (null == formControl)
			return false ;
		
		String sPath = formControl.getPath() ;
		
		for (FormControl otherFormControl : _formPanel.getControls())
		{
			if (otherFormControl.isInformationProvider())
			{
				if (otherFormControl.isPathInExclusions(sPath))
				{
					FormDataData formDataData = getInformationFromControl(otherFormControl) ;
					if (null != formDataData)
						return true ;
				}
			}
		}
		
		return false ;
	}
	
	/** 
	 * Get a {@link FormControl} from its path
	 * 
	 * @return The {@link FormControl} is found, <code>null</code> if not
	 */
	public FormControl getFormControlForPath(final String sPath, FormBlockPanel masterBlock)
	{
		if ((null == sPath) || "".equals(sPath))
			return null ;
		
		FormBlockPanel referenceBlock = masterBlock ;
		if (null == referenceBlock)
			referenceBlock = _formPanel ;
		
		for (FormControl formControl : referenceBlock.getControls())
			if (formControl.getPath().equals(sPath))
				return formControl ;
		
		return null ;
	}
	
	/** 
	 * Get the {@link FormDataData} contained in the {@link FormControl} defined by a path
	 */
	public FormDataData getContentForPath(final String sPath, FormBlockPanel masterBlock)
	{
		if ((null == sPath) || "".equals(sPath))
			return null ;
		
		FormControl formControl = getFormControlForPath(sPath, masterBlock) ;
		if (null == formControl)
			return null ;
		
		return getInformationFromControl(formControl) ;
	}
	
	public void emptyContentForPath(final String sPath, FormBlockPanel masterBlock)
	{
		if ((null == sPath) || "".equals(sPath))
			return ;
		
		FormControl formControl = getFormControlForPath(sPath, masterBlock) ;
		if (null == formControl)
			return ;
		
		ControlModel controlModel = (ControlModel) formControl.getWidget() ;
		if (null == controlModel)
			return ;
		
		controlModel.resetContent() ;
	}
	
	/** 
	 * Get the {@link FormDataData} contained in a FormControl
	 * 
	 */
	protected FormDataData getInformationFromControl(FormControl formControl)
	{
		if (null == formControl)
			return null ;
		
		ControlModel controlModel = (ControlModel) formControl.getWidget() ;
		if (null == controlModel)
			return null ;
			
		return controlModel.getContent() ;
	}
	
	/** 
	 * Get the array of {@link FormDataData} contained in a multiple data FormControl
	 * 
	 */
	protected ArrayList<FormDataData> getMultipleInformationFromControl(FormControl formControl)
	{
		if (null == formControl)
			return null ;
		
		ControlModelMulti controlModel = (ControlModelMulti) formControl.getWidget() ;
		if (null == controlModel)
			return null ;
			
		return controlModel.getMultipleContent() ;
	}
	
	/**
	 * Get the first {@link FormDataData} in a list, in order to feed "single data" controls
	 *  
	 * @param aContent List of {@link FormDataData} (should be empty or contain only one element)
	 * 
	 * @return A {@link FormDataData} if the list is not <code>null</code> or empty, <code>null</code> if it is
	 */
	protected FormDataData getSingleInformationContent(ArrayList<FormDataData> aContent)
	{
		if ((null == aContent) || aContent.isEmpty())
			return null ;
		
		return aContent.get(0) ;
	}
	
	/** 
	 * initWarningDialogBox - Initialize warning dialog box
	 * 
	 * @param    nothing
	 * @return   nothing  
	 */
	private void initWarningDialogBox()
	{
		_WarnindDialogBox = new DialogBox() ;
		_WarnindDialogBox.setPopupPosition(100, 200) ;
		_WarnindDialogBox.setText(constants.warning()) ;
		_WarnindDialogBox.setAnimationEnabled(true) ;
		
		_WarnindDialogBoxLabel = new Label("") ;
		_WarnindDialogBoxLabel.addStyleName("warningDialogLabel") ;
		
		_WarningDialogBoxOkButton = new Button(constants.generalOk()) ;
		_WarningDialogBoxOkButton.setSize("70px", "30px") ;
		_WarningDialogBoxOkButton.getElement().setId("okbutton") ;
		
		FlowPanel warningPannel = new FlowPanel() ;
		warningPannel.add(_WarnindDialogBoxLabel) ;
		warningPannel.add(_WarningDialogBoxOkButton) ;
		
		_WarnindDialogBox.add(warningPannel) ;
	}
	
	/** 
	 * initDeleteConfirmationDialogBox - Initialize delete confirmation dialog box
	 * 
	 * @param    nothing
	 * @return   nothing  
	 */
	private void initDeleteConfirmationDialogBox()
	{
		_DeleteConfirmationDialogBox = new DialogBox() ;
		_DeleteConfirmationDialogBox.setPopupPosition(100, 200) ;
		_DeleteConfirmationDialogBox.setText(constants.warning()) ;
		_DeleteConfirmationDialogBox.setAnimationEnabled(true) ;
		
		_DeleteConfirmationDialogBoxLabel = new Label("") ;
		_DeleteConfirmationDialogBoxLabel.addStyleName("warningDialogLabel") ;
    
		_DeleteConfirmationDialogBoxCancelButton = new Button(constants.generalCancel()) ;
		_DeleteConfirmationDialogBoxCancelButton.setSize("70px", "30px") ;
		_DeleteConfirmationDialogBoxCancelButton.getElement().setId("cancelbutton") ;
		
		_DeleteConfirmationDialogBoxOkButton = new Button(constants.generalOk()) ;
		_DeleteConfirmationDialogBoxOkButton.setSize("70px", "30px") ;
		_DeleteConfirmationDialogBoxOkButton.getElement().setId("okbutton") ;
		
		FlowPanel warningPannel = new FlowPanel() ;
		warningPannel.add(_DeleteConfirmationDialogBoxLabel) ;
		warningPannel.add(_DeleteConfirmationDialogBoxCancelButton) ;
		warningPannel.add(_DeleteConfirmationDialogBoxOkButton) ;
		
		_DeleteConfirmationDialogBox.add(warningPannel) ;
	}

	/** 
	 * Get message text from message ID
	 * 
	 * @param    sMessage the unique identifier of the message
	 * @return   the human readable message
	 */
	protected String getPopupWarningMessage(final String sMessage)
	{
		if      (sMessage.equals("WARNING_FORM_ALREADY_EXIST"))
			return constants.warningAlreadyExist() ;
		else if (sMessage.equals("ERROR_MUST_ENTER_EVERY_INFORMATION"))
			return constants.mandatoryEnterAll() ;
		
		return "" ;
	}
	
	/** 
	 * popupWarningMessage - Display warning dialog box
	 * 
	 * @param    nothing
	 * @return   nothing  
	 */
	@Override
	public void popupWarningMessage(final String sMessage)
	{
		_WarnindDialogBoxLabel.setText(getPopupWarningMessage(sMessage)) ;
		_WarnindDialogBox.show() ;
	}
	
	@Override
	public void closeWarningDialog() {
		_WarnindDialogBox.hide() ;
	}
	
	/** 
	 * popupWarningMessage - Display delete confirmation dialog box
	 * 
	 * @param    bIsBastket true for basket deletion, false for basket element deletion
	 * @return   nothing  
	 */
	@Override
	public void popupDeleteConfirmationMessage(final boolean bIsBastket)
	{
		if (bIsBastket)
			_DeleteConfirmationDialogBoxLabel.setText(constants.confirmDeleteBasket()) ;
		else 
			_DeleteConfirmationDialogBoxLabel.setText(constants.confirmDeleteElement()) ;
		
		_DeleteConfirmationDialogBox.show() ;
	}
	
	@Override
	public void closeDeleteConfirmationDialog() {
		_DeleteConfirmationDialogBox.hide() ;
	}
		
	public void initSpeedListBox(ListBox speedListBox, int iSelectedValue) 
	{
		int iSelectedIndex = -1 ;
		
		for (int i = 1 ; i < 30 ; i++)
		{
			speedListBox.addItem(Integer.toString(i)) ;
			
			if (i == iSelectedValue)
				iSelectedIndex = i - 1 ;
		}
		
		if (-1 == iSelectedIndex)
			iSelectedIndex = 5 ;
		
		speedListBox.setVisibleItemCount(1) ;
		speedListBox.setItemSelected(iSelectedIndex, true) ;
	}
	
	/** 
	 * Return the current panel to add widgets to
	 * 
	 * @param masterBlock Reference block
	 * 
	 * @return Either the main form panel or the current block
	 */
	public FormBlockPanel getCurrentFormPanel(FormBlockPanel masterBlock)
	{
		FormBlockPanel referenceBlock = masterBlock ;
		if (null == referenceBlock)
			referenceBlock = _formPanel ;
		
		if (referenceBlock.isBlockStackEmpty())
			return referenceBlock ;
		
		return referenceBlock.peekBlockStack() ;
	}
	
	/** 
	 * Insert a new block to the form
	 * 
	 * @param sBgColor block's background color
	 * @param sCaption block's sCaption
	 */
	@Override
	public void insertNewBlock(final FormBlockInformation presentation, FormBlockPanel masterBlock)
	{
		FormBlockPanel referenceBlock = masterBlock ;
		if (null == referenceBlock)
			referenceBlock = _formPanel ;
		
		FormBlockPanel newBlock = new FormBlockPanel(presentation) ;
		
		String sBlockStyle = presentation.getStyle() ;
		if ((null != sBlockStyle) && false == "".equals(sBlockStyle))
			newBlock.addStyleName(sBlockStyle) ;
		
		// add the new block to the form
		//
		FormBlockPanel currentBlock = getCurrentFormPanel(referenceBlock) ;
		
		if (null == currentBlock)
			return ;
			
		currentBlock.insertBlock(newBlock) ;
		
		// newBlock.applyStyle() ;
		
		// add it to the top of the stack
		//
		referenceBlock.pushBlockStack(newBlock) ;
	}
	
	/** 
	 * Signal that the top form's block already received all its elements
	 * 
	 */
	@Override
	public void endOfBlock(final boolean bInPdfWhenEmpty, FormBlockPanel masterBlock)
	{
		FormBlockPanel referenceBlock = masterBlock ;
		if (null == referenceBlock)
			referenceBlock = _formPanel ;
		
		// In screenshot mode, we have to check  remove the new block if empty
		//
		if (_bScreenShotMode && (false == bInPdfWhenEmpty))
		{
			FormBlockPanel currentBlock = getCurrentFormPanel(referenceBlock) ;
			if ((null != currentBlock) && (currentBlock.getRowCount() == 0))
			{
				referenceBlock.popBlockStack() ;
				
				FormBlockPanel fatherBlock = referenceBlock.peekBlockStack() ;
				if (null != fatherBlock)
					fatherBlock.removeLastSonBlock() ;
				
				return ;
			}
		}
		
		referenceBlock.popBlockStack() ;
	}
	
	/** 
	 * Insert a new control to the form... used for generic controls
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
	 * @param bInPdfWhenEmpty <code>true</code> if the control is to be inserted in the PDF, even when empty, <code>false</code> if not
	 * @param masterBlock     Form's root panel (if null, then the form global panel is used)
	 * @param bEdited         <code>true</code> if the form is being edited, <code>false</code> if it is new
	 */
	@SuppressWarnings("deprecation")
	public void insertNewGenericControl(final String sControlPath, final ArrayList<FormDataData> aContent, final String sControlCaption, final String sControlType, final String sControlSubtype, final String sControlUnit, final String sControlValue, final ArrayList<FormControlOptionData> aOptions, final String sControlStyle, final String sCaptionStyle, final boolean bInitFromPrev, final String sExclusion, final boolean bInBlockCell, final boolean bInPdfWhenEmpty, FormBlockPanel masterBlock, boolean bEdited)
	{
		FormBlockPanel referenceBlock = masterBlock ;
		if (null == referenceBlock)
			referenceBlock = _formPanel ;
		
		// Get the panel to add the control to
		// 
		FormBlockPanel currentBlock = getCurrentFormPanel(referenceBlock) ;
		if (null == currentBlock)
			return ;
		
		if ("Edit".equalsIgnoreCase(sControlType))
		{
			FormDataData content = getSingleInformationContent(aContent) ;
			
			// Screenshot mode, just insert a Label that displays the label
			//
			if (_bScreenShotMode)
			{
				String sContent = "" ;
				if ((null != content) && (false == content.hasNoData()))
					sContent = content.getValue() ;
					
				if ((false == "".equals(sContent)) || bInPdfWhenEmpty)
				{
					Label editAsLabel = new Label(sContent) ;
					
					if ((null != sControlStyle) && false == "".equals(sControlStyle))
						editAsLabel.addStyleName(sControlStyle) ;
					
					currentBlock.insertControl(sControlCaption, sCaptionStyle, editAsLabel, null, bInBlockCell) ;
				}
				return ;
			}
			
			// Not in screenshot mode, insert a control (either for numerial or text entry)
			//
			if ("Integer".equalsIgnoreCase(sControlSubtype))
			{
				FormIntegerBox box = new FormIntegerBox(sControlPath) ;
				box.addStyleName("integerTextBox") ;
				box.setContent(content, sControlValue) ;
				box.setInitFromPrev(bInitFromPrev) ;
				
				if ((null != sControlStyle) && false == "".equals(sControlStyle))
					box.addStyleName(sControlStyle) ;
				
				referenceBlock.addControl(new FormControl(box.getControlBase(), box, content, sExclusion)) ;
				
				if ((null == sControlUnit) || "".equals(sControlUnit))
					currentBlock.insertControl(sControlCaption, sCaptionStyle, box, box.getControlBase(), bInBlockCell) ;
				else
				{
					FlexTable EditTable = new FlexTable() ;
					EditTable.addStyleName("editTable") ;
					EditTable.setWidget(0, 0, box) ;
					EditTable.setWidget(0, 1, new Label(sControlUnit)) ;
					currentBlock.insertControl(sControlCaption, sCaptionStyle, EditTable, null, bInBlockCell) ;
				}
			}
			else
			{
				FormTextBox box = new FormTextBox(sControlPath) ;
				
				if ((null != sControlStyle) && false == "".equals(sControlStyle))
					box.addStyleName(sControlStyle) ;
				
				box.setContent(content, sControlValue) ;
				box.setInitFromPrev(bInitFromPrev) ;
				
				referenceBlock.addControl(new FormControl(box.getControlBase(), box, content, sExclusion)) ;
				currentBlock.insertControl(sControlCaption, sCaptionStyle, box, box.getControlBase(), bInBlockCell) ;
			}
		}
		else if ("Text".equalsIgnoreCase(sControlType))
		{
			FormDataData content = getSingleInformationContent(aContent) ;
			
			// Screenshot mode, just insert a Label that displays the text
			//
			if (_bScreenShotMode)
			{
				String sContent = "" ;
				if ((null != content) && (false == content.hasNoData()))
					sContent = content.getValue() ;
					
				if ((false == "".equals(sContent)) || bInPdfWhenEmpty)
				{
					Label textAsLabel = new Label(sContent) ;
					
					if ((null != sControlStyle) && false == "".equals(sControlStyle))
						textAsLabel.addStyleName(sControlStyle) ;
					
					currentBlock.insertControl(sControlCaption, sCaptionStyle, textAsLabel, null, bInBlockCell) ;
				}
				return ;
			}
			
			// Not in screenshot mode, insert a text entry control
			//
			FormTextArea text = new FormTextArea(sControlPath) ;
			
			if ((null != sControlStyle) && false == "".equals(sControlStyle))
				text.addStyleName(sControlStyle) ;
			
			text.setContent(content, sControlValue) ;
			text.setInitFromPrev(bInitFromPrev) ;
			
			referenceBlock.addControl(new FormControl(text.getControlBase(), text, content, sExclusion)) ;
			currentBlock.insertControl(sControlCaption, sCaptionStyle, text, text.getControlBase(), bInBlockCell) ;
		}
		else if ("Date".equalsIgnoreCase(sControlType))
		{
			FormDataData content = getSingleInformationContent(aContent) ;
			
			// Screenshot mode, just insert a Label that displays the date
			//
			if (_bScreenShotMode)
			{
				String sDateLabel = "" ;
				if ((null != content) && (false == content.hasNoData()))
				{
					EventDateControl dateControl = new EventDateControl(null, sControlPath) ;
					dateControl.setContent(content, sControlValue) ;
					
					sDateLabel =  "" + dateControl.getDayForDate(content.getValue()) + " " ;
					sDateLabel += dateControl.getMonthLabel(dateControl.getMonthForDate(content.getValue())) ;
					sDateLabel += " " + dateControl.getYearForDate(content.getValue()) ;
				}
					
				if ((false == "".equals(sDateLabel)) || bInPdfWhenEmpty)
				{
					Label dateAsLabel = new Label(sDateLabel) ;
					
					if ((null != sControlStyle) && false == "".equals(sControlStyle))
						dateAsLabel.addStyleName(sControlStyle) ;
					
					currentBlock.insertControl(sControlCaption, sCaptionStyle, dateAsLabel, null, bInBlockCell) ;
				}
				return ;
			}
			
			// Not in screenshot mode, insert a date entry control
			//
			EventDateControl dateControl = new EventDateControl(null, sControlPath) ;
			dateControl.setEdited(bEdited) ;
			
			if ((null != sControlStyle) && false == "".equals(sControlStyle))
				dateControl.addStyleName(sControlStyle) ;
			
			dateControl.setInitFromPrev(bInitFromPrev) ;
			
			// If no date specified, initialize with "now" or "yesterday" 
			//
			if (null == content)
			{
				FormDataData fakeContent = new FormDataData() ;
				
				Date tNow = new Date() ;
				
				String sContentToInitialize = "" ;
				
/*
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
*/
					sContentToInitialize = GlobalParameters.getDateAsString(tNow) ;
				
				fakeContent.setValue(sContentToInitialize) ;
				dateControl.setContent(fakeContent, sControlValue) ;
			}
			else
				dateControl.setContent(content, sControlValue) ;
			
			referenceBlock.addControl(new FormControl(dateControl.getControlBase(), dateControl, content, sExclusion)) ;
			currentBlock.insertControl(sControlCaption, sCaptionStyle, dateControl, dateControl.getControlBase(), bInBlockCell) ;
		}
		else if ("RadioButton".equalsIgnoreCase(sControlType))
		{
			FormDataData content = getSingleInformationContent(aContent) ;
			
			if (_bScreenShotMode)
			{
				String sLabel = "" ;
				// if ((null != content) && (false == content.hasNoData())) : no data = no value, this is normal here
				if (null != content)
				{
				// Get option for content
					//
					String sPath  = content.getPath() ;
					
					for (FormControlOptionData optionData : aOptions)
					{
						String sLocalPath = sControlPath + "/" + optionData.getPath() ;
						if (sLocalPath.equals(sPath))
						{
							sLabel = optionData.getCaption() ;
							break ;
						}
					}
				}
					
				if (sLabel.isEmpty() && (false == bInPdfWhenEmpty))
					return ;
				
				Label listAsLabel = new Label(sLabel) ;
				
				if ((null != sControlStyle) && false == "".equals(sControlStyle))
					listAsLabel.addStyleName(sControlStyle) ;
				
				currentBlock.insertControl(sControlCaption, sCaptionStyle, listAsLabel, null, bInBlockCell) ;
				
				return ;
			}
			
			FormRadioButtons radioControl = new FormRadioButtons(aOptions, sControlPath) ;
			
			if ((null != sControlStyle) && false == "".equals(sControlStyle))
				radioControl.addStyleName(sControlStyle) ;
			
			radioControl.setContent(content, sControlValue) ;
			radioControl.setInitFromPrev(bInitFromPrev) ;
			
			referenceBlock.addControl(new FormControl(radioControl.getControlBase(), radioControl, content, sExclusion)) ;
			currentBlock.insertControl(sControlCaption, sCaptionStyle, radioControl, radioControl.getControlBase(), bInBlockCell) ;
		}
		else if ("CheckBox".equalsIgnoreCase(sControlType))
		{
			FormDataData content = getSingleInformationContent(aContent) ;
			
			if (_bScreenShotMode)
			{
				String sLabel = "" ;
				if ((null != content) && (false == content.hasNoData()))
					sLabel = "OK" ;
				
				if (sLabel.isEmpty() && (false == bInPdfWhenEmpty))
					return ;
				
				Label listAsLabel = new Label(sLabel) ;
				
				if ((null != sControlStyle) && false == "".equals(sControlStyle))
					listAsLabel.addStyleName(sControlStyle) ;
				
				currentBlock.insertControl(sControlCaption, sCaptionStyle, listAsLabel, null, bInBlockCell) ;
				
				return ;
			}
			
			FormCheckBox checkBox = new FormCheckBox(sControlCaption, sControlPath) ;
			
			if ((null != sControlStyle) && false == "".equals(sControlStyle))
				checkBox.addStyleName(sControlStyle) ;
			
			checkBox.setContent(content, sControlValue) ;
			checkBox.setInitFromPrev(bInitFromPrev) ;
			
			referenceBlock.addControl(new FormControl(checkBox.getControlBase(), checkBox, content, sExclusion)) ;
			currentBlock.insertControl("", sCaptionStyle, checkBox, checkBox.getControlBase(), bInBlockCell) ;
		}
		else if ("ListBox".equalsIgnoreCase(sControlType))
		{
			FormDataData content = getSingleInformationContent(aContent) ;
			
			// Screenshot mode, just insert a Label that displays the text
			//
			if (_bScreenShotMode)
			{
				String sLabel = "" ;
				// if ((null != content) && (false == content.hasNoData())) : no data = no value, this is normal here
				if (null != content)
				{
				// Get option for content
					//
					String sPath  = content.getPath() ;
					
					for (FormControlOptionData optionData : aOptions)
					{
						String sLocalPath = sControlPath + "/" + optionData.getPath() ;
						if (sLocalPath.equals(sPath))
						{
							sLabel = optionData.getCaption() ;
							break ;
						}
					}
				}
					
				if (sLabel.isEmpty() && (false == bInPdfWhenEmpty))
					return ;
				
				Label listAsLabel = new Label(sLabel) ;
				
				if ((null != sControlStyle) && false == "".equals(sControlStyle))
					listAsLabel.addStyleName(sControlStyle) ;
				
				currentBlock.insertControl(sControlCaption, sCaptionStyle, listAsLabel, null, bInBlockCell) ;
				
				return ;
			}
			
			FormListBox listBox = new FormListBox(aOptions, sControlPath) ;
			
			if ((null != sControlStyle) && false == "".equals(sControlStyle))
				listBox.addStyleName(sControlStyle) ;
			
			listBox.setContent(content, sControlValue) ;
			listBox.setInitFromPrev(bInitFromPrev) ;
			
			referenceBlock.addControl(new FormControl(listBox.getControlBase(), listBox, content, sExclusion)) ;
			currentBlock.insertControl(sControlCaption, sCaptionStyle, listBox, listBox.getControlBase(), bInBlockCell) ;
		}
		else if ("StaticButton".equalsIgnoreCase(sControlType))
		{
			if (_bScreenShotMode)
				return ;
			
			FormDataData content = getSingleInformationContent(aContent) ;
			
			StaticButton staticButton = new StaticButton(sControlCaption, sControlPath) ;
			
			if ((null != sControlStyle) && false == "".equals(sControlStyle))
				staticButton.addStyleName(sControlStyle) ;
			
			referenceBlock.addControl(new FormControl(staticButton.getControlBase(), staticButton, content, false, false)) ;
			currentBlock.insertControl(sControlCaption, sCaptionStyle, staticButton, staticButton.getControlBase(), bInBlockCell) ;
		}
		else if ("Table".equalsIgnoreCase(sControlType))
		{
			// Screenshot mode, just insert a FlexTable that displays the information
			//
			if (_bScreenShotMode)
			{
				if (((null != aContent) && (false == aContent.isEmpty())) || bInPdfWhenEmpty)
				{
					FormFlexTable table = new FormFlexTable() ;
					table.initCaptionRow(aOptions) ;
					table.setMultipleContent(sControlPath, aContent, aOptions) ;
					
					if ((null != sControlStyle) && false == "".equals(sControlStyle))
						table.addStyleName(sControlStyle) ;
					
					currentBlock.insertControl(sControlCaption, sCaptionStyle, table, null, bInBlockCell) ;
				}
				return ;
			}
			
			TableControl tableControl = new TableControl(aOptions, sControlPath) ;
			
			if ((null != sControlStyle) && false == "".equals(sControlStyle))
				tableControl.addStyleName(sControlStyle) ;
			
			tableControl.setMultipleContent(aContent, sControlValue) ;
			tableControl.setInitFromPrev(bInitFromPrev) ;
			
			referenceBlock.addControl(new FormControl(tableControl.getControlBase(), tableControl, aContent, sExclusion)) ;
			currentBlock.insertControl(sControlCaption, sCaptionStyle, tableControl, tableControl.getControlBase(), bInBlockCell) ;
		}
	}
	
	/**
	 * Disable sub controls for inactive controls
	 */
	public void inactivateInactiveControlsSiblings(FormBlockPanel masterBlock)
	{
		FormBlockPanel referenceBlock = masterBlock ;
		if (null == referenceBlock)
			referenceBlock = _formPanel ;
		
		for (FormControl control : referenceBlock.getControls())
		{
			FormDataData content = control.getContent() ;
			if ((null == content) || content.isEmpty())
			{
				String sPath = control.getPath() ;
				for (FormControl otherControl : referenceBlock.getControls())
				{
					String sOtherPath = otherControl.getPath() ;
					if ((false == sOtherPath.equals(sPath)) && sOtherPath.startsWith(sPath))
						otherControl.getWidget().getElement().setAttribute("disabled", "false") ;
				}
			}
		}
	}
	
	/**
	 * Return a HTML text from a raw text
	 */
/*
	protected String getHtmlText(final String sContent)
	{
		if ((null == sContent) || "".equals(sContent))
			return sContent ;
		
		String sReturn = sContent.replaceAll("(\r\n|\n)", "<br />") ;
		
		return sReturn ;
	}
*/
	
	/** 
	 * Find a control from its path
	 * 
	 * @param  sControlPath control's path (arborescent identifier)
	 * 
	 * @return the control as a widget if found, <code>null</code> if not
	 */
	@Override
	public Widget getControlForPath(final String sControlPath, FormBlockPanel masterBlock) 
	{
		if ((null == sControlPath) || "".equals(sControlPath))
			return null ;
		
		FormBlockPanel referenceBlock = masterBlock ;
		if (null == referenceBlock)
			referenceBlock = _formPanel ;
		
		if (referenceBlock.getControls().isEmpty())
			return null ;
		
		for (FormControl formControl : referenceBlock.getControls())
			if (sControlPath.equals(formControl.getPath()))
				return formControl.getWidget() ;
		
		return null ;
	}
	
	/** 
	 * Find a {@link FormControl} from its path
	 * 
	 * @param  sControlPath control's path (arborescent identifier)
	 * 
	 * @return the control if found, <code>null</code> if not
	 */
	@Override
	public FormControl getPlainControlForPath(final String sControlPath, FormBlockPanel masterBlock) 
	{
		if ((null == sControlPath) || "".equals(sControlPath))
			return null ;
		
		FormBlockPanel referenceBlock = masterBlock ;
		if (null == referenceBlock)
			referenceBlock = _formPanel ;
		
		if (referenceBlock.getControls().isEmpty())
			return null ;
		
		for (FormControl formControl : referenceBlock.getControls())
			if (sControlPath.equals(formControl.getPath()))
				return formControl ;
		
		return null ;
	}
	
	@Override
	public void setDefaultValues()
	{
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}
	
	@Override
	public HasClickHandlers getReset() {
		return _cancelButton ;
	}

	@Override
	public Button getSubmitButton() {
		return _submitButton ; 
	}
	
	@Override
	public Button getSubmitDraftButton() {
		return _submitDraftButton ; 
	}

	@Override
	public void setSubmitDraftButtonVisible(final boolean bVisible) {
		_submitDraftButton.setVisible(bVisible) ;
	}
	
	@Override
	public HasClickHandlers getWarningOk()
	{
		return _WarningDialogBoxOkButton ;
	}
	
	@Override
	public HasClickHandlers getDeleteConfirmationOk() {
		return _DeleteConfirmationDialogBoxOkButton ;
	}
	
	@Override
	public HasClickHandlers getDeleteConfirmationCancel() {
		return _DeleteConfirmationDialogBoxCancelButton ;
	}

	@Override
	public HasChangeHandlers getDateChanged()
	{
		Widget widget = getControlForPath("$date$", null) ;
		if (null == widget)
			return null ;
		
		EventDateControl dateControl = (EventDateControl) widget ;
		return dateControl ;
	}
	
	@Override
	public void showWaitCursor() {
		PrimegeBaseDisplay.switchToWaitCursor() ;
	}

	@Override
	public void showDefaultCursor() {
		PrimegeBaseDisplay.switchToDefaultCursor() ;
	}
	
	@Override
	public HasClickHandlers getInitFromPreviousButton()
	{
		Widget btnAsWidget = getControlForPath("$initFromPrev$", null) ;
		if (null == btnAsWidget)
			return null ;
		
		return (HasClickHandlers) btnAsWidget ;
	}
	
	@Override
	public ArrayList<FormControl> getControls(FormBlockPanel masterBlock)
	{
		FormBlockPanel referenceBlock = masterBlock ;
		if (null == referenceBlock)
			referenceBlock = _formPanel ;
		
		return referenceBlock.getControls() ;
	}
	
	@Override
	public void setScreenShotMode(final boolean bScreenShotMode) {
		_bScreenShotMode = bScreenShotMode ;
	}
	
	@Override
	public boolean isScreenShotMode() {
		return _bScreenShotMode ;
	}
  
  /**
   * Create an annotation block panel and add it to the actions list
   * 
   * @param sCaption          Action title
   * @param iAnnotationFormID Identifier of annotation's form being displayed (<code>-1</code> if a new one)
   * 
   * @return The newly created block
   */
  @Override
  public FormBlockPanel getNewActionBlock(final String sCaption, final String sDate, final int iAnnotationFormID, final String sActionId, ClickHandler actionClickHandler)
	{
  	// First, check if this annotation is already displayed
  	//
  	FormBlockPanel existingFBP = getActionFromAnnotationID(iAnnotationFormID, "") ;
  	if (null != existingFBP)
  		return existingFBP ;
  	
  	// If not already existing, create it
  	//
  	
  	// Create command panel
  	//
  	FlowPanel commandPanel = new FlowPanel() ;
  	commandPanel.addStyleName("annotationCommandPanel") ;
  	
  	if ((null != sCaption) && (false == sCaption.isEmpty()))
  	{
  		String sDisplayedCaption = sCaption ;
  		
  		if (false == sDate.isEmpty())
  		{
  			EventDateControl dateControl = new EventDateControl(null, "") ;
				
  			sDisplayedCaption += " " + constants.formsAt() + " " + dateControl.getDayForDate(sDate) + " " +
  					dateControl.getMonthLabel(dateControl.getMonthForDate(sDate)) +
  					" " + dateControl.getYearForDate(sDate) ;
  		}
  		
  		Label captionLabel = new Label(sDisplayedCaption) ;
  		commandPanel.add(captionLabel) ;
  	}
  	
  	// Create bloc panel
  	//
  	FormBlockPanel newBlock = new FormBlockPanel(new FormBlockInformation(null)) ;
  	newBlock.addStyleName("annotationFormBlockPanel") ;
  	
  	newBlock.setActionIdentifier(sActionId) ;
  	
  	// Create buttons panel
  	//
  	FlowPanel buttonsPanel = new FlowPanel() ;
		buttonsPanel.addStyleName("formButtonsPanel") ;
  	
		// Create the global action panel
		//
  	FlowPanel actionPanel = new FlowPanel() ;
  	actionPanel.addStyleName("formAnnotationPanel") ;
  	
  	actionPanel.add(commandPanel) ;
  	actionPanel.add(newBlock) ;
  	actionPanel.add(buttonsPanel) ;
  	
  	_actionsHistoryPannel.add(actionPanel) ;
  	
  	newBlock.getElement().setAttribute("id", "annotation-id" + iAnnotationFormID) ;
  	
  	// New annotation, create save and cancel buttons immediately
  	//
  	if (-1 == iAnnotationFormID)
  	{
  		Button submitButton = new Button(constants.validateNewForm(), actionClickHandler) ;
  		submitButton.getElement().setAttribute("id", "action_save-action" + sActionId) ;
  		submitButton.addStyleName("button green") ;
  		Button submitDraftButton = new Button(constants.validateDraftForm(), actionClickHandler) ;
  		submitDraftButton.getElement().setAttribute("id", "action_draft-action" + sActionId) ;
  		submitDraftButton.addStyleName("button orange draft_button") ;
  		Button cancelButton = new Button(constants.generalCancel(), actionClickHandler) ;
  		cancelButton.getElement().setAttribute("id", "action_cancel-action" + sActionId) ;
  		cancelButton.addStyleName("cancel_button button red") ;
  		
  		buttonsPanel.add(submitButton) ;
  		buttonsPanel.add(submitDraftButton) ;
  		buttonsPanel.add(cancelButton) ;
  	}
  	// Previous annotation, create edit and delete buttons
  	//
  	else
  	{
  		Button editButton = new Button(constants.formEdit(), actionClickHandler) ;
  		editButton.getElement().setAttribute("id", "action_edit-id" + iAnnotationFormID) ;
  		editButton.addStyleName("button small white") ;
  		Button deleteButton = new Button(constants.formDelete(), actionClickHandler) ;
  		deleteButton.getElement().setAttribute("id", "action_delete-id" + iAnnotationFormID) ;
  		deleteButton.addStyleName("button small red") ;
  		
  		// commandPanel.add(editButton) ;
  		// commandPanel.add(deleteButton) ;
  		buttonsPanel.add(editButton) ;
  		buttonsPanel.add(deleteButton) ;
  	}
  	
  	_aActions.add(newBlock) ;
  	
  	return newBlock ;
  }
  
  /**
   * Install proper buttons to a block depending on its closed/opened status
   * 
   * @param formBlockPanel     Block panel to set buttons for
   * @param iAnnotationFormID  Annotation form identifier for this block
   * @param actionClickHandler Click handler to attach new buttons to
   * @param bOpened            <code>true</code> if in Edit mode; <code>false</code> if closed
   */
  @Override
  public void setActionBlockEditButtons(final FormBlockPanel formBlockPanel, final int iAnnotationFormID, ClickHandler actionClickHandler, boolean bOpened)
	{
  	if (null == formBlockPanel)
  		return ;
  	
  	Widget parentWidget = formBlockPanel.getParent() ;
  	if (null == parentWidget)
  		return ;
  	
  	FlowPanel buttonsPanel = getButtonsPanelForBlock(formBlockPanel) ;
  	if (null == buttonsPanel)
  		return ;
  	
  	buttonsPanel.clear() ;
  	
  	// Annotation edition is openend
   	//
   	if (bOpened)
   	{
   		Button submitButton = new Button(constants.validateNewForm(), actionClickHandler) ;
   		submitButton.getElement().setAttribute("id", "action_save-id" + iAnnotationFormID) ;
   		submitButton.addStyleName("button green") ;
   		Button submitDraftButton = new Button(constants.validateDraftForm(), actionClickHandler) ;
   		submitDraftButton.getElement().setAttribute("id", "action_draft-id" + iAnnotationFormID) ;
   		submitDraftButton.addStyleName("button orange draft_button") ;
   		Button cancelButton = new Button(constants.generalCancel(), actionClickHandler) ;
   		cancelButton.getElement().setAttribute("id", "action_cancel-id" + iAnnotationFormID) ;
   		cancelButton.addStyleName("cancel_button button red") ;
   		
   		buttonsPanel.add(submitButton) ;
   		buttonsPanel.add(submitDraftButton) ;
   		buttonsPanel.add(cancelButton) ;
   	}
   	// Previous annotation, create edit and delete buttons
   	//
   	else
   	{
   		Button editButton = new Button(constants.formEdit(), actionClickHandler) ;
   		editButton.getElement().setAttribute("id", "action_edit-id" + iAnnotationFormID) ;
   		editButton.addStyleName("button small white") ;
   		Button deleteButton = new Button(constants.formDelete(), actionClickHandler) ;
   		deleteButton.getElement().setAttribute("id", "action_delete-id" + iAnnotationFormID) ;
   		deleteButton.addStyleName("button small red") ;
   		
   		buttonsPanel.add(editButton) ;
   		buttonsPanel.add(deleteButton) ;
   	}
	}
  
  /**
   * Get a block's button panel
   * 
   * @param formBlockPanel The panel which buttons panel is to be found
   * 
   * @return A panel if found, <code>null</code> if not
   */
  protected FlowPanel getButtonsPanelForBlock(final FormBlockPanel formBlockPanel)
  {
  	if (null == formBlockPanel)
  		return null ;
  	
  	return getPanelForStyle(formBlockPanel, "formButtonsPanel") ;
  }
  
  /**
   * Get a block's panel from its CSS style
   * 
   * @param formBlockPanel The panel which buttons panel is to be found
   * @param sStyleName     Name of the style the looked for panel must possess
   * 
   * @return A panel if found, <code>null</code> if not
   */
  protected FlowPanel getPanelForStyle(final FormBlockPanel formBlockPanel, final String sStyleName)
  {
  	if ((null == formBlockPanel) || (null == sStyleName) || sStyleName.isEmpty())
  		return null ;
  	
  	Widget parentWidget = formBlockPanel.getParent() ;
  	if (null == parentWidget)
  		return null ;
  	
  	FlowPanel actionPanel = (FlowPanel) parentWidget ;
  	
  	Iterator<Widget> arrayOfWidgets = actionPanel.iterator() ;
  	while (arrayOfWidgets.hasNext())
  	{
  		Widget ch = arrayOfWidgets.next() ;
  		if (ch.getStyleName().contains(sStyleName))
  			return (FlowPanel) ch ;
  	}
  	
  	return null ;
  }
  
  /**
   * Remove an action block
   */
  public void removeActionBlock(final FormBlockPanel formBlockPanel)
  {
  	if (null == formBlockPanel)
  		return ;
  	
  	// Remove from the list
  	//
  	_aActions.remove(formBlockPanel) ;
  	
  	// Remove from parent
  	//
  	Widget parentWidget = formBlockPanel.getParent() ;
  	if (null == parentWidget)
  		return ;
  	
  	parentWidget.removeFromParent() ;
  }
  
  /**
   * Clear the form block
   */
  @Override
  public void clearActionBlock(final FormBlockPanel formBlockPanel)
  {
  	if (null == formBlockPanel)
  		return ;
  	
  	formBlockPanel.clear() ;
  }
  
  /**
   * Get the {@link FormBlockPanel} for a given annotation identifier
   * 
   * @param iAnnotationID Annotation identifier to look for
   * @param sActionId     Action identifier, only used when annotation identifier is <code>-1</code>
   * 
   * @return The {@link FormBlockPanel} if found, <code>null</code> if not
   */
  @Override
  public FormBlockPanel getActionFromAnnotationID(final int iAnnotationID, final String sActionId)
  {
  	if ((null == _aActions) || _aActions.isEmpty())
  		return null ;
  	
  	if (iAnnotationID > 0)
  		return getSpecificActionFromAnnotationID(iAnnotationID) ;
  	
  	// If there, it means that iAnnotationID = -1
  	//
  	for (FormBlockPanel formBlockPanel : _aActions)
  	{
  		if (-1 == formBlockPanel.getFormIdentifier())
  		{
  			if ((false == sActionId.isEmpty()) && sActionId.equals(formBlockPanel.getActionIdentifier()))
  				return formBlockPanel ;
  		}
  	}
  	
  	return null ;
  }

  /**
   * Get the {@link FormBlockPanel} for a really specified (not -1) annotation identifier
   * 
   * @param iAnnotationID Annotation identifier to look for
   * 
   * @return The {@link FormBlockPanel} if found, <code>null</code> if not
   */
  protected FormBlockPanel getSpecificActionFromAnnotationID(final int iAnnotationID)
  {
  	if ((null == _aActions) || _aActions.isEmpty())
  		return null ;
  	
  	String sIdAsString = "" + iAnnotationID ;
  	
  	for (FormBlockPanel formBlockPanel : _aActions)
  	{
  		// The edited block is instantiated, get its form identifier here
  		//
  		if (null != formBlockPanel.getEditedBlock())
  		{
  			if (formBlockPanel.getFormIdentifier() == iAnnotationID)
  				return formBlockPanel ;
  		}
  		// If not instantiated, get form identifier from id
  		//
  		else
  		{
  			String sPanelId = getIdentifierFromPattern(formBlockPanel, "annotation-id") ;
  			if (sIdAsString.equals(sPanelId))
  				return formBlockPanel ;
  		}
  	}
  	
  	return null ;
  }
  
  /** 
	 * When a widget "id" attribute is in the form "pattern" + identifier ; we must parse it to return the
	 * identifier as an int
	 * 
	 * @param widget   The interface element which identifier is to be returned
	 * @param sPattern The sequence of characters, in the "id" attribute, that precedes the identifier
	 *  
	 * @return The ID attached to this element if found; <code>-1</code> if not  
	 */
  protected int getAnnotationId(Widget widget, final String sPattern)
  {
  	if (null == widget)
  		return -1 ;
  	
  	String sID = getIdentifierFromPattern(widget, sPattern) ;
  	if ((null == sID) || sID.isEmpty())
  		return -1 ;
  	
  	try
  	{
  		return Integer.valueOf(sID) ;
  	}
  	catch (NumberFormatException e)
  	{
  		return -1 ;
  	}
  }
  
  public void initializeActionControls()
  {
  	// Global actions and annotations panel
  	//
  	_actionsCommandPannel = new FlowPanel() ;
  	_actionsCommandPannel.addStyleName("annotationsCommandPanel") ;
  	
  	Label annotationsCaption = new Label(constants.annotations()) ;
  	annotationsCaption.addStyleName("annotationsCommandPanelCaption") ;
  	_actionsCommandPannel.add(annotationsCaption) ;
  	
  	// Button panel
  	//
		_actionsButtonsPannel = new FlowPanel() ;
		_actionsButtonsPannel.addStyleName("annotationsButtonsPanel") ;
		
		Label actionsCaption = new Label(constants.newAnnotations()) ;
		actionsCaption.addStyleName("annotationsButtonPanelCaption") ;
		_actionsButtonsPannel.add(actionsCaption) ;
		
		_actionsCommandPannel.add(_actionsButtonsPannel) ;
  	
		// Existing annotations panel
		//
		_actionsHistoryPannel = new FlowPanel() ;
		_actionsHistoryPannel.addStyleName("annotationsHistoryPanel") ;
		_actionsCommandPannel.add(_actionsHistoryPannel) ;
		
		_globalPanel.add(_actionsCommandPannel) ;
  }
  
  public void initializeActionHistory()
  {
  	_actionsHistoryPannel.clear() ;
  }
  
  @Override
  public void addNewActionButton(final String sCaption, ClickHandler handler, final String sActionId)
  {
  	if ((null == sCaption) || sCaption.isEmpty() || (null == sActionId) || sActionId.isEmpty())
  		return ;
  	
  	Button newAnnotationButton = new Button(sCaption, handler) ;
  	newAnnotationButton.addStyleName("button white") ;
  	newAnnotationButton.getElement().setAttribute("id", "action-id" + sActionId) ;
  	_actionsButtonsPannel.add(newAnnotationButton) ;
  	
  	_aNewActionsButtons.add(newAnnotationButton) ;
  }
  
  /** 
	 * Find the button that correspond to this widget and return its ID
	 * 
	 * @param  sender the Widget to be resolved as a Button
	 *  
	 * @return The archetype ID attached to this button if found; -1 if not  
	 */
	@Override
	public String getNewAnnotationID(Widget sender) 
	{
		if ((null == sender) || _aNewActionsButtons.isEmpty())
			return "" ;
		
		for (Button button : _aNewActionsButtons)
			if (button == sender)
				return getIdForNewAnnotationButton(button) ;
		
		return "" ;
	}
	
	/**
	 * Get widget's action type
	 */
	public String getActionButtonType(Widget actionButton)
	{
		if (null == actionButton)
			return "" ;
		
		String sButtonId = actionButton.getElement().getAttribute("id") ;
		if (sButtonId.isEmpty())
			return "" ;
		
		if (sButtonId.startsWith("action_save"))
			return "action_save" ;
		if (sButtonId.startsWith("action_draft"))
			return "action_draft" ;
		if (sButtonId.startsWith("action_cancel"))
			return "action_cancel" ;
		
		if (sButtonId.startsWith("action_edit"))
			return "action_edit" ;
		if (sButtonId.startsWith("action_delete"))
			return "action_delete" ;
		
		return "" ;
	}
	
	/**
	 * Get the form identifier an action buttons applies to
	 * 
	 * @param actionButton The Widget to get information from
	 * 
	 * @return <code>-2</code> if failed, the form identifier if successful
	 * 
	 */
	@Override
	public int getActionButtonFormID(Widget actionButton)
	{
		if (null == actionButton)
			return -2 ;
		
		String sButtonId = actionButton.getElement().getAttribute("id") ;
		if (sButtonId.isEmpty())
			return -2 ;
		
		// Action buttons' id is in the form "action-type-id" + form identifier (example: action_edit-id225)
		//
		// We get the action type to find what follows
		//
		String sActionType = getActionButtonType(actionButton) ;
		if (sActionType.isEmpty())
			return -2 ;
		
		int iActionTypeLen = sActionType.length() ;
		String sNext = sButtonId.substring(iActionTypeLen) ;
		
		// We get what is after "-id" and return it as an int
		//
		if ((false == sNext.startsWith("-id")) || (sNext.length() < 4))
			return -2 ;
		
		try {
			return Integer.parseInt(sNext.substring(3)) ;
		} catch (NumberFormatException e) {
			return -2 ;
		}
	}
	
	/**
	 * Get the action identifier an action buttons (usually for a new annotation) applies to
	 * 
	 * @param actionButton The Widget to get information from
	 * 
	 * @return <code>""</code> if failed, the action identifier if successful
	 * 
	 */
	@Override
	public String getActionButtonActionID(Widget actionButton)
	{
		if (null == actionButton)
			return "" ;
		
		String sButtonId = actionButton.getElement().getAttribute("id") ;
		if (sButtonId.isEmpty())
			return "" ;
		
		// Action buttons' id is in the form "action-type-action" + form identifier (example: action_edit-action1B)
		//
		// We get the action type to find what follows
		//
		String sActionType = getActionButtonType(actionButton) ;
		if (sActionType.isEmpty())
			return "" ;
		
		int iActionTypeLen = sActionType.length() ;
		String sNext = sButtonId.substring(iActionTypeLen) ;
		
		// We get what is after "-action"
		//
		if ((false == sNext.startsWith("-action")) || (sNext.length() < 4))
			return "" ;
		
		return sNext.substring(7) ;
	}
	
	/** 
	 * The button Id is in the form "action-id" + iArchetypeId; we must parse it to return the 
	 * archetype ID as an int
	 * 
	 * @param  newFormButton the Button which ID is to be returned as an int
	 *  
	 * @return The archetype ID attached to this button if found; -1 if not  
	 */
	public String getIdForNewAnnotationButton(Button newFormButton) {
		return getIdentifierFromPattern(newFormButton, "action-id") ;
	}
	
	/** 
	 * When a widget "id" attribute is in the form "pattern" + identifier ; we must parse it to return the
	 * identifier
	 * 
	 * @param widget   The interface element which identifier is to be returned
	 * @param sPattern The sequence of characters, in the "id" attribute, that precedes the identifier
	 *  
	 * @return The ID attached to this element if found; <code>""</code> if not  
	 */
	public static String getIdentifierFromPattern(Widget widget, String sPattern) 
	{
		if ((null == widget) || (null == sPattern) || "".equals(sPattern))
			return "" ;
		
		String sButtonId = widget.getElement().getAttribute("id") ;
		
		int iPatternLen  = sPattern.length() ;
		int iButtonIdLen = sButtonId.length() ;
		
		if (iButtonIdLen <= iPatternLen)
			return "" ;
		
		if (false == sPattern.equals(sButtonId.substring(0, iPatternLen)))
			return "" ;
		
		return sButtonId.substring(iPatternLen, iButtonIdLen) ;
	}
  
  @Override
  public FormBlockPanel getMasterForm() {
  	return _formPanel ;
  }
}
