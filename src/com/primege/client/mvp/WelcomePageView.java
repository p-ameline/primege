package com.primege.client.mvp;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class WelcomePageView extends Composite implements WelcomePagePresenter.Display 
{
	// private final LoginViewConstants constants = GWT.create(LoginViewConstants.class) ;
	
	private final FlowPanel _workspace;

	public WelcomePageView() 
	{
		// Main table
		//
		// FlexTable mainTable = new FlexTable() ;
		
		// Workspace
		//
		_workspace = new FlowPanel() ;
		_workspace.addStyleName("welcomeWorkspace") ;
		
		HTML logo = new HTML("<img src=\"logo_Carlsberg.png\">") ;
		logo.addStyleName("logo") ;
		_workspace.add(logo) ;
		
		initWidget(_workspace) ;
	}

	@Override
	public FlowPanel getWorkspace() {
		return _workspace ;
	}

	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
