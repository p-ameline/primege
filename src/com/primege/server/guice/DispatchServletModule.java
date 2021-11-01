package com.primege.server.guice;

import net.customware.gwt.dispatch.server.guice.GuiceStandardDispatchServlet;
import com.google.inject.servlet.ServletModule;

public class DispatchServletModule extends ServletModule {

	@Override
	public void configureServlets() {
		// NOTE: the servlet context will probably need changing
		serve("/primege/dispatch").with(GuiceStandardDispatchServlet.class) ;
	}
}
