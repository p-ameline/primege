package com.primege.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EcogenService</code>.
 */
public interface PrimegeServiceAsync {
	void primegeServer(String input, AsyncCallback<String> callback);
}
