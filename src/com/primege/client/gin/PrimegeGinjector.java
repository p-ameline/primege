package com.primege.client.gin;

import net.customware.gwt.dispatch.client.gin.StandardDispatchModule;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.primege.client.global.PrimegeSupervisor;
import com.primege.client.mvp.AppPresenter;
import com.primege.client.mvp.DashboardPresenter;
import com.primege.client.mvp.FormPresenter;
import com.primege.client.mvp.LoginHeaderPresenter;
import com.primege.client.mvp.LoginResponsePresenter;
import com.primege.client.mvp.PostLoginHeaderPresenter;
import com.primege.client.mvp.WelcomePagePresenter;

@GinModules({ StandardDispatchModule.class, PrimegeClientModule.class })
public interface PrimegeGinjector extends Ginjector 
{
	PrimegeSupervisor        getPrimegeSupervisor() ;

	AppPresenter             getAppPresenter() ;

	LoginHeaderPresenter     getLoginPresenter() ;	
	LoginResponsePresenter   getLoginResponsePresenter() ;
	PostLoginHeaderPresenter getPostLoginHeaderPresenter() ;
	FormPresenter            getFormPresenter() ;
	DashboardPresenter       getDashboardPresenter() ;
	WelcomePagePresenter     getWelcomePagePresenter() ;	
}
