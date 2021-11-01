package com.primege.client.mvp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.primege.client.global.PrimegeSupervisor;
import com.primege.shared.database.CityData;
import com.primege.shared.database.EventData;
import com.primege.shared.model.CityForDateBlock;
import com.primege.shared.model.DashboardBlocks;
import com.primege.shared.model.User;
import com.primege.shared.rpc.GetDashboardBlocksAction;
import com.primege.shared.rpc.GetDashboardBlocksResult;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

/**
 * Presenter from the presenter/view model for dashboards
 *
 */
public class DashboardPresenter extends DashboardPresenterModel<DashboardPresenter.Display>
{
	public interface Display extends DashboardInterfaceModel
	{
		public void resetAll() ;
		
		public void setEvent(EventData event) ;
		public void setCities(ArrayList<CityData> aCities) ;
		
		public void insertColumn(final CityForDateBlock blockForCol) ;
	}
	
	@Inject
	public DashboardPresenter(final Display           display, 
							              final EventBus          eventBus,
							              final DispatchAsync     dispatcher,
							              final PrimegeSupervisor supervisor) 
	{
		super(display, eventBus, dispatcher, supervisor) ;
	}
	
	protected void resetAll()
	{
		display.resetAll() ;
		
		resetAll4Model() ;
		
		display.setEvent(((User) _supervisor.getUser()).getEventData()) ;
		display.setCities(((User) _supervisor.getUser()).getCities()) ;
	}
	
	/**
	 * Fill the dashboard with information from the database
	 *
	 */
	protected void fillDashboard()
	{
		if ("".equals(_sPivot))
			return ;
		
		// Pivot is the city
		//
		if ("$city$".equals(_sPivot))
		{
			String sCity = "" ;
			
			if ("".equals(_sStaticPivotValue))
				sCity = display.getPivotInformation() ;
			else
				sCity = _sStaticPivotValue ;
				
			if ("".equals(sCity))
				return ;
			
			int iCityId = Integer.parseInt(sCity) ;
			if (iCityId > 0)
				_dispatcher.execute(new GetDashboardBlocksAction(_supervisor.getUserId(), ((PrimegeSupervisor) _supervisor).getEventId(), iCityId), new getDashboardBlocksCallback()) ;
			
			return ;
		}
		
		// Pivot is the date
		//
		if ("$date$".equals(_sPivot))
		{
			String sDate = "" ;
			
			if ("".equals(_sStaticPivotValue))
				sDate = display.getPivotInformation() ;
			else
				sDate = _sStaticPivotValue ;
			
			if ("".equals(sDate))
				return ;
				
			_dispatcher.execute(new GetDashboardBlocksAction(_supervisor.getUserId(), ((PrimegeSupervisor) _supervisor).getEventId(), sDate), new getDashboardBlocksCallback()) ;
		}
	}
	
	protected class getDashboardBlocksCallback implements AsyncCallback<GetDashboardBlocksResult> 
	{
		public getDashboardBlocksCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("getDashboardBlocksCallback: Unhandled error", cause) ;
		}//end handleFailure

		@Override
		public void onSuccess(GetDashboardBlocksResult value) 
		{
			DashboardBlocks dashboardBlocks = value.getDashboardsBlocks() ;
			
			if (dashboardBlocks.isEmpty())
			{
				Window.alert("Aucune information à afficher") ;
				return ;
			}
			
			ArrayList<CityForDateBlock> aCity4block = dashboardBlocks.getInformation() ;
			
			// Pivot is the city - it means that all CityForDateBlock share the same city
			//
			if ("$city$".equals(_sPivot))
			{
				Collections.sort(aCity4block, new Comparator<CityForDateBlock>() {
	        @Override
	        public int compare(CityForDateBlock o1, CityForDateBlock o2) {
	        	
	        	// We sort the dates in reverse order so that the more recent is at left
	        	// and hence immediately visible without scrolling
	        	//
	        	// return o1.getDate().compareTo(o2.getDate()) ;
	        	return o2.getDate().compareTo(o1.getDate()) ;
	        }
				}) ;
			}
			
			// Pivot is the date - it means that all CityForDateBlock share the same date
			//
			if ("$date$".equals(_sPivot))
			{
				Collections.sort(aCity4block, new Comparator<CityForDateBlock>() {
					@Override
			    public int compare(CityForDateBlock o1, CityForDateBlock o2) {
						return o1.getCityId() - o2.getCityId() ;
			    }
				}) ;
			}
			
			for (Iterator<CityForDateBlock> it = aCity4block.iterator() ; it.hasNext() ; )
				display.insertColumn(it.next()) ;
		}
	}
}
