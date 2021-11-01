package com.primege.client.gin;

import net.customware.gwt.presenter.client.DefaultEventBus;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.gin.AbstractPresenterModule;

import com.google.inject.Singleton;
import com.primege.client.CachingDispatchAsync;
import com.primege.client.global.PrimegeSupervisor;
import com.primege.client.mvp.AppPresenter;
import com.primege.client.mvp.DashboardPresenter;
import com.primege.client.mvp.DashboardView;
import com.primege.client.mvp.FormPresenter;
import com.primege.client.mvp.FormView;
import com.primege.client.mvp.LoginHeaderPresenter;
import com.primege.client.mvp.LoginHeaderView;
import com.primege.client.mvp.LoginResponsePresenter;
import com.primege.client.mvp.LoginResponseView;
import com.primege.client.mvp.MainPresenter;
import com.primege.client.mvp.MainView;
import com.primege.client.mvp.PostLoginHeaderPresenter;
import com.primege.client.mvp.PostLoginHeaderView;
import com.primege.client.mvp.WelcomePagePresenter;
import com.primege.client.mvp.WelcomePageView;

public class PrimegeClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() 
	{		
		bind(EventBus.class).to(DefaultEventBus.class).in(Singleton.class) ;
		
		bindPresenter(MainPresenter.class,            MainPresenter.Display.class,            MainView.class) ;		
		bindPresenter(WelcomePagePresenter.class,     WelcomePagePresenter.Display.class,     WelcomePageView.class) ;
		bindPresenter(LoginHeaderPresenter.class,     LoginHeaderPresenter.Display.class,     LoginHeaderView.class) ;
		bindPresenter(PostLoginHeaderPresenter.class, PostLoginHeaderPresenter.Display.class, PostLoginHeaderView.class) ;
		bindPresenter(LoginResponsePresenter.class,   LoginResponsePresenter.Display.class,   LoginResponseView.class) ;
		bindPresenter(FormPresenter.class,            FormPresenter.Display.class,            FormView.class) ;
		bindPresenter(DashboardPresenter.class,       DashboardPresenter.Display.class,       DashboardView.class) ;
		
		bind(AppPresenter.class).in(Singleton.class) ;
		bind(PrimegeSupervisor.class).in(Singleton.class) ;
		bind(CachingDispatchAsync.class) ;		
	}
}
