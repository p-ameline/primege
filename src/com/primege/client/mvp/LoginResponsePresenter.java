package com.primege.client.mvp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.primege.client.event.GoToEditFormEvent;
import com.primege.client.event.GoToNewFormEvent;
import com.primege.client.event.GoToOpenDashboardEvent;
import com.primege.client.event.GoToUserParamsEvent;
import com.primege.client.event.HeaderButtonsEvent;
import com.primege.client.event.LoginSuccessEvent;
import com.primege.client.event.LoginSuccessEventHandler;
import com.primege.client.event.PostLoginHeaderDisplayEvent;
import com.primege.client.global.PrimegeSupervisor;
import com.primege.shared.database.ArchetypeData;
import com.primege.shared.database.CityData;
import com.primege.shared.database.FormData;
import com.primege.shared.database.FormDataModel;
import com.primege.shared.database.SiteData;
import com.primege.shared.database.UserRoleData;
import com.primege.shared.model.User;
import com.primege.shared.rpc.DeleteFormAction;
import com.primege.shared.rpc.DeleteFormResult;
import com.primege.shared.rpc.GetCsvAction;
import com.primege.shared.rpc.GetCsvResult;
import com.primege.shared.rpc.GetFormsAction;
import com.primege.shared.rpc.GetFormsResult;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

public class LoginResponsePresenter extends WidgetPresenter<LoginResponsePresenter.Display>
{	
	private final DispatchAsync     _dispatcher ;
	private final PrimegeSupervisor _supervisor ;
	
	private       ClickHandler      _newFormClickHandler ;
	private       ClickHandler      _openDashboardClickHandler ;
	
	private       ArrayList<Integer>   _aAllowedSites = new ArrayList<Integer>() ;
		
	public interface Display extends PrimegeBaseInterface
	{
		FlowPanel        getWorkspace() ;
		HasClickHandlers getEditUserData() ;
		HasClickHandlers getFormsSearchButton() ;
		HasClickHandlers getEditFormButton() ;
		HasClickHandlers getDeleteFormButton() ;
		HasClickHandlers getBuildCsv() ;
		
		void             hideBuildCsvButton() ;
		void             activateEditFormButton(boolean bActivate) ;
		void             activateDeleteFormButton(boolean bActivate) ;
		
		void             addNewFormButton(String sCaption, ClickHandler handler, int iArchetypeId) ;
		void             addOpenDashboardButton(String sCaption, ClickHandler handler, int iArchetypeId) ;
		
		int              getArchetypeIdForNewForm(Widget sender) ;
		int              getArchetypeIdForOpenDashboard(Widget sender) ;
		
		void             clearFormsList() ;
		void             addForm(String sFormLabel, int iFormId) ;
		
		void             setFormDates(Date tDate) ;
		void             setFormDateFrom(Date tDate) ;
		void             setFormDateTo(Date tDate) ;
		String           getFormDateFrom() ;
		String           getFormDateTo() ;
	
		void             addFormMgtButtonForSite(int iSiteId) ;
		void             getSelectedSites(ArrayList<Integer> aSelectedSites) ;
		
		int              getSelectedCity() ;
		
		int              getSelectedForm() ;
		
		void             popupWarningMessage(String sMessage) ;
		void             popupMessage(String sMessage) ;
		void             closeWarningDialog() ;
		HasClickHandlers getWarningOk() ;
		
		void             popupDeleteMessage() ;
		void             closeDeleteDialog() ;
		HasClickHandlers getDeleteOk() ;
		HasClickHandlers getDeleteCancel() ;
	}
	
	private ArrayList<FormData> _aForms = new ArrayList<FormData>() ;
	private int                 _iFormToDelete ;
	
	@Inject
	public LoginResponsePresenter(final Display display, 
                                final EventBus eventBus,
                                final DispatchAsync dispatcher,
                                final PrimegeSupervisor supervisor)
	{
		super(display, eventBus) ;
      
		_dispatcher = dispatcher ;
		_supervisor = supervisor ;
		
		_iFormToDelete = -1 ;
		
		createArchetypesInterfaces() ;
		
		initAllowedSites() ;
		createFormManagementSitesButtons() ;
		
		// Deprecated (Don't call bind since it is already called by super EcogenBasePresenter)
		//
		bind() ;
		
		String sUserPseudo = _supervisor.getUserPseudo() ;
		if ((null == sUserPseudo) || (false == sUserPseudo.equals("Administrateur")))
		{
			display.hideBuildCsvButton() ;
		}
		
		eventBus.fireEvent(new HeaderButtonsEvent(false, true)) ;
		
		Date tNow = new Date() ;
		display.setFormDates(tNow) ;
	}
	
	@Override
	protected void onBind() 
	{		
		Log.info("Entering LoginResponsePresenter::onBind()") ;
		
		HasClickHandlers editDataHandler = display.getEditUserData() ; 
		if (null != editDataHandler)
		{
			display.getEditUserData().addClickHandler(new ClickHandler(){
				public void onClick(final ClickEvent event){
					Log.info("Tracing!!");
					eventBus.fireEvent(new GoToUserParamsEvent()) ;
				}
			});
		}
		
		display.getBuildCsv().addClickHandler(new ClickHandler(){
			public void onClick(final ClickEvent event){
			  Log.info("Building CSV");
			  buildCSV() ;
			}
		});
						
		eventBus.addHandler(LoginSuccessEvent.TYPE, new LoginSuccessEventHandler(){
			public void onLoginSuccess(final LoginSuccessEvent event) 
			{
				FlowPanel workSpace = event.getWorkspace() ;
				workSpace.clear() ;
				workSpace.add(getDisplay().asWidget()) ;
				
				eventBus.fireEvent(new HeaderButtonsEvent(false, true)) ;
				eventBus.fireEvent(new PostLoginHeaderDisplayEvent("")) ;
				
				refreshFormsList() ;
			}
		});
		
		display.getFormsSearchButton().addClickHandler(new ClickHandler(){
			public void onClick(final ClickEvent event){
			  Log.info("Refreshing the forms list");
			  refreshFormsList() ;
			}
		});
		
		display.getEditFormButton().addClickHandler(new ClickHandler(){
			public void onClick(final ClickEvent event){
			  Log.info("Editing form");
			  editForm() ;
			}
		});
		
		display.getDeleteFormButton().addClickHandler(new ClickHandler(){
			public void onClick(final ClickEvent event){
			  Log.info("Deleting form") ;
			  if (-1 == _iFormToDelete)
			  { 
			  	int iSelectedPatientMessage = display.getSelectedForm() ;
					if (-1 == iSelectedPatientMessage)
					{
						display.popupWarningMessage("ERROR_MUST_SELECT_FORM") ;
						return ;
					}
					_iFormToDelete = iSelectedPatientMessage ;
					display.popupDeleteMessage() ; 
			  }
			}
		});
		
		/**
		 * Reacts to Ok button in warning dialog box
		 * */
		display.getWarningOk().addClickHandler(new ClickHandler(){
			public void onClick(final ClickEvent event)
			{
			  display.closeWarningDialog() ; 
			}
		});
		
		/**
		 * Reacts to Ok button in delete dialog box
		 * */
		display.getDeleteOk().addClickHandler(new ClickHandler(){
			public void onClick(final ClickEvent event)
			{
			  display.closeDeleteDialog() ; 
			  if (-1 != _iFormToDelete)
			  	deleteForm(_iFormToDelete) ;
			}
		});
		
		/**
		 * Reacts to Cancel button in delete dialog box
		 * */
		display.getDeleteCancel().addClickHandler(new ClickHandler(){
			public void onClick(final ClickEvent event)
			{
			  display.closeDeleteDialog() ; 
			  _iFormToDelete = -1 ;
			}
		});
	}
	
	private void refreshFormsList()
	{
		int iUserId = _supervisor.getUserId() ;
		
		GetFormsAction formAction = new GetFormsAction() ;
		formAction.setUserId(iUserId) ;
		formAction.setEventId(_supervisor.getEventId()) ;
		
		// Get forms for dates
		//
		formAction.setEventDateFrom(display.getFormDateFrom()) ;
		formAction.setEventDateTo(display.getFormDateTo()) ;
		
		// Limited to site this user is allowed to edit
		//
/*
		User user = _supervisor.getUser() ;
		if (null != user)
		{
			ArrayList<UserRoleData> roles = user.getRoles() ;
			if ((null != roles) && (false == roles.isEmpty()))
				for (Iterator<UserRoleData> it = roles.iterator() ; it.hasNext() ; )
				{
					UserRoleData roleData = it.next() ;
					if (roleData.getSiteId() != -1)
						formAction.addSiteId(roleData.getSiteId()) ;
				}
		}
*/
		
		ArrayList<Integer> aSelectedSites = new ArrayList<Integer>() ;
		display.getSelectedSites(aSelectedSites) ;
		if (false == aSelectedSites.isEmpty())
			for (Integer siteId : aSelectedSites)
				formAction.addSiteId(siteId) ;
		
		int iSelectedCity = display.getSelectedCity() ;
		if (-1 != iSelectedCity)
			formAction.addCityId(iSelectedCity) ;
		
		_dispatcher.execute(formAction, new refreshFormsListCallback()) ;
	}
	
	/**
	 * Create the "new form" and "open dashboard" buttons and their click handlers
	 *
	 * */
	private void createArchetypesInterfaces()
	{
		// Get user and her list of archetypes descriptions
		//
		User currentUser = (User) _supervisor.getUser() ;
		
		if (null == currentUser)
			return ;
		
		ArrayList<ArchetypeData> aArchetypes = currentUser.getArchetypes() ;
		
		if ((null == aArchetypes) || aArchetypes.isEmpty())
			return ;
		
		// Create click handlers
		//
		_newFormClickHandler = new ClickHandler()
		{
			public void onClick(final ClickEvent event) 
			{
			  Widget sender = (Widget) event.getSource() ;
			  int iArchetypeId = display.getArchetypeIdForNewForm(sender) ;
			  
			  eventBus.fireEvent(new GoToNewFormEvent(iArchetypeId, null)) ;
			}
		} ;
		
		_openDashboardClickHandler = new ClickHandler()
		{
			public void onClick(final ClickEvent event) 
			{
			  Widget sender = (Widget) event.getSource() ;
			  int iArchetypeId = display.getArchetypeIdForOpenDashboard(sender) ;
			  
			  eventBus.fireEvent(new GoToOpenDashboardEvent(iArchetypeId)) ;
			}
		} ;
		
		// Create buttons
		//
		for (ArchetypeData archetype : aArchetypes)
		{
			if      (archetype.isForm())
				display.addNewFormButton(archetype.getLabel(), _newFormClickHandler, archetype.getId()) ;
			else if (archetype.isDashboard())
				display.addOpenDashboardButton(archetype.getLabel(), _openDashboardClickHandler, archetype.getId()) ;
		}
	}
	
	/**
	 * Create the "new form" and "open dashboard" buttons and their click handlers
	 *
	 * */
	private void createFormManagementSitesButtons()
	{
		if (_aAllowedSites.isEmpty())
			return ;
		
		for (Integer site : _aAllowedSites)
			display.addFormMgtButtonForSite(site.intValue()) ;
	}
	
	/**
	 * Fill the array of allowed sites from the array of roles
	 *
	 * */
	private void initAllowedSites()
	{
		_aAllowedSites.clear() ;
		
		User user = (User) _supervisor.getUser() ;
		if (null == user)
			return ;
			
		ArrayList<UserRoleData> roles = user.getRoles() ;
		if ((null == roles) || roles.isEmpty())
			return ;
			
		for (UserRoleData roleData : roles)
		{
			if (roleData.getSiteId() != -1)
			{
				int iSite4role = roleData.getSiteId() ;
				
				boolean bRoleAlreadyThere = false ;
				if (false == _aAllowedSites.isEmpty())
					for (Iterator<Integer> itSite = _aAllowedSites.iterator() ; (itSite.hasNext()) && (false == bRoleAlreadyThere) ; )
						if (itSite.next().intValue() == iSite4role)
							bRoleAlreadyThere = true ;
				
				if (false == bRoleAlreadyThere)
					_aAllowedSites.add(roleData.getSiteId()) ;
			}
		}
	}
	
	protected class refreshFormsListCallback implements AsyncCallback<GetFormsResult> 
	{
		public refreshFormsListCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			
		}//end handleFailure

		@Override
		public void onSuccess(GetFormsResult value) 
		{
			display.clearFormsList() ;
			_aForms.clear() ;
			
			display.activateEditFormButton(false) ;
			display.activateDeleteFormButton(false) ;
			
			ArrayList<FormDataModel> aForms = value.getForms() ;
			if (aForms.isEmpty())
				return ;
			
			for (FormDataModel formData : aForms)
			{
				FormData form = (FormData) formData ;
				_aForms.add(new FormData(form)) ;
				display.addForm(getFormLabel(form), form.getFormId()) ;
			}
			
			display.activateEditFormButton(true) ;
			display.activateDeleteFormButton(true) ;
		}
	}
	
	private void editForm()
	{
		int iSelectedPatientMessage = display.getSelectedForm() ;
		if (-1 == iSelectedPatientMessage)
		{
			display.popupWarningMessage("ERROR_MUST_SELECT_FORM") ;
			return ;
		}
		
		eventBus.fireEvent(new GoToEditFormEvent(iSelectedPatientMessage)) ;
	}
	
	private void deleteForm(int iFormToDelete) 
	{
		_dispatcher.execute(new DeleteFormAction(_supervisor.getUserId(), iFormToDelete), new deleteFormCallback()) ;
	}
	
	protected class deleteFormCallback implements AsyncCallback<DeleteFormResult> 
	{
		public deleteFormCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause) ;
			
		}//end handleFailure

		@Override
		public void onSuccess(DeleteFormResult value) 
		{
			String sErrorMessage = value.getMessage() ;
			if ((null != sErrorMessage) && (false == sErrorMessage.equals("")))
				display.popupMessage(sErrorMessage) ;
			
			refreshFormsList() ;
			
			_iFormToDelete = -1 ;
		}
	}
	
	private void buildCSV()
	{
		_dispatcher.execute(new GetCsvAction(_supervisor.getUserId(), _supervisor.getEventId()), new buildCSVCallback()) ;
	}
	
	protected class buildCSVCallback implements AsyncCallback<GetCsvResult> 
	{
		public buildCSVCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			
		}//end handleFailure

		@Override
		public void onSuccess(GetCsvResult value) 
		{
			String sErrorMessage = value.getMessage() ;
			if (false == sErrorMessage.equals(""))
				display.popupMessage(sErrorMessage) ;
			else
				display.popupMessage("Fichier CSV disponible") ;
		}
	}
	
	protected String getFormLabel(FormData formData)
	{
		if (null == formData)
			return "" ;
		
		User user = (User) _supervisor.getUser() ;
		if (null == user)
			return "" ;
		
		String sLabel = "" ;
		
		// Get site label
		//
		int iSite = formData.getSiteId() ;
		
		ArrayList<SiteData> sites = user.getSites() ;
		if ((null != sites) && (false == sites.isEmpty()))
		{
			for (SiteData site : sites)
				if (site.getId() == iSite)
					sLabel = site.getLabel() ;
		}
		
		// Get city label
		//
		int iCity = formData.getCityId() ;
		
		ArrayList<CityData> cities = user.getCities() ;
		if ((null != cities) && (false == cities.isEmpty()))
		{
			for (CityData city : cities)
				if (city.getId() == iCity)
					sLabel += " - " + city.getLabel() ;
		}
		
		// Get date
		//
		String sDate = formData.getEventDate() ;
		if ((null != sDate) && (false == "".equals(sDate)))
		{
			sLabel += " - " + sDate.substring(6, 8) + "/" + sDate.substring(4, 6) + "/" + sDate.substring(0, 4) ;
		}
		
		return sLabel ;
	}
	
	@Override
	protected void onUnbind() {
		// Add unbind functionality here for more complex presenters
	}

	@Override
	public void revealDisplay() {
		// nothing to do, there is more useful in UI which may be buried
		// in a tab bar, tree, etc.
	}

	@Override
	protected void onRevealDisplay()
	{
		// TODO Auto-generated method stub
		
	}
}
