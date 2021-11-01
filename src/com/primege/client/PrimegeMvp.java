package com.primege.client;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import com.primege.client.gin.PrimegeGinjector;
import com.primege.client.global.PrimegeSupervisor;
import com.primege.client.mvp.AppPresenter;
import com.primege.client.ui.PrimegeResources;

public class PrimegeMvp implements EntryPoint 
{
	private final PrimegeGinjector injector = GWT.create(PrimegeGinjector.class);
	
	public void onModuleLoad() 
	{
		Log.info("onModuleLoad") ;
		
		PrimegeResources.INSTANCE.css().ensureInjected() ;
		
		final PrimegeSupervisor supervisor = injector.getPrimegeSupervisor() ;
		
		supervisor.setInjector(injector) ;
		final AppPresenter appPresenter = injector.getAppPresenter() ;
		
		String sId   = "" ;
	  String sStep = Window.Location.getParameter("step") ;
	  if (null != sStep)
	  {
	  	Log.info("step parameter detected") ;
	  	if (sStep.equals("creation"))
	  	{
	  		Log.info("switching to creation mode") ;
	  		sId = Window.Location.getParameter("pid") ;
	  	}
	  }
	  else
	  	Log.info("step parameter not detected") ;

		appPresenter.go(RootPanel.get(), sStep, sId) ;
	}
}
