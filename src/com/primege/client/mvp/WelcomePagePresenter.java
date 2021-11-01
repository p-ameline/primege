package com.primege.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.primege.client.event.WelcomePageEvent;
import com.primege.client.event.WelcomePageEventHandler;
import com.primege.client.global.PrimegeSupervisor;

public class WelcomePagePresenter extends WidgetPresenter<WelcomePagePresenter.Display> 
{	
	public interface Display extends WidgetDisplay 
	{			
		public FlowPanel  getWorkspace() ;
	}

	@Inject
	public WelcomePagePresenter(final Display display, 
			                        final EventBus eventBus,
			                        final DispatchAsync dispatcher,
			                        final PrimegeSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		bind() ;
	}
	
	/**
	 * Try to send the greeting message
	 */
	@Override
	protected void onBind() 
	{
		Log.debug("Entering WelcomePagePresenter::onBind()") ;
		
		eventBus.addHandler(WelcomePageEvent.TYPE, new WelcomePageEventHandler() {
			public void onWelcome(WelcomePageEvent event)
			{
				Log.debug("Loading Welcome Page") ;
				// RootPanel.get().clear();
				// RootPanel.get().add(display.asWidget());
				event.getWorkspace().clear() ;
				FlowPanel WelcomeWorkspace = event.getWorkspace() ;
				WelcomeWorkspace.add(display.asWidget()) ;
			}
		});
	}
		
	@Override
	protected void onUnbind() {
		// Add unbind functionality here for more complex presenters.
	}

	public void refreshDisplay() {
		// This is called when the presenter should pull the latest data
		// from the server, etc. In this case.	
	}

	public void revealDisplay() {
		// Nothing to do. This is more useful in UI which may be buried
		// in a tab bar, tree, etc.
	}

	@Override
	protected void onRevealDisplay()
	{
		// TODO Auto-generated method stub
		
	}
}
