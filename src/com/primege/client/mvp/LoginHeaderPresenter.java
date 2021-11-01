package com.primege.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.primege.client.event.GoToLoginResponseEvent;
import com.primege.client.event.LoginPageEvent;
import com.primege.client.event.LoginPageEventHandler;
import com.primege.client.event.LoginSentEvent;
import com.primege.client.event.LoginSentEventHandler;
import com.primege.client.global.PrimegeSupervisor;
import com.primege.shared.database.UserData;
import com.primege.shared.rpc.LoginUserInfo;
import com.primege.shared.rpc.LoginUserResult;

public class LoginHeaderPresenter extends WidgetPresenter<LoginHeaderPresenter.Display> {
	
	public interface Display extends WidgetDisplay 
	{	
		public String    getUser() ;
		public String    getPassWord() ;
		public FlexTable getLoginTable() ;
		public Button    getSendLogin() ;
		public DialogBox getErrDialogBox() ;
		public Button    getErrDialogBoxOkButton() ;
		public Button    getErrDialogBoxSendIdsButton() ;
		public void      showWaitCursor() ;
		public void      showDefaultCursor() ;
	}

	private final DispatchAsync     _dispatcher ;
	private final PrimegeSupervisor _supervisor ;
	
	@Inject
	public LoginHeaderPresenter(final Display           display, 
						                  final EventBus          eventBus,
						                  final DispatchAsync     dispatcher,
						                  final PrimegeSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_dispatcher = dispatcher ;
		_supervisor = supervisor ;
		
		bind() ;
	}
	
	@Override
	protected void onBind() 
	{
		Log.info("Entering LoginHeaderPresenter::onBind()") ;
		
		eventBus.addHandler(LoginPageEvent.TYPE, new LoginPageEventHandler() {
	      public void onLogin(LoginPageEvent event) 
	      {
					Log.info("Creating Login Label");
				  event.getHeader().clear() ;
				  FlowPanel Header = event.getHeader() ;
				  Header.add(display.getLoginTable()) ;
					Log.info("Creating Login Label success");
				}
		});
		
		display.getSendLogin().addClickHandler(new ClickHandler(){
				public void onClick(final ClickEvent event)
				{
					display.showWaitCursor() ;
					eventBus.fireEvent(new LoginSentEvent(display.getUser(),display.getPassWord())) ;
				}
		});
		
		display.getErrDialogBoxOkButton().addClickHandler(new ClickHandler(){
				public void onClick(final ClickEvent event)
				{
					display.getErrDialogBox().hide() ;
				}
		});
	 
		eventBus.addHandler(LoginSentEvent.TYPE, new LoginSentEventHandler() {
		 		public void onSendLogin(LoginSentEvent event) 
		 		{
					Log.info("Sending User and PassWord") ;
				  doSendLogin(event.getName(), event.getPassword()) ;
				}
		});
	}
	
	public void doSendLogin(String UserName, String PassWord) 
	{
		Log.info("Calling doLogin") ;		
		Log.debug("doSend(): LoginPresenter") ;
		System.out.println("before dispatcher.execute()") ;

		_dispatcher.execute(new LoginUserInfo(UserName, PassWord), new LoginUserCallback()) ;
		
		//Window.alert("connect!");
		Log.info("first!") ;
		System.out.println("after dispatcher.execute()") ;
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
	
	public class LoginUserCallback implements AsyncCallback<LoginUserResult>
	{
		public LoginUserCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			Log.info("error!!");
			display.showDefaultCursor() ;
		}

		@Override
		public void onSuccess(LoginUserResult value) 
		{
			display.showDefaultCursor() ;
			
			// No user found, display an error message
			//
			UserData userData = value.getUserData() ;
			if ((null == userData) || userData.isEmpty())
			{
				display.getErrDialogBox().show() ;
				return ;
			}
			
			Log.info("Congratulation!") ;	

			_supervisor.setUser(value.getUser()) ;
			
			Log.info("Successfully logged! 1") ;	
      display.getLoginTable().setVisible(false) ;
			eventBus.fireEvent(new GoToLoginResponseEvent()) ;
			Log.info("Successfully logged! 2") ;
		}
	}
	
	protected void onPlaceRequest(final PlaceRequest request) {
		// this is a popup
	}

	@Override
	protected void onRevealDisplay()
	{
		// TODO Auto-generated method stub
		
	}	
}
