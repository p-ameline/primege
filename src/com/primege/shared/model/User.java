package com.primege.shared.model ;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable ;

import com.primege.shared.database.ArchetypeData;
import com.primege.shared.database.CityData;
import com.primege.shared.database.EventData;
import com.primege.shared.database.SiteData;
import com.primege.shared.database.UserData;
import com.primege.shared.database.UserRoleData;

/**
 * A user, along with all relevant information (current event, roles and forms she is allowed to fill) 
 * 
 * Created: 20 may 2016
 * Author: PA
 * 
 */
public class User extends UserModel implements IsSerializable 
{
	private ArrayList<UserRoleData> _aRoles ;
	private EventData               _eventData ;
	private ArrayList<CityData>     _aCities ;
	private ArrayList<SiteData>     _aSites ;

	/**
	 * Default constructor (with zero information)
	 */
	public User() 
	{
		super() ;
		
		_aRoles      = null ;
		_eventData   = null ;
		_aCities     = null ;
		_aSites      = null ;
	}
	
	/**
	 * Plain vanilla constructor 
	 */
	public User(final UserData userData, final EventData eventData, final ArrayList<UserRoleData> aRoles, final ArrayList<ArchetypeData> aArchetypes, final ArrayList<CityData> aCities, final ArrayList<SiteData> aSites) 
	{
		super(userData, aArchetypes) ;
		
		_eventData = new EventData(eventData) ;
		
		setRoles(aRoles) ;
		setCities(aCities) ;
		setSites(aSites) ;
	}
		
	/**
	 * Copy constructor 
	 */
	public User(final User model) 
	{
		initFromUser(model) ;
	}
	
	public void reset()
	{
		reset4Model() ;
		
		if (null != _aRoles)
			_aRoles.clear() ;
		if (null != _eventData)
			_eventData.reset() ;
		if (null != _aCities)
			_aCities.clear() ;
		if (null != _aSites)
			_aSites.clear() ;
	}
	
	public void initFromUser(final User model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		init(model._userData, model.getEventData(), model.getRoles(), model._aArchetypes, model._aCities, model._aSites) ;
	}
	
	public void init(final UserData userData, final EventData eventData, final ArrayList<UserRoleData> aRoles, final ArrayList<ArchetypeData> aArchetypes, final ArrayList<CityData> aCities, final ArrayList<SiteData> aSites)
	{
		reset() ;
		
		if ((null == userData) && (null == eventData) && (null == aRoles) && (null == aArchetypes))
			return ;
	
		initModel(userData, aArchetypes) ;
		
		if (null != aRoles)
			setRoles(aRoles) ;
		
		if (null != eventData)
			_eventData = new EventData(eventData) ;
		
		if (null != aCities)
			setCities(aCities) ;
		
		if (null != aSites)
			setSites(aSites) ;
	}
	
	public ArrayList<UserRoleData> getRoles() {
		return _aRoles ;
	}
	
	public EventData getEventData() {
		return _eventData ;
	}
	
	/**
	 * Fill the array of roles from a model
	 * 
	 * @param aRoles array of roles to copy in order to initialize the local array
	 */
	public void setRoles(final ArrayList<UserRoleData> aRoles) 
	{
		if (null == aRoles)
		{
			_aRoles = null ;
			return ;
		}
		
		if (null == _aRoles)
			_aRoles = new ArrayList<UserRoleData>() ;
		else
			_aRoles.clear() ;
			
		if (aRoles.isEmpty())
			return ;
		
		for (UserRoleData userRole : aRoles)
			_aRoles.add(new UserRoleData(userRole)) ;
	}
	
	/**
	 * Add a role into the array of roles from copying a model
	 * 
	 * @param role UserRoleData to add a copy from in the local array
	 */
	public void addRole(final UserRoleData role)
	{
		if (null == role)
			return ;
		
		if (null == _aRoles)
			_aRoles = new ArrayList<UserRoleData>() ;
			
		_aRoles.add(new UserRoleData(role)) ;
	}
	
	/**
	 * Fill the EventData information by initializing local object from a model
	 * 
	 * @param eventData EventData information to copy into local block
	 */
	public void setEventData(EventData eventData) 
	{
		if (null == eventData)
		{
			_eventData = null ;
			return ;
		}
			
		if (null == _eventData)
			_eventData = new EventData(eventData) ;
		else
			_eventData.initFromEventData(eventData) ;
	}
	
	public ArrayList<CityData> getCities() {
		return _aCities ;
	}
	
	public ArrayList<SiteData> getSites() {
		return _aSites ;
	}
	
	/**
	 * Fill the array of cities from a model
	 * 
	 * @param aCities array of cities to copy in order to initialize the local array
	 */
	public void setCities(final ArrayList<CityData> aCities) 
	{
		if (null == aCities)
		{
			_aCities = null ;
			return ;
		}
		
		if (null == _aCities)
			_aCities = new ArrayList<CityData>() ;
		else
			_aCities.clear() ;
			
		if (aCities.isEmpty())
			return ;
		
		for (CityData city : aCities)
			_aCities.add(new CityData(city)) ;
	}
	
	/**
	 * Add a city into the array of cities from copying a model
	 * 
	 * @param city CityData to add a copy from in the local array
	 */
	public void addCity(final CityData city)
	{
		if (null == city)
			return ;
		
		if (null == _aCities)
			_aCities = new ArrayList<CityData>() ;
			
		_aCities.add(new CityData(city)) ;
	}
	
	/**
	 * Fill the array of sites from a model
	 * 
	 * @param aSites array of sites to copy in order to initialize the local array
	 */
	public void setSites(final ArrayList<SiteData> aSites) 
	{
		if (null == aSites)
		{
			_aSites = null ;
			return ;
		}
		
		if (null == _aSites)
			_aSites = new ArrayList<SiteData>() ;
		else
			_aSites.clear() ;
			
		if (aSites.isEmpty())
			return ;
		
		for (SiteData site : aSites)
			_aSites.add(new SiteData(site)) ;
	}
	
	/**
	 * Add a site into the array of sites from copying a model
	 * 
	 * @param site SiteData to add a copy from in the local array
	 */
	public void addSite(final SiteData site)
	{
		if (null == site)
			return ;
		
		if (null == _aSites)
			_aSites = new ArrayList<SiteData>() ;
			
		_aSites.add(new SiteData(site)) ;
	}
}
