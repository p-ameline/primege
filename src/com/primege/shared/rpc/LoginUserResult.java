package com.primege.shared.rpc;

import java.util.ArrayList;

import com.primege.shared.database.ArchetypeData;
import com.primege.shared.database.CityData;
import com.primege.shared.database.EventData;
import com.primege.shared.database.SiteData;
import com.primege.shared.database.UserData;
import com.primege.shared.database.UserRoleData;
import com.primege.shared.model.User;

import net.customware.gwt.dispatch.shared.Result;

public class LoginUserResult implements Result
{
	private User   _user = new User() ;
	private String _sVersion ;
	
	/**
	 * */
	public LoginUserResult()
	{
		super() ;
		
		_sVersion = "" ;
	}
		
	public User getUser() {
		return _user ;
	}
	public void setUser(User user) {
		_user.initFromUser(user) ;
	}
	
	public String getVersion() {
		return _sVersion ;
	}
	public void setVersion(String sVersion) {
		_sVersion = sVersion ;
	}
	
	public void setUserData(UserData userData) {
		_user.setUserData(userData) ;
	}
	public UserData getUserData() {
		return _user.getUserData() ;
	}
	
	public void setEventData(EventData eventData) {
		_user.setEventData(eventData) ;
	}
	public EventData getEventData() {
		return _user.getEventData() ;
	}
	
	public ArrayList<UserRoleData> getRoles() {
		return _user.getRoles() ;
	}
	public void setRoles(ArrayList<UserRoleData> aRoles) {
		_user.setRoles(aRoles) ;
	}
	
	public ArrayList<ArchetypeData> getArchetypes() {
		return _user.getArchetypes() ;
	}
	public void setArchetypes(ArrayList<ArchetypeData> aArchetypes) {
		_user.setArchetypes(aArchetypes) ;
	}
	
	public ArrayList<CityData> getCities() {
		return _user.getCities() ;
	}
	public void setCities(ArrayList<CityData> aCities) {
		_user.setCities(aCities) ;
	}
	
	public ArrayList<SiteData> getSites() {
		return _user.getSites() ;
	}
	public void setSites(ArrayList<SiteData> aSites) {
		_user.setSites(aSites) ;
	}
}
