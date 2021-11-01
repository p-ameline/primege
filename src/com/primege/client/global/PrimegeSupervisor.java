package com.primege.client.global;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import java.util.ArrayList;

import com.google.inject.Inject;

import com.primege.client.gin.PrimegeGinjector;
import com.primege.shared.database.SiteData;
import com.primege.shared.model.User;

public class PrimegeSupervisor extends PrimegeSupervisorModel
{
	private   PrimegeGinjector _injector ;
	
	@Inject
	public PrimegeSupervisor(final DispatchAsync dispatcher, final EventBus eventBus)
	{
		super(dispatcher, eventBus) ;
		
		_user     = new User() ;
		_injector = null ;
	}
	
	public void setInjector(PrimegeGinjector injector) {
		_injector = injector ;
	}
	public PrimegeGinjector getInjector() {
		return _injector ;
	}
	
	public int getEventId() {
		if ((null == _user) || (null == ((User) _user).getEventData()))
			return -1 ;
		return ((User) _user).getEventData().getId() ;
	}
	
	public SiteData getSiteFromId(final int iSiteId)
	{
		if (null == _user)
			return null ;
		
		ArrayList<SiteData> aSites = ((User) _user).getSites() ;
		if ((null == aSites) || aSites.isEmpty())
			return null ;
		
		for (SiteData site : aSites)
			if (site.getId() == iSiteId)
				return site ;
		
		return null ;
	}
}
