package com.primege.client.global;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.inject.Inject;
import com.primege.client.util.DataDictionaryProxy;
import com.primege.shared.model.UserModel;

public class PrimegeSupervisorModel 
{
	private   EventBus      _eventBus ;
	private   DispatchAsync _dispatcher ;
	protected UserModel     _user ;

	private   String        _sPreviousDate ;
	private   String        _sPreviousHour ;

	private   DataDictionaryProxy _dataDictionaryProxy ;

	@Inject
	public PrimegeSupervisorModel(final DispatchAsync dispatcher, final EventBus eventBus)
	{
		_dispatcher    = dispatcher ;
		_eventBus      = eventBus ;

		_dataDictionaryProxy = new DataDictionaryProxy(dispatcher) ;

		_sPreviousDate = "" ;
		_sPreviousHour = "" ;
	}

	public String getPreviousDate() {
		return _sPreviousDate;
	}
	public void setPreviousDate(String sPreviousDate) {
		_sPreviousDate = sPreviousDate;
	}

	public String getPreviousHour() {
		return _sPreviousHour;
	}
	public void setPreviousHour(String sPreviousHour) {
		_sPreviousHour = sPreviousHour;
	}

	public UserModel getUser() {
		return _user ;
	}
	public void setUser(UserModel otherUser) {
		_user.initFromUserModel(otherUser) ;
	}

	public String getUserPseudo() {
		if (null == _user.getUserData())
			return "" ;
		return _user.getUserData().getLabel() ;
	}

	public int getUserId() {
		if ((null == _user) || (null == _user.getUserData()))
			return -1 ;
		return _user.getUserData().getId() ;
	}

	public EventBus getEventBus() {
		return _eventBus ;
	}

	public DispatchAsync getDispatcher() { 
		return _dispatcher ;
	}

	public DataDictionaryProxy getDataDictionaryProxy() {
		return _dataDictionaryProxy ;
	}
}
