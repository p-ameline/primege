package com.primege.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface LoginSuccessEventHandler extends EventHandler 
{
	void onLoginSuccess(LoginSuccessEvent event);
}
