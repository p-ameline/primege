package com.primege.server.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.primege.server.DbParameters;
import com.primege.server.Logger;

public class PrimegeServletConfig extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() 
	{
		Injector primegeInjector = Guice.createInjector(new ServerModule(), new DispatchServletModule()) ;
		
		DbParameters dbParameters = primegeInjector.getInstance(DbParameters.class) ;
		
		Logger.trace("Listener started for primege", -1, Logger.TraceLevel.STEP, dbParameters.getTrace()) ;
		
		return primegeInjector ;
	}
}