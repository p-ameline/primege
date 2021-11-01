package com.primege.shared.model ;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable ;

import com.primege.shared.database.ArchetypeData;
import com.primege.shared.database.UserData;

/**
 * A user, along with all relevant information (current event, roles and forms she is allowed to fill) 
 * 
 * Created: 20 may 2016
 * Author: PA
 * 
 */
public class UserModel implements IsSerializable 
{
	protected UserData                 _userData ;
	protected ArrayList<ArchetypeData> _aArchetypes ;

	/**
	 * Default constructor (with zero information)
	 */
	public UserModel() 
	{
		_userData           = null ;
		_aArchetypes        = null ;
	}
	
	/**
	 * Plain vanilla constructor 
	 */
	public UserModel(final UserData userData, final ArrayList<ArchetypeData> aArchetypes) 
	{
		_userData  = new UserData(userData) ;

		setArchetypes(aArchetypes) ;
	}
		
	/**
	 * Copy constructor 
	 */
	public UserModel(final UserModel model) 
	{
		initFromUserModel(model) ;
	}
	
	public void reset4Model()
	{
		if (null != _userData)
			_userData.reset() ;
		if (null != _aArchetypes)
			_aArchetypes.clear() ;
	}
	
	public void initFromUserModel(final UserModel model)
	{
		if (null == model)
		{
			reset4Model() ;
			return ;
		}
		
		initModel(model._userData, model._aArchetypes) ;
	}
	
	public void initModel(final UserData userData, final ArrayList<ArchetypeData> aArchetypes)
	{
		reset4Model() ;
		
		if ((null == userData) && (null == aArchetypes))
			return ;
		
		if (null != userData)
			_userData = new UserData(userData) ;
		
		if (null != aArchetypes)
			setArchetypes(aArchetypes) ;
	}
	
	public UserData getUserData() {
		return _userData ;
	}
	
	/**
	 * Fill the UserData information by initializing local object from a model
	 * 
	 * @param userData UserData information to copy into local block
	 */
	public void setUserData(UserData userData) 
	{
		if (null == userData)
		{
			_userData = null ;
			return ;
		}
			
		if (null == _userData)
			_userData = new UserData(userData) ;
		else
			_userData.initFromUser(userData) ;
	}
 
	public ArrayList<ArchetypeData> getArchetypes() {
		return _aArchetypes ;
	}
	
	/**
	 * Get an archetype from its identifier
	 * 
	 * @param iArchetypeId Identifier of archetype to look for
	 * 
	 * @return The {@link ArchetypeData} if found, <code>null</code> if not
	 */
	public ArchetypeData getArchetypeFromId(int iArchetypeId)
	{
		if (_aArchetypes.isEmpty())
			return null ;
		
		for (ArchetypeData archetype : _aArchetypes)
			if (archetype.getId() == iArchetypeId)
				return archetype ;
		
		return null ;
	}
	
	/**
	 * Fill the array of archetypes from a model
	 * 
	 * @param aArchetypes array of archetypes to copy in order to initialize the local array
	 */
	public void setArchetypes(final ArrayList<ArchetypeData> aArchetypes) {
		setArchetypesArray(aArchetypes) ;
	}
	
	/**
	 * Add an archetype into the array of archetypes from copying a model
	 * 
	 * @param archetype ArchetypeData to add a copy from in the local array
	 */
	public void addArchetype(final ArchetypeData archetype) {
		addArchetypeToArray(archetype, _aArchetypes) ;
	}
	
	/**
	 * Fill the array of archetypes from a model
	 * 
	 * @param aArchetypes array of archetypes to copy in order to initialize the local array
	 * @param bForAction  if <code>true</code> set <code>_aArchetypes</code>, if <code>flase</code> set <code>_aManagedArchetypes</code>
	 */
	public void setArchetypesArray(final ArrayList<ArchetypeData> aArchetypes) 
	{
		if (null == aArchetypes)
		{
			_aArchetypes = null ;
			return ;
		}
		
		if (null == _aArchetypes)
			_aArchetypes = new ArrayList<ArchetypeData>() ;
		else
			_aArchetypes.clear() ;

		if (aArchetypes.isEmpty())
			return ;
		
		for (ArchetypeData archetype : aArchetypes)
			_aArchetypes.add(new ArchetypeData(archetype)) ;
	}
	
	/**
	 * Add an archetype into the array of archetypes from copying a model
	 * 
	 * @param archetype ArchetypeData to add a copy from in the local array
	 */
	public void addArchetypeToArray(final ArchetypeData archetype, ArrayList<ArchetypeData> aArray)
	{
		if (null == archetype)
			return ;
		
		if (null == aArray)
			aArray = new ArrayList<ArchetypeData>() ;
		
		if (false == aArray.contains(archetype))
			aArray.add(new ArchetypeData(archetype)) ;
	}	
}
