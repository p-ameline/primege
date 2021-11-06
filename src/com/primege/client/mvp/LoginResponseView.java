package com.primege.client.mvp;

//import com.google.gwt.core.client.GWT;
import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.primege.client.global.PrimegeSupervisor;
import com.primege.client.loc.PrimegeViewConstants;
import com.primege.client.widgets.EventCityControl;
import com.primege.client.widgets.EventDateControl;
import com.primege.shared.database.SiteData;
import com.primege.shared.model.User;

public class LoginResponseView extends PrimegeBaseDisplay implements LoginResponsePresenter.Display
{
	private final PrimegeViewConstants constants = GWT.create(PrimegeViewConstants.class) ;
	
	private FlowPanel _workspace ;
	private FlowPanel _newFormsButtonsPanel ;
	
	private ArrayList<Button>   _aNewFormsButtons      = new ArrayList<Button>() ;
	private ArrayList<Button>   _aOpenDashboardButtons = new ArrayList<Button>() ;
	
	private FlowPanel _sitesButtonsPanel ;
	private ArrayList<CheckBox> _aMgmtSiteButtons      = new ArrayList<CheckBox>() ;
	
	private Button           _EditFormButton ;
	private Button           _DeleteFormButton ;
	private Button           _EditUserInformationButton ;
	private Button           _BuidCSVButton ;
	
	private EventDateControl _MgtFromDate ;
	private EventDateControl _MgtToDate ;
	private EventCityControl _MgtCity ;

	private ListBox          _FormsListBox ;
	
	private Button           _FormsSearchButton ;
	
	private DialogBox      _WarnindDialogBox ;
	private Label          _WarnindDialogBoxLabel ;
	private Button         _WarningDialogBoxOkButton ;
	
	private DialogBox      _DeleteDialogBox ;
	private Label          _DeleteDialogBoxLabel ;
	private Button         _DeleteDialogBoxOkButton ;
	private Button         _DeleteDialogBoxCancelButton ;
	
	protected final PrimegeSupervisor _supervisor ;
	
	@Inject
	public LoginResponseView(final PrimegeSupervisor supervisor) 
	{
		//super();
		
		_supervisor = supervisor ;
		
		initWorkspace() ;
	}
					 
	public void initWorkspace() 
	{					
		_EditUserInformationButton = null ;
		
		_BuidCSVButton = new Button(constants.buildCsv()) ;
		_BuidCSVButton.getElement().setAttribute("id", "build_csv-id") ;
		_BuidCSVButton.addStyleName("button white buildCSVButton") ;
				
		_workspace = new FlowPanel() ;
		_workspace.addStyleName("mapworkspace") ;
		
		// Insert new forms buttons
		//
		_newFormsButtonsPanel = new FlowPanel() ;
		
		if (false == _aNewFormsButtons.isEmpty())
			for (Button button : _aNewFormsButtons)
				_newFormsButtonsPanel.add(button) ;
		
		_workspace.add(_newFormsButtonsPanel) ;
	
		installFormsManagementControls() ;
		
		// _workspace.add(_EditUserInformationButton) ;
		_workspace.add(_BuidCSVButton) ;
		
		initWarningDialogBox() ;
		initDeleteDialogBox() ;
		
		initWidget(_workspace) ;
	}
	
	private void installFormsManagementControls()
	{
		FlowPanel formsMgtPannel = new FlowPanel() ;		
		formsMgtPannel.addStyleName("formsManagementPanel") ;
		
		// Date from and date to
		//
		_MgtFromDate = new EventDateControl(((User) _supervisor.getUser()).getEventData(), "") ;
		_MgtFromDate.addStyleName("formsManagementDate") ;
		_MgtToDate   = new EventDateControl(((User) _supervisor.getUser()).getEventData(), "") ;
		_MgtToDate.addStyleName("formsManagementDate") ;

		Label formsFromLabel = new Label(constants.formsFromDay()) ;
		formsFromLabel.addStyleName("formsManagementLabel") ;
		Label formsToLabel   = new Label(constants.formsToDay()) ;
		formsToLabel.addStyleName("formsManagementLabel") ;

		// Sites
		//
		Label sitesLabel = new Label(constants.formsSite()) ;
		sitesLabel.addStyleName("formsManagementLabel") ;
		
		_sitesButtonsPanel = new FlowPanel() ;
		
		// Cities
		//
		Label citiesLabel = new Label(constants.formsCity()) ;
		citiesLabel.addStyleName("formsManagementLabel") ;
		
		_MgtCity = new EventCityControl(((User) _supervisor.getUser()).getCities(), "") ; 
		
		// Controls table
		//
		FlexTable queryControls = new FlexTable() ;
		queryControls.setWidget(0, 0, formsFromLabel) ;
		queryControls.setWidget(0, 1, _MgtFromDate) ;
		queryControls.setWidget(0, 2, sitesLabel) ;
		queryControls.setWidget(0, 3, _sitesButtonsPanel) ;
		queryControls.setWidget(1, 0, formsToLabel) ;
		queryControls.setWidget(1, 1, _MgtToDate) ;
		queryControls.setWidget(1, 2, citiesLabel) ;
		queryControls.setWidget(1, 3, _MgtCity) ;
		
		formsMgtPannel.add(queryControls) ;
		
		_FormsSearchButton = new Button(constants.formsSearch()) ;
		_FormsSearchButton.getElement().setAttribute("id", "form_search-id") ;
		_FormsSearchButton.addStyleName("button red formsSearchButton") ;
		formsMgtPannel.add(_FormsSearchButton) ;
		
		_FormsListBox = new ListBox() ;
		_FormsListBox.setMultipleSelect(true) ;
		_FormsListBox.addStyleName("formsManagementList") ;
		_FormsListBox.setVisibleItemCount(10) ;
		
		formsMgtPannel.add(_FormsListBox) ;
		
		_EditFormButton = new Button(constants.formEdit()) ;
		_EditFormButton.getElement().setAttribute("id", "form_edit-id") ;
		_EditFormButton.addStyleName("button red formEditButton") ;
		formsMgtPannel.add(_EditFormButton) ;
		
		_DeleteFormButton = new Button(constants.formDelete()) ;
		_DeleteFormButton.getElement().setAttribute("id", "form_delete-id") ;
		_DeleteFormButton.addStyleName("button red formEditButton") ;
		formsMgtPannel.add(_DeleteFormButton) ;
		
		_workspace.add(formsMgtPannel) ; 
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
	 * initDeleteDialogBox - Initialize delete dialog box
	 * 
	 * @param    nothing
	 * @return   nothing  
	 */
	private void initDeleteDialogBox()
	{
		_DeleteDialogBox = new DialogBox() ;
		_DeleteDialogBox.setPopupPosition(100, 200) ;
		_DeleteDialogBox.setText(constants.warning()) ;
		_DeleteDialogBox.setAnimationEnabled(true) ;
		
		_DeleteDialogBoxLabel = new Label(constants.confirmDeleteForm()) ;
		_DeleteDialogBoxLabel.addStyleName("warningDialogLabel") ;
    
		_DeleteDialogBoxOkButton = new Button(constants.generalOk()) ;
		_DeleteDialogBoxOkButton.setSize("70px", "30px") ;
		_DeleteDialogBoxOkButton.getElement().setId("deleteokbutton") ;
		
		_DeleteDialogBoxCancelButton = new Button(constants.generalCancel()) ;
		_DeleteDialogBoxCancelButton.setSize("70px", "30px") ;
		_DeleteDialogBoxCancelButton.getElement().setId("deletecancelbutton") ;
		
		FlowPanel deletePannel = new FlowPanel() ;
		deletePannel.add(_DeleteDialogBoxLabel) ;
		deletePannel.add(_DeleteDialogBoxOkButton) ;
		deletePannel.add(_DeleteDialogBoxCancelButton) ;
		
		_DeleteDialogBox.add(deletePannel) ;
	}
	
	@Override
  public void setFormDates(Date tDate)
	{
		_MgtFromDate.initFromDate(tDate, true) ;
		_MgtToDate.initFromDate(tDate, true) ;
	}
	
	@Override
  public void setFormDateFrom(Date tDate)
	{
		_MgtFromDate.initFromDate(tDate, true) ;
	}
	
	@Override
  public void setFormDateTo(Date tDate)
	{
		_MgtToDate.initFromDate(tDate, true) ;
	}
	
	@Override
	public void addFormMgtButtonForSite(int iSiteId)
	{
		SiteData site = _supervisor.getSiteFromId(iSiteId) ;
		
		if (null == site)
			return ;
		
		CheckBox newSiteButton = new CheckBox(site.getLabel()) ;
		newSiteButton.getElement().setAttribute("id", "site-id" + iSiteId) ;
		
		// Checked by default
		//
		newSiteButton.setValue(true);
		
		_aMgmtSiteButtons.add(newSiteButton) ;
		
		_sitesButtonsPanel.add(newSiteButton) ;
	}
	
	/** 
	 * Get the form Id of the selected form in forms list
	 * 
	 * @return The selected form's Id if a form is selected, -1 if not  
	 */
	@Override
	public int getSelectedForm()
	{
		int iSelectedItem = _FormsListBox.getSelectedIndex() ;
		if (-1 == iSelectedItem)
			return -1 ;
		
		String sIndexValue = _FormsListBox.getValue(iSelectedItem) ;
		
		return Integer.parseInt(sIndexValue) ;
	}
							
	/** 
	 * popupWarningMessage - Display warning dialog box
	 * 
	 * @param    nothing
	 * @return   nothing  
	 */
	@Override
	public void popupWarningMessage(String sMessage)
	{
		if      (sMessage.equals("ERROR_MUST_SELECT_ENCOUNTER"))
			_WarnindDialogBoxLabel.setText(constants.warningAlreadyExist()) ;
		else if (sMessage.equals("ERROR_MUST_ENTER_EVERY_INFORMATION"))
			_WarnindDialogBoxLabel.setText(constants.mandatoryEnterAll()) ;
		
		_WarnindDialogBox.show() ;
	}
	
	@Override
	public void popupMessage(String sMessage)
	{
		_WarnindDialogBoxLabel.setText(sMessage) ;		
		_WarnindDialogBox.show() ;
	}
	
	@Override
	public void closeWarningDialog() {
		_WarnindDialogBox.hide() ;
	}
	
	@Override
	public void popupDeleteMessage() {
		_DeleteDialogBox.show() ;
	}
	
	@Override
	public void closeDeleteDialog() {
		_DeleteDialogBox.hide() ;
	}
	
	@Override
	public HasClickHandlers getDeleteOk() {
		return _DeleteDialogBoxOkButton ;
	}
	
	@Override
	public HasClickHandlers getDeleteCancel() {
		return _DeleteDialogBoxCancelButton ;
	}
	
	public void reset() {
	}

	public Widget asWidget() {
		return this;
	}
		
	public FlowPanel getWorkspace() {
		return _workspace ;
	}
	
	@Override
	public HasClickHandlers getEditUserData() {
		return _EditUserInformationButton ;
	}
	
	@Override
	public HasClickHandlers getBuildCsv() {
		return _BuidCSVButton ;
	}
		
	@Override
	public void hideBuildCsvButton() {
		_BuidCSVButton.setVisible(false) ;
	}
	
	@Override
	public HasClickHandlers getFormsSearchButton() {
		return _FormsSearchButton ;
	}
	
	@Override
	public HasClickHandlers getEditFormButton() {
		return _EditFormButton ;
	}
	
	@Override
	public HasClickHandlers getDeleteFormButton() {
		return _DeleteFormButton ;
	}
	
	@Override
	public void activateEditFormButton(boolean bActivate) {
		_EditFormButton.setEnabled(bActivate) ;
	}
	
	@Override
	public void activateDeleteFormButton(boolean bActivate) {
		_DeleteFormButton.setEnabled(bActivate) ;
	}
	
	@Override
	public String getFormDateFrom() {
		return _MgtFromDate.getContentAsString() ;
	}
	
	@Override
	public String getFormDateTo() {
		return _MgtToDate.getContentAsString() ;
	}
	
	@Override
	public void clearFormsList() {
		_FormsListBox.clear() ;
	}
	
	@Override
	public void addForm(String sFormName, int iMessageId) {
		_FormsListBox.addItem(sFormName, Integer.toString(iMessageId)) ;
	}

	@Override
	public void addNewFormButton(final String sCaption, ClickHandler handler, int iArchetypeId) 
	{
		Button newFormButton = new Button(sCaption, handler) ;
		newFormButton.getElement().setAttribute("id", "new_form-id" + iArchetypeId) ;
		newFormButton.addStyleName("button red newFormButton") ;
		
		_aNewFormsButtons.add(newFormButton) ;
		_newFormsButtonsPanel.add(newFormButton) ;
	}
	
	@Override
	public void addOpenDashboardButton(final String sCaption, ClickHandler handler, int iArchetypeId) 
	{
		Button newFormButton = new Button(sCaption, handler) ;
		newFormButton.getElement().setAttribute("id", "open_dash-id" + iArchetypeId) ;
		newFormButton.addStyleName("button orange newFormButton") ;
		
		_aOpenDashboardButtons.add(newFormButton) ;
		_newFormsButtonsPanel.add(newFormButton) ;
	}
	
	/** 
	 * Find the button that correspond to this widget and return its ID
	 * 
	 * @param  sender the Widget to be resolved as a Button
	 *  
	 * @return The archetype ID attached to this button if found; -1 if not  
	 */
	@Override
	public int getArchetypeIdForNewForm(Widget sender) 
	{
		if ((null == sender) || _aNewFormsButtons.isEmpty())
			return -1 ;
		
		for (Button button : _aNewFormsButtons)
			if (button == sender)
				return getArchetypeIdForNewFormButton(button) ;
		
		return -1 ;
	}
	
	/** 
	 * Find the button that correspond to this widget and return its ID
	 * 
	 * @param  sender the Widget to be resolved as a Button
	 *  
	 * @return The archetype ID attached to this button if found; -1 if not  
	 */
	@Override
	public int getArchetypeIdForOpenDashboard(Widget sender)
	{
		if (null == sender)
			return -1 ;
		
		for (Button button : _aOpenDashboardButtons)
			if (button == sender)
				return getArchetypeIdForOpenDashboardButton(button) ;

		return -1 ;
	}
	
	/** 
	 * The button Id is in the form "new_form-id" + iArchetypeId; we must parse it to return the 
	 * archetype ID as an int
	 * 
	 * @param  newFormButton the Button which ID is to be returned as an int
	 *  
	 * @return The archetype ID attached to this button if found; -1 if not  
	 */
	public int getArchetypeIdForNewFormButton(Button newFormButton) {
		return getIdFromPattern(newFormButton, "new_form-id") ;
	}
	
	/** 
	 * The button Id is in the form "open_dash-id" + iArchetypeId; we must parse it to return the 
	 * archetype ID as an int
	 * 
	 * @param  openDashboardButton the Button which ID is to be returned as an int
	 *  
	 * @return The archetype ID attached to this button if found; -1 if not  
	 */
	public int getArchetypeIdForOpenDashboardButton(Button openDashboardButton) {
		return getIdFromPattern(openDashboardButton, "open_dash-id") ;
	}
	
	/** 
	 * The button Id is in the form "new_form-id" + iArchetypeId; we must parse it to return the 
	 * archetype ID as an int
	 * 
	 * @param  newFormButton the Button which ID is to be returned as an int
	 *  
	 * @return The archetype ID attached to this button if found; -1 if not  
	 */
	public String getIdentifierFromPattern(Widget button, String sPattern) 
	{
		if ((null == button) || (null == sPattern) || "".equals(sPattern))
			return "" ;
		
		String sButtonId = button.getElement().getAttribute("id") ;
		
		int iPatternLen  = sPattern.length() ;
		int iButtonIdLen = sButtonId.length() ;
		
		if (iButtonIdLen <= iPatternLen)
			return "" ;
		
		if (false == sPattern.equals(sButtonId.substring(0, iPatternLen)))
			return "" ;
		
		return sButtonId.substring(iPatternLen, iButtonIdLen) ;
	}
	
	/** 
	 * The button Id is in the form "new_form-id" + iArchetypeId; we must parse it to return the 
	 * archetype ID as an int
	 * 
	 * @param  newFormButton the Button which ID is to be returned as an int
	 *  
	 * @return The archetype ID attached to this button if found; -1 if not  
	 */
	public int getIdFromPattern(Widget button, String sPattern) 
	{
		if ((null == button) || (null == sPattern) || "".equals(sPattern))
			return -1 ;
		
		String sArchetypeId = getIdentifierFromPattern(button, sPattern) ;
		
		if (sArchetypeId.isEmpty())
			return -1 ;
		
		try
		{
			return Integer.parseInt(sArchetypeId) ;
		} 
		catch (NumberFormatException e) 
		{
			return -1 ;
		}
	}
	
	@Override
	public void getSelectedSites(ArrayList<Integer> aSelectedSites)
	{
		if ((null == aSelectedSites) || _aMgmtSiteButtons.isEmpty())
			return ;
		
		for (CheckBox chkBox : _aMgmtSiteButtons)
		{
			if (chkBox.getValue())
			{
				int iId = getIdFromPattern(chkBox, "site-id") ;
				aSelectedSites.add(new Integer(iId)) ;
			}
		}
	}
	
	@Override
	public int getSelectedCity() {
		return _MgtCity.getSelectedCityId() ;
	}
	
	@Override
	public HasClickHandlers getWarningOk()
	{
		return _WarningDialogBoxOkButton ;
	}
}
