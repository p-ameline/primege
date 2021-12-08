package com.primege.client.mvp;

import java.util.ArrayList;
import java.util.Date;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.google.inject.Inject;

import com.primege.client.global.PrimegeSupervisor;
import com.primege.client.util.FormControlOptionData;
import com.primege.shared.database.CityData;
import com.primege.shared.database.EventData;
import com.primege.shared.database.FormData;
import com.primege.shared.database.FormDataData;
import com.primege.shared.database.FormDataModel;
import com.primege.shared.model.FormBlock;
import com.primege.shared.model.FormBlockModel;
import com.primege.shared.model.User;
import com.primege.shared.rpc.GetFormBlockAction;
import com.primege.shared.rpc.GetFormBlockResult;
import com.primege.shared.rpc.GetFormsAction;
import com.primege.shared.rpc.RegisterFormAction;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

public class FormPresenter extends FormPresenterModel<FormPresenter.Display>
{

	public interface Display extends FormInterfaceModel
	{
		void              setEvent(EventData event) ;
		void              setCities(ArrayList<CityData> cities) ;
		
		void              setEventDate(String sEventDate) ;
		void              initEventDate(Date tDate) ;
		void              setEventCity(int iCityId) ;
		
		HasChangeHandlers getCityChanged() ;
		HasChangeHandlers getSiteChanged() ;
	}
	
	@Inject
	public FormPresenter(final Display           display, 
							         final EventBus          eventBus,
							         final DispatchAsync     dispatcher,
							         final PrimegeSupervisor supervisor) 
	{
		super(display, eventBus, dispatcher, supervisor) ;
		
		Date tNow = new Date() ;
		display.initEventDate(tNow) ;
	}
	
	@Override
	protected void onBind() 
	{
		Log.debug("Entering FormPresenter::onBind()") ;
		super.onBind() ;
			
		/**
		 * submit user registration information
		 * */
		display.getSubmitButton().addClickHandler(new SubmitHandler()) ; 		
	}
	
	protected void resetAll()
	{
		resetAll4Model() ;
		
		display.resetAll() ;
		display.setEvent(((User) _supervisor.getUser()).getEventData()) ;
		display.setCities(((User) _supervisor.getUser()).getCities()) ;
		
		Date tNow = new Date() ;
		display.initEventDate(tNow) ;
		
		display.setDefaultValues() ;
	}
	
	protected class SubmitHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			if (_bSaveInProgress)
				return ;
			
			// If we know that a form already exist in database, we shouldn't save it twice
			//
			if (_bFormAlreadyExist)
			{
				display.popupWarningMessage("WARNING_FORM_ALREADY_EXIST") ;
				return ;
			}
			// if (_bCheckingFormExist)
			//	return ;
			
			ArrayList<FormDataData> aFormInformation = new ArrayList<FormDataData>() ;
			boolean bAllControlFilled = display.getContent(aFormInformation, null) ;
			
			// If a control is not filled or invalid, we refuse to save
			//
			if (false == bAllControlFilled)
			{
				display.popupWarningMessage("ERROR_MUST_ENTER_EVERY_INFORMATION") ;
				return ;
			}
			
			int    iCityId    = getIntValueForPath("$city$", null) ;
			int    iSiteId    = getIntValueForPath("$site$", null) ;
			String sEventDate = getValueForPath("$date$", null) ;
			
		// TODO Check if we can get the edited root (in case it is not a new form)
			String sRoot = "" ;

			FormData formData = new FormData(_iFormId, "", sRoot, ((PrimegeSupervisor) _supervisor).getEventId(), iCityId, iSiteId, sEventDate, _supervisor.getUserId(), _sRecordDate, _iArchetypeId, FormDataModel.FormStatus.valid) ;
						
			removeFromInformation(aFormInformation, "$city$") ;
			removeFromInformation(aFormInformation, "$site$") ;
			removeFromInformation(aFormInformation, "$date$") ;

			FormBlock<FormDataData> formBlock = new FormBlock<FormDataData>("", formData, aFormInformation) ;
			
			if (areDataOk(formBlock))
			{
				_bSaveInProgress = true ;
				display.showWaitCursor() ;
				_dispatcher.execute(new RegisterFormAction(_supervisor.getUserId(), formBlock, _aTraits), new RegisterFormCallback()) ;
			}
		}
	}
	
	protected void createChangeHandlers()
	{
		// Change handler to check if ongoing report doesn't already exist in database
		//
		_CheckExistChangeHandler = new ChangeHandler()
		{
			public void onChange(final ChangeEvent event) 
			{
				int    iCityId    = getIntValueForPath("$city$", null) ;
				int    iSiteId    = getIntValueForPath("$site$", null) ;
				String sEventDate = getValueForPath("$date$", null) ;
				
				//
				//
				if ((-1 == iCityId) || (-1 == iSiteId) || "".equals(sEventDate))
					return ;
				
				// In case we are editing a form that is already saved, we must first check
				// if one of the document label information has changed
				//
				if (null != getEditedBlock())
				{
					FormData documentLabel = (FormData) ((FormBlock<FormDataData>) getEditedBlock()).getDocumentLabel() ;
					if (null != documentLabel)
					{
						if ((documentLabel.getCityId() == iCityId) &&
								(documentLabel.getSiteId() == iSiteId) &&
								documentLabel.getEventDate().equals(sEventDate))
						{
							_bFormAlreadyExist = false ;
							return ;
						}
					}
				}
			
				GetFormsAction getFormsAction = new GetFormsAction() ;
				getFormsAction.setUserId(_supervisor.getUserId()) ;
				
				getFormsAction.setEventId(((PrimegeSupervisor) _supervisor).getEventId()) ;
				getFormsAction.setEventDateFrom(sEventDate) ;
				getFormsAction.setEventDateTo(sEventDate) ;
				getFormsAction.addCityId(iCityId) ;
				getFormsAction.addSiteId(iSiteId) ;
				
				_bCheckingFormExist = true ;
				
				_dispatcher.execute(getFormsAction, new CheckExistFormCallback()) ;
			}
		} ;
		
		HasChangeHandlers cityChngHandler = display.getCityChanged() ;
		if (null != cityChngHandler)
			cityChngHandler.addChangeHandler(_CheckExistChangeHandler) ;
		
		HasChangeHandlers siteChngHandler = display.getSiteChanged() ;
		if (null != siteChngHandler)
			siteChngHandler.addChangeHandler(_CheckExistChangeHandler) ;
		
		HasChangeHandlers dateChngHandler = display.getDateChanged() ;
		if (null != dateChngHandler)
			dateChngHandler.addChangeHandler(_CheckExistChangeHandler) ;
	}
	
	protected ArrayList<FormDataData> getEditedInformationForPath(final String sPath, final ArrayList<FormControlOptionData> aOptions, FormBlockModel<FormDataData> aInformation)
	{
		if ((null == sPath) || "".equals(sPath))
			return null ;
		
		// Artificial paths
		//
		if ("$city$".equals(sPath) || "$site$".equals(sPath) || "$date$".equals(sPath))
		{
			if (null == aInformation)
        return null ;
			
			FormData formData = (FormData) ((FormBlock<FormDataData>) aInformation).getDocumentLabel() ;
			if (null == formData)
				return null ;
			
			FormDataData fakeInformation = new FormDataData() ;
			if ("$city$".equals(sPath))
				fakeInformation.setValue(Integer.toString(formData.getCityId())) ;
			if ("$site$".equals(sPath))
				fakeInformation.setValue(Integer.toString(formData.getSiteId())) ;
			if ("$date$".equals(sPath))
				fakeInformation.setValue(formData.getEventDate()) ;

			ArrayList<FormDataData> aContent = new ArrayList<FormDataData>() ;
			aContent.add(fakeInformation) ;
			
			return aContent ;
		}
		
		return getEditedInformationForRegularPath(aInformation, sPath, aOptions) ;
	}
	
	public void initFromExistingInformation()
	{
		if (-1 == _iFormId)
			return ;
		
		_dispatcher.execute(new GetFormBlockAction(_supervisor.getUserId(), _iFormId), new editFormCallback()) ;
	}
	
	protected class editFormCallback implements AsyncCallback<GetFormBlockResult> 
	{
		public editFormCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			
		}//end handleFailure

		@Override
		public void onSuccess(GetFormBlockResult value) 
		{
			setEditedBlock(new FormBlock<FormDataData>(value.getFormBlock())) ;
			initFromBlock((FormBlock<FormDataData>) getEditedBlock()) ;
		}
	}
	
	protected void initControlsFromPreviousInformation()
	{	
	}
	
	protected void initFromBlock(FormBlock<FormDataData> block)
	{
		if (null == block)
			return ;
		
		_sRecordDate = block.getDocumentLabel().getEntryDateHour() ;
		
		FormData formData = (FormData) block.getDocumentLabel() ;
		if (null == formData)
			return ;
			
		_iArchetypeId = formData.getArchetypeId() ;
			
		getArchetype() ;
	}

	@Override
	protected void onRevealDisplay()
	{
		// TODO Auto-generated method stub
		
	}	
}
