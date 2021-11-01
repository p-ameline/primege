package com.primege.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;

import com.primege.client.event.BackToWelcomePageEvent;
import com.primege.client.event.BackToWelcomePageEventHandler;
import com.primege.client.event.EditFormEvent;
import com.primege.client.event.GoToEditFormEvent;
import com.primege.client.event.GoToEditFormEventHandler;
import com.primege.client.event.GoToLoginResponseEvent;
import com.primege.client.event.GoToLoginResponseEventHandler;
import com.primege.client.event.GoToNewFormEvent;
import com.primege.client.event.GoToNewFormEventHandler;
import com.primege.client.event.GoToOpenDashboardEvent;
import com.primege.client.event.GoToOpenDashboardEventHandler;
import com.primege.client.event.LoginPageEvent;
import com.primege.client.event.LoginSuccessEvent;
import com.primege.client.event.OpenDashboardEvent;
import com.primege.client.event.PostLoginHeaderEvent;
import com.primege.client.event.WelcomePageEvent;
import com.primege.client.gin.PrimegeGinjector;
import com.primege.client.global.PrimegeSupervisor;
import com.primege.shared.database.FormLink;

public class MainPresenter extends WidgetPresenter<MainPresenter.Display> 
{
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
/*private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	*/
	public interface Display extends WidgetDisplay
	{
		public FlowPanel getWorkspace() ;
		public FlowPanel getHeader() ;
		public FlowPanel getFooter() ; 
		//public VerticalPanel getBody();
	}
	
	private final PrimegeSupervisor _supervisor ;
	
	private       boolean          _isWelcomePageCreated ;
	private       boolean          _isLoginCreated ;
	private       boolean          _isPostLoginPageCreated ;
	private       boolean          _isPostLoginHeaderCreated ;
	private       boolean          _isNewFormPageCreated ;
	private       boolean          _isOpenDashboardPageCreated ;

	private       ScheduledCommand _pendingEvents ;

	@Inject
	public MainPresenter(final Display          display, 
						           final EventBus         eventBus, 
						           final DispatchAsync    dispatcher,
						           final PrimegeSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_isLoginCreated             = false ;
		_isPostLoginPageCreated     = false ;
		_isPostLoginHeaderCreated   = false ;
		_isWelcomePageCreated       = false ;
		_isNewFormPageCreated       = false ;
		_isOpenDashboardPageCreated = false ;
		
		_supervisor = supervisor ;
		
		bind() ;
	}
	
	@Override
	protected void onBind() 
	{
		eventBus.addHandler(BackToWelcomePageEvent.TYPE, new BackToWelcomePageEventHandler() {
			@Override
			public void onBackToWelcome(BackToWelcomePageEvent event) 
			{
				Log.info("Back to welcome page") ;
				doLoad() ;	
			}
		});
		
		eventBus.addHandler(GoToLoginResponseEvent.TYPE, new GoToLoginResponseEventHandler() 
		{
			@Override
			public void onGoToLoginResponse(GoToLoginResponseEvent event) 
			{
				Log.info("Call to go to post login page") ;
				goToPostLoginHeader() ;
				goToPostLoginPage() ;	
			}
		});
		
		eventBus.addHandler(GoToOpenDashboardEvent.TYPE, new GoToOpenDashboardEventHandler() 
		{
			@Override
			public void onGoToOpenDashboard(GoToOpenDashboardEvent event) 
			{
				Log.info("Call to open a dashboard") ;
				goToOpenDashboard(event.getArchetypeId()) ;	
			}
		});
		
		eventBus.addHandler(GoToNewFormEvent.TYPE, new GoToNewFormEventHandler() 
		{
			@Override
			public void onGoToNewForm(GoToNewFormEvent event) 
			{
				Log.info("Call to go to new form") ;
				goToNewForm(event.getArchetypeId(), event.getFormLink()) ;	
			}
		});
		
		eventBus.addHandler(GoToEditFormEvent.TYPE, new GoToEditFormEventHandler() 
		{
			@Override
			public void onGoToEditForm(GoToEditFormEvent event) 
			{
				Log.info("Call to go to edit form") ;
				int iFormId = event.getFormId() ;
				goToEditForm(iFormId) ;	
			}
		});
				
		doLoad() ;	
		doLogin() ;
	}

	public void doLoad()
	{
		Log.info("Calling Load");
		if ((false == _isWelcomePageCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			PrimegeGinjector injector = _supervisor.getInjector() ;
			injector.getWelcomePagePresenter() ;//这种getXXpresenter就是用来生产这个presenter的实例的
			_isWelcomePageCreated = true ;
		}
		display.getWorkspace().clear() ;
		
		// If LoginSuccessEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(WelcomePageEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new WelcomePageEvent(display.getWorkspace())) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
		}
		else
			eventBus.fireEvent(new WelcomePageEvent(display.getWorkspace())) ;		
	}
	
	public void goToPostLoginPage()
	{
		Log.info("Going to post login page") ;
		display.getWorkspace().clear() ;
		if ((false == _isPostLoginPageCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			_isPostLoginPageCreated = true ;
			PrimegeGinjector injector = _supervisor.getInjector() ;
			injector.getLoginResponsePresenter() ;
		}

		// If LoginSuccessEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(LoginSuccessEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new LoginSuccessEvent(display.getWorkspace())) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
			else
			{
				// Create a new timer that calls goToPostLoginPage() again later.
		    Timer t = new Timer() {
		      public void run() {
		      	goToPostLoginPage() ;
		      }
		    };
		    // Schedule the timer to run once in 5 seconds.
		    t.schedule(1000);
			}
		}
		else
			eventBus.fireEvent(new LoginSuccessEvent(display.getWorkspace())) ;	
	}
		
	public void goToNewUserParamsPage(String sId)
	{
		if ((null == sId) || sId.equals(""))
			return ;
		
		// validateNewUser(sId) ;
	}
	
	public void goToPostLoginHeader()
	{
		Log.info("Switch to post-login header") ;
		display.getHeader().clear() ;
		if ((false == _isPostLoginHeaderCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			PrimegeGinjector injector = _supervisor.getInjector() ;
			injector.getPostLoginHeaderPresenter() ;
			_isPostLoginHeaderCreated = true ;
		}

		// If UserParamsSentEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(PostLoginHeaderEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new PostLoginHeaderEvent(display.getHeader())) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
			else
			{
				// Create a new timer that calls goToPostLoginHeader() again later.
		    Timer t = new Timer() {
		      public void run() {
		      	goToPostLoginHeader() ;
		      }
		    };
		    // Schedule the timer to run once in 5 seconds.
		    t.schedule(1000);
			}
		}
		else
			eventBus.fireEvent(new PostLoginHeaderEvent(display.getHeader())) ;	
	}

	/**
	 * New encounter
	 * */
	public void goToNewForm(final int iArchetypeId, final FormLink formLink)
	{
		Log.info("Going to new form page") ;
		
		display.getWorkspace().clear() ;
		
		if ((false == _isNewFormPageCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			PrimegeGinjector injector = _supervisor.getInjector() ;
			injector.getFormPresenter() ;
			_isNewFormPageCreated = true ;
		}

		// If UserParamsSentEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(EditFormEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new EditFormEvent(display.getWorkspace(), -1, iArchetypeId, formLink)) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
			else
			{
				// Create a new timer that calls goToNewEncounter() again later.
		    Timer t = new Timer() {
		      public void run() {
		      	goToNewForm(iArchetypeId, formLink) ;
		      }
		    };
		    // Schedule the timer to run once in 5 seconds.
		    t.schedule(1000);
			}
		}
		else
			eventBus.fireEvent(new EditFormEvent(display.getWorkspace(), -1, iArchetypeId, formLink)) ;	
	}
	
	/**
	 * Edit existing encounter
	 * 
	 * @param iPatientMessageId : Id of patient message to be modified
	 * 
	 * */
	public void goToEditForm(final int iFormId)
	{
		if (-1 == iFormId)
			return ;
		
		Log.info("Going to edit encounter page") ;
		
		display.getWorkspace().clear() ;
		
		if ((false == _isNewFormPageCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			PrimegeGinjector injector = _supervisor.getInjector() ;
			injector.getFormPresenter() ;
			_isNewFormPageCreated = true ;
		}

		// If UserParamsSentEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(EditFormEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new EditFormEvent(display.getWorkspace(), iFormId, -1, null)) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
		}
		else
			eventBus.fireEvent(new EditFormEvent(display.getWorkspace(), iFormId, -1, null)) ;
	}
	
	/**
	 * Open dashboard
	 * */
	public void goToOpenDashboard(final int iArchetypeId)
	{
		Log.info("Going to dashboard page") ;
		
		display.getWorkspace().clear() ;
		
		if ((false == _isOpenDashboardPageCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			PrimegeGinjector injector = _supervisor.getInjector() ;
			injector.getDashboardPresenter() ;
			_isOpenDashboardPageCreated = true ;
		}

		// If UserParamsSentEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(OpenDashboardEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new OpenDashboardEvent(display.getWorkspace(), iArchetypeId)) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
			else
			{
				// Create a new timer that calls goToNewEncounter() again later.
		    Timer t = new Timer() {
		      public void run() {
		      	goToOpenDashboard(iArchetypeId) ;
		      }
		    };
		    // Schedule the timer to run once in 5 seconds.
		    t.schedule(1000);
			}
		}
		else
			eventBus.fireEvent(new OpenDashboardEvent(display.getWorkspace(), iArchetypeId)) ;	
	}
		
	public void doLogin() 
	{
		Log.info("Calling doLogin") ;
		if ((false == _isLoginCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			PrimegeGinjector injector = _supervisor.getInjector() ;
			injector.getLoginPresenter() ;
			_isLoginCreated = true ;
		}
		
	// If UserParamsSentEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(LoginPageEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new LoginPageEvent(display.getHeader())) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
		}
		else
			eventBus.fireEvent(new LoginPageEvent(display.getHeader())) ;
				
		if (false == eventBus.isEventHandled(LoginPageEvent.TYPE))
			Log.info("Error in eventBus") ;
	}
		
	@Override
	protected void onUnbind() {
	}

	public void refreshDisplay() {	
	}

	public void revealDisplay() {
	}
		
	protected void onPlaceRequest(final PlaceRequest request) {	
	}

	@Override
	protected void onRevealDisplay()
	{
		// TODO Auto-generated method stub
	}
}
