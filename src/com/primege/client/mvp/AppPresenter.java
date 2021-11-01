package com.primege.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class AppPresenter 
{
	private HasWidgets    _container ;
	private MainPresenter _mainPresenter ;
	
	@Inject
	public AppPresenter(final DispatchAsync dispatcher, final MainPresenter mainPresenter) 
	{
		_mainPresenter = mainPresenter ;		
	}
	
	private void showMain() 
	{
		_container.clear() ;
		_container.add(_mainPresenter.getDisplay().asWidget()) ;
	}
		
	public void go(final HasWidgets container, String sStep, String sId) 
	{
		_container = container ;	
		
		showMain() ;
		
		// For testing only
		// sStep = "creation" ;
		// sId   = "reg0" ;
		// End For testing only
		
		if ((null != sStep) && (sStep.equals("creation")))
		{
			Log.info("Creation phase") ;
			_mainPresenter.goToNewUserParamsPage(sId) ;
		}
	}
}
