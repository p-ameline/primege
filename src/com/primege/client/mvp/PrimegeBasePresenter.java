package com.primege.client.mvp;

import com.google.inject.Inject;

import com.primege.client.global.PrimegeSupervisorModel;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

public abstract class PrimegeBasePresenter<D extends PrimegeBaseInterface> extends WidgetPresenter<D> 
{
	private       final DispatchAsync          _dispatcher ;
	private       final PrimegeSupervisorModel _supervisor ;
	
	protected           String _sPredefinedCity ;
			
	@Inject
	public PrimegeBasePresenter(D                           display, 
							               EventBus                     eventBus,
							               final DispatchAsync          dispatcher,
							               final PrimegeSupervisorModel supervisor) 
	{
		super(display, eventBus) ;
		
		_dispatcher = dispatcher ;
		_supervisor = supervisor ;
		
		_sPredefinedCity = "" ;
			
		bind() ;		
	}
	
	@Override
	protected void onBind()
	{
	}
			
	@Override
	protected void onUnbind() {
	}

	@Override
	public void revealDisplay() {
	}
}
